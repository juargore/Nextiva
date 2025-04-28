/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/22/18.
 */

public class AccessDevice implements Serializable {

    @Nullable
    private String mName;
    @Nullable
    private String mLevel;
    @Nullable
    @Enums.AccessDeviceTypes.AccessDeviceType
    private String mType;
    @Nullable
    private String mLinePort;
    @Nullable
    private String mDeviceTypeUrl;
    @Nullable
    private String mUsername;
    @Nullable
    private String mPassword;
    @Nullable
    private String mVersion;
    @Nullable
    private String mEndpointType;
    private boolean mAllowTermination = false;

    public AccessDevice() {
    }

    @Nullable
    public String getName() {
        return mName;
    }

    public void setName(@Nullable String name) {
        mName = name;
    }

    @Nullable
    public String getLevel() {
        return mLevel;
    }

    public void setLevel(@Nullable String level) {
        mLevel = level;
    }

    @Nullable
    @Enums.AccessDeviceTypes.AccessDeviceType
    public String getType() {
        return mType;
    }

    public void setType(@Nullable @Enums.AccessDeviceTypes.AccessDeviceType String type) {
        mType = type;
    }

    @Nullable
    public String getLinePort() {
        return mLinePort;
    }

    public void setLinePort(@Nullable String linePort) {
        mLinePort = linePort;
    }

    @Nullable
    public String getDeviceTypeUrl() {
        return mDeviceTypeUrl;
    }

    public void setDeviceTypeUrl(@Nullable String deviceTypeUrl) {
        mDeviceTypeUrl = deviceTypeUrl;
    }

    @Nullable
    public String getUsername() {
        return mUsername;
    }

    public void setUsername(@Nullable String username) {
        mUsername = username;
    }

    @Nullable
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(@Nullable String password) {
        mPassword = password;
    }

    @Nullable
    public String getVersion() {
        return mVersion;
    }

    public void setVersion(@Nullable String version) {
        mVersion = version;
    }

    @Nullable
    public String getEndpointType() {
        return mEndpointType;
    }

    public void setEndpointType(@Nullable String endpointType) {
        mEndpointType = endpointType;
    }

    public boolean isAllowTermination() {
        return mAllowTermination;
    }

    public void setAllowTermination(boolean allowTermination) {
        mAllowTermination = allowTermination;
    }
}
