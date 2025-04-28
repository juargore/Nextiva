/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.managers;

import android.text.TextUtils;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.models.ChatMessage;
import com.nextiva.nextivaapp.android.net.buses.RxBus;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ChatConversationInvitationReceivedEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ChatConversationJoinedEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ChatConversationLeftEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ChatConversationParticipantJoinedEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ChatConversationParticipantLeftEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ChatConversationParticipantsEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ChatConversationParticipantsInvitedEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ChatConversationStartedEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.XmppErrorEvent;
import com.nextiva.nextivaapp.android.util.NumberUtil;
import com.nextiva.nextivaapp.android.xmpp.iqpackets.UniqueResultIQ;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPChatManager;
import com.nextiva.nextivaapp.android.xmpp.util.IQBuilderUtil;
import com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants;
import com.nextiva.nextivaapp.android.xmpp.util.XmppDebuggingUtil;

import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by joedephillipo on 3/30/18.
 */

public class NextivaXMPPChatManager implements
        XMPPChatManager,
        ParticipantStatusListener {

    private SessionManager mSessionManager;
    private DbManager mDbManager;
    private XMPPTCPConnection mConnection;

    private final LogManager mLogManager;

    private ArrayList<ChatConversationInvitationReceivedEvent> mGroupChatInvitations = new ArrayList<>();

    private final PresenceListener mParticipationPresenceListener = presence -> {
        if (presence.getType() == Presence.Type.unavailable) {
            RxBus.INSTANCE.publish(new ChatConversationParticipantLeftEvent(presence.getFrom().getResourceOrEmpty().toString(), presence.getFrom().asBareJid().toString()));
        }
    };

    @Inject
    public NextivaXMPPChatManager(LogManager logManager) {
        mLogManager = logManager;
    }

    private void initChatStateChangedListener() {
//        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

//        mConnection.addAsyncStanzaListener(
//                packet -> {
//                    if (packet.hasExtension(Enums.Chats.States.ACTIVE, NextivaXMPPConstants.CHAT_STATES_NAMESPACE)) {
//                        getBus().post(new ChatStateChangedEvent(true, new NextivaChatState(packet.getFrom().asEntityBareJidIfPossible().toString(),
//                                                                                           Enums.Chats.States.ACTIVE, "")));
//
//                    } else if (packet.hasExtension(Enums.Chats.States.COMPOSING, NextivaXMPPConstants.CHAT_STATES_NAMESPACE)) {
//                        getBus().post(new ChatStateChangedEvent(true, new NextivaChatState(packet.getFrom().asEntityBareJidIfPossible().toString(),
//                                                                                           Enums.Chats.States.COMPOSING,
//                                                                                           "")));
//
//                    } else if (packet.hasExtension(Enums.Chats.States.PAUSED, NextivaXMPPConstants.CHAT_STATES_NAMESPACE)) {
//                        getBus().post(new ChatStateChangedEvent(true, new NextivaChatState(packet.getFrom().asEntityBareJidIfPossible().toString(),
//                                                                                           Enums.Chats.States.PAUSED,
//                                                                                           "")));
//
//                    } else if (packet.hasExtension(Enums.Chats.States.INACTIVE, NextivaXMPPConstants.CHAT_STATES_NAMESPACE)) {
//                        getBus().post(new ChatStateChangedEvent(true, new NextivaChatState(packet.getFrom().asEntityBareJidIfPossible().toString(),
//                                                                                           Enums.Chats.States.INACTIVE,
//                                                                                           "")));
//
//                    } else if (packet.hasExtension(Enums.Chats.States.GONE, NextivaXMPPConstants.CHAT_STATES_NAMESPACE)) {
//                        getBus().post(new ChatStateChangedEvent(true, new NextivaChatState(packet.getFrom().asEntityBareJidIfPossible().toString(),
//                                                                                           Enums.Chats.States.GONE,
//                                                                                           "")));
//                    }
//
//                    mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
//
//                },
//                stanza -> stanza.getFrom() != null && stanza.hasExtension(NextivaXMPPConstants.CHAT_STATES_NAMESPACE));
    }

    @Override
    public void createGroupChat(ArrayList<String> jidList) {
        try {
            IQ getUniqueRoomIdIQ = new IQ(NextivaXMPPConstants.CHAT_UNIQUE_ID_TAGNAME) {
                @Override
                protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
                    xml.attribute(NextivaXMPPConstants.IQ_NAMESPACE, NextivaXMPPConstants.CHAT_UNIQUE_ID_NAMESPACE);
                    xml.rightAngleBracket();
                    return xml;
                }
            };

            getUniqueRoomIdIQ.setType(IQ.Type.get);
            getUniqueRoomIdIQ.setTo(JidCreate.bareFrom(NextivaXMPPConstants.CHAT_MUC_PREFIX + mConnection.getUser().asBareJid().getDomain().toString()));

            mConnection.sendStanza(getUniqueRoomIdIQ);
            mConnection.sendIqWithResponseCallback(getUniqueRoomIdIQ, packet -> {
                if (packet instanceof UniqueResultIQ) {
                    try {
                        String uniqueId = ((UniqueResultIQ) packet).getUniqueId();
                        MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(mConnection);
                        MultiUserChat muc = mucManager.getMultiUserChat(JidCreate.entityBareFrom(uniqueId +
                                NextivaXMPPConstants.CHAT_MUC_UNIQUE_SEPARATOR +
                                NumberUtil.asciiToHexString(mConnection.getUser().asBareJid().toString()) +
                                NextivaXMPPConstants.CHAT_MUC_RESOURCE_PREFIX +
                                mConnection.getUser().asBareJid().getDomain().toString()));
                        Resourcepart nickname = Resourcepart.from(mConnection.getUser().asBareJid().toString());

                        muc.create(nickname);

                        Form form = muc.getConfigurationForm();
                        Form submitForm = form.createAnswerForm();

                        for (FormField formField : form.getFields()) {
                            submitForm.setDefaultAnswer(formField.getVariable());
                        }

                        muc.sendConfigurationForm(submitForm);
                        muc.addParticipantListener(mParticipationPresenceListener);
                        muc.addParticipantStatusListener(this);

                        for (String jid : jidList) {
                            muc.invite(JidCreate.entityBareFrom(jid), "");
                        }

                        RxBus.INSTANCE.publish(new ChatConversationStartedEvent(true, muc.getRoom().asBareJid().toString()));


                    } catch (SmackException.NoResponseException |
                            MultiUserChatException.MucAlreadyJoinedException |
                            MultiUserChatException.MissingMucCreationAcknowledgeException |
                            MultiUserChatException.NotAMucServiceException |
                            XMPPException.XMPPErrorException |
                            XmppStringprepException e) {
                        RxBus.INSTANCE.publish(new ChatConversationStartedEvent(false, null));
                        mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
                    }

                } else {
                    RxBus.INSTANCE.publish(new ChatConversationStartedEvent(false, null));
                }
            });

        } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
            RxBus.INSTANCE.publish(new ChatConversationStartedEvent(false, null));
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
        }
    }

    @Override
    public void joinGroupChat(String mucJid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            mConnection.sendIqWithResponseCallback(
                    IQBuilderUtil.getMucDiscoFeatures(mucJid),
                    packet -> {
                        try {
                            MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(mConnection);
                            MultiUserChat muc = mucManager.getMultiUserChat(JidCreate.entityBareFrom(mucJid));

                            if (muc != null) {
                                if (!muc.isJoined()) {
                                    muc.addParticipantStatusListener(this);
                                    muc.addParticipantListener(mParticipationPresenceListener);
                                    muc.join(Resourcepart.from(mConnection.getUser().asBareJid().toString()));
                                }

                                RxBus.INSTANCE.publish(new ChatConversationJoinedEvent(true, mucJid, false));

                            } else {
                                RxBus.INSTANCE.publish(new ChatConversationJoinedEvent(false, mucJid, false));
                            }

                            handlePendingInvitations(mucJid);

                        } catch (XmppStringprepException | SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException | InterruptedException | MultiUserChatException.NotAMucServiceException e) {
                            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
                            RxBus.INSTANCE.publish(new ChatConversationJoinedEvent(false, mucJid, false));
                        }
                    },
                    exception -> {
                        // We received IQ with type error/item-not-found
                        mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, exception.getClass().getSimpleName());
                        RxBus.INSTANCE.publish(new ChatConversationJoinedEvent(true, mucJid, true));
                        handlePendingInvitations(mucJid);
                    });

        } catch (NullPointerException | SmackException.NotConnectedException | InterruptedException e) {
            //NullPointerException if Room is closed

            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            RxBus.INSTANCE.publish(new ChatConversationJoinedEvent(false, mucJid, false));
        }
    }

    @Override
    public void leaveGroupChat(String mucJid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(mConnection);
            MultiUserChat muc = mucManager.getMultiUserChat(JidCreate.entityBareFrom(mucJid));

            if (muc != null && muc.isJoined()) {
                muc.leave();
                muc.removeParticipantListener(mParticipationPresenceListener);
                muc.removeParticipantStatusListener(this);
            }

            RxBus.INSTANCE.publish(new ChatConversationLeftEvent(true, mucJid));

        } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            RxBus.INSTANCE.publish(new ChatConversationLeftEvent(false, mucJid));
        }
    }

    @Override
    public void requestChatParticipants(String jid) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(mConnection);
            MultiUserChat muc = mucManager.getMultiUserChat(JidCreate.entityBareFrom(jid));
            ArrayList<String> participantsList = new ArrayList<>();

            if (muc != null && muc.isJoined() && mSessionManager.getUserDetails() != null) {
                for (EntityFullJid bareJid : muc.getOccupants()) {
                    if (mSessionManager.getUserDetails() != null &&
                            !TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId()) &&
                            !TextUtils.equals(bareJid.getResourceOrEmpty().toString().toLowerCase(), mSessionManager.getUserDetails().getImpId().toLowerCase())) {
                        participantsList.add(bareJid.getResourceOrEmpty().toString());
                    }
                }
            }

            if (participantsList.size() > 0) {
                RxBus.INSTANCE.publish(new ChatConversationParticipantsEvent(true, jid, participantsList));

            } else {
                RxBus.INSTANCE.publish(new ChatConversationParticipantsEvent(true, jid, null));
            }

        } catch (XmppStringprepException e) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            RxBus.INSTANCE.publish(new ChatConversationParticipantsEvent(true, jid, new ArrayList<>()));
        }
    }

    @Override
    public void inviteChatParticipants(String jid, ArrayList<String> jidList) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(mConnection);
            MultiUserChat muc = mucManager.getMultiUserChat(JidCreate.entityBareFrom(jid));

            if (muc != null && muc.isJoined() && mSessionManager.getUserDetails() != null) {
                for (String jidToInvite : jidList) {
                    muc.invite(JidCreate.entityBareFrom(jidToInvite), "");
                }
            }

            RxBus.INSTANCE.publish(new ChatConversationParticipantsInvitedEvent(true));

        } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
            RxBus.INSTANCE.publish(new ChatConversationParticipantsInvitedEvent(false));
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
        }
    }

    // --------------------------------------------------------------------------------------------
    // XMPPChatManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void init(XMPPTCPConnection connection, SessionManager sessionManager, DbManager dbManager) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        mConnection = connection;
        mSessionManager = sessionManager;
        mDbManager = dbManager;
        mGroupChatInvitations = new ArrayList<>();
        initChatStateChangedListener();

        // Desktop App sends invitations in a strange fashion.  This is how we can handle getting them.
        mConnection.addAsyncStanzaListener(
                (packet) -> {
                    try {
                        Message message = (Message) packet;
                        GroupChatInvitation invitation = (GroupChatInvitation) message.getExtension(NextivaXMPPConstants.CHAT_MUC_INVITATION_NAMESPACE);
                        MultiUserChat muc = MultiUserChatManager.getInstanceFor(mConnection).getMultiUserChat(JidCreate.entityBareFrom(invitation.getRoomAddress()));

                        if (muc != null && !muc.isJoined()) {
                            ChatConversationInvitationReceivedEvent event = new ChatConversationInvitationReceivedEvent(muc.getRoom().asBareJid().toString(), false, message.getFrom().asBareJid().toString());
                            boolean alreadyInvited = false;

                            for (ChatConversationInvitationReceivedEvent receivedEvent : mGroupChatInvitations) {
                                if (TextUtils.equals(receivedEvent.getRoomId(), event.getRoomId())) {
                                    alreadyInvited = true;
                                }
                            }

                            if (!alreadyInvited) {
                                if (mGroupChatInvitations.size() == 0) {
                                    RxBus.INSTANCE.publish(event);
                                }

                                mGroupChatInvitations.add(event);
                            }
                        }
                    } catch (XmppStringprepException e) {
                        mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
                    }
                }, (stanza) -> {
                    // Filter the stanza to make sure it's what we're looking for
                    if (stanza instanceof Message) {
                        Message message = (Message) stanza;

                        if (message.getType() == Message.Type.normal && message.getExtension(NextivaXMPPConstants.CHAT_MUC_INVITATION_NAMESPACE) != null && message.getExtension(NextivaXMPPConstants.CHAT_MUC_INVITATION_NAMESPACE) instanceof GroupChatInvitation) {
                            GroupChatInvitation invitation = (GroupChatInvitation) message.getExtension(NextivaXMPPConstants.CHAT_MUC_INVITATION_NAMESPACE);

                            return !TextUtils.isEmpty(invitation.getRoomAddress());
                        } else if (message.getType() == Message.Type.groupchat) {
                            saveGroupChatHistoryMessage(message);
                        }
                    }

                    return false;
                });

        MultiUserChatManager.getInstanceFor(mConnection).addInvitationListener(
                (xmppConnection, muc, inviter, reason, password, message, invitation) -> {
                    if (muc != null && !muc.isJoined()) {
                        ChatConversationInvitationReceivedEvent event = new ChatConversationInvitationReceivedEvent(muc.getRoom().asBareJid().toString(), false, inviter.asBareJid().toString());
                        boolean alreadyInvited = false;

                        for (ChatConversationInvitationReceivedEvent receivedEvent : mGroupChatInvitations) {
                            if (TextUtils.equals(receivedEvent.getRoomId(), event.getRoomId())) {
                                alreadyInvited = true;
                            }
                        }

                        if (!alreadyInvited) {
                            if (mGroupChatInvitations.size() == 0) {
                                RxBus.INSTANCE.publish(event);
                            }

                            mGroupChatInvitations.add(event);
                        }
                    }
                });
    }

    @Override
    public void declineGroupChatInvitation(String mucJid, String inviterJid) {
        try {
            MultiUserChatManager.getInstanceFor(mConnection).decline(JidCreate.entityBareFrom(mucJid), JidCreate.entityBareFrom(inviterJid), "");
            handlePendingInvitations(mucJid);

        } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
        }
    }

    @Override
    public void sendGroupChatMessage(String jid, String chatMessage) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        EntityBareJid bareJid;

        try {
            bareJid = JidCreate.entityBareFrom(jid);

            try {
                Message message = new Message();
                message.setTo(bareJid);
                message.setType(Message.Type.groupchat);
                message.setBody(chatMessage);

                message.addExtension(new ExtensionElement() {
                    @Override
                    public String getNamespace() {
                        return null;
                    }

                    @Override
                    public String getElementName() {
                        return null;
                    }

                    @Override
                    public CharSequence toXML(String namespace) {
                        return NextivaXMPPConstants.CHAT_STATE_ACTIVE_EXTENSION_ELEMENT;
                    }
                });

                MultiUserChatManager.getInstanceFor(mConnection).getMultiUserChat(JidCreate.entityBareFrom(jid)).sendMessage(message);

                ChatMessage messageToSave = new ChatMessage();
                if (mSessionManager.getUserDetails() != null && !TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId())) {
                    messageToSave.setFrom(mSessionManager.getUserDetails().getImpId());
                    messageToSave.setTo(jid);
                    messageToSave.setTimestamp(System.currentTimeMillis());
                    messageToSave.setChatWith(jid);
                    messageToSave.setIsSender(true);
                    messageToSave.setType(Enums.Chats.ConversationTypes.GROUP_CHAT);
                    messageToSave.setBody(chatMessage);
                    messageToSave.setIsRead(true);

                    mDbManager.saveChatMessage(messageToSave);
                    mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
                }

            } catch (SmackException.NotConnectedException | InterruptedException exception) {
                mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, exception.getClass().getSimpleName());

                RxBus.INSTANCE.publish(new XmppErrorEvent(exception));
                XmppDebuggingUtil.displayDebugLogMessage(exception, Thread.currentThread().getStackTrace()[2]);
            }

        } catch (XmppStringprepException e) {
            RxBus.INSTANCE.publish(new XmppErrorEvent(e));
            XmppDebuggingUtil.displayDebugLogMessage(e, Thread.currentThread().getStackTrace()[2]);
        }
    }

    @Override
    public void sendChatState(String toJid, @Enums.Chats.States.State final String chatState) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        try {
            if (mConnection != null) {
                ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
                Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(toJid));

                Message message = new Message();
                message.setType(Message.Type.normal);

                message.addExtension(new ExtensionElement() {
                    @Override
                    public String getNamespace() {
                        return null;
                    }

                    @Override
                    public String getElementName() {
                        return null;
                    }

                    @Override
                    public CharSequence toXML(String namespace) {
                        switch (chatState) {
                            case Enums.Chats.States.ACTIVE:
                                return NextivaXMPPConstants.CHAT_STATE_ACTIVE_EXTENSION_ELEMENT;
                            case Enums.Chats.States.COMPOSING:
                                return NextivaXMPPConstants.CHAT_STATE_COMPOSING_EXTENSION_ELEMENT;
                            case Enums.Chats.States.GONE:
                                return NextivaXMPPConstants.CHAT_STATE_GONE_EXTENSION_ELEMENT;
                        }
                        return null;
                    }
                });

                chat.send(message);

                mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
            }

        } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException exception) {
            mLogManager.xmppLogToFile(Enums.Logging.STATE_ERROR, exception.getClass().getSimpleName());

            XmppDebuggingUtil.displayDebugLogMessage(exception, Thread.currentThread().getStackTrace()[2]);
        }
    }

    private void saveGroupChatHistoryMessage(Message message) {
        mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(mConnection);

        if (mSessionManager.getUserDetails() != null &&
                !TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId()) &&
                !TextUtils.isEmpty(message.getBody()) &&
                message.getExtension("timestamp", "jabber:client") != null &&
                message.getExtension("delay", "urn:xmpp:delay") != null &&
                !message.getBody().contains(NextivaXMPPConstants.CHAT_MUC_USS_SHARE) &&
                mucManager.getMultiUserChat(message.getFrom().asEntityBareJidIfPossible()).isJoined() &&
                !TextUtils.isEmpty(message.getFrom().getResourceOrEmpty().toString())) {

            boolean isSender = TextUtils.equals(message.getFrom().getResourceOrEmpty().toString().toLowerCase(),
                    mSessionManager.getUserDetails().getImpId().toLowerCase());

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setBody(message.getBody());
            chatMessage.setFrom(message.getFrom().getResourceOrEmpty().toString());
            chatMessage.setChatWith(message.getFrom().asBareJid().toString());
            chatMessage.setTo(isSender ?
                    message.getFrom().asBareJid().toString() :
                    message.getFrom().getResourceOrEmpty().toString());
            chatMessage.setIsSender(isSender);
            chatMessage.setIsRead(false);
            chatMessage.setType(Enums.Chats.ConversationTypes.GROUP_CHAT);
            chatMessage.setThreadId(message.getThread());
            chatMessage.setLanguage(message.getLanguage());
            chatMessage.setTimestamp(System.currentTimeMillis());
            chatMessage.setUIName(mDbManager.getUINameFromJid(chatMessage.getFrom()));

            if (message.getExtension("timestamp", "jabber:client") instanceof StandardExtensionElement) {
                chatMessage.setTimestamp(System.currentTimeMillis());
            }

            mDbManager.saveChatMessage(chatMessage);
            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
        }
    }

    // --------------------------------------------------------------------------------------------

    @Override
    public void handlePendingInvitations() {
        ArrayList<ChatConversationInvitationReceivedEvent> tempEventList = new ArrayList<>(mGroupChatInvitations);

        for (ChatConversationInvitationReceivedEvent event : tempEventList) {
            if (event.getWasHandled()) {
                mGroupChatInvitations.remove(event);
            }
        }

        if (mGroupChatInvitations.size() > 0) {
            RxBus.INSTANCE.publish(mGroupChatInvitations.get(0));
        }
    }

    private void handlePendingInvitations(String mucJid) {
        ArrayList<ChatConversationInvitationReceivedEvent> tempEventList = new ArrayList<>(mGroupChatInvitations);

        for (ChatConversationInvitationReceivedEvent event : tempEventList) {
            if (TextUtils.equals(mucJid, event.getRoomId()) || event.getWasHandled()) {
                mGroupChatInvitations.remove(event);
            }
        }

        if (mGroupChatInvitations.size() > 0) {
            RxBus.INSTANCE.publish(mGroupChatInvitations.get(0));
        }
    }

    // --------------------------------------------------------------------------------------------
    // Participant Status Listener
    // --------------------------------------------------------------------------------------------

    @Override
    public void joined(EntityFullJid participant) {
        RxBus.INSTANCE.publish(new ChatConversationParticipantJoinedEvent(participant.getResourceOrEmpty().toString(), participant.asBareJid().toString()));
    }

    @Override
    public void left(EntityFullJid participant) {

    }

    @Override
    public void kicked(EntityFullJid participant, Jid actor, String reason) {

    }

    @Override
    public void voiceGranted(EntityFullJid participant) {

    }

    @Override
    public void voiceRevoked(EntityFullJid participant) {

    }

    @Override
    public void banned(EntityFullJid participant, Jid actor, String reason) {

    }

    @Override
    public void membershipGranted(EntityFullJid participant) {

    }

    @Override
    public void membershipRevoked(EntityFullJid participant) {

    }

    @Override
    public void moderatorGranted(EntityFullJid participant) {

    }

    @Override
    public void moderatorRevoked(EntityFullJid participant) {

    }

    @Override
    public void ownershipGranted(EntityFullJid participant) {

    }

    @Override
    public void ownershipRevoked(EntityFullJid participant) {

    }

    @Override
    public void adminGranted(EntityFullJid participant) {

    }

    @Override
    public void adminRevoked(EntityFullJid participant) {

    }

    @Override
    public void nicknameChanged(EntityFullJid participant, Resourcepart newNickname) {

    }


    // --------------------------------------------------------------------------------------------

}
