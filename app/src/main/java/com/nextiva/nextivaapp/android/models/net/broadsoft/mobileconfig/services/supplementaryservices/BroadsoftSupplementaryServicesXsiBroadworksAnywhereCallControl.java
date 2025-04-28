/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.supplementaryservices;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftSupplementaryServicesXsiBroadworksAnywhereCallControl extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Text(required = false)
    private String mValue;

    public BroadsoftSupplementaryServicesXsiBroadworksAnywhereCallControl() {
    }

    @VisibleForTesting
    public BroadsoftSupplementaryServicesXsiBroadworksAnywhereCallControl(@Nullable String isEnabled, @Nullable String value) {
        super(isEnabled);
        mValue = value;
    }

    @Nullable
    public String getValue() {
        return mValue;
    }
}
