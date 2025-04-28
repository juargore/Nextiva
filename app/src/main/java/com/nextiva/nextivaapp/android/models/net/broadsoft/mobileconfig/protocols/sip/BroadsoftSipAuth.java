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
public class BroadsoftSipAuth implements Serializable {

    @Nullable
    @Element(name = "username", required = false)
    private String mUsername;
    @Nullable
    @Element(name = "password", required = false)
    private String mPassword;

    public BroadsoftSipAuth() {
    }

    @VisibleForTesting
    public BroadsoftSipAuth(@Nullable String username) {
        mUsername = username;
    }

    @Nullable
    public String getUsername() {
        return mUsername;
    }

    @Nullable
    public String getPassword() {
        return mPassword;
    }
}
