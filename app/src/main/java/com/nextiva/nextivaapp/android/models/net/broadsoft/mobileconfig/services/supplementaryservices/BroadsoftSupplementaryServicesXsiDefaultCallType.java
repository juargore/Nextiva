/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.supplementaryservices;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftSupplementaryServicesXsiDefaultCallType extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "type", required = false)
    private String mType;
    @Nullable
    @Element(name = "sip-call", required = false)
    private BroadsoftMobileConfigGeneralSetting mSipCall;
    @Nullable
    @Element(name = "call-back", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallBack;
    @Nullable
    @Element(name = "call-through", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallThrough;
    @Nullable
    @Element(name = "mobile-call", required = false)
    private BroadsoftMobileConfigGeneralSetting mMobileCall;
    @Nullable
    @Element(name = "always-ask", required = false)
    private BroadsoftMobileConfigGeneralSetting mAlwaysAsk;

    public BroadsoftSupplementaryServicesXsiDefaultCallType() {
    }

    @Nullable
    public String getType() {
        return mType;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getSipCall() {
        return mSipCall;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallBack() {
        return mCallBack;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallThrough() {
        return mCallThrough;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getMobileCall() {
        return mMobileCall;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getAlwaysAsk() {
        return mAlwaysAsk;
    }
}
