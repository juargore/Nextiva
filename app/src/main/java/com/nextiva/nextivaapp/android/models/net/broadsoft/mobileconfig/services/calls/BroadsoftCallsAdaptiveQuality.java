/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftCallsAdaptiveQuality extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @ElementList(name = "levels", required = false)
    private ArrayList<BroadsoftCallsAdaptiveQualityLevel> mLevelsList;

    public BroadsoftCallsAdaptiveQuality() {
    }

    @Nullable
    public ArrayList<BroadsoftCallsAdaptiveQualityLevel> getLevelsList() {
        return mLevelsList;
    }
}
