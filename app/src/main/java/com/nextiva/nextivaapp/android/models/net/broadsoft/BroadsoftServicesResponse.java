/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftService;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by adammacdonald on 3/16/18.
 */

@Root(name = "Services", strict = false)
public class BroadsoftServicesResponse {

    @Nullable
    @ElementList(name = "service", inline = true, required = false)
    private ArrayList<BroadsoftService> mServicesList;

    public BroadsoftServicesResponse() {
    }

    public BroadsoftServicesResponse(@Nullable ArrayList<BroadsoftService> servicesList) {
        mServicesList = servicesList;
    }

    @Nullable
    public ArrayList<BroadsoftService> getServicesList() {
        return mServicesList;
    }
}
