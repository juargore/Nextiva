/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;

/**
 * Created by adammacdonald on 3/21/18.
 */

@Element(name = "simRingLocations")
public class BroadsoftSimultaneousRingPersonalLocations {

    @Nullable
    @ElementList(name = "simRingLocations", required = false)
    private ArrayList<BroadsoftSimultaneousRingLocation> mSimultaneousRingLocations;

    public BroadsoftSimultaneousRingPersonalLocations() {
    }

    public BroadsoftSimultaneousRingPersonalLocations(@Nullable ArrayList<BroadsoftSimultaneousRingLocation> simultaneousRingLocations) {
        mSimultaneousRingLocations = simultaneousRingLocations;
    }

    @Nullable
    public ArrayList<BroadsoftSimultaneousRingLocation> getSimultaneousRingLocations() {
        return mSimultaneousRingLocations;
    }

    public void setSimultaneousRingLocations(@Nullable ArrayList<BroadsoftSimultaneousRingLocation> simultaneousRingLocations) {
        mSimultaneousRingLocations = simultaneousRingLocations;
    }
}
