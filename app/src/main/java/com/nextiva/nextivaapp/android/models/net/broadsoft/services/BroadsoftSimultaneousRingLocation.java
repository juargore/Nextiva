/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by adammacdonald on 3/21/18.
 */

@Root(name = "simRingLocation", strict = false)
public class BroadsoftSimultaneousRingLocation {

    @Nullable
    @Element(name = "address", required = false)
    private String mAddress;
    @Nullable
    @Element(name = "answerConfirmationRequired", required = false)
    private Boolean mAnswerConfirmationRequired;

    public BroadsoftSimultaneousRingLocation() {
    }

    public BroadsoftSimultaneousRingLocation(@NonNull SimultaneousRingLocation simultaneousRingLocation) {
        this(simultaneousRingLocation.getPhoneNumber(),
             simultaneousRingLocation.getAnswerConfirmationRequiredRaw());
    }

    public BroadsoftSimultaneousRingLocation(@Nullable String address, @Nullable Boolean answerConfirmationRequired) {
        mAddress = address;
        mAnswerConfirmationRequired = answerConfirmationRequired;
    }

    @Nullable
    public String getAddress() {
        return mAddress;
    }

    @Nullable
    public Boolean getAnswerConfirmationRequired() {
        return mAnswerConfirmationRequired;
    }
}
