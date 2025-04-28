/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.emergencydialing.BroadsoftEmergencyDialingDialSequence;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.emergencydialing.BroadsoftEmergencyDialingNonPhones;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.emergencydialing.BroadsoftEmergencyDialingUpdateEmergencyLocation;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftServicesEmergencyDialing implements Serializable {

    @Nullable
    @Element(name = "update-emergency-location", required = false)
    private BroadsoftEmergencyDialingUpdateEmergencyLocation mUpdateEmergencyLocation;
    @Nullable
    @Element(name = "suggest-emergency-voip-call", required = false)
    private BroadsoftMobileConfigGeneralSetting mSuggestEmergencyVoipCall;
    @Nullable
    @Element(name = "dial-sequence", required = false)
    private BroadsoftEmergencyDialingDialSequence mDialSequence;
    @Nullable
    @Element(name = "non-phones", required = false)
    private BroadsoftEmergencyDialingNonPhones mNonPhones;
    @Nullable
    @ElementList(name = "numbers", required = false)
    private ArrayList<String> mNumbers;

    public BroadsoftServicesEmergencyDialing() {
    }

    @Nullable
    public BroadsoftEmergencyDialingUpdateEmergencyLocation getUpdateEmergencyLocation() {
        return mUpdateEmergencyLocation;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getSuggestEmergencyVoipCall() {
        return mSuggestEmergencyVoipCall;
    }

    @Nullable
    public BroadsoftEmergencyDialingDialSequence getDialSequence() {
        return mDialSequence;
    }

    @Nullable
    public BroadsoftEmergencyDialingNonPhones getNonPhones() {
        return mNonPhones;
    }

    @Nullable
    public ArrayList<String> getNumbers() {
        return mNumbers;
    }
}
