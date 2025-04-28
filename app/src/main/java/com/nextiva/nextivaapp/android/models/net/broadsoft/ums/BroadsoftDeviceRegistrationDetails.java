/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.ums;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adammacdonald on 3/7/18.
 */

public class BroadsoftDeviceRegistrationDetails implements Serializable {

    @SerializedName("app-id")
    private String mAppId;
    @SerializedName("device-type")
    private String mDeviceType;
    @SerializedName("device-version")
    private String mDeviceVersion;
    @SerializedName("client-version")
    private String mClientVersion;
    @SerializedName("token")
    private String mToken;

    public BroadsoftDeviceRegistrationDetails(String appId,
                                              String deviceType,
                                              String deviceVersion,
                                              String clientVersion,
                                              String token) {
        mAppId = appId;
        mDeviceType = deviceType;
        mDeviceVersion = deviceVersion;
        mClientVersion = clientVersion;
        mToken = token;
    }

    public String getAppId() {
        return mAppId;
    }

    public void setAppId(String appId) {
        mAppId = appId;
    }

    public String getDeviceType() {
        return mDeviceType;
    }

    public void setDeviceType(String deviceType) {
        mDeviceType = deviceType;
    }

    public String getDeviceVersion() {
        return mDeviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        mDeviceVersion = deviceVersion;
    }

    public String getClientVersion() {
        return mClientVersion;
    }

    public void setClientVersion(String clientVersion) {
        mClientVersion = clientVersion;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }
}
