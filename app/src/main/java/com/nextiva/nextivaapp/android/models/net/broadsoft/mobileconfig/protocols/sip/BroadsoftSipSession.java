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
public class BroadsoftSipSession implements Serializable {

    @Nullable
    @Attribute(name = "type", required = false)
    private String mType;
    @Nullable
    @Element(name = "expires-sec", required = false)
    private String mExpiresSec;
    @Nullable
    @Element(name = "rel100", required = false)
    private BroadsoftMobileConfigGeneralSetting mRel100;

    public BroadsoftSipSession() {
    }

    @VisibleForTesting
    public BroadsoftSipSession(@Nullable String type, @Nullable String expiresSec) {
        mType = type;
        mExpiresSec = expiresSec;
    }

    @Nullable
    public String getType() {
        return mType;
    }

    @Nullable
    public String getExpiresSec() {
        return mExpiresSec;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getRel100() {
        return mRel100;
    }
}
