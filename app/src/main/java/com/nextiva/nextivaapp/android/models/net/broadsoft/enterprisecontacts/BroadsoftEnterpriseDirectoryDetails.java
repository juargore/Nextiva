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
public class BroadsoftEnterpriseDirectoryDetails {

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
    private String mHiranganaFirstName;
    @Nullable
    @Element(name = "groupId", required = false)
    private String mGroupId;
    @Nullable
    @Element(name = "number", required = false)
    private String mPhoneNumber;
    @Nullable
    @Element(name = "extension", required = false)
    private String mExtension;
    @Nullable
    @Element(name = "additionalDetails", required = false)
    private BroadsoftEnterpriseAdditionalDetails mAdditionalDetails;

    public BroadsoftEnterpriseDirectoryDetails() {
    }

    public BroadsoftEnterpriseDirectoryDetails(
            @Nullable String userId,
            @Nullable String firstName,
            @Nullable String lastName,
            @Nullable String phoneNumber,
            @Nullable String extension,
            @Nullable BroadsoftEnterpriseAdditionalDetails additionalDetails) {

        mUserId = userId;
        mFirstName = firstName;
        mLastName = lastName;
        mPhoneNumber = phoneNumber;
        mExtension = extension;
        mAdditionalDetails = additionalDetails;
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
    public String getHiranganaFirstName() {
        return mHiranganaFirstName;
    }

    @Nullable
    public String getGroupId() {
        return mGroupId;
    }

    @Nullable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @Nullable
    public String getExtension() {
        return mExtension;
    }

    @Nullable
    public BroadsoftEnterpriseAdditionalDetails getAdditionalDetails() {
        return mAdditionalDetails;
    }
}
