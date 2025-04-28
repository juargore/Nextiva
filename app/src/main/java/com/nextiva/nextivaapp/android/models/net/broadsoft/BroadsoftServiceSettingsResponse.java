/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBroadWorksAnywhereLocation;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftSimultaneousRingLocation;
import com.nextiva.nextivaapp.android.xml.converters.NilValueStringConverter;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import java.util.ArrayList;

/**
 * Created by adammacdonald on 3/16/18.
 */

@Root(strict = false)
public class BroadsoftServiceSettingsResponse {

    @Nullable
    @Element(name = "active", required = false)
    private Boolean mActive;
    @Nullable
    @Element(name = "ringSplash", required = false)
    private Boolean mRingSplash;
    @Nullable
    @Element(name = "numberOfRings", required = false)
    private Integer mNumberOfRings;
    @Nullable
    @Element(name = "remoteOfficeNumber", required = false)
    private String mRemoteOfficeNumber;
    @Nullable
    @Element(name = "forwardToPhoneNumber", required = false)
    @Convert(value = NilValueStringConverter.class)
    private String mForwardToPhoneNumber;
    @Nullable
    @Element(name = "alertAllLocationsForClickToDialCalls", required = false)
    private Boolean mAlertAllLocationsForClickToDialCalls;
    @Nullable
    @Element(name = "alertAllLocationsForGroupPagingCalls", required = false)
    private Boolean mAlertAllLocationsForGroupPagingCalls;
    @Nullable
    @ElementList(name = "locations", required = false)
    private ArrayList<BroadsoftBroadWorksAnywhereLocation> mNextivaAnywhereLocationsList;
    @Element(name = "incomingCalls", required = false)
    private String mIncomingCalls;
    @Nullable
    @ElementList(name = "simRingLocations", required = false)
    private ArrayList<BroadsoftSimultaneousRingLocation> mSimultaneousRingLocationsList;

    public BroadsoftServiceSettingsResponse() {
    }

    public BroadsoftServiceSettingsResponse(
            @Nullable Boolean active,
            @Nullable Boolean ringSplash,
            @Nullable Integer numberOfRings,
            @Nullable String remoteOfficeNumber,
            @Nullable String forwardToPhoneNumber,
            @Nullable Boolean alertAllLocationsForClickToDialCalls,
            @Nullable Boolean alertAllLocationsForGroupPagingCalls,
            @Nullable ArrayList<BroadsoftBroadWorksAnywhereLocation> nextivaAnywhereLocationsList,
            @Nullable String incomingCalls,
            @Nullable ArrayList<BroadsoftSimultaneousRingLocation> simultaneousRingLocationsList) {

        mActive = active;
        mRingSplash = ringSplash;
        mNumberOfRings = numberOfRings;
        mRemoteOfficeNumber = remoteOfficeNumber;
        mForwardToPhoneNumber = forwardToPhoneNumber;
        mAlertAllLocationsForClickToDialCalls = alertAllLocationsForClickToDialCalls;
        mAlertAllLocationsForGroupPagingCalls = alertAllLocationsForGroupPagingCalls;
        mNextivaAnywhereLocationsList = nextivaAnywhereLocationsList;
        mIncomingCalls = incomingCalls;
        mSimultaneousRingLocationsList = simultaneousRingLocationsList;
    }

    @Nullable
    public Boolean getActive() {
        return mActive;
    }

    @Nullable
    public Boolean getRingSplash() {
        return mRingSplash;
    }

    @Nullable
    public Integer getNumberOfRings() {
        return mNumberOfRings;
    }

    @Nullable
    public String getRemoteOfficeNumber() {
        return mRemoteOfficeNumber;
    }

    @Nullable
    public String getForwardToPhoneNumber() {
        return mForwardToPhoneNumber;
    }

    @Nullable
    public Boolean getAlertAllLocationsForClickToDialCalls() {
        return mAlertAllLocationsForClickToDialCalls;
    }

    @Nullable
    public Boolean getAlertAllLocationsForGroupPagingCalls() {
        return mAlertAllLocationsForGroupPagingCalls;
    }

    @Nullable
    public ArrayList<BroadsoftBroadWorksAnywhereLocation> getNextivaAnywhereLocationsList() {
        return mNextivaAnywhereLocationsList;
    }

    public String getIncomingCalls() {
        return mIncomingCalls;
    }

    @Nullable
    public ArrayList<BroadsoftSimultaneousRingLocation> getSimultaneousRingLocationsList() {
        return mSimultaneousRingLocationsList;
    }
}
