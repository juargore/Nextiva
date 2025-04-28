/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.ums;

import com.google.gson.annotations.SerializedName;
import com.nextiva.nextivaapp.android.constants.Enums;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by adammacdonald on 3/9/18.
 */

public class BroadsoftChatMessageDetails implements Serializable {

    @SerializedName("to")
    private String mTo;
    @SerializedName("from")
    private String mFrom;
    @Enums.Chats.ConversationTypes.Type
    @SerializedName("type")
    private String mType;
    @SerializedName("lang")
    private String mLanguage;
    @SerializedName("body")
    private String mBody;
    @SerializedName("msgId")
    private String mMessageId;
    @SerializedName("thread_id")
    private String mThreadId;
    @SerializedName("members")
    private ArrayList<BroadsoftUmsJid> mMembers;
    @SerializedName("domain")
    private String mDomain;

    public BroadsoftChatMessageDetails(String to,
                                       String from,
                                       @Enums.Chats.ConversationTypes.Type String type,
                                       String language,
                                       String body) {

        mTo = to;
        mFrom = from;
        mType = type;
        mLanguage = language;
        mBody = body;
    }

    public BroadsoftChatMessageDetails(String to,
                                       String from,
                                       @Enums.Chats.ConversationTypes.Type String type,
                                       String language,
                                       String body,
                                       String threadId,
                                       ArrayList<BroadsoftUmsJid> members) {

        mTo = to;
        mFrom = from;
        mType = type;
        mLanguage = language;
        mBody = body;
        mThreadId = threadId;
        mMembers = members;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        mTo = to;
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String from) {
        mFrom = from;
    }

    @Enums.Chats.ConversationTypes.Type
    public String getType() {
        return mType;
    }

    public void setType(@Enums.Chats.ConversationTypes.Type String type) {
        mType = type;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(String messageId) {
        mMessageId = messageId;
    }

    public String getThreadId() {
        return mThreadId;
    }

    public void setThreadId(String threadId) {
        mThreadId = threadId;
    }

    public ArrayList<BroadsoftUmsJid> getMembers() {
        return mMembers;
    }

    public void setMembers(ArrayList<BroadsoftUmsJid> members) {
        mMembers = members;
    }

    public String getDomain() {
        return mDomain;
    }

    public void setDomain(String domain) {
        mDomain = domain;
    }
}
