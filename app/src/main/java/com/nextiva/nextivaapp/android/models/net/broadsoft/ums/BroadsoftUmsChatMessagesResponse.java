/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.ums;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.gson.annotations.SerializedName;

/**
 * Created by adammacdonald on 3/7/18.
 */

public class BroadsoftUmsChatMessagesResponse extends BroadsoftUmsBaseResponse {

    @Nullable
    @SerializedName("messages")
    private BroadsoftUmsChatMessage[] mChatMessages;

    public BroadsoftUmsChatMessagesResponse() {
    }

    @VisibleForTesting
    public BroadsoftUmsChatMessagesResponse(@Nullable BroadsoftUmsChatMessage[] chatMessages) {
        mChatMessages = chatMessages;
    }

    @Nullable
    public BroadsoftUmsChatMessage[] getChatMessages() {
        return mChatMessages;
    }
}
