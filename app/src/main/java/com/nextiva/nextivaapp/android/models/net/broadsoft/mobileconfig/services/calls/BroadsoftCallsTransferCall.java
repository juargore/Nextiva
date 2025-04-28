/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftCallsTransferCall extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "xsi-enabled", required = false)
    private String mXsiEnabled;
    @Nullable
    @Attribute(name = "type", required = false)
    private String mType;

    public BroadsoftCallsTransferCall() {
    }

    @Nullable
    public String getXsiEnabled() {
        return mXsiEnabled;
    }

    @Nullable
    public String getType() {
        return mType;
    }
}