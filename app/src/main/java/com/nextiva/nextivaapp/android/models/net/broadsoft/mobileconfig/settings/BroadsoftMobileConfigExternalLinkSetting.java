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
public class BroadsoftMobileConfigExternalLinkSetting implements Serializable {

    @Nullable
    @Attribute(name = "url", required = false)
    private String mUrl;

    public BroadsoftMobileConfigExternalLinkSetting() {
    }

    @Nullable
    public String getUrl() {
        return mUrl;
    }
}
