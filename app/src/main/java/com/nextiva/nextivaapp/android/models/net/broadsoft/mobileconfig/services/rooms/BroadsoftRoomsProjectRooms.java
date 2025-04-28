/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.rooms;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftRoomsProjectRooms extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "create", required = false)
    private String mCreate;

    public BroadsoftRoomsProjectRooms() {
    }

    @Nullable
    public String getCreate() {
        return mCreate;
    }
}
