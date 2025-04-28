/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftSipTransports implements Serializable {

    @Nullable
    @Attribute(name = "type", required = false)
    private String mType;
    @Nullable
    @Element(name = "udp", required = false)
    private BroadsoftSipTransport mUdp;
    @Nullable
    @Element(name = "tcp", required = false)
    private BroadsoftSipTransport mTcp;
    @Nullable
    @Element(name = "tls", required = false)
    private BroadsoftSipTransport mTls;

    public BroadsoftSipTransports() {
    }

    @VisibleForTesting
    public BroadsoftSipTransports(@Nullable String type, @Nullable BroadsoftSipTransport tcp, @Nullable BroadsoftSipTransport udp) {
        mType = type;
        mTcp = tcp;
        mUdp = udp;
    }

    @Nullable
    public String getType() {
        return mType;
    }

    @Nullable
    public BroadsoftSipTransport getUdp() {
        return mUdp;
    }

    @Nullable
    public BroadsoftSipTransport getTcp() {
        return mTcp;
    }

    @Nullable
    public BroadsoftSipTransport getTls() {
        return mTls;
    }
}
