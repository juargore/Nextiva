/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.enterprisecontacts;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by joedephillipo on 2/16/18.
 */

@Root
public class BroadsoftEnterpriseAdditionalDetails {

    @Nullable
    @Element(name = "emailAddress", required = false)
    private String mEmailAddress;
    @Nullable
    @Element(name = "department", required = false)
    private String mDepartment;
    @Nullable
    @Element(name = "impId", required = false)
    private String mImpId;
    @Nullable
    @Element(name = "mobile", required = false)
    private String mMobileNumber;
    @Nullable
    @Element(name = "yahooId", required = false)
    private String mYahooId;
    @Nullable
    @Element(name = "pager", required = false)
    private String mPagerNumber;
    @Nullable
    @Element(name = "title", required = false)
    private String mTitle;
    @Nullable
    @Element(name = "location", required = false)
    private String mLocation;
    @Nullable
    @Element(name = "addressLine1", required = false)
    private String mAddressLineOne;
    @Nullable
    @Element(name = "addressLine2", required = false)
    private String mAddressLineTwo;
    @Nullable
    @Element(name = "city", required = false)
    private String mCity;
    @Nullable
    @Element(name = "state", required = false)
    private String mState;
    @Nullable
    @Element(name = "zip", required = false)
    private String mZip;
    @Nullable
    @Element(name = "country", required = false)
    private String mCountry;

    public BroadsoftEnterpriseAdditionalDetails() {
    }

    public BroadsoftEnterpriseAdditionalDetails(@Nullable String emailAddress, @Nullable String impId, @Nullable String mobileNumber) {
        mEmailAddress = emailAddress;
        mImpId = impId;
        mMobileNumber = mobileNumber;
    }

    @Nullable
    public String getEmailAddress() {
        return mEmailAddress;
    }

    @Nullable
    public String getDepartment() {
        return mDepartment;
    }

    @Nullable
    public String getImpId() {
        return mImpId;
    }

    @Nullable
    public String getMobileNumber() {
        return mMobileNumber;
    }

    @Nullable
    public String getYahooId() {
        return mYahooId;
    }

    @Nullable
    public String getPagerNumber() {
        return mPagerNumber;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getLocation() {
        return mLocation;
    }

    @Nullable
    public String getAddressLineOne() {
        return mAddressLineOne;
    }

    @Nullable
    public String getAddressLineTwo() {
        return mAddressLineTwo;
    }

    @Nullable
    public String getCity() {
        return mCity;
    }

    @Nullable
    public String getState() {
        return mState;
    }

    @Nullable
    public String getZip() {
        return mZip;
    }

    @Nullable
    public String getCountry() {
        return mCountry;
    }
}
