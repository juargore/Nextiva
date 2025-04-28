/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/22/18.
 */

@Root
public class BroadsoftSipCredentials implements Serializable {

    @Nullable
    @Element(name = "username", required = false)
    private String mUsername;
    @Nullable
    @Element(name = "password", required = false)
    private String mPassword;
    @Nullable
    @Element(name = "phone-number", required = false)
    private String mPhoneNumber;
    @Nullable
    @Element(name = "auth", required = false)
    private BroadsoftSipAuth mSipAuth;

    public BroadsoftSipCredentials() {
    }

    @VisibleForTesting
    public BroadsoftSipCredentials(
            @Nullable BroadsoftSipAuth sipAuth,
            @Nullable String username,
            @Nullable String password) {

        mSipAuth = sipAuth;
        mUsername = username;
        mPassword = password;
    }

    @Nullable
    public String getUsername() {
        return mUsername;
    }

    @Nullable
    public String getPassword() {
        return mPassword;
    }

    @Nullable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @Nullable
    public BroadsoftSipAuth getSipAuth() {
        return mSipAuth;
    }
}
