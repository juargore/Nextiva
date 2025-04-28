/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

/**
 * Created by adammacdonald on 3/21/18.
 */

@Order(elements = {"phoneNumber", "description", "active", "broadworksCallControl", "useDiversionInhibitor", "answerConfirmationRequired"})
@Root(name = "location", strict = false)
public class BroadsoftBroadWorksAnywhereLocation extends BaseBroadsoftBroadWorksAnywhereLocation {

    @Nullable
    @Element(name = "description", required = false)
    private String mDescription;

    public BroadsoftBroadWorksAnywhereLocation() {
    }

    public BroadsoftBroadWorksAnywhereLocation(@NonNull NextivaAnywhereLocation nextivaAnywhereLocation) {
        this(nextivaAnywhereLocation.getPhoneNumber(),
             nextivaAnywhereLocation.getDescription(),
             nextivaAnywhereLocation.getActiveRaw(),
             nextivaAnywhereLocation.getCallControlEnabledRaw(),
             nextivaAnywhereLocation.getPreventDivertingCallsRaw(),
             nextivaAnywhereLocation.getAnswerConfirmationRequiredRaw());
    }

    public BroadsoftBroadWorksAnywhereLocation(
            @Nullable String phoneNumber,
            @Nullable String description,
            @Nullable Boolean active,
            @Nullable Boolean callControlEnabled,
            @Nullable Boolean preventDivertingCalls,
            @Nullable Boolean answerConfirmationRequired) {

        mPhoneNumber = phoneNumber;
        mDescription = description;
        mActive = active;
        mCallControlEnabled = callControlEnabled;
        mPreventDivertingCalls = preventDivertingCalls;
        mAnswerConfirmationRequired = answerConfirmationRequired;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }
}
