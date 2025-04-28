/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftSipTransport implements Serializable {

    @Nullable
    @Attribute(name = "version", required = false)
    private String mVersion;
    @Nullable
    @Element(name = "keepalive", required = false)
    private BroadsoftSipKeepalive mKeepalive;
    @Nullable
    @Element(name = "tcp-size-threshold-bytes", required = false)
    private String mTcpSizeThresholdBytes;
    @Nullable
    @Element(name = "ignore-ssl-errors", required = false)
    private BroadsoftMobileConfigGeneralSetting mIgnoreSslErrors;

    public BroadsoftSipTransport() {
    }


    @VisibleForTesting
    public BroadsoftSipTransport(@Nullable BroadsoftSipKeepalive keepalive) {
        mKeepalive = keepalive;
    }


    @Nullable
    public String getVersion() {
        return mVersion;
    }

    @Nullable
    public BroadsoftSipKeepalive getKeepalive() {
        return mKeepalive;
    }

    @Nullable
    public String getTcpSizeThresholdBytes() {
        return mTcpSizeThresholdBytes;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getIgnoreSslErrors() {
        return mIgnoreSslErrors;
    }
}
