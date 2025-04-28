/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;

/**
 * Created by adammacdonald on 3/21/18.
 */

public abstract class BaseBroadsoftBroadWorksAnywhereLocation {

    @Nullable
    @Element(name = "phoneNumber", required = false)
    String mPhoneNumber;
    @Nullable
    @Element(name = "active", required = false)
    Boolean mActive;
    @Nullable
    @Element(name = "broadworksCallControl", required = false)
    Boolean mCallControlEnabled;
    @Nullable
    @Element(name = "useDiversionInhibitor", required = false)
    Boolean mPreventDivertingCalls;
    @Nullable
    @Element(name = "answerConfirmationRequired", required = false)
    Boolean mAnswerConfirmationRequired;

    @Nullable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @Nullable
    public Boolean getActive() {
        return mActive;
    }

    @Nullable
    public Boolean getCallControlEnabled() {
        return mCallControlEnabled;
    }

    @Nullable
    public Boolean getPreventDivertingCalls() {
        return mPreventDivertingCalls;
    }

    @Nullable
    public Boolean getAnswerConfirmationRequired() {
        return mAnswerConfirmationRequired;
    }
}
