/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.models.ChatConversation;

import java.util.ArrayList;

/**
 * Created by joedephillipo on 2/26/18.
 */

public class
ChatConversationListItem extends SimpleBaseListItem<ChatConversation> {

    private DbPresence mPresence;
    @Nullable
    private byte[] mAvatarBytes;
    @Nullable
    private String mFormattedTimeString;
    @Nullable
    private String mDisplayName;
    @Nullable
    private ArrayList<String> mParticipants;


    public int getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(int unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }

    @Nullable
    private int unreadMessagesCount;

    public ChatConversationListItem(ChatConversation data, DbPresence presence) {
        super(data);
        mPresence = presence;
    }

    public DbPresence getPresence() {
        return mPresence;
    }

    @SuppressWarnings("unused")
    public void setPresence(DbPresence presence) {
        mPresence = presence;
    }

    @Nullable
    public byte[] getAvatarBytes() {
        return mAvatarBytes;
    }

    public void setAvatarBytes(@Nullable byte[] avatarBytes) {
        mAvatarBytes = avatarBytes;
    }

    @Nullable
    public String getFormattedTimeString() {
        return mFormattedTimeString;
    }

    public void setFormattedTimeString(@Nullable String formattedTimeString) {
        mFormattedTimeString = formattedTimeString;
    }

    @Nullable
    public ArrayList<String> getParticipants() {
        return mParticipants;
    }

    public void setParticipants(@Nullable ArrayList<String> participants) {
        mParticipants = participants;
    }

    @Nullable
    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(@Nullable String displayName) {
        mDisplayName = displayName;
    }
}
