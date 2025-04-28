/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.managers.interfaces;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.models.NextivaContact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joedephillipo on 3/4/18.
 */

public interface XMPPConnectionActionManager {

    void startConnection();

    void stopConnection();

    void disconnectConnection();

    void refreshRoster(boolean forceRefresh);

    void subscribeToContact(String jid);

    void unsubscribeFromContact(String jid);

    void acceptSubscriptionRequest(String jid);

    void declineSubscriptionRequest(String jid);

    void handlePendingSubscriptionRequests();

    void contactUpdateRefresh();

    void setPresence(DbPresence presence);

    void updateRoster(ArrayList<NextivaContact> newContacts, List<NextivaContact> nextivaContacts, int action);

    void sendChatMessage(String jid, String message, @Enums.Chats.ConversationTypes.Type String chatType);

    void sendChatState(String jid, @Enums.Chats.States.State final String chatState);

    void setPubSubStatusText(String statusText);

    void startGroupChat(ArrayList<String> jidList);

    void joinGroupChat(String mucJid);

    void leaveGroupChat(String mucJid);

    void handlePendingGroupChatInvitations();

    void requestChatParticipants(String jid);

    void declineGroupChatInvitation(String mucJid, String inviterJid);

    void inviteChatParticipants(String jid, ArrayList<String> jidsToInvite);

    void sendVCardUpdatedPresence();

    void testConnection();
}
