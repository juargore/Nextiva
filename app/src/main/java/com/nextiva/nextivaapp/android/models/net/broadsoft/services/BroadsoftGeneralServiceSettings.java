/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by adammacdonald on 3/16/18.
 */

@Root
abstract class BroadsoftGeneralServiceSettings extends BroadsoftBaseServiceSettings {

    @Nullable
    @Element(name = "active", required = false)
    Boolean mActive;

    BroadsoftGeneralServiceSettings() {
    }

    BroadsoftGeneralServiceSettings(@Nullable Boolean active) {
        mActive = active;
    }

    @Nullable
    public Boolean getActive() {
        return mActive;
    }
}
