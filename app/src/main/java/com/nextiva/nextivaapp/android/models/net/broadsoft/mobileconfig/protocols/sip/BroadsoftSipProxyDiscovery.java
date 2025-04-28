/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/22/18.
 */

@Root
public class BroadsoftSipProxyDiscovery implements Serializable {

    @Nullable
    @Element(name = "record-name", required = false)
    private String mRecordName;
    @Nullable
    @Element(name = "domain-override", required = false)
    private String mDomainOverride;
    @Nullable
    @Attribute(name = "enabled", required = false)
    private String mIsEnabled;

    public BroadsoftSipProxyDiscovery() {
    }

    @Nullable
    public String getRecordName() {
        return mRecordName;
    }

    @Nullable
    public String getDomainOverride() {
        return mDomainOverride;
    }

    @Nullable
    public String getIsEnabled() {
        return mIsEnabled;
    }
}
