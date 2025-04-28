/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.managers.interfaces;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.ArrayList;

/**
 * Created by joedephillipo on 3/30/18.
 */

public interface XMPPChatManager {

    void init(XMPPTCPConnection connection, SessionManager sessionManager, DbManager dbManager);

    void sendGroupChatMessage(String jid, String chatMessage);

    void sendChatState(String toJid, @Enums.Chats.States.State final String chatState);

    void joinGroupChat(String mucJid);

    void leaveGroupChat(String mucJid);

    void handlePendingInvitations();

    void declineGroupChatInvitation(String mucJid, String inviterJid);

    void createGroupChat(ArrayList<String> jidList);

    void requestChatParticipants(String jid);

    void inviteChatParticipants(String jid, ArrayList<String> jidList);
}
