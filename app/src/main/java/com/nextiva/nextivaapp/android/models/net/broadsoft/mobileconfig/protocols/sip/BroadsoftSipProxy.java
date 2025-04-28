/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftSipProxy implements Serializable {

    @Nullable
    @Attribute(name = "address", required = false)
    private String mAddress;
    @Nullable
    @Attribute(name = "port", required = false)
    private String mPort;
    @Nullable
    @Attribute(name = "lr", required = false)
    private String mLr;

    public BroadsoftSipProxy() {
    }

    @VisibleForTesting
    public BroadsoftSipProxy(@Nullable String address, @Nullable String port) {
        mAddress = address;
        mPort = port;
    }

    @Nullable
    public String getAddress() {
        return mAddress;
    }

    @Nullable
    public String getPort() {
        return mPort;
    }

    @Nullable
    public String getLr() {
        return mLr;
    }
}
