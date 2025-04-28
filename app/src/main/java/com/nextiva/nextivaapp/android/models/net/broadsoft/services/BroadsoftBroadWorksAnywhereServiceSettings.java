/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by adammacdonald on 3/21/18.
 */

@Root(name = "BroadWorksAnywhere", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
public class BroadsoftBroadWorksAnywhereServiceSettings extends BroadsoftBaseServiceSettings {

    @Nullable
    @Element(name = "alertAllLocationsForClickToDialCalls", required = false)
    private Boolean mAlertForClickToDialCalls;
    @Nullable
    @Element(name = "alertAllLocationsForGroupPagingCalls", required = false)
    private Boolean mAlertForGroupPagingCalls;
    @Nullable
    @ElementList(name = "locations", required = false)
    private ArrayList<BroadsoftBroadWorksAnywhereLocation> mBroadsoftBroadWorksAnywhereLocationsList;

    public BroadsoftBroadWorksAnywhereServiceSettings() {
    }

    public BroadsoftBroadWorksAnywhereServiceSettings(@Nullable Boolean alertForClickToDialCalls,
                                                      @Nullable Boolean alertForGroupPagingCalls,
                                                      @Nullable ArrayList<BroadsoftBroadWorksAnywhereLocation> broadsoftBroadWorksAnywhereLocationsList) {

        mAlertForClickToDialCalls = alertForClickToDialCalls;
        mAlertForGroupPagingCalls = alertForGroupPagingCalls;
        mBroadsoftBroadWorksAnywhereLocationsList = broadsoftBroadWorksAnywhereLocationsList;
    }

    @Nullable
    public Boolean getAlertForClickToDialCalls() {
        return mAlertForClickToDialCalls;
    }

    @Nullable
    public Boolean getAlertForGroupPagingCalls() {
        return mAlertForGroupPagingCalls;
    }

    @Nullable
    public ArrayList<BroadsoftBroadWorksAnywhereLocation> getBroadsoftBroadWorksAnywhereLocationsList() {
        return mBroadsoftBroadWorksAnywhereLocationsList;
    }
}
