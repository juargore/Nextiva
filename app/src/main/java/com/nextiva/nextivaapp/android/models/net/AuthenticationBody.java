/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/2/18.
 */

public class AuthenticationBody implements Serializable {

    @NonNull
    @SerializedName("userName")
    private String mUsername;
    @NonNull
    @SerializedName("password")
    private String mPassword;
    @NonNull
    @SerializedName("realm")
    private String mRealm;

    public AuthenticationBody(@NonNull String username, @NonNull String password, @NonNull String realm) {
        mUsername = username;
        mPassword = password;
        mRealm = realm;
    }

    @NonNull
    public String getUsername() {
        return mUsername;
    }

    public void setUsername(@NonNull String username) {
        mUsername = username;
    }

    @NonNull
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(@NonNull String password) {
        mPassword = password;
    }

    @NonNull
    public String getRealm() {
        return mRealm;
    }

    public void setRealm(@NonNull String realm) {
        mRealm = realm;
    }
}
