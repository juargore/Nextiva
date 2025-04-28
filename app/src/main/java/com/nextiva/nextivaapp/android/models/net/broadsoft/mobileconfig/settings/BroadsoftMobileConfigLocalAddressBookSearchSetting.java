/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/22/18.
 */

@Root
public class BroadsoftMobileConfigLocalAddressBookSearchSetting extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "default", required = false)
    private String mDefault;

    public BroadsoftMobileConfigLocalAddressBookSearchSetting() {
    }

    @Nullable
    public String getDefault() {
        return mDefault;
    }
}
