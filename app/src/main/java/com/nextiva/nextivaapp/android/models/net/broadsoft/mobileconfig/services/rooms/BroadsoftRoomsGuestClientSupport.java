/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.rooms;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftRoomsGuestClientSupport extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "guest-client-url", required = false)
    private String mGuestClientUrl;
    @Nullable
    @Element(name = "guest-client-domain", required = false)
    private String mGuestClientDomain;

    public BroadsoftRoomsGuestClientSupport() {
    }

    @Nullable
    public String getGuestClientUrl() {
        return mGuestClientUrl;
    }

    @Nullable
    public String getGuestClientDomain() {
        return mGuestClientDomain;
    }
}
