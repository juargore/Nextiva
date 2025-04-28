/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftServicesForcedLogout extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "control-protocol", required = false)
    private String mControlProtocol;
    @Nullable
    @Attribute(name = "appid", required = false)
    private String mAppId;

    public BroadsoftServicesForcedLogout() {
    }

    @Nullable
    public String getControlProtocol() {
        return mControlProtocol;
    }

    @Nullable
    public String getAppId() {
        return mAppId;
    }
}
