/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.emergencydialing;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftEmergencyDialingNonPhones implements Serializable {

    @Nullable
    @Element(name = "show-warning-dialog", required = false)
    private String mShowWarningDialog;
    @Nullable
    @Element(name = "dialing-behavior", required = false)
    private String mDialingBehavior;

    public BroadsoftEmergencyDialingNonPhones() {
    }

    @Nullable
    public String getShowWarningDialog() {
        return mShowWarningDialog;
    }

    @Nullable
    public String getDialingBehavior() {
        return mDialingBehavior;
    }
}
