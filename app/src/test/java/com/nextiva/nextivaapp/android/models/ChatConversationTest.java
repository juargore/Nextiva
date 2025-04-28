/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import android.text.TextUtils;

import com.nextiva.nextivaapp.android.constants.Enums;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

/**
 * Created by adammacdonald on 3/22/18.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class ChatConversationTest {

    private static MockedStatic<TextUtils> textUtils;

    @BeforeClass
    public static void global() {
        textUtils = Mockito.mockStatic(TextUtils.class);
        textUtils.when(() -> TextUtils.isEmpty(any(CharSequence.class))).thenAnswer((Answer<Boolean>) invocation -> {
            CharSequence a = (CharSequence) invocation.getArguments()[0];
            return !(a != null && a.length() > 0);
        });
        textUtils.when(() -> TextUtils.isEmpty(null)).thenAnswer((Answer<Boolean>) invocation -> true);
        textUtils.when(() -> TextUtils.equals(any(CharSequence.class), any(CharSequence.class))).thenAnswer(invocation -> {
            CharSequence a = (CharSequence) invocation.getArguments()[0];
            CharSequence b = (CharSequence) invocation.getArguments()[1];
            if (a == b) {
                return true;
            }
            int length;
            if (a != null && b != null && (length = a.length()) == b.length()) {
                if (a instanceof String && b instanceof String) {
                    return a.equals(b);
                } else {
                    for (int i = 0; i < length; i++) {
                        if (a.charAt(i) != b.charAt(i)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        });
    }

    @AfterClass
    public static void tearDown() {
        textUtils.close();
    }

    @Test
    public void addChatMessage_regularChatMessage_addsToArrayList() {
        ChatMessage chatMessage = new ChatMessage();

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.addChatMessage(chatMessage);

        assertEquals(1, chatConversation.getChatMessagesList().size());
        assertEquals(chatMessage, chatConversation.getChatMessagesList().get(0));
    }

    @Test
    public void addChatMessage_unreadChatMessage_increasesUnreadCount() {
        ChatMessage chatMessage = new ChatMessage(null, null, null, null, null, false, false, null, null, null, null, null, null, null, Enums.Chats.SentStatus.SUCCESSFUL, null);

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);

        assertEquals(0, chatConversation.getUnreadCount());
        chatConversation.addChatMessage(chatMessage);
        assertEquals(1, chatConversation.getUnreadCount());
    }

    @Test
    public void addChatMessage_participantChatMessage_addsToMemberList() {
        ChatMessage chatMessage = new ChatMessage(null, null, null, null, null, false, false, null, null, null, null, "Participant", null, null, Enums.Chats.SentStatus.SUCCESSFUL, null);

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.addChatMessage(chatMessage);

        assertEquals(1, chatConversation.getMembersList().size());
        assertEquals("Participant", chatConversation.getMembersList().get(0));
    }

    @Test
    public void addChatMessage_guestChatMessage_addsToMemberList() {
        ChatMessage chatMessage = new ChatMessage(null, null, null, null, null, false, false, null, null, null, null, null, "Guest First", "Guest Last", Enums.Chats.SentStatus.SUCCESSFUL, null);

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.addChatMessage(chatMessage);

        assertEquals(1, chatConversation.getMembersList().size());
        assertEquals("Guest First Guest Last", chatConversation.getMembersList().get(0));
    }

    @Test
    public void addChatMessage_senderChatMessage_addsToMemberList() {
        ChatMessage chatMessage = new ChatMessage(null, "Sender", null, null, null, true, false, null, null, null, null, null, null, null, Enums.Chats.SentStatus.SUCCESSFUL, null);

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.addChatMessage(chatMessage);

        assertEquals(1, chatConversation.getMembersList().size());
        assertEquals("Sender", chatConversation.getMembersList().get(0));
    }

    @Test
    public void addChatMessage_recipientChatMessage_addsToMemberList() {
        ChatMessage chatMessage = new ChatMessage(null, null, "Recipient", null, null, false, false, null, null, null, null, null, null, null, Enums.Chats.SentStatus.SUCCESSFUL, null);

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.addChatMessage(chatMessage);

        assertEquals(1, chatConversation.getMembersList().size());
        assertEquals("Recipient", chatConversation.getMembersList().get(0));
    }

    @Test
    public void getMembers_nullList_returnsNull() {
        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);

        assertNull(chatConversation.getMembers());
    }

    @Test
    public void getMembers_emptyList_returnsNull() {
        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.setMembersList(new ArrayList<>());

        assertNull(chatConversation.getMembers());
    }

    @Test
    public void getMembers_populatedList_returnsCorrectly() {
        ArrayList<String> membersList = new ArrayList<>();
        membersList.add("Guy");
        membersList.add("Person");
        membersList.add("Woman");

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.setMembersList(membersList);

        assertEquals("Guy; Person; Woman", chatConversation.getMembers());
    }

    @Test
    public void getLastMessageBody_nullList_returnsNull() {
        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);

        assertNull(chatConversation.getLastMessageBody());
    }

    @Test
    public void getLastMessageBody_emptyList_returnsNull() {
        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.setChatMessagesList(new ArrayList<>());

        assertNull(chatConversation.getLastMessageBody());
    }

    @Test
    public void getLastMessageBody_populatedList_returnsCorrectly() {
        ChatMessage chatMessage1 = new ChatMessage(null, null, null, null, "Message Body 1", false, false, null, null, null, null, null, null, null, Enums.Chats.SentStatus.SUCCESSFUL, null);
        ChatMessage chatMessage2 = new ChatMessage(null, null, null, null, "Message Body 2", false, false, null, null, null, null, null, null, null, Enums.Chats.SentStatus.SUCCESSFUL, null);

        ArrayList<ChatMessage> chatMessagesList = new ArrayList<>();
        chatMessagesList.add(chatMessage1);
        chatMessagesList.add(chatMessage2);

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.setChatMessagesList(chatMessagesList);

        assertEquals("Message Body 1", chatConversation.getLastMessageBody());
    }

    @Test
    public void getLastMessageTimestamp_nullList_returnsNegativeOne() {
        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);

        assertEquals(-1, chatConversation.getLastMessageTimestamp());
    }

    @Test
    public void getLastMessageTimestamp_emptyList_returnsNegativeOne() {
        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.setChatMessagesList(new ArrayList<>());

        assertEquals(-1, chatConversation.getLastMessageTimestamp());
    }

    @Test
    public void getLastMessageTimestamp_nullTimestamp_returnsNegativeOne() {
        ChatMessage chatMessage1 = new ChatMessage(null, null, null, null, null, false, false, null, null, null, null, null, null, null, Enums.Chats.SentStatus.SUCCESSFUL, null);

        ArrayList<ChatMessage> chatMessagesList = new ArrayList<>();
        chatMessagesList.add(chatMessage1);

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.setChatMessagesList(chatMessagesList);

        assertEquals(-1, chatConversation.getLastMessageTimestamp());
    }

    @Test
    public void getLastMessageTimestamp_populatedList_returnsCorrectly() {
        ChatMessage chatMessage1 = new ChatMessage(null, null, null, null, null, false, false, 1L, null, null, null, null, null, null, Enums.Chats.SentStatus.SUCCESSFUL, null);
        ChatMessage chatMessage2 = new ChatMessage(null, null, null, null, null, false, false, 2L, null, null, null, null, null, null, Enums.Chats.SentStatus.SUCCESSFUL, null);

        ArrayList<ChatMessage> chatMessagesList = new ArrayList<>();
        chatMessagesList.add(chatMessage1);
        chatMessagesList.add(chatMessage2);

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);
        chatConversation.setChatMessagesList(chatMessagesList);

        assertEquals(1L, chatConversation.getLastMessageTimestamp());
    }

    @Test
    public void isMyRoomGroupChat_incorrectType_returnsFalse() {
        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);

        assertFalse(chatConversation.isRoomGroupChat());
    }

    @Test
    public void isMyRoomGroupChat_nullMucJid_returnsFalse() {
        ArrayList<String> membersList = new ArrayList<>();

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.GROUP_CHAT);
        chatConversation.setMembersList(membersList);

        assertFalse(chatConversation.isRoomGroupChat());
    }

    @Test
    public void isMyRoomGroupChat_nonMatchingMucJid_returnsFalse() {
        ArrayList<String> membersList = new ArrayList<>();
        membersList.add("something@muc");

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.GROUP_CHAT);
        chatConversation.setMembersList(membersList);

        assertFalse(chatConversation.isRoomGroupChat());
    }

    @Test
    public void isMyRoomGroupChat_myRoomMucJid_returnsTrue() {
        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.GROUP_CHAT);
        chatConversation.setChatWith("something@muc-myroom-someotherhash");

        assertTrue(chatConversation.isRoomGroupChat());
    }

    @Test
    public void isUniqueGroupChat_incorrectType_returnsFalse() {
        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.CHAT);

        assertFalse(chatConversation.isUniqueGroupChat());
    }

    @Test
    public void isUniqueGroupChat_nullMucJid_returnsFalse() {
        ArrayList<String> membersList = new ArrayList<>();

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.GROUP_CHAT);
        chatConversation.setMembersList(membersList);

        assertFalse(chatConversation.isUniqueGroupChat());
    }

    @Test
    public void isUniqueGroupChat_nonMatchingMucJid_returnsFalse() {
        ArrayList<String> membersList = new ArrayList<>();
        membersList.add("something@muc");

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.GROUP_CHAT);
        chatConversation.setMembersList(membersList);

        assertFalse(chatConversation.isUniqueGroupChat());
    }

    @Test
    public void isUniqueGroupChat_myRoomMucJid_returnsTrue() {
        ArrayList<String> membersList = new ArrayList<>();
        membersList.add("something@muc-unique-someotherhash");

        ChatConversation chatConversation = new ChatConversation(Enums.Chats.ConversationTypes.GROUP_CHAT);
        chatConversation.setMembersList(membersList);

        assertTrue(chatConversation.isUniqueGroupChat());
    }
}
