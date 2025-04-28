/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by adammacdonald on 3/7/18.
 */

public class ChatConversation implements Serializable {

    @Enums.Chats.ConversationTypes.Type
    private String mConversationType;
    private String mChatWith;
    private ArrayList<String> mMembersList;
    private ArrayList<ChatMessage> mChatMessagesList;
    private byte[] mAvatar;
    private int mUnreadCount = 0;

    public ChatConversation(@Enums.Chats.ConversationTypes.Type String conversationType) {
        mConversationType = conversationType;
    }

    @Enums.Chats.ConversationTypes.Type
    public String getConversationType() {
        return mConversationType;
    }

    public void setConversationType(@Enums.Chats.ConversationTypes.Type String conversationType) {
        mConversationType = conversationType;
    }

    public ArrayList<ChatMessage> getChatMessagesList() {
        return mChatMessagesList;
    }

    public void setChatMessagesList(ArrayList<ChatMessage> chatMessagesList) {
        mChatMessagesList = chatMessagesList;
        mUnreadCount = 0;

        for (ChatMessage chatMessage : chatMessagesList) {
            if (!chatMessage.isRead()) {
                mUnreadCount++;
            }
        }
    }

    public void addChatMessage(@NonNull ChatMessage chatMessage) {
        if (mChatMessagesList == null) {
            mChatMessagesList = new ArrayList<>();
        }

        if (!mChatMessagesList.contains(chatMessage)) {
            mChatMessagesList.add(chatMessage);
        }

        if (!chatMessage.isRead()) {
            mUnreadCount++;
        }

        if (!TextUtils.isEmpty(chatMessage.getParticipant())) {
            addMember(chatMessage.getParticipant());

        } else if (!TextUtils.isEmpty(chatMessage.getGuestFullName())) {
            addMember(chatMessage.getGuestFullName());

        } else if (chatMessage.isSender()) {
            addMember(chatMessage.getTo());

        } else {
            addMember(chatMessage.getFrom());
        }
    }

    public ArrayList<String> getMembersList() {
        return mMembersList;
    }

    public void setMembersList(ArrayList<String> membersList) {
        mMembersList = membersList;
    }

    private void addMember(String member) {
        if (mMembersList == null) {
            mMembersList = new ArrayList<>();
        }

        if (!mMembersList.contains(member)) {
            mMembersList.add(member);
        }
    }

    public int getUnreadCount() {
        return mUnreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        mUnreadCount = unreadCount;
    }

    @Nullable
    public String getMembers() {
        if (mMembersList != null && !mMembersList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String member : mMembersList) {
                stringBuilder.append(member)
                        .append("; ");
            }

            if (stringBuilder.length() > 2) {
                stringBuilder.setLength(stringBuilder.length() - 2);
            }

            return stringBuilder.toString();

        } else {
            return null;
        }
    }

    @Nullable
    public String getLastMessageBody() {
        if (mChatMessagesList != null && !mChatMessagesList.isEmpty()) {
            return mChatMessagesList.get(0).getBody();

        } else {
            return null;
        }
    }

    public long getLastMessageTimestamp() {
        if (mChatMessagesList != null &&
                !mChatMessagesList.isEmpty()) {

            return mChatMessagesList.get(0).getTimestamp();

        } else {
            return -1;
        }
    }

    @Nullable
    private String getMucJid() {
        if (mMembersList != null && !mMembersList.isEmpty()) {
            for (String member : mMembersList) {
                if (!TextUtils.isEmpty(member) && member.contains("@muc")) {
                    return member;
                }
            }
        }

        return null;
    }

    public boolean isRoomGroupChat() {
        return TextUtils.equals(Enums.Chats.ConversationTypes.GROUP_CHAT, mConversationType) &&
                mChatWith != null &&
                mChatWith.contains("-myroom-");
    }

    public String getRoomOwnerJid() {
        return StringUtil.getUserJidFromRoomJid(getChatWith());
    }

    public boolean isUniqueGroupChat() {
        String mucJid = getMucJid();

        return TextUtils.equals(Enums.Chats.ConversationTypes.GROUP_CHAT, mConversationType) &&
                mucJid != null &&
                mucJid.contains("-unique-");
    }

    public String getChatWith() {
        return mChatWith;
    }

    public void setChatWith(String chatWith) {
        mChatWith = chatWith;
    }
}
