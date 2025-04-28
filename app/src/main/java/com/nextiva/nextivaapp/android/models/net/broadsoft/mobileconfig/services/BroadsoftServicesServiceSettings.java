/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.servicesettings.BroadsoftServiceSettingsLinks;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftServicesServiceSettings implements Serializable {

    @Nullable
    @Element(name = "links", required = false)
    private BroadsoftServiceSettingsLinks mLinks;

    public BroadsoftServicesServiceSettings() {
    }

    @Nullable
    public BroadsoftServiceSettingsLinks getLinks() {
        return mLinks;
    }
}
