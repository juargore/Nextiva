/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.models.ChatMessage;

/**
 * Created by adammacdonald on 3/2/18.
 */

public class ChatMessageListItem extends SimpleBaseListItem<ChatMessage> {

    @Enums.Chats.MessageBubbleTypes.Type
    private int mBubbleType;
    private String mHumanReadableDatetime;
    @Nullable
    private byte[] mAvatarBytes;

    public ChatMessageListItem(@NonNull ChatMessage data,
                               String humanReadableDatetime,
                               @Nullable byte[] avatarBytes) {

        this(data, Enums.Chats.MessageBubbleTypes.END, humanReadableDatetime, avatarBytes);
        mAvatarBytes = avatarBytes;
    }

    public ChatMessageListItem(@NonNull ChatMessage data,
                               @Enums.Chats.MessageBubbleTypes.Type int bubbleType,
                               String humanReadableDatetime,
                               @Nullable byte[] avatarBytes) {

        super(data);
        mBubbleType = bubbleType;
        mHumanReadableDatetime = humanReadableDatetime;
        mAvatarBytes = avatarBytes;
    }

    @Enums.Chats.MessageBubbleTypes.Type
    public int getBubbleType() {
        return mBubbleType;
    }

    public void setBubbleType(@Enums.Chats.MessageBubbleTypes.Type int bubbleType) {
        mBubbleType = bubbleType;
    }

    public String getHumanReadableDatetime() {
        return mHumanReadableDatetime;
    }

    public void setHumanReadableDatetime(String humanReadableDatetime) {
        mHumanReadableDatetime = humanReadableDatetime;
    }

    @Nullable
    public byte[] getAvatarBytes() {
        return mAvatarBytes;
    }

    public void setAvatarBytes(@Nullable byte[] avatarBytes) {
        mAvatarBytes = avatarBytes;
    }
}
