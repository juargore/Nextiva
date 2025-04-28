/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.emergencydialing;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftEmergencyDialingUpdateEmergencyLocation extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "url", required = false)
    private BroadsoftEmergencyDialingUpdateEmergencyLocationUrl mUrl;
    @Nullable
    @Element(name = "update-location-at-login", required = false)
    private BroadsoftEmergencyDialingUpdateEmergencyLocationUpdateLocationAtLogin mUpdateLocationAtLogin;
    @Nullable
    @Element(name = "update-location-at-will", required = false)
    private BroadsoftMobileConfigGeneralSetting mUpdateLocationAtWill;

    public BroadsoftEmergencyDialingUpdateEmergencyLocation() {
    }

    @Nullable
    public BroadsoftEmergencyDialingUpdateEmergencyLocationUrl getUrl() {
        return mUrl;
    }

    @Nullable
    public BroadsoftEmergencyDialingUpdateEmergencyLocationUpdateLocationAtLogin getUpdateLocationAtLogin() {
        return mUpdateLocationAtLogin;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getUpdateLocationAtWill() {
        return mUpdateLocationAtWill;
    }
}
