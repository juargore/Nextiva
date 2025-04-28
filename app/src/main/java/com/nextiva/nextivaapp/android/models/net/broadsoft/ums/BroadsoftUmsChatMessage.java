/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.ums;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.gson.annotations.SerializedName;
import com.nextiva.nextivaapp.android.constants.Enums;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by adammacdonald on 3/7/18.
 */

public class BroadsoftUmsChatMessage implements Serializable {

    @Nullable
    @SerializedName("msgid")
    private String mMessageId;
    @Nullable
    @SerializedName("to")
    private String mTo;
    @Nullable
    @SerializedName("from")
    private String mFrom;
    @Nullable
    @Enums.Chats.ConversationTypes.Type
    @SerializedName("type")
    private String mType;
    @Nullable
    @SerializedName("lang")
    private String mLanguage;
    @Nullable
    @SerializedName("thread_id")
    private String mThreadId;
    @Nullable
    @SerializedName("body")
    private String mBody;
    @Nullable
    @SerializedName("isSender")
    private Boolean mIsSender;
    @Nullable
    @SerializedName("read")
    private Boolean mIsRead;
    @Nullable
    @SerializedName("stamp")
    private Long mTimestamp;
    @Nullable
    @SerializedName("participant")
    private String mParticipant;
    @Nullable
    @SerializedName("guestFirst")
    private String mGuestFirstName;
    @Nullable
    @SerializedName("guestLast")
    private String mGuestLastName;
    @Nullable
    @SerializedName("members")
    private ArrayList<BroadsoftUmsJid> mMembers;

    public BroadsoftUmsChatMessage() {
    }

    @VisibleForTesting
    public BroadsoftUmsChatMessage(
            @Nullable String messageId,
            @Nullable String to,
            @Nullable String from,
            @Nullable String type,
            @Nullable String body,
            @Nullable String threadId,
            @Nullable Boolean isSender,
            @Nullable Boolean isRead,
            @Nullable Long timestamp,
            @Nullable String participant,
            @Nullable String guestFirstName,
            @Nullable String guestLastName,
            @Nullable ArrayList<BroadsoftUmsJid> members) {

        mMessageId = messageId;
        mTo = to;
        mFrom = from;
        mType = type;
        mBody = body;
        mIsSender = isSender;
        mIsRead = isRead;
        mThreadId = threadId;
        mTimestamp = timestamp;
        mParticipant = participant;
        mGuestFirstName = guestFirstName;
        mGuestLastName = guestLastName;
        mMembers = members;
    }

    @Nullable
    public String getMessageId() {
        return mMessageId;
    }

    @Nullable
    public String getTo() {
        return mTo;
    }

    @Nullable
    public String getFrom() {
        return mFrom;
    }

    @Nullable
    @Enums.Chats.ConversationTypes.Type
    public String getType() {
        return mType;
    }

    @Nullable
    public String getLanguage() {
        return mLanguage;
    }

    @Nullable
    public String getBody() {
        return mBody;
    }

    @Nullable
    public Boolean getSender() {
        return mIsSender;
    }

    @Nullable
    public Boolean getRead() {
        return mIsRead;
    }

    @Nullable
    public Long getTimestamp() {
        return mTimestamp;
    }

    @Nullable
    public String getParticipant() {
        return mParticipant;
    }

    @Nullable
    public String getGuestFirstName() {
        return mGuestFirstName;
    }

    @Nullable
    public String getGuestLastName() {
        return mGuestLastName;
    }

    @Nullable
    public ArrayList<BroadsoftUmsJid> getMembers() {
        return mMembers;
    }

    public void setMembers(@Nullable ArrayList<BroadsoftUmsJid> members) {
        mMembers = members;
    }

    @Nullable
    public String getThreadId() {
        return mThreadId;
    }

    public void setThreadId(@Nullable String threadId) {
        mThreadId = threadId;
    }
}
