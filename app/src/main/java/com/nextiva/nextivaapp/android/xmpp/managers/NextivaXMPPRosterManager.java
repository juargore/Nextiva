/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.managers;

import android.app.Application;
import android.os.Handler;
import android.text.TextUtils;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.net.buses.RxBus;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.RosterPresenceSubscriptionRequestEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.RosterResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.XmppErrorEvent;
import com.nextiva.nextivaapp.android.util.GuidUtil;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPPresenceManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPRosterManager;
import com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants;
import com.nextiva.nextivaapp.android.xmpp.util.PresenceUtil;
import com.nextiva.nextivaapp.android.xmpp.util.XmppDebuggingUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by joedephillipo on 3/30/18.
 */

public class NextivaXMPPRosterManager implements XMPPRosterManager, RosterListener {

    private final Application mApplication;
    private final UmsRepository mUmsRepository;
    private final DbManager mDbManager;
    private final XMPPPresenceManager mPresenceManager;
    private final XMPPConnectionActionManager mActionManager;
    private final LogManager mLogManager;

    final private ArrayList<RosterPresenceSubscriptionRequestEvent> mSubscriptionRequests = new ArrayList<>();

    private XMPPTCPConnection mConnection;
    private CompositeDisposable mCompositeDisposable;

    @Inject
    public NextivaXMPPRosterManager(
            Application application,
            XMPPPresenceManager presenceManager,
            DbManager dbManager,
            UmsRepository umsRepository,
            XMPPConnectionActionManager actionManager,
            LogManager logManager) {

        mApplication = application;
        mPresenceManager = presenceManager;
        mDbManager = dbManager;
        mUmsRepository = umsRepository;
        mActionManager = actionManager;
        mLogManager = logManager;
    }

    // --------------------------------------------------------------------------------------------
    // XMPPRosterManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void init(XMPPTCPConnection connection, CompositeDisposable compositeDisposable) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        Roster.getInstanceFor(connection).addRosterListener(NextivaXMPPRosterManager.this);
        mConnection = connection;
        mCompositeDisposable = compositeDisposable;

        mConnection.addAsyncStanzaListener(packet -> {
            try {
                String fromJid = packet.getFrom().asBareJid().toString();

                RosterEntry rosterEntry = Roster.getInstanceFor(mConnection).getEntry(JidCreate.bareFrom(fromJid));

                if (rosterEntry != null && rosterEntry.isSubscriptionPending()) {
                    acceptSubscriptionRequest(packet.getFrom().asBareJid().toString());

                } else {
                    RosterPresenceSubscriptionRequestEvent event = new RosterPresenceSubscriptionRequestEvent(false, fromJid, null);
                    boolean alreadyInvited = false;

                    for (RosterPresenceSubscriptionRequestEvent request : mSubscriptionRequests) {
                        if (TextUtils.equals(request.getJid(), event.getJid())) {
                            alreadyInvited = true;
                        }
                    }

                    if (!alreadyInvited) {
                        if (mSubscriptionRequests.size() == 0) {
                            RxBus.INSTANCE.publish(event);
                        }

                        mSubscriptionRequests.add(event);
                    }
                }
            } catch (XmppStringprepException e) {
                mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());

            }
        }, stanza -> stanza instanceof Presence && ((Presence) stanza).getType() == Presence.Type.subscribe);

        mConnection.addAsyncStanzaListener(packet -> {
            try {
                String fromJid = packet.getFrom().asBareJid().toString();
                RosterEntry rosterEntry = Roster.getInstanceFor(mConnection).getEntry(JidCreate.bareFrom(fromJid));

                if (rosterEntry != null) {
                    rosterEntry.cancelSubscription();
                    Presence unsubscribed = new Presence(rosterEntry.getJid(), Presence.Type.unsubscribed);
                    mConnection.sendStanza(unsubscribed);
                }
            } catch (XmppStringprepException e) {
                mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            }
        }, stanza -> stanza instanceof Presence && ((Presence) stanza).getType() == Presence.Type.unsubscribe);

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void refreshRoster(final boolean isUpdateFromContactDetails) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        ArrayList<NextivaContact> rosterContacts = getRosterContacts();

        mCompositeDisposable.add(
                mUmsRepository.getContactStorage()
                        .subscribe(addressBookContacts -> {
                            ArrayList<NextivaContact> tempRosterContacts = new ArrayList<>(rosterContacts);
                            Roster roster = Roster.getInstanceFor(mConnection);
                            RosterEntry rosterEntry;
                            boolean foundContact;

                            // Merge our standard xmpp roster with the broadworks addressbook contacts
                            for (NextivaContact rosterContact : tempRosterContacts) {
                                foundContact = false;

                                for (NextivaContact addressbookContact : addressBookContacts) {
                                    if (!TextUtils.isEmpty(addressbookContact.getJid()) &&
                                            !TextUtils.isEmpty(rosterContact.getJid()) &&
                                            TextUtils.equals(addressbookContact.getJid().toLowerCase(), rosterContact.getJid().toLowerCase()) &&
                                            rosterContact.getJid().endsWith(NextivaXMPPConstants.JID_SUFFIX)) {
                                        foundContact = true;
                                        addressbookContact.setSubscriptionState(rosterContact.getSubscriptionState());
                                        rosterContacts.remove(rosterContact);
                                        break;
                                    }
                                }

                                if (!foundContact) {
                                    try {
                                        rosterEntry = roster.getEntry(JidCreate.bareFrom(rosterContact.getJid()));

                                        if (rosterEntry != null && !rosterEntry.canSeeHisPresence() && !rosterEntry.canSeeMyPresence()) {
                                            rosterContacts.remove(rosterContact);
                                        }
                                    } catch (XmppStringprepException e) {
                                        mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
                                    }
                                }
                            }

                            // If there are contacts not in our addressbook we need to add them in the else.
                            if (rosterContacts.size() == 0) {
                                continueRefreshingRoster(addressBookContacts, isUpdateFromContactDetails);

                            } else if (addressBookContacts.size() == 0) {
                                RxBus.INSTANCE.publish(new RosterResponseEvent(true, 0));

                            } else {
                                ArrayList<String> jidList = new ArrayList<>();

                                for (NextivaContact nextivaContact : rosterContacts) {
                                    jidList.add(nextivaContact.getJid());
                                }

                                mCompositeDisposable.add(
                                        mDbManager.getDirectoryContactsInJids(jidList)
                                                .subscribe((nextivaContacts, throwable) -> {
                                                    if (nextivaContacts != null && nextivaContacts.size() > 0) {
                                                        mActionManager.updateRoster(new ArrayList<>(nextivaContacts),
                                                                addDirectoryContactsToRosterList(nextivaContacts, addressBookContacts),
                                                                Enums.Contacts.UpdateActions.ADD_NEW_CONTACT);
                                                    } else {
                                                        continueRefreshingRoster(addressBookContacts, isUpdateFromContactDetails);
                                                    }
                                                }));
                            }
                        }));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    private void continueRefreshingRoster(ArrayList<NextivaContact> addressBookContacts, boolean isUpdateFromContactDetails) {
        ArrayList<String> jidList = new ArrayList<>();

        for (NextivaContact contact : addressBookContacts) {
            if (!TextUtils.isEmpty(contact.getJid()) && contact.getJid().endsWith(NextivaXMPPConstants.JID_SUFFIX)) {
                jidList.add(contact.getJid());
            }
        }

        mPresenceManager.refreshPresences(addressBookContacts, jidList, isUpdateFromContactDetails);
    }

    @Override
    public void updateBroadsoftRosterContacts(ArrayList<NextivaContact> updatedContacts) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        mCompositeDisposable.add(
                mUmsRepository.setContactStorageWithContacts(updatedContacts)
                        .subscribe(success -> mActionManager.contactUpdateRefresh()));
    }

    @Override
    public void updateXMPPRosterContacts(ArrayList<NextivaContact> contactsToAdd, @Enums.Contacts.UpdateActions.UpdateAction int actionType) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (actionType > -1 && actionType != Enums.Contacts.UpdateActions.ADD_NEW_CONTACT) {
            Roster roster = Roster.getInstanceFor(mConnection);

            mCompositeDisposable.add(
                    mUmsRepository.getVCards(contactsToAdd, mCompositeDisposable)
                            .subscribe());


            for (NextivaContact nextivaContact : contactsToAdd) {
                if (!TextUtils.isEmpty(nextivaContact.getJid())) {
                    if (actionType == Enums.Contacts.UpdateActions.ADD_EXISTING_CONTACT) {
                        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start, mApplication.getString(R.string.log_message_add_flow));
                        addContactToRoster(roster, nextivaContact);

                    } else if (actionType == Enums.Contacts.UpdateActions.REMOVE) {
                        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start, mApplication.getString(R.string.log_message_edit_flow));
                        deleteContactFromRoster(roster, nextivaContact);
                    }
                }
            }
        }

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    private void addContactToRoster(Roster roster, NextivaContact nextivaContact) {
        try {
            BareJid jid = JidCreate.entityBareFrom(nextivaContact.getJid());
            roster.createEntry(jid, nextivaContact.getUiName(), null);

            for (RosterEntry entry : roster.getEntries()) {
                if (entry.getJid().equals(jid)) {
                    try {
                        roster.sendSubscriptionRequest(jid);
                    } catch (SmackException.NotLoggedInException | InterruptedException | SmackException.NotConnectedException e) {

                        RxBus.INSTANCE.publish(new XmppErrorEvent(e));
                        XmppDebuggingUtil.displayDebugLogMessage(e, Thread.currentThread().getStackTrace()[2]);
                    }
                }
            }

            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success, mApplication.getString(R.string.log_message_add_flow));

        } catch (XmppStringprepException | SmackException.NotLoggedInException | SmackException.NoResponseException | SmackException.NotConnectedException | XMPPException.XMPPErrorException | InterruptedException exception) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, mApplication.getString(R.string.log_message_add_flow_error, exception.getClass().getSimpleName()));

            RxBus.INSTANCE.publish(new XmppErrorEvent(exception));
            XmppDebuggingUtil.displayDebugLogMessage(exception, Thread.currentThread().getStackTrace()[2]);
        }
    }

    private void deleteContactFromRoster(Roster roster, NextivaContact nextivaContact) {
        mDbManager.deleteContactByContactId(mCompositeDisposable, nextivaContact.getUserId());

        try {
            BareJid jid = JidCreate.entityBareFrom(nextivaContact.getJid());

            for (RosterEntry entry : roster.getEntries()) {
                if (entry.getJid().equals(jid)) {
                    try {
                        Presence unsubscribePresence = new Presence(JidCreate.bareFrom(jid), Presence.Type.unsubscribe);
                        mConnection.sendStanza(unsubscribePresence);
                        Presence unsubscribedPresence = new Presence(JidCreate.bareFrom(jid), Presence.Type.unsubscribed);
                        mConnection.sendStanza(unsubscribedPresence);
                        roster.removeEntry(entry);

                        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success, mApplication.getString(R.string.log_message_edit_flow));

                    } catch (SmackException.NotLoggedInException | SmackException.NoResponseException | XmppStringprepException | XMPPException.XMPPErrorException | InterruptedException | SmackException.NotConnectedException exception) {
                        mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, mApplication.getString(R.string.log_message_edit_flow_error, exception.getClass().getSimpleName()));

                        XmppDebuggingUtil.displayDebugLogMessage(exception, Thread.currentThread().getStackTrace()[2]);
                        RxBus.INSTANCE.publish(new XmppErrorEvent(exception));
                    }
                }
            }
        } catch (XmppStringprepException e) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, mApplication.getString(R.string.log_message_edit_flow_error, e.getClass().getSimpleName()));

        }
    }

    private ArrayList<NextivaContact> addDirectoryContactsToRosterList(List<NextivaContact> directoryContacts, ArrayList<NextivaContact> addressBookContacts) {
        for (NextivaContact nextivaContact : directoryContacts) {
            nextivaContact.setUserId(String.valueOf(GuidUtil.getUniqueContactId(mDbManager)));
            nextivaContact.setContactType(Enums.Contacts.ContactTypes.PERSONAL);

            addressBookContacts.add(nextivaContact);
        }

        return addressBookContacts;
    }

    @Override
    public void subscribeToContact(String jid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            Presence subscribe = new Presence(JidCreate.bareFrom(jid), Presence.Type.subscribe);
            mConnection.sendStanza(subscribe);

            mDbManager.updatePresence(PresenceUtil.getPendingPresence(jid), mCompositeDisposable);

        } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
        }
    }

    @Override
    public void unsubscribeFromContact(String jid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            Presence unsubscribe = new Presence(JidCreate.bareFrom(jid), Presence.Type.unsubscribe);
            Roster roster = Roster.getInstanceFor(mConnection);
            mConnection.sendStanza(unsubscribe);
            roster.getEntry(JidCreate.bareFrom(jid)).cancelSubscription();

            mDbManager.updatePresence(PresenceUtil.getUnsubscribedPresence(jid), mCompositeDisposable);

        } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
        }
    }

    @Override
    public void acceptSubscriptionRequest(String jid) {
        handlePendingSubscriptionRequests(jid);
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            subscribeToContact(jid);
            Presence subscribed = new Presence(JidCreate.bareFrom(jid), Presence.Type.subscribed);
            mConnection.sendStanza(subscribed);

            // Immediately refreshing is too soon for the server so we give the server a quarter second to catch up then refresh.
            // Then when we call to refresh rosterContacts we will see a user in our Xmpp roster that is not in our Broadsoft roster so it will be added.
            new Handler().postDelayed(() -> refreshRoster(false), Constants.ONE_SECOND_IN_MILLIS);

        } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
        }
    }

    @Override
    public void declineSubscriptionRequest(String jid) {
        handlePendingSubscriptionRequests(jid);
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (!TextUtils.isEmpty(jid)) {
            try {
                Presence unsubscribed = new Presence(JidCreate.bareFrom(jid), Presence.Type.unsubscribed);
                mConnection.sendStanza(unsubscribed);

            } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
                mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            }
        }
    }

    private void handlePendingSubscriptionRequests(String jid) {
        ArrayList<RosterPresenceSubscriptionRequestEvent> tempEventList = new ArrayList<>(mSubscriptionRequests);

        for (RosterPresenceSubscriptionRequestEvent event : tempEventList) {
            if (TextUtils.equals(jid, event.getJid()) || event.getWasHandled()) {
                if (event.getDialog() != null) {
                    event.getDialog().dismiss();
                }

                mSubscriptionRequests.remove(event);
            }
        }

        if (mSubscriptionRequests.size() > 0) {
            RxBus.INSTANCE.publish(mSubscriptionRequests.get(0));
        }
    }

    private ArrayList<NextivaContact> getRosterContacts() {
        ArrayList<NextivaContact> nextivaContacts = new ArrayList<>();
        Roster roster = Roster.getInstanceFor(mConnection);

        for (RosterEntry rosterEntry : roster.getEntries()) {
            nextivaContacts.add(nextivaContactFromRosterEntry(rosterEntry));
        }

        return nextivaContacts;
    }

    private NextivaContact nextivaContactFromRosterEntry(RosterEntry rosterEntry) {
        NextivaContact nextivaContact = new NextivaContact("");
        nextivaContact.setJid(rosterEntry.getJid().toString());
        nextivaContact.setDisplayName(rosterEntry.getName());

        if (rosterEntry.getJid() != null && !TextUtils.isEmpty(rosterEntry.getJid().toString()) &&
                rosterEntry.getJid().toString().endsWith(NextivaXMPPConstants.JID_SUFFIX)) {
            if (rosterEntry.canSeeHisPresence() && rosterEntry.canSeeMyPresence()) {
                nextivaContact.setSubscriptionState(Enums.Contacts.SubscriptionStates.SUBSCRIBED);

            } else if (rosterEntry.isSubscriptionPending()) {
                nextivaContact.setSubscriptionState(Enums.Contacts.SubscriptionStates.PENDING);

            } else {
                nextivaContact.setSubscriptionState(Enums.Contacts.SubscriptionStates.UNSUBSCRIBED);
            }
        }

        return nextivaContact;
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // RosterListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void entriesAdded(final Collection<Jid> addresses) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mConnection == null || !mConnection.isConnected()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, R.string.log_message_xmpp_connection_null_or_disconnected);
            return;
        }

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void entriesUpdated(Collection<Jid> addresses) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mConnection == null || !mConnection.isConnected()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, R.string.log_message_xmpp_connection_null_or_disconnected);
            return;
        }

        RosterEntry rosterEntry;
        boolean isUnsubscribed;
        DbPresence presenceToUpdate;

        for (Jid jid : addresses) {
            rosterEntry = Roster.getInstanceFor(mConnection).getEntry(jid.asBareJid());
            isUnsubscribed = !rosterEntry.canSeeMyPresence() && !rosterEntry.canSeeHisPresence();
            presenceToUpdate = null;

            if (rosterEntry.canSeeHisPresence() && !rosterEntry.canSeeMyPresence() && !rosterEntry.isSubscriptionPending()) {
                presenceToUpdate = PresenceUtil.getUnsubscribedPresence(jid.asBareJid().toString());

            } else if (isUnsubscribed && rosterEntry.isSubscriptionPending()) {
                presenceToUpdate = PresenceUtil.getPendingPresence(jid.asBareJid().toString());

            } else if (!isUnsubscribed) {
                handlePendingSubscriptionRequests(jid.asBareJid().toString());
                mPresenceManager.refreshPresences(new ArrayList<String>() {{
                    add(jid.asBareJid().toString());
                }});
            }

            if (presenceToUpdate != null) {
                mDbManager.updatePresence(presenceToUpdate, mCompositeDisposable);
            }
        }

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void entriesDeleted(final Collection<Jid> addresses) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mConnection == null || !mConnection.isConnected()) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, R.string.log_message_xmpp_connection_null_or_disconnected);
            return;
        }

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void presenceChanged(Presence presence) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
    }
    // --------------------------------------------------------------------------------------------
}
