/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

class NextivaChatState {

    @Nullable
    private String mJid;
    @Nullable
    @Enums.Chats.States.State
    private String mState;
    @Nullable
    private String mTimestamp;

    public NextivaChatState(@Nullable String jid, @Nullable String state, @Nullable String timestamp) {
        mJid = jid;
        mState = state;
        mTimestamp = timestamp;
    }

    @Nullable
    public String getJid() {
        return mJid;
    }

    public void setJid(@Nullable String jid) {
        mJid = jid;
    }

    @Nullable
    @Enums.Chats.States.State
    public String getState() {
        return mState;
    }

    public void setState(@Nullable @Enums.Chats.States.State String state) {
        mState = state;
    }

    @Nullable
    public String getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(@Nullable String timestamp) {
        mTimestamp = timestamp;
    }
}
