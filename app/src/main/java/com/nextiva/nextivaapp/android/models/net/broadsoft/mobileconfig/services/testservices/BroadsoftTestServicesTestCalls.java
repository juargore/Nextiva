/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.testservices;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftTestServicesTestCalls extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "enable-add-remove-video", required = false)
    private String mEnableAddRemoveVideo;
    @Nullable
    @ElementList(name = "test-number", required = false, inline = true)
    private ArrayList<BroadsoftTestServicesTestNumber> mTestNumbersList;

    public BroadsoftTestServicesTestCalls() {
    }

    @Nullable
    public String getEnableAddRemoveVideo() {
        return mEnableAddRemoveVideo;
    }

    @Nullable
    public ArrayList<BroadsoftTestServicesTestNumber> getTestNumbersList() {
        return mTestNumbersList;
    }
}
