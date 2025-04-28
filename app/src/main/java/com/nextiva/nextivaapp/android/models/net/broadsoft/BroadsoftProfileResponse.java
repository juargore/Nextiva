/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by joedephillipo on 2/14/18.
 */

@Root(name = "Profile", strict = false)
public class BroadsoftProfileResponse {

    @Nullable
    @Element(name = "details", required = false)
    private BroadsoftProfileDetails mDetails;
    @Nullable
    @Element(name = "additionalDetails", required = false)
    private BroadsoftProfileAdditionalDetails mAdditionalDetails;
    @Nullable
    @Element(name = "passwordExpiresDays", required = false)
    private String mPasswordExpiresDays;
    @Nullable
    @Element(name = "fac", required = false)
    private String mFac;
    @Nullable
    @Element(name = "registrations", required = false)
    private String mRegistrations;
    @Nullable
    @Element(name = "scheduleList", required = false)
    private String mScheduleList;
    @Nullable
    @Element(name = "portalPasswordChange", required = false)
    private String mPortalPasswordChange;
    @Nullable
    @Element(name = "countryCode", required = false)
    private String mCountryCode;

    public BroadsoftProfileResponse() {
    }

    public BroadsoftProfileResponse(@Nullable BroadsoftProfileDetails details, @Nullable BroadsoftProfileAdditionalDetails additionalDetails) {
        mDetails = details;
        mAdditionalDetails = additionalDetails;
    }

    @Nullable
    public BroadsoftProfileDetails getDetails() {
        return mDetails;
    }

    @Nullable
    public BroadsoftProfileAdditionalDetails getAdditionalDetails() {
        return mAdditionalDetails;
    }

    @Nullable
    public String getPasswordExpiresDays() {
        return mPasswordExpiresDays;
    }

    @Nullable
    public String getFac() {
        return mFac;
    }

    @Nullable
    public String getRegistrations() {
        return mRegistrations;
    }

    @Nullable
    public String getScheduleList() {
        return mScheduleList;
    }

    @Nullable
    public String getPortalPasswordChange() {
        return mPortalPasswordChange;
    }

    @Nullable
    public String getCountryCode() {
        return mCountryCode;
    }
}
