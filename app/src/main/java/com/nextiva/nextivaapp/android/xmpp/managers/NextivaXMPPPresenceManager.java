/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.managers;

import android.text.TextUtils;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.net.buses.RxBus;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.XmppErrorEvent;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPPresenceManager;
import com.nextiva.nextivaapp.android.xmpp.util.IQBuilderUtil;
import com.nextiva.nextivaapp.android.xmpp.util.IQReaderUtil;
import com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants;
import com.nextiva.nextivaapp.android.xmpp.util.PresenceUtil;
import com.nextiva.nextivaapp.android.xmpp.util.XmppDebuggingUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by joedephillipo on 3/30/18.
 */
public class NextivaXMPPPresenceManager implements
        XMPPPresenceManager,
        PresenceEventListener {

    private final SessionManager mSessionManager;
    private final UmsRepository mUmsRepository;
    private final DbManager mDbManager;
    private final LogManager mLogManager;
    private final ConnectionStateManager mConnectionStateManager;
    private final XMPPConnectionActionManager mXMPPConnectionActionManager;

    private CompositeDisposable mCompositeDisposable;
    private XMPPTCPConnection mConnection;

    @Inject
    public NextivaXMPPPresenceManager(
            SessionManager sessionManager,
            DbManager dbManager,
            UmsRepository umsRepository,
            LogManager logManager,
            ConnectionStateManager connectionStateManager,
            XMPPConnectionActionManager xmppConnectionActionManager) {

        mSessionManager = sessionManager;
        mUmsRepository = umsRepository;
        mDbManager = dbManager;
        mLogManager = logManager;
        mConnectionStateManager = connectionStateManager;
        mXMPPConnectionActionManager = xmppConnectionActionManager;
    }

    private void initPresence() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        DbPresence nextivaPresence = mSessionManager.getUserPresence();

        if (nextivaPresence != null) {
            nextivaPresence.setState(Enums.Contacts.PresenceStates.AVAILABLE);
        } else if (mSessionManager.getUserDetails() != null) {
            nextivaPresence = new DbPresence(mSessionManager.getUserDetails().getImpId(), Enums.Contacts.PresenceStates.AVAILABLE, Constants.PRESENCE_MOBILE_PRIORITY, null, Enums.Contacts.PresenceTypes.AVAILABLE);
        }

        setPresence(nextivaPresence);

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    // --------------------------------------------------------------------------------------------
    // XMPPPresenceManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void init(XMPPTCPConnection connection, CompositeDisposable compositeDisposable) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        final Roster roster = Roster.getInstanceFor(connection);
        roster.addPresenceEventListener(NextivaXMPPPresenceManager.this);
        roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
        mConnection = connection;
        mCompositeDisposable = compositeDisposable;
        initPresence();

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);

    }

    @Override
    public void setPresence(DbPresence nextivaPresence) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mConnection == null || !mConnection.isConnected() || nextivaPresence == null) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, R.string.log_message_xmpp_connection_null_or_disconnected);
            reconnect();
            return;
        }

        try {
            Presence presence = PresenceUtil.dbPresenceToSmackPresence(nextivaPresence);
            mConnection.sendStanza(presence);
            mCompositeDisposable.add(mUmsRepository.getSuperPresence(mCompositeDisposable).subscribe());

        } catch (SmackException.NotConnectedException | InterruptedException exception) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, exception.getClass().getSimpleName());

            RxBus.INSTANCE.publish(new XmppErrorEvent(exception));
            exception.printStackTrace();
        }

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    private void reconnect() {
        if (mXMPPConnectionActionManager != null) {
            mXMPPConnectionActionManager.startConnection();
        }
    }

    @Override
    public void sendVCardUpdatePresence() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (!mConnectionStateManager.isXmppConnected()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_xmpp_disconnected);
            return;
        }

        if (mSessionManager.getUserPresence() != null) {
            try {
                mConnection.sendStanza(IQBuilderUtil.getVCardUpdatePresence());
                mConnection.sendStanza(PresenceUtil.dbPresenceToSmackPresence(mSessionManager.getUserPresence()));

                mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);

            } catch (SmackException.NotConnectedException | InterruptedException exception) {
                XmppDebuggingUtil.displayDebugLogMessage(exception, Thread.currentThread().getStackTrace()[2]);
                RxBus.INSTANCE.publish(new XmppErrorEvent(exception));

                mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, exception.getClass().getSimpleName());
            }

        } else {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_FAILURE, R.string.log_message_required_data_unavailable);
        }
    }

    @Override
    public void refreshPresences(ArrayList<NextivaContact> contactList, ArrayList<String> jidList, boolean isUpdateFromContactDetails) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (!mConnectionStateManager.isXmppConnected()) {
            RxBus.INSTANCE.publish(new RxEvents.RosterResponseEvent(false, 0));
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_xmpp_disconnected);
            return;
        }

        if (mConnection == null || !mConnection.isConnected()) {
            RxBus.INSTANCE.publish(new RxEvents.RosterResponseEvent(false, 0));
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, R.string.log_message_xmpp_connection_null_or_disconnected);
            return;
        }

        mCompositeDisposable.add(
                mUmsRepository.getOnDemandPresences(jidList)
                        .subscribe(dbPresences -> {
                            for (DbPresence presence : dbPresences) {
                                if (mConnection != null && mConnection.getUser() != null &&
                                        mConnection.getUser().asBareJid() != null &&
                                        !TextUtils.equals(presence.getJid(), mConnection.getUser().asBareJid().toString()) &&
                                        presence.getJid().endsWith(NextivaXMPPConstants.JID_SUFFIX)) {
                                    for (NextivaContact contact : contactList) {
                                        if (!TextUtils.isEmpty(contact.getJid()) &&
                                                !TextUtils.isEmpty(presence.getJid()) &&
                                                TextUtils.equals(contact.getJid().toLowerCase(), presence.getJid().toLowerCase())) {
                                            switch (contact.getSubscriptionState()) {
                                                case Enums.Contacts.SubscriptionStates.SUBSCRIBED:
                                                    contact.setPresence(presence);
                                                    break;
                                                case Enums.Contacts.SubscriptionStates.PENDING:
                                                    contact.setPresence(PresenceUtil.getPendingPresence(contact.getJid()));
                                                    break;
                                                case Enums.Contacts.SubscriptionStates.UNSUBSCRIBED:
                                                    contact.setPresence(PresenceUtil.getUnsubscribedPresence(contact.getJid()));
                                                    break;
                                            }
                                        }
                                    }

                                }
                            }

                            mDbManager.saveRosterContacts(mCompositeDisposable,
                                                          mUmsRepository,
                                                          contactList,
                                                          isUpdateFromContactDetails);
                        }));

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void refreshPresences(ArrayList<String> jids) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (!mConnectionStateManager.isXmppConnected()) {
            RxBus.INSTANCE.publish(new RxEvents.RosterResponseEvent(false, 0));
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_xmpp_disconnected);
            return;
        }

        if (mConnection == null || !mConnection.isAuthenticated()) {
            RxBus.INSTANCE.publish(new RxEvents.RosterResponseEvent(false, 0));
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, R.string.log_message_xmpp_connection_null_or_disconnected);
            return;
        }

        mCompositeDisposable.add(
                mUmsRepository.getOnDemandPresences(jids)
                        .subscribe(dbPresences -> {
                            for (DbPresence presence : dbPresences) {
                                if (!TextUtils.equals(presence.getJid(), mConnection.getUser().asBareJid().toString())) {
                                    mDbManager.updatePresence(presence, mCompositeDisposable);

                                }
                            }
                        }));
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // PresenceEventListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void presenceAvailable(FullJid address, final Presence availablePresence) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (!mConnectionStateManager.isXmppConnected()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_xmpp_disconnected);
            return;
        }

        if (mConnection == null || !mConnection.isAuthenticated()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, R.string.log_message_xmpp_connection_null_or_disconnected);
            return;
        }

        if (!TextUtils.equals(availablePresence.getFrom().asEntityFullJidIfPossible().toString(), mConnection.getUser().toString()) &&
                TextUtils.equals(availablePresence.getFrom().asBareJid().toString(), mConnection.getUser().asBareJid().toString())) {
            mUmsRepository.getSuperPresence(mCompositeDisposable).subscribe();

        } else if (availablePresence.getFrom() != null && availablePresence.getFrom().asBareJid() != null && mConnection.getUser() != null && mConnection.getUser().asBareJid() != null) {
            if (IQReaderUtil.isVCardUpdatePresence(availablePresence)) {
                mUmsRepository.getVCard(availablePresence.getFrom().asBareJid().toString(), mCompositeDisposable)
                        .subscribe();

            } else if (IQReaderUtil.isManualTruePresence(availablePresence) && availablePresence.getMode() != Presence.Mode.xa) {
                DbPresence nextivaPresence = new DbPresence();
                nextivaPresence.setJid(availablePresence.getFrom().asBareJid().toString());
                nextivaPresence.setType(Enums.Contacts.PresenceTypes.AVAILABLE);
                nextivaPresence.setPriority(availablePresence.getPriority());
                nextivaPresence.setStatus(availablePresence.getStatus());

                switch (availablePresence.getMode()) {
                    case available:
                        nextivaPresence.setState(Enums.Contacts.PresenceStates.AVAILABLE);
                        break;
                    case away:
                        nextivaPresence.setState(Enums.Contacts.PresenceStates.AWAY);
                        break;
                    case dnd:
                        nextivaPresence.setState(Enums.Contacts.PresenceStates.BUSY);
                        break;
                    default:
                        nextivaPresence.setState(Enums.Contacts.PresenceStates.OFFLINE);
                        break;
                }

                mDbManager.updatePresence(nextivaPresence, mCompositeDisposable);

            } else if (IQReaderUtil.isManualFalsePresence(availablePresence) && IQReaderUtil.getPresenceFromPresenceWithBroadsoftNamespace(availablePresence) != null) {
                DbPresence nextivaPresence = IQReaderUtil.getPresenceFromPresenceWithBroadsoftNamespace(availablePresence);
                if (nextivaPresence != null) {
                    mDbManager.updatePresence(nextivaPresence, mCompositeDisposable);
                }

            } else if ((availablePresence.getMode() == Presence.Mode.xa || availablePresence.getPriority() == Constants.PRESENCE_ON_CALL_PRIORITY) && mConnection.isAuthenticated()) {
                ArrayList<String> jid = new ArrayList<>();
                jid.add(availablePresence.getFrom().asBareJid().toString());
                refreshPresences(jid);
            }

        } else {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_FAILURE, R.string.log_message_required_data_unavailable);
        }
    }

    @Override
    public void presenceUnavailable(FullJid address, Presence unavailablePresence) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        if (!mConnectionStateManager.isXmppConnected()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_xmpp_disconnected);
            return;
        }

        if (mConnection == null || !mConnection.isConnected()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, R.string.log_message_xmpp_connection_null_or_disconnected);
            return;
        }

        if (unavailablePresence.getFrom() != null && unavailablePresence.getFrom().asBareJid() != null && mConnection.getUser() != null && mConnection.getUser().asBareJid() != null
                && TextUtils.isEmpty(unavailablePresence.getStanzaId())) {
            if (TextUtils.equals(mConnection.getUser().asBareJid().toString(), unavailablePresence.getFrom().asBareJid().toString())) {
                mUmsRepository.getSuperPresence(mCompositeDisposable).subscribe();

            } else {
                ArrayList<String> jid = new ArrayList<>();
                jid.add(unavailablePresence.getFrom().asBareJid().toString());
                refreshPresences(jid);
            }

        } else {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_FAILURE, R.string.log_message_required_data_unavailable);
        }
    }

    @Override
    public void presenceError(Jid address, Presence errorPresence) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
    }

    @Override
    public void presenceSubscribed(BareJid address, Presence subscribedPresence) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (!mConnectionStateManager.isXmppConnected()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_xmpp_disconnected);
            return;
        }

        if (mConnection == null || !mConnection.isConnected()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, R.string.log_message_xmpp_connection_null_or_disconnected);
            return;
        }

        if (subscribedPresence.getFrom() != null && subscribedPresence.getFrom().asBareJid() != null && mConnection.getUser() != null && mConnection.getUser().asBareJid() != null) {
            ArrayList<String> jid = new ArrayList<>();
            jid.add(subscribedPresence.getFrom().asBareJid().toString());
            refreshPresences(jid);

        } else {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_FAILURE, R.string.log_message_required_data_unavailable);
        }
    }

    @Override
    public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (!mConnectionStateManager.isXmppConnected()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_xmpp_disconnected);
            return;
        }

        if (mConnection == null || !mConnection.isConnected()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, R.string.log_message_xmpp_connection_null_or_disconnected);
            return;
        }

        if (unsubscribedPresence.getFrom() != null && unsubscribedPresence.getFrom().asBareJid() != null && mConnection.getUser() != null && mConnection.getUser().asBareJid() != null) {
            DbPresence presence = PresenceUtil.getUnsubscribedPresence(unsubscribedPresence.getFrom().asBareJid().toString());
            if (!TextUtils.equals(unsubscribedPresence.getFrom().asBareJid().toString(), mConnection.getUser().asBareJid().toString())) {
                mDbManager.updatePresence(presence, mCompositeDisposable);
            } else {
                mUmsRepository.getSuperPresence(mCompositeDisposable).subscribe();
            }
        } else {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_FAILURE, R.string.log_message_required_data_unavailable);
        }
    }
    // --------------------------------------------------------------------------------------------
}
