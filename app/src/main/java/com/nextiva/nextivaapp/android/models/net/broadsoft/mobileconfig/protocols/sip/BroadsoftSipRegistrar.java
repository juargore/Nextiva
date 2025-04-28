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
public class BroadsoftSipRegistrar implements Serializable {

    @Nullable
    @Attribute(name = "uri", required = false)
    private String mUri;
    @Nullable
    @Attribute(name = "port", required = false)
    private String mPort;

    public BroadsoftSipRegistrar() {
    }

    @VisibleForTesting
    public BroadsoftSipRegistrar(@Nullable String uri, @Nullable String port) {
        mUri = uri;
        mPort = port;
    }

    @Nullable
    public String getUri() {
        return mUri;
    }

    @Nullable
    public String getPort() {
        return mPort;
    }
}
