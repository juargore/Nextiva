/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/2/18.
 */

public class AuthenticationResponse implements Serializable {

    @Nullable
    @SerializedName("message")
    private String mMessage;
    @Nullable
    @SerializedName("tokenId")
    private String mTokenId;

    public AuthenticationResponse() {
    }

    @Nullable
    public String getMessage() {
        return mMessage;
    }

    @Nullable
    public String getTokenId() {
        return mTokenId;
    }
}
