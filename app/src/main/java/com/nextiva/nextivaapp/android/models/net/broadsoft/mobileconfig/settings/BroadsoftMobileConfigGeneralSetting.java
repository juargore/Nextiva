/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/22/18.
 */

@Root
public class BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "enabled", required = false)
    protected String mIsEnabled;

    public BroadsoftMobileConfigGeneralSetting() {
    }


    @VisibleForTesting
    public BroadsoftMobileConfigGeneralSetting(@Nullable String isEnabled) {
        mIsEnabled = isEnabled;
    }

    @Nullable
    public String isEnabled() {
        return mIsEnabled;
    }
}
