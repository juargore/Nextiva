/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.supplementaryservices.BroadsoftSupplementaryServicesXsi;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftServicesSupplementaryServices extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "xsi", required = false)
    private BroadsoftSupplementaryServicesXsi mXsi;

    public BroadsoftServicesSupplementaryServices() {
    }

    @VisibleForTesting
    public BroadsoftServicesSupplementaryServices(@Nullable BroadsoftSupplementaryServicesXsi xsi) {
        mXsi = xsi;
    }

    @Nullable
    public BroadsoftSupplementaryServicesXsi getXsi() {
        return mXsi;
    }
}
