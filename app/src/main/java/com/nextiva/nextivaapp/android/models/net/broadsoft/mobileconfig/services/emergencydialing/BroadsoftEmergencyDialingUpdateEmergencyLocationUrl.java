/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.emergencydialing;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftEmergencyDialingUpdateEmergencyLocationUrl implements Serializable {

    @Nullable
    @Attribute(name = "verification-timeout", required = false)
    private String mVerificationTimeout;
    @Nullable
    @Attribute(name = "use-basic-auth", required = false)
    private String mUseBasicAuth;
    @Nullable
    @Text(required = false)
    private String mUrl;

    public BroadsoftEmergencyDialingUpdateEmergencyLocationUrl() {
    }

    @Nullable
    public String getVerificationTimeout() {
        return mVerificationTimeout;
    }

    @Nullable
    public String getUseBasicAuth() {
        return mUseBasicAuth;
    }

    @Nullable
    public String getUrl() {
        return mUrl;
    }
}
