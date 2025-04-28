/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.ums;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adammacdonald on 3/7/18.
 */

public class BroadsoftDeviceRegistrationBody implements Serializable {

    @NonNull
    @SerializedName("registration")
    private BroadsoftDeviceRegistrationDetails mRegistrationDetails;

    public BroadsoftDeviceRegistrationBody(@NonNull BroadsoftDeviceRegistrationDetails registrationDetails) {
        mRegistrationDetails = registrationDetails;
    }

    @NonNull
    public BroadsoftDeviceRegistrationDetails getRegistrationDetails() {
        return mRegistrationDetails;
    }

    public void setRegistrationDetails(@NonNull BroadsoftDeviceRegistrationDetails registrationDetails) {
        mRegistrationDetails = registrationDetails;
    }
}
