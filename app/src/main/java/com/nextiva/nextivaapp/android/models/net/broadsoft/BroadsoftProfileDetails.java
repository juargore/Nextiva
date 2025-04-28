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

@Root
public class BroadsoftProfileDetails {

    @Nullable
    @Element(name = "userId", required = false)
    private String mUserId;
    @Nullable
    @Element(name = "firstName", required = false)
    private String mFirstName;
    @Nullable
    @Element(name = "lastName", required = false)
    private String mLastName;
    @Nullable
    @Element(name = "hiranganaLastName", required = false)
    private String mHiranganaLastName;
    @Nullable
    @Element(name = "hiranganaFirstName", required = false)
    private String mHirangamaFirstName;
    @Nullable
    @Element(name = "serviceProvider", required = false)
    private String mServiceProvider;
    @Nullable
    @Element(name = "isEnterprise", required = false)
    private Boolean mIsEnterprise;
    @Nullable
    @Element(name = "groupId", required = false)
    private String mGroupId;
    @Nullable
    @Element(name = "number", required = false)
    private String mNumber;
    @Nullable
    @Element(name = "extension", required = false)
    private String mExtension;

    public BroadsoftProfileDetails() {
    }

    public BroadsoftProfileDetails(@Nullable String firstName, @Nullable String lastName) {
        mFirstName = firstName;
        mLastName = lastName;
    }

    @Nullable
    public String getUserId() {
        return mUserId;
    }

    @Nullable
    public String getFirstName() {
        return mFirstName;
    }

    @Nullable
    public String getLastName() {
        return mLastName;
    }

    @Nullable
    public String getHiranganaLastName() {
        return mHiranganaLastName;
    }

    @Nullable
    public String getHirangamaFirstName() {
        return mHirangamaFirstName;
    }

    @Nullable
    public String getGroupId() {
        return mGroupId;
    }

    @Nullable
    public String getNumber() {
        return mNumber;
    }

    @Nullable
    public String getExtension() {
        return mExtension;
    }

    @Nullable
    public String getServiceProvider() {
        return mServiceProvider;
    }

    public void setServiceProvider(@Nullable final String serviceProvider) {
        mServiceProvider = serviceProvider;
    }

    @Nullable
    public Boolean getEnterprise() {
        return mIsEnterprise;
    }

    public void setEnterprise(@Nullable final Boolean enterprise) {
        mIsEnterprise = enterprise;
    }
}
