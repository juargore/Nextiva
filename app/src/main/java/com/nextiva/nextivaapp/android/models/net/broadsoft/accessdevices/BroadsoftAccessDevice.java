/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.accessdevices;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root(name = "accessDevice", strict = false)
class BroadsoftAccessDevice {

    @Nullable
    @Element(name = "deviceName", required = false)
    private String mDeviceName;
    @Nullable
    @Element(name = "deviceLevel", required = false)
    private String mDeviceLevel;
    @Nullable
    @Enums.AccessDeviceTypes.AccessDeviceType
    @Element(name = "deviceType", required = false)
    private String mDeviceType;
    @Nullable
    @Element(name = "deviceLinePort", required = false)
    private String mDeviceLinePort;
    @Nullable
    @Element(name = "deviceTypeUrl", required = false)
    private String mDeviceTypeUrl;
    @Nullable
    @Element(name = "deviceUserNamePassword", required = false)
    private BroadsoftDeviceCredentials mDeviceCredentials;
    @Nullable
    @Element(name = "version", required = false)
    private String mDeviceVersion;
    @Nullable
    @Element(name = "allowTermination", required = false)
    private Boolean mAllowTermination;
    @Nullable
    @Element(name = "endpointType", required = false)
    private String mEndpointType;

    public BroadsoftAccessDevice() {
    }

    @Nullable
    public String getDeviceName() {
        return mDeviceName;
    }

    @Nullable
    public String getDeviceLevel() {
        return mDeviceLevel;
    }

    @Nullable
    @Enums.AccessDeviceTypes.AccessDeviceType
    public String getDeviceType() {
        return mDeviceType;
    }

    @Nullable
    public String getDeviceLinePort() {
        return mDeviceLinePort;
    }

    @Nullable
    public String getDeviceTypeUrl() {
        return mDeviceTypeUrl;
    }

    @Nullable
    public BroadsoftDeviceCredentials getDeviceCredentials() {
        return mDeviceCredentials;
    }

    @Nullable
    public String getDeviceVersion() {
        return mDeviceVersion;
    }

    public void setDeviceVersion(@Nullable String deviceVersion) {
        mDeviceVersion = deviceVersion;
    }

    @Nullable
    public String getEndpointType() {
        return mEndpointType;
    }

    public Boolean getAllowTermination() {
        return mAllowTermination;
    }
}
