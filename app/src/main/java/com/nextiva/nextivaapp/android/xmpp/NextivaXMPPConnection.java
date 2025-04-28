/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp;

import android.app.Application;
import android.text.TextUtils;

import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.net.buses.RxBus;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.RegisterDeviceResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.XmppCannotConnectEvent;
import com.nextiva.nextivaapp.android.net.interceptors.UmsAuthenticator;
import com.nextiva.nextivaapp.android.net.interceptors.UmsHostInterceptor;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.xmpp.iqpackets.providers.PresenceStatusTextIQProvider;
import com.nextiva.nextivaapp.android.xmpp.iqpackets.providers.UniqueIQProvider;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPChatManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPPresenceManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPPubSubManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPRosterManager;
import com.nextiva.nextivaapp.android.xmpp.smack.NextivaSmackDebuggerFactory;
import com.nextiva.nextivaapp.android.xmpp.util.IQBuilderUtil;
import com.nextiva.nextivaapp.android.xmpp.util.NextivaDiscoverInfoProvider;
import com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.dns.HostAddress;
import org.jivesoftware.smack.util.dns.SRVRecord;
import org.jivesoftware.smackx.chatstates.provider.ChatStateExtensionProvider;
import org.jxmpp.stringprep.XmppStringprepException;
import org.minidns.dnsname.DnsName;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ServiceScoped;
import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by joedephillipo on 3/1/18.
 */


@ServiceScoped
public class NextivaXMPPConnection implements ConnectionListener, ReconnectionListener {

    public static final String ACTION_REFRESH_ROSTER = "ACTION_REFRESH_ROSTER";
    public static final String ACTION_CONTACT_UPDATE_REFRESH = "ACTION_CONTACT_UPDATE_REFRESH";
    public static final String ACTION_SET_PRESENCE = "ACTION_SET_PRESENCE";
    public static final String ACTION_UPDATE_CONTACTS = "ACTION_UPDATE_CONTACTS";
    public static final String ACTION_SEND_CHAT_MESSAGE = "ACTION_SEND_CHAT_MESSAGE";
    public static final String ACTION_SEND_CHAT_STATE = "ACTION_UPDATE_CHAT_STATE";
    public static final String ACTION_SUBSCRIBE_TO_CONTACT = "ACTION_SUBSCRIBE_TO_CONTACT";
    public static final String ACTION_UNSUBSCRIBE_FROM_CONTACT = "ACTION_UNSUBSCRIBE_FROM_CONTACT";
    public static final String ACTION_ACCEPT_SUBSCRIPTION_REQUEST = "ACTION_ACCEPT_SUBSCRIPTION_REQUEST";
    public static final String ACTION_DECLINE_SUBSCRIPTION_REQUEST = "ACTION_DECLINE_SUBSCRIPTION_REQUEST";
    public static final String ACTION_HANDLE_PENDING_SUBSCRIPTION_REQUESTS = "ACTION_HANDLE_PENDING_SUBSCRIPTION_REQUESTS";
    public static final String ACTION_SET_PUBSUB_STATUS_TEXT = "ACTION_SET_PUBSUB_STATUS_TEXT";
    public static final String ACTION_JOIN_GROUP_CHAT = "ACTION_JOIN_GROUP_CHAT";
    public static final String ACTION_LEAVE_GROUP_CHAT = "ACTION_LEAVE_GROUP_CHAT";
    public static final String ACTION_START_GROUP_CHAT = "ACTION_START_GROUP_CHAT";
    public static final String ACTION_REQUEST_GROUP_CHAT_PARTICIPANTS = "ACTION_REQUEST_GROUP_CHAT_PARTICIPANTS";
    public static final String ACTION_INVITE_GROUP_CHAT_PARTICIPANTS = "ACTION_INVITE_GROUP_CHAT_PARTICIPANTS";
    public static final String ACTION_DECLINE_GROUP_CHAT_INVITATION = "ACTION_DECLINE_GROUP_CHAT_INVITATION";
    public static final String ACTION_HANDLE_PENDING_GROUP_CHAT_INVITATIONS = "ACTION_HANDLE_PENDING_GROUP_CHAT_INVITATIONS";
    public static final String ACTION_SEND_VCARD_UPDATED_PRESENCE = "ACTION_SEND_VCARD_UPDATED_PRESSENCE";
    public static final String ACTION_DISCONNECT_CONNECTION = "ACTION_DISCONNECT_CONNECTION";
    public static final String ACTION_TEST_CONNECTION = "ACTION_TEST_CONNECTION";

    @Inject
    Application mApplication;
    @Inject
    SessionManager mSessionManager;
    @Inject
    DbManager mDbManager;
    @Inject
    UmsRepository mUmsRepository;
    @Inject
    UmsHostInterceptor mUmsHostInterceptor;
    @Inject
    UmsAuthenticator mUmsAuthenticator;
    @Inject
    XMPPRosterManager mRosterManager;
    @Inject
    XMPPPresenceManager mPresenceManager;
    @Inject
    XMPPChatManager mChatManager;
    @Inject
    XMPPPubSubManager mPubSubManager;
    @Inject
    LogManager mLogManager;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    ConnectionStateManager mConnectionStateManager;
    @Inject
    XMPPConnectionActionManager mXMPPConnectionActionManager;
    @Inject
    SharedPreferencesManager mSharedPreferencesManager;
    @Inject
    UserRepository mUserRepository;
    @Inject
    SchedulerProvider mSchedulerProvider;

    private final XMPPUser mXMPPUser;
    private XMPPTCPConnection mConnection;
    private final CompositeDisposable mCompositeDisposable;
    private boolean badJidExceptionAlreadyThrown = false;


    @Inject
    public NextivaXMPPConnection(Application application, XMPPUser xmppUser, CompositeDisposable compositeDisposable,
                                 SessionManager sessionManager, DbManager dbManager, UmsRepository umsRepository,
                                 UmsHostInterceptor umsHostInterceptor, UmsAuthenticator umsAuthenticator,
                                 XMPPRosterManager rosterManager, XMPPPresenceManager presenceManager,
                                 XMPPChatManager chatManager, XMPPPubSubManager pubSubManager, LogManager logManager,
                                 ConfigManager configManager, ConnectionStateManager connectionStateManager,
                                 XMPPConnectionActionManager xmppConnectionActionManager,
                                 SharedPreferencesManager sharedPreferencesManager, UserRepository userRepository,
                                 SchedulerProvider schedulerProvider) {
        this.mApplication = application;
        this.mXMPPUser = xmppUser;
        this.mCompositeDisposable = compositeDisposable;
        this.mSessionManager = sessionManager;
        this.mDbManager = dbManager;
        this.mUmsRepository = umsRepository;
        this.mUmsHostInterceptor = umsHostInterceptor;
        this.mUmsAuthenticator = umsAuthenticator;
        this.mRosterManager = rosterManager;
        this.mPresenceManager = presenceManager;
        this.mChatManager = chatManager;
        this.mPubSubManager = pubSubManager;
        this.mLogManager = logManager;
        this.mConfigManager = configManager;
        this.mConnectionStateManager = connectionStateManager;
        this.mXMPPConnectionActionManager = xmppConnectionActionManager;
        this.mSharedPreferencesManager = sharedPreferencesManager;
        this.mUserRepository = userRepository;
        this.mSchedulerProvider = schedulerProvider;

        SmackConfiguration.DEBUG = BuildConfig.DEBUG;
        setXMPPConnected(Enums.Xmpp.ConnectionStates.DISCONNECTED);
    }

    public void connect() throws IOException, SmackException, XMPPException {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        setXMPPConnected(Enums.Xmpp.ConnectionStates.CONNECTING);
        setUpProviders();

        SRVRecord record = getHighestPrioritySRVRecord();

        int connectionTimeout = (int) Constants.ONE_MINUTE_IN_MILLIS;

        if (mXMPPUser.getKeepAliveIntervalSec() != 0) {
            connectionTimeout = mXMPPUser.getKeepAliveIntervalSec() * (int) Constants.ONE_SECOND_IN_MILLIS;
        }

        try {
            XMPPTCPConnectionConfiguration.Builder tcpConfBuilder = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(mXMPPUser.getUserName(), mXMPPUser.getPassword())
                    .setResource(mXMPPUser.getResource() + " " + mSharedPreferencesManager.getString(mSharedPreferencesManager.XMPP_RESOURCE_UUID, ""))
                    .setXmppDomain(mXMPPUser.getDomain())
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setConnectTimeout(connectionTimeout)
                    .setSendPresence(true)
                    .setDebuggerFactory(new NextivaSmackDebuggerFactory(mApplication));

            if (record != null && !TextUtils.isEmpty(record.getHost())) {
                tcpConfBuilder.setHost(record.getHost());
            }

            mConnection = new XMPPTCPConnection(tcpConfBuilder.build());
            mConnection.setUseStreamManagement(true);
            mConnection.setUseStreamManagementResumption(true);
            mConnection.addConnectionListener(this);
            mConnection.setParsingExceptionCallback(stanzaData -> mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, stanzaData.getContent().toString()));
            mConnection.connect();

            if (mConnection != null) {
                mConnection.login();
                ReconnectionManager.setEnabledPerDefault(true);
                ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
                reconnectionManager.enableAutomaticReconnection();
            }

            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);

        } catch (XmppStringprepException | InterruptedException exception) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, exception.getClass().getSimpleName());
        }
    }

    public void disconnect() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        setXMPPConnected(Enums.Xmpp.ConnectionStates.DISCONNECTING);

        try {
            new Thread(() -> Completable.fromAction(() -> {
                if (mConnection != null && mConnection.isConnected()) {
                    mConnection.disconnect();
                }
            }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe());
        } catch (Exception e) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            e.printStackTrace();
        }

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    private void setUpProviders() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        ProviderManager.addIQProvider(NextivaXMPPConstants.IQ_CHILD_ELEMENT_PUBSUB,
                                      NextivaXMPPConstants.PUBSUB_IQ_NAMESPACE_PROTOCOLS_PUBSUB,
                                      new PresenceStatusTextIQProvider(mDbManager));
        ProviderManager.addIQProvider(NextivaXMPPConstants.CHAT_UNIQUE_ID_TAGNAME,
                                      NextivaXMPPConstants.CHAT_UNIQUE_ID_NAMESPACE,
                                      new UniqueIQProvider());

        // Replacing this default IQProvider with a custom version. The custom version is
        // identical to the default, except 'assert' line is removed to prevent crashing.
        ProviderManager.removeIQProvider(NextivaXMPPConstants.IQ_CHILD_ELEMENT_QUERY, NextivaXMPPConstants.PUBSUB_FEATURE_JABBER_PROTOCOL_DISCO);
        ProviderManager.addIQProvider(NextivaXMPPConstants.IQ_CHILD_ELEMENT_QUERY, NextivaXMPPConstants.PUBSUB_FEATURE_JABBER_PROTOCOL_DISCO,
                                      new NextivaDiscoverInfoProvider());

        ProviderManager.addExtensionProvider(Enums.Chats.States.ACTIVE, NextivaXMPPConstants.CHAT_STATES_NAMESPACE, new ChatStateExtensionProvider());
        ProviderManager.addExtensionProvider(Enums.Chats.States.COMPOSING, NextivaXMPPConstants.CHAT_STATES_NAMESPACE, new ChatStateExtensionProvider());
        ProviderManager.addExtensionProvider(Enums.Chats.States.PAUSED, NextivaXMPPConstants.CHAT_STATES_NAMESPACE, new ChatStateExtensionProvider());
        ProviderManager.addExtensionProvider(Enums.Chats.States.INACTIVE, NextivaXMPPConstants.CHAT_STATES_NAMESPACE, new ChatStateExtensionProvider());
        ProviderManager.addExtensionProvider(Enums.Chats.States.GONE, NextivaXMPPConstants.CHAT_STATES_NAMESPACE, new ChatStateExtensionProvider());

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    private SRVRecord getHighestPrioritySRVRecord() {
        SRVRecord recordToUse = null;
        try {
            if (mConnectionStateManager.isInternetConnected()) {
                if (mXMPPUser.getDomain() != null) {
                    List<HostAddress> hostAddresses = DNSUtil.resolveXMPPServiceDomain(DnsName.from(mXMPPUser.getDomain()), new ArrayList<>(), ConnectionConfiguration.DnssecMode.disabled);
                    if (!hostAddresses.isEmpty()) {
                        for (HostAddress hostAddress : hostAddresses) {
                            if (hostAddress instanceof SRVRecord) {
                                SRVRecord record = (SRVRecord) hostAddress;

                                if (recordToUse == null || record.getPriority() < recordToUse.getPriority()) {
                                    recordToUse = record;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, exception.getClass().getSimpleName());
        }
        return recordToUse;
    }

    private void setXMPPConnected(@Enums.Xmpp.ConnectionStates.ConnectionState final int connectionState) {
        if (mLogManager != null) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        }

        mConnectionStateManager.postXmppConnectionState(connectionState);

        if (connectionState == Enums.Xmpp.ConnectionStates.CONNECTED && mConnection != null && mConnection.getUser() != null) {
            mPubSubManager.subscribeToPubSub(mXMPPUser.getUserName(), mXMPPUser.getDomain(), mConnection.getUser().toString());

            DbPresence nextivaPresence = new DbPresence();
            nextivaPresence.setState(Enums.Contacts.PresenceStates.AVAILABLE);
            nextivaPresence.setType(Enums.Contacts.PresenceTypes.AVAILABLE);
            nextivaPresence.setPriority(Constants.PRESENCE_MOBILE_PRIORITY);

            DbPresence sessionManagerUserPresence = mSessionManager.getUserPresence();

            if (sessionManagerUserPresence != null && !TextUtils.isEmpty(sessionManagerUserPresence.getStatus())) {
                nextivaPresence.setStatus(sessionManagerUserPresence.getStatus());
            }

            mPresenceManager.setPresence(nextivaPresence);
            mPubSubManager.getPubSubPresenceStatusText(mXMPPUser.getUserName(), mXMPPUser.getDomain(), mConnection.getUser().toString());

        } else if (connectionState != Enums.Xmpp.ConnectionStates.DISCONNECTED) {
            DbPresence nextivaPresence = new DbPresence();
            nextivaPresence.setState(Enums.Contacts.PresenceStates.NONE);
            nextivaPresence.setType(Enums.Contacts.PresenceTypes.UNAVAILABLE);

            DbPresence oldNextivaPresence = mSessionManager.getUserPresence();
            if (oldNextivaPresence != null && !TextUtils.isEmpty(oldNextivaPresence.getStatus())) {
                nextivaPresence.setStatus(oldNextivaPresence.getStatus());
            }

            if (connectionState == Enums.Xmpp.ConnectionStates.CONNECTING) {
                mPresenceManager.setPresence(nextivaPresence);
            }
        }

        if (mLogManager != null) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
        }
    }

    private void pingConnection() {
        try {
            mConnection.sendIqWithResponseCallback(IQBuilderUtil.getPingIQ(), packet -> {
                if (packet instanceof IQ) {
                    RxBus.INSTANCE.publish(new RxEvents.XmppPingEvent(((IQ) packet).getType() == IQ.Type.result));
                } else {
                    RxBus.INSTANCE.publish(new RxEvents.XmppPingEvent(false));
                }
            });
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            RxBus.INSTANCE.publish(new RxEvents.XmppPingEvent(false));
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
        }
    }

    public void handleEvent(XmppActions.BaseXmppConnectionAction event) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mConnection != null && mConnectionStateManager.getXmppConnectionStateLiveData() != null &&
                mConnectionStateManager.getXmppConnectionStateLiveData().getValue() != null &&
                mConnectionStateManager.getXmppConnectionStateLiveData().getValue() == Enums.Xmpp.ConnectionStates.CONNECTED) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_xmpp_connected);

            switch (event.getAction()) {
                case ACTION_REFRESH_ROSTER:
                    mRosterManager.refreshRoster(false);
                    break;
                case ACTION_CONTACT_UPDATE_REFRESH:
                    mRosterManager.refreshRoster(true);
                    break;
                case ACTION_SET_PRESENCE:
                    if (event instanceof XmppActions.XmppSetPresence) {
                        DbPresence nextivaPresence = ((XmppActions.XmppSetPresence) event).getDbPresence();
                        mPresenceManager.setPresence(nextivaPresence);
                    }
                    break;
                case ACTION_UPDATE_CONTACTS:
                    if (event instanceof XmppActions.XmppUpdateRoster) {
                        XmppActions.XmppUpdateRoster action = (XmppActions.XmppUpdateRoster) event;
                        ArrayList<NextivaContact> updatedContacts = action.getNextivaContacts();
                        mRosterManager.updateBroadsoftRosterContacts(updatedContacts);

                        if (action.getContactsToAdd() != null && !action.getContactsToAdd().isEmpty()) {
                            mRosterManager.updateXMPPRosterContacts(action.getContactsToAdd(),
                                                                    action.getActionType());
                        }
                    }
                    break;
                case ACTION_SUBSCRIBE_TO_CONTACT:
                    if (event instanceof XmppActions.XmppSubscribeToContact) {
                        mRosterManager.subscribeToContact(((XmppActions.XmppSubscribeToContact) event).getJid());
                    }
                    break;
                case ACTION_UNSUBSCRIBE_FROM_CONTACT:
                    if (event instanceof XmppActions.XmppUnsubscribeFromContact) {
                        mRosterManager.unsubscribeFromContact(((XmppActions.XmppUnsubscribeFromContact) event).getJid());
                    }
                    break;
                case ACTION_ACCEPT_SUBSCRIPTION_REQUEST:
                    if (event instanceof XmppActions.XmppAcceptSubscriptionRequest) {
                        mRosterManager.acceptSubscriptionRequest(((XmppActions.XmppAcceptSubscriptionRequest) event).getJid());
                    }
                    break;
                case ACTION_DECLINE_SUBSCRIPTION_REQUEST:
                    if (event instanceof XmppActions.XmppDeclineSubscriptionRequest) {
                        mRosterManager.declineSubscriptionRequest(((XmppActions.XmppDeclineSubscriptionRequest) event).getJid());
                    }
                    break;
                case ACTION_HANDLE_PENDING_SUBSCRIPTION_REQUESTS:
                    if (event instanceof XmppActions.XmppHandlePendingSubscriptionRequests) {
                        mRosterManager.declineSubscriptionRequest(null);
                    }
                    break;
                case ACTION_SEND_CHAT_MESSAGE:
                    if (event instanceof XmppActions.XmppSendChatMessage) {
                        XmppActions.XmppSendChatMessage sendChatAction = (XmppActions.XmppSendChatMessage) event;

                        if (!TextUtils.isEmpty(sendChatAction.getChatType()) &&
                                TextUtils.equals(sendChatAction.getChatType(), Enums.Chats.ConversationTypes.GROUP_CHAT)) {
                            mChatManager.sendGroupChatMessage(sendChatAction.getJid(),
                                                              sendChatAction.getChatMessage());
                        }
                    }
                    break;
                case ACTION_SEND_CHAT_STATE:
                    if (event instanceof XmppActions.XmppSendChatState) {
                        XmppActions.XmppSendChatState sendChatStateAction = (XmppActions.XmppSendChatState) event;

                        if (!TextUtils.isEmpty(sendChatStateAction.getJid()) &&
                                !TextUtils.isEmpty(sendChatStateAction.getChatState())) {
                            mChatManager.sendChatState(sendChatStateAction.getJid(),
                                                       sendChatStateAction.getChatState());
                        }
                    }
                    break;
                case ACTION_SET_PUBSUB_STATUS_TEXT:
                    if (event instanceof XmppActions.XmppSetPubSubStatusText) {
                        mPubSubManager.setPubSubPresenceStatusText(mXMPPUser.getUserName(),
                                                                   mXMPPUser.getDomain(),
                                                                   ((XmppActions.XmppSetPubSubStatusText) event).getStatusText());
                    }
                    break;
                case ACTION_START_GROUP_CHAT:
                    if (event instanceof XmppActions.XmppStartGroupChat) {
                        mChatManager.createGroupChat(((XmppActions.XmppStartGroupChat) event).getJidList());
                    }
                    break;
                case ACTION_JOIN_GROUP_CHAT:
                    if (event instanceof XmppActions.XmppJoinGroupChat) {
                        mChatManager.joinGroupChat(((XmppActions.XmppJoinGroupChat) event).getMucJid());
                    }
                    break;
                case ACTION_LEAVE_GROUP_CHAT:
                    if (event instanceof XmppActions.XmppLeaveGroupChat) {
                        mChatManager.leaveGroupChat(((XmppActions.XmppLeaveGroupChat) event).getMucJid());
                    }
                    break;
                case ACTION_REQUEST_GROUP_CHAT_PARTICIPANTS:
                    if (event instanceof XmppActions.XmppRequestChatParticipants) {
                        mChatManager.requestChatParticipants(((XmppActions.XmppRequestChatParticipants) event).getMucJid());
                    }
                    break;
                case ACTION_INVITE_GROUP_CHAT_PARTICIPANTS:
                    if (event instanceof XmppActions.XmppInviteChatParticipants) {
                        mChatManager.inviteChatParticipants(((XmppActions.XmppInviteChatParticipants) event).getJid(),
                                                            ((XmppActions.XmppInviteChatParticipants) event).getJidsToInvite());
                    }
                    break;
                case ACTION_DECLINE_GROUP_CHAT_INVITATION:
                    if (event instanceof XmppActions.XmppDeclineGroupChatInvitation) {
                        mChatManager.declineGroupChatInvitation(((XmppActions.XmppDeclineGroupChatInvitation) event).getMucJid(),
                                                                ((XmppActions.XmppDeclineGroupChatInvitation) event).getInviterJid());
                    }
                    break;
                case ACTION_HANDLE_PENDING_GROUP_CHAT_INVITATIONS:
                    mChatManager.handlePendingInvitations();
                    break;
                case ACTION_SEND_VCARD_UPDATED_PRESENCE:
                    mPresenceManager.sendVCardUpdatePresence();
                    break;
                case ACTION_DISCONNECT_CONNECTION:
                    disconnect();
                    break;
                case ACTION_TEST_CONNECTION:
                    pingConnection();
                    break;
            }
        } else {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_xmpp_disconnected);

            // If we have a mobile config then the user is logged in and we should reboot the xmpp connection.  If there
            // is no mobile config that means we are logged out or logging out and we shouldn't try to restart
            // the connection.
            if (mConfigManager.getMobileConfig() != null && mConnectionStateManager.isInternetConnected()) {
                mXMPPConnectionActionManager.startConnection();
            }
        }

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    // --------------------------------------------------------------------------------------------
    // ConnectionListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void connected(XMPPConnection connection) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        mUmsHostInterceptor.setHost(connection.getHost());
        mSessionManager.setUmsHost(connection.getHost());
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        mChatManager.init(mConnection, mSessionManager, mDbManager);
        mPresenceManager.init(mConnection, mCompositeDisposable);
        mRosterManager.init(mConnection, mCompositeDisposable);
        mPubSubManager.init(mConnection);

        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null && !TextUtils.isEmpty(mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader())) {
            mUmsAuthenticator.setAuthorizationHeader(mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader());
        }

        mCompositeDisposable.add(
                mUmsRepository.registerDevice()
                        .subscribe(success -> {
                            mUmsRepository.setDeviceFinishedRegistering();

                            if (mUmsRepository.isClientSetup()) {
                                mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_ums_client_setup);

                                mCompositeDisposable.addAll(
                                        mUmsRepository.getSuperPresence(mCompositeDisposable)
                                                .subscribe(),
                                        mUmsRepository.getChatConversations(0)
                                                .subscribe());
                                mUserRepository.removeExpiredPushNotificationRegistrations(mCompositeDisposable);
                                mRosterManager.refreshRoster(false);
                            }

                            RxBus.INSTANCE.publish(new RegisterDeviceResponseEvent(true));
                        }));

        initRxListeners();
        setXMPPConnected(Enums.Xmpp.ConnectionStates.CONNECTED);

        try {
            mConnection.sendStanza(IQBuilderUtil.getBindResourceSetIQ(mConnection.getUser().getResourceOrEmpty().toString()));
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
        }

        if (mSessionManager.getUserDetails() != null && !TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId())) {
            mUmsRepository.getVCard(mSessionManager.getUserDetails().getImpId(), mCompositeDisposable).subscribe();
        }

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void connectionClosed() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        setXMPPConnected(Enums.Xmpp.ConnectionStates.DISCONNECTED);
        mConnection = null;

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (e instanceof XmppStringprepException || e instanceof XmlPullParserException) {
            if (!badJidExceptionAlreadyThrown) {
                RxBus.INSTANCE.publish(new XmppCannotConnectEvent(e));
                badJidExceptionAlreadyThrown = true;
            }
        }

        setXMPPConnected(Enums.Xmpp.ConnectionStates.DISCONNECTED);

        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // ReconnectionListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void reconnectingIn(int seconds) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
    }

    @Override
    public void reconnectionFailed(Exception e) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
    }
    // --------------------------------------------------------------------------------------------

    private void initRxListeners() {
        mCompositeDisposable.add(
                RxBus.INSTANCE.listen(XmppActions.BaseXmppConnectionAction.class)
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(this::handleEvent));
    }
}