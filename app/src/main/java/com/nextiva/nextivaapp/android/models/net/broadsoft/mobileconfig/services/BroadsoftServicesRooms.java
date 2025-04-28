/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.rooms.BroadsoftRoomsConferenceBridge;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.rooms.BroadsoftRoomsMyRoom;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.rooms.BroadsoftRoomsProjectRooms;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftServicesRooms extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "default-room-history-size", required = false)
    private Integer mDefaultRoomHistorySize;
    @Nullable
    @Element(name = "myroom", required = false)
    private BroadsoftRoomsMyRoom mMyRoom;
    @Nullable
    @Element(name = "projectrooms", required = false)
    private BroadsoftRoomsProjectRooms mProjectRooms;
    @Nullable
    @Element(name = "conference-bridge", required = false)
    private BroadsoftRoomsConferenceBridge mConferenceBridge;

    public BroadsoftServicesRooms() {
    }

    @Nullable
    public Integer getDefaultRoomHistorySize() {
        return mDefaultRoomHistorySize;
    }

    @Nullable
    public BroadsoftRoomsMyRoom getMyRoom() {
        return mMyRoom;
    }

    @Nullable
    public BroadsoftRoomsProjectRooms getProjectRooms() {
        return mProjectRooms;
    }

    @Nullable
    public BroadsoftRoomsConferenceBridge getConferenceBridge() {
        return mConferenceBridge;
    }
}
