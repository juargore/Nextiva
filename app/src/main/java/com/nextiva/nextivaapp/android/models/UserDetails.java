/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/2/18.
 */

public class UserDetails implements Serializable {

    @Nullable
    private int[] mCorporateAccountNumbers;
    @Nullable
    private String mCreateTimestamp;
    @Nullable
    private String[] mDn;
    @Nullable
    private String mEmail;
    @Nullable
    private String mLocation;
    @Nullable
    private String mImpId;
    @Nullable
    private String mFirstName;
    @Nullable
    private String mLastName;
    @Nullable
    private String mLoginId;
    @Nullable
    private String mModifyTimestamp;
    @Nullable
    private String mNextivaUUID;
    @Nullable
    private String mPostalAddress;
    @Nullable
    private String mPreferredTimeZone;
    @Nullable
    private String mRealm;
    @Nullable
    private String mTelephoneNumber;
    @Nullable
    private String mUserId;
    @Nullable
    private String mUserStatus;
    @Nullable
    private String mExtension;
    @Nullable
    private String mServiceProvider;
    @Nullable
    private Boolean mIsEnterprise;
    @Nullable
    private String mGroupId;

    public UserDetails() {
    }

    @Nullable
    public int[] getCorporateAccountNumbers() {
        return mCorporateAccountNumbers;
    }

    public void setCorporateAccountNumbers(@Nullable int[] corporateAccountNumbers) {
        mCorporateAccountNumbers = corporateAccountNumbers;
    }

    @Nullable
    public String getCreateTimestamp() {
        return mCreateTimestamp;
    }

    public void setCreateTimestamp(@Nullable String createTimestamp) {
        mCreateTimestamp = createTimestamp;
    }

    @Nullable
    public String[] getDn() {
        return mDn;
    }

    public void setDn(@Nullable String[] dn) {
        mDn = dn;
    }

    @Nullable
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(@Nullable String email) {
        mEmail = email;
    }

    @Nullable
    public String getLocation() {
        return mLocation;
    }

    public void setLocation(@Nullable final String location) {
        mLocation = location;
    }

    @Nullable
    public String getImpId() {
        return mImpId;
    }

    public void setImpId(@Nullable String impId) {
        mImpId = impId;
    }

    @Nullable
    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(@Nullable String firstName) {
        mFirstName = firstName;
    }

    @Nullable
    public String getLastName() {
        return mLastName;
    }

    public void setLastName(@Nullable String lastName) {
        mLastName = lastName;
    }

    @Nullable
    public String getLoginId() {
        return mLoginId;
    }

    public void setLoginId(@Nullable String loginId) {
        mLoginId = loginId;
    }

    @Nullable
    public String getModifyTimestamp() {
        return mModifyTimestamp;
    }

    public void setModifyTimestamp(@Nullable String modifyTimestamp) {
        mModifyTimestamp = modifyTimestamp;
    }

    @Nullable
    public String getNextivaUUID() {
        return mNextivaUUID;
    }

    public void setNextivaUUID(@Nullable String nextivaUUID) {
        mNextivaUUID = nextivaUUID;
    }

    @Nullable
    public String getPostalAddress() {
        return mPostalAddress;
    }

    public void setPostalAddress(@Nullable String postalAddress) {
        mPostalAddress = postalAddress;
    }

    @Nullable
    public String getPreferredTimeZone() {
        return mPreferredTimeZone;
    }

    public void setPreferredTimeZone(@Nullable String preferredTimeZone) {
        mPreferredTimeZone = preferredTimeZone;
    }

    @Nullable
    public String getRealm() {
        return mRealm;
    }

    public void setRealm(@Nullable String realm) {
        mRealm = realm;
    }

    @Nullable
    public String getTelephoneNumber() {
        return mTelephoneNumber;
    }

    public void setTelephoneNumber(@Nullable String telephoneNumber) {
        mTelephoneNumber = telephoneNumber;
    }

    @Nullable
    public String getUserId() {
        return mUserId;
    }

    public void setUserId(@Nullable String userId) {
        mUserId = userId;
    }

    @Nullable
    public String getUserStatus() {
        return mUserStatus;
    }

    public void setUserStatus(@Nullable String userStatus) {
        mUserStatus = userStatus;
    }

    @Nullable
    public String getFullName() {
        if (!TextUtils.isEmpty(getFirstName()) && !TextUtils.isEmpty(getLastName())) {
            return getFirstName() +
                    " " +
                    getLastName();

        } else if (!TextUtils.isEmpty(getFirstName())) {
            return getFirstName();

        } else if (!TextUtils.isEmpty(getLastName())) {
            return getLastName();
        }

        return "";
    }

    @Nullable
    public String getExtension() {
        return mExtension;
    }

    public void setExtension(@Nullable final String extension) {
        mExtension = extension;
    }

    @Nullable
    public String getServiceProvider() {
        return mServiceProvider;
    }

    public void setServiceProvider(@Nullable final String serviceProvider) {
        mServiceProvider = serviceProvider;
    }

    @Nullable
    public boolean getIsEnterprise() {
        return mIsEnterprise;
    }

    public void setIsEnterprise(@Nullable final Boolean isEnterprise) {
        mIsEnterprise = isEnterprise;
    }

    @Nullable
    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(@Nullable final String groupId) {
        mGroupId = groupId;
    }
}
