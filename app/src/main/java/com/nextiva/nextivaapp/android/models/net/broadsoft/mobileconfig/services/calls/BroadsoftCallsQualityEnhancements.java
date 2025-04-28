/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftCallsQualityEnhancements extends BroadsoftMobileConfigGeneralSetting implements Serializable {


    @Nullable
    @Element(name = "qos", required = false)
    private BroadsoftCallsQos mQos;

    public BroadsoftCallsQualityEnhancements() {
    }

    @Nullable
    public BroadsoftCallsQos getQos() {
        return mQos;
    }
}
