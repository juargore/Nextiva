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

public class ValidateTokenResponse implements Serializable {

    @Nullable
    @SerializedName("realm")
    private String mRealm;
    @Nullable
    @SerializedName("uid")
    private String mUID;
    @Nullable
    @SerializedName("valid")
    private Boolean mIsValid;

    public ValidateTokenResponse() {
    }

    @Nullable
    public String getRealm() {
        return mRealm;
    }

    @Nullable
    public String getUID() {
        return mUID;
    }

    @Nullable
    public Boolean getIsValid() {
        return mIsValid;
    }
}
