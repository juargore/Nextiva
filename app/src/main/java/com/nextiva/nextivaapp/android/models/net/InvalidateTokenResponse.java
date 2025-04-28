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

public class InvalidateTokenResponse implements Serializable {

    @Nullable
    @SerializedName("result")
    private String mResult;
    @Nullable
    @SerializedName("message")
    private String mMessage;

    public InvalidateTokenResponse() {
    }

    @Nullable
    public String getResult() {
        return mResult;
    }

    @Nullable
    public String getMessage() {
        return mMessage;
    }
}
