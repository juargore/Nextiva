/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.testservices.BroadsoftTestServicesTestCalls;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftServicesTestServices extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "test-calls", required = false)
    private BroadsoftTestServicesTestCalls mTestCallsList;

    public BroadsoftServicesTestServices() {
    }

    @Nullable
    public BroadsoftTestServicesTestCalls getTestCallsList() {
        return mTestCallsList;
    }
}
