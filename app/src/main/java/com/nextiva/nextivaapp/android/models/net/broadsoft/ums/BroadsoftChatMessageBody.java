/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.ums;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adammacdonald on 3/9/18.
 */

public class BroadsoftChatMessageBody implements Serializable {

    @NonNull
    @SerializedName("message")
    private BroadsoftChatMessageDetails mMessage;

    public BroadsoftChatMessageBody(@NonNull BroadsoftChatMessageDetails message) {
        mMessage = message;
    }

    @NonNull
    public BroadsoftChatMessageDetails getMessage() {
        return mMessage;
    }

    public void setMessage(@NonNull BroadsoftChatMessageDetails message) {
        mMessage = message;
    }
}
