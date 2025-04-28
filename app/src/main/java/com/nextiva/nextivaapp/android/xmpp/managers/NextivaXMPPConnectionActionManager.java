/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.managers;

import android.app.Application;
import android.content.Intent;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.net.buses.RxBus;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;
import com.nextiva.nextivaapp.android.xmpp.NextivaXMPPConnectionService;
import com.nextiva.nextivaapp.android.xmpp.XmppActions;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by joedephillipo on 3/1/18.
 */

public class NextivaXMPPConnectionActionManager implements XMPPConnectionActionManager {

    final private Intent mStartIntent;

    private final Application mApplication;
    private final DbManager mDbManager;
    private final LogManager mLogManager;
    private final SessionManager mSessionManager;

    @Inject
    public NextivaXMPPConnectionActionManager(
            Application application,
            DbManager dbManager,
            LogManager logManager,
            SessionManager sessionManager) {

        mApplication = application;
        mDbManager = dbManager;
        mLogManager = logManager;
        mSessionManager = sessionManager;

        mStartIntent = NextivaXMPPConnectionService.newIntent(mApplication);
    }

    // --------------------------------------------------------------------------------------------
    // XMPPConnectionActionManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void startConnection() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        if (ApplicationUtil.isAppInForeground(mApplication) && !mSessionManager.isNextivaConnectEnabled()) {
            mApplication.startService(mStartIntent);
        }
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void stopConnection() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        mApplication.stopService(mStartIntent);
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void disconnectConnection() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppDisconnectConnection());
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void refreshRoster(boolean forceRefresh) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppRefreshRoster(forceRefresh));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void contactUpdateRefresh() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppContactUpdateRefresh());
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void setPresence(DbPresence presence) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppSetPresence(presence));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void updateRoster(ArrayList<NextivaContact> contactsToAdd, List<NextivaContact> nextivaContacts, int action) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppUpdateRoster(contactsToAdd, new ArrayList<>(nextivaContacts), action));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void subscribeToContact(String jid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppSubscribeToContact(jid));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void unsubscribeFromContact(String jid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppUnsubscribeFromContact(jid));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void acceptSubscriptionRequest(String jid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppAcceptSubscriptionRequest(jid));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void declineSubscriptionRequest(String jid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppDeclineSubscriptionRequest(jid));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void handlePendingSubscriptionRequests() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppHandlePendingSubscriptionRequests());
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void sendChatMessage(String jid, String chatMessage, @Enums.Chats.ConversationTypes.Type String chatType) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppSendChatMessage(jid, chatMessage, chatType));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void sendChatState(String jid, @Enums.Chats.States.State final String chatState) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppSendChatState(jid, chatState));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void setPubSubStatusText(String statusText) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppSetPubSubStatusText(statusText));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void startGroupChat(ArrayList<String> jidList) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppStartGroupChat(jidList));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void joinGroupChat(String mucJid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppJoinGroupChat(mucJid));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void leaveGroupChat(String mucJid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppLeaveGroupChat(mucJid));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void requestChatParticipants(String mucJid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppRequestChatParticipants(mucJid));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void inviteChatParticipants(String jid, ArrayList<String> jidsToInvite) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppInviteChatParticipants(jid, jidsToInvite));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void declineGroupChatInvitation(String mucJid, String inviterJid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppDeclineGroupChatInvitation(mucJid, inviterJid));
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void handlePendingGroupChatInvitations() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppHandlePendingGroupChatInvitations());
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void sendVCardUpdatedPresence() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppSendVCardUpdatedPresence());
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public void testConnection() {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        RxBus.INSTANCE.publish(new XmppActions.XmppTestConnection());
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }
    // --------------------------------------------------------------------------------------------
}
