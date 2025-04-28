/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.accessdevices;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.models.AccessDevice;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftAccessDevicesResponse {

    @Nullable
    @ElementList(name = "accessDevice", inline = true, required = false)
    private ArrayList<BroadsoftAccessDevice> mAccessDevicesList;

    public BroadsoftAccessDevicesResponse() {
    }

    @Nullable
    public ArrayList<BroadsoftAccessDevice> getAccessDevicesList() {
        return mAccessDevicesList;
    }

    @Nullable
    public AccessDevice getAccessDevice(@Enums.AccessDeviceTypes.AccessDeviceType String accessDeviceType) {
        AccessDevice accessDevice = new AccessDevice();

        if (mAccessDevicesList != null) {
            for (BroadsoftAccessDevice broadsoftAccessDevice : mAccessDevicesList) {
                if (broadsoftAccessDevice != null && TextUtils.equals(broadsoftAccessDevice.getDeviceType(), accessDeviceType)) {
                    accessDevice.setName(broadsoftAccessDevice.getDeviceName());
                    accessDevice.setLevel(broadsoftAccessDevice.getDeviceLevel());
                    accessDevice.setType(broadsoftAccessDevice.getDeviceType());
                    accessDevice.setLinePort(broadsoftAccessDevice.getDeviceLinePort());
                    accessDevice.setDeviceTypeUrl(broadsoftAccessDevice.getDeviceTypeUrl());
                    accessDevice.setVersion(broadsoftAccessDevice.getDeviceVersion());
                    accessDevice.setEndpointType(broadsoftAccessDevice.getEndpointType());
                    accessDevice.setAllowTermination(Boolean.TRUE.equals(broadsoftAccessDevice.getAllowTermination()));

                    if (broadsoftAccessDevice.getDeviceCredentials() != null) {
                        accessDevice.setUsername(broadsoftAccessDevice.getDeviceCredentials().getUserName());
                        accessDevice.setPassword(broadsoftAccessDevice.getDeviceCredentials().getPassword());
                    }

                    return accessDevice;
                }
            }
        }

        return null;
    }
}
