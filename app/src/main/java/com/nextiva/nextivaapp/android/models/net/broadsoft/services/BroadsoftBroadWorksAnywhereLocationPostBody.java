/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.xml.converters.EmptyValueConverter;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

/**
 * Created by adammacdonald on 3/21/18.
 */

@Order(elements = {"phoneNumber", "description", "active", "broadworksCallControl", "useDiversionInhibitor", "answerConfirmationRequired"})
@Root(name = "BroadWorksAnywhereLocation", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
public class BroadsoftBroadWorksAnywhereLocationPostBody extends BaseBroadsoftBroadWorksAnywhereLocation {

    @Nullable
    @Element(name = "description", required = false)
    @Convert(value = EmptyValueConverter.class)
    private String mDescription;

    public BroadsoftBroadWorksAnywhereLocationPostBody() {
    }

    public BroadsoftBroadWorksAnywhereLocationPostBody(@NonNull NextivaAnywhereLocation nextivaAnywhereLocation) {
        this(nextivaAnywhereLocation.getPhoneNumber(),
             nextivaAnywhereLocation.getDescription(),
             nextivaAnywhereLocation.getActiveRaw(),
             nextivaAnywhereLocation.getCallControlEnabledRaw(),
             nextivaAnywhereLocation.getPreventDivertingCallsRaw(),
             nextivaAnywhereLocation.getAnswerConfirmationRequiredRaw());
    }

    private BroadsoftBroadWorksAnywhereLocationPostBody(
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
