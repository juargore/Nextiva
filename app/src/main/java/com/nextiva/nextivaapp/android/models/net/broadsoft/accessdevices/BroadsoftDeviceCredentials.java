/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.accessdevices;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftDeviceCredentials {

    @Nullable
    @Element(name = "userName", required = false)
    private String mUserName;
    @Nullable
    @Element(name = "password", required = false)
    private String mPassword;

    public BroadsoftDeviceCredentials() {
    }

    @Nullable
    public String getUserName() {
        return mUserName;
    }

    @Nullable
    public String getPassword() {
        return mPassword;
    }
}
