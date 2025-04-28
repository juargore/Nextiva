/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.util.DbConstants;
import com.nextiva.nextivaapp.android.util.GsonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by adammacdonald on 3/7/18.
 */

public class ChatMessage implements Serializable {

    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_MESSAGE_ID)
    private String mMessageId;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_TO)
    private String mTo;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_FROM)
    private String mFrom;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_TYPE)
    @Enums.Chats.ConversationTypes.Type
    private String mType;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_BODY)
    private String mBody;
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_IS_SENDER)
    private boolean mIsSender;
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_IS_READ)
    private boolean mIsRead;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_TIMESTAMP)
    private Long mTimestamp;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_THREAD_ID)
    private String mThreadId;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_LANGUAGE)
    private String mLanguage;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_PARTICIPANT)
    private String mParticipant;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_GUEST_FIRST)
    private String mGuestFirstName;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_GUEST_LAST)
    private String mGuestLastName;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_SENT_STATUS)
    @Enums.Chats.SentStatus.Status
    private Integer mSentStatus;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_CHAT_WITH)
    private String mChatWith;
    @Nullable
    @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_MEMBERS)
    private String mMembersString;
    @ColumnInfo(name = DbConstants.VCARDS_COLUMN_NAME_PHOTO_DATA)
    private byte[] mAvatar;
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_UI_NAME)
    private String mUIName;
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_PRESENCE_STATE)
    private Integer mPresenceState = Enums.Contacts.PresenceStates.NONE;
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_PRIORITY)
    private Integer mPresencePriority;
    @Ignore
    private boolean mShowTimeSeparator;

    public ChatMessage() {
    }

    public ChatMessage(@Nullable String messageId,
                       @Nullable String to,
                       @Nullable String from,
                       @Nullable @Enums.Chats.ConversationTypes.Type String type,
                       @Nullable String body,
                       boolean isSender,
                       boolean isRead,
                       @Nullable Long timestamp,
                       @Nullable String threadId,
                       @Nullable String membersString,
                       @Nullable String language,
                       @Nullable String participant,
                       @Nullable String guestFirstName,
                       @Nullable String guestLastName,
                       @NonNull Integer sentStatus,
                       @Nullable String chatWith) {

        mMessageId = messageId;
        mTo = to;
        mFrom = from;
        mType = type;
        mBody = body;
        mIsSender = isSender;
        mIsRead = isRead;
        mTimestamp = timestamp;
        mThreadId = threadId;
        mMembersString = membersString;
        mLanguage = language;
        mParticipant = participant;
        mGuestFirstName = guestFirstName;
        mGuestLastName = guestLastName;
        mSentStatus = sentStatus;
        mChatWith = chatWith;
    }

    @Nullable
    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(@Nullable String messageId) {
        mMessageId = messageId;
    }

    @Nullable
    public String getChatWith() {
        return mChatWith;
    }

    public void setChatWith(@Nullable String chatWith) {
        mChatWith = chatWith;
    }

    @Nullable
    public String getTo() {
        return mTo;
    }

    public void setTo(@Nullable String to) {
        mTo = to;
    }

    @Nullable
    public String getFrom() {
        return mFrom;
    }

    public void setFrom(@Nullable String from) {
        mFrom = from;
    }

    @Nullable
    @Enums.Chats.ConversationTypes.Type
    public String getType() {
        return mType;
    }

    public void setType(@Nullable @Enums.Chats.ConversationTypes.Type String type) {
        mType = type;
    }

    @Nullable
    public String getBody() {
        return mBody;
    }

    public void setBody(@Nullable String body) {
        mBody = body;
    }

    @NonNull
    public Long getTimestamp() {
        return (mTimestamp != null) ? mTimestamp : -1;
    }

    public void setTimestamp(@Nullable Long timestamp) {
        mTimestamp = timestamp;
    }

    @Nullable
    public String getParticipant() {
        return mParticipant;
    }

    public void setParticipant(@Nullable String participant) {
        mParticipant = participant;
    }

    @Nullable
    public String getGuestFirstName() {
        return mGuestFirstName;
    }

    public void setGuestFirstName(@Nullable String guestFirstName) {
        mGuestFirstName = guestFirstName;
    }

    @Nullable
    public String getGuestLastName() {
        return mGuestLastName;
    }

    public void setGuestLastName(@Nullable String guestLastName) {
        mGuestLastName = guestLastName;
    }

    @Nullable
    public String getGuestFullName() {
        if (!TextUtils.isEmpty(mGuestFirstName) && !TextUtils.isEmpty(mGuestLastName)) {
            return mGuestFirstName + " " + mGuestLastName;

        } else {
            return mGuestFirstName;
        }
    }

    public boolean isSender() {
        return mIsSender;
    }

    public void setIsSender(boolean sender) {
        mIsSender = sender;
    }

    public boolean isRead() {
        return mIsRead;
    }

    public void setIsRead(boolean read) {
        mIsRead = read;
    }

    @Nullable
    public String getThreadId() {
        return mThreadId;
    }

    public void setThreadId(@Nullable String threadId) {
        mThreadId = threadId;
    }

    @Nullable
    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(@Nullable String language) {
        mLanguage = language;
    }

    public boolean showTimeSeparator() {
        return mShowTimeSeparator;
    }

    public void setShowTimeSeparator(boolean showTimeSeparator) {
        mShowTimeSeparator = showTimeSeparator;
    }

    public byte[] getAvatar() {
        return mAvatar;
    }

    public void setAvatar(byte[] avatar) {
        mAvatar = avatar;
    }

    public String getUIName() {
        return mUIName;
    }

    public void setUIName(String UIName) {
        mUIName = UIName;
    }

    public Integer getPresenceState() {
        return mPresenceState;
    }

    public void setPresenceState(Integer presenceState) {
        mPresenceState = presenceState;
    }

    public Integer getPresencePriority() {
        return mPresencePriority;
    }

    public void setPresencePriority(Integer presencePriority) {
        mPresencePriority = presencePriority;
    }

    @Enums.Chats.SentStatus.Status
    public Integer getSentStatus() {
        return mSentStatus != null ? mSentStatus : Enums.Chats.SentStatus.SUCCESSFUL;
    }

    public void setSentStatus(@Enums.Chats.SentStatus.Status Integer mSentStatus) {
        this.mSentStatus = mSentStatus;
    }

    @Nullable
    public String getMembersString() {
        return mMembersString;
    }

    public void setMembersString(@Nullable String membersString) {
        mMembersString = membersString;
    }

    public ArrayList<String> getMembersList() {
        return (ArrayList<String>) GsonUtil.getObject(ArrayList.class, mMembersString);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessage{" +
                "mMessageId='" + mMessageId + '\'' +
                ", mTo='" + mTo + '\'' +
                ", mFrom='" + mFrom + '\'' +
                ", mType='" + mType + '\'' +
                ", mBody='" + mBody + '\'' +
                ", mIsSender=" + mIsSender +
                ", mIsRead=" + mIsRead +
                ", mTimestamp=" + mTimestamp +
                ", mThreadId='" + mThreadId + '\'' +
                ", mLanguage='" + mLanguage + '\'' +
                ", mParticipant='" + mParticipant + '\'' +
                ", mGuestFirstName='" + mGuestFirstName + '\'' +
                ", mGuestLastName='" + mGuestLastName + '\'' +
                ", mChatWith='" + mChatWith + '\'' +
                ", mAvatar=" + Arrays.toString(mAvatar) +
                ", mUIName='" + mUIName + '\'' +
                ", mPresenceState=" + mPresenceState +
                ", mPresencePriority=" + mPresencePriority +
                '}';
    }
}
