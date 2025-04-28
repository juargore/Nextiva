/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.emergencydialing;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftEmergencyDialingUpdateEmergencyLocationUpdateLocationAtLogin extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "required", required = false)
    private String mRequired;
    @Nullable
    @Attribute(name = "recurring", required = false)
    private String mRecurring;

    public BroadsoftEmergencyDialingUpdateEmergencyLocationUpdateLocationAtLogin() {
    }

    @Nullable
    public String getRequired() {
        return mRequired;
    }

    @Nullable
    public String getRecurring() {
        return mRecurring;
    }
}
