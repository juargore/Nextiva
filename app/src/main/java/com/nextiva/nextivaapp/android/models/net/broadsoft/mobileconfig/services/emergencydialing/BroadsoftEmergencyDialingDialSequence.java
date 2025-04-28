/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.emergencydialing;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftEmergencyDialingDialSequence implements Serializable {

    @Nullable
    @Attribute(name = "mode", required = false)
    private String mMode;

    public BroadsoftEmergencyDialingDialSequence() {
    }

    @Nullable
    public String getMode() {
        return mMode;
    }
}
