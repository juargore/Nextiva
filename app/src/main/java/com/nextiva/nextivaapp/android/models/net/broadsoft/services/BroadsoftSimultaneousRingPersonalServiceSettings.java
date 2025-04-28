/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.xml.converters.NilValueSimultaneousRingLocationsConverter;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

/**
 * Created by adammacdonald on 3/21/18.
 */

@Root(name = "SimultaneousRingPersonal", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
public class BroadsoftSimultaneousRingPersonalServiceSettings extends BroadsoftGeneralServiceSettings {

    @Nullable
    @Element(name = "incomingCalls", required = false)
    private String mIncomingCalls;
    @Nullable
    @Element(name = "simRingLocations", required = false)
    @Convert(value = NilValueSimultaneousRingLocationsConverter.class)
    private BroadsoftSimultaneousRingPersonalLocations mSimultaneousRingLocations;

    public BroadsoftSimultaneousRingPersonalServiceSettings() {
    }

    public BroadsoftSimultaneousRingPersonalServiceSettings(
            @Nullable Boolean active,
            @Nullable String incomingCalls,
            @Nullable BroadsoftSimultaneousRingPersonalLocations simultaneousRingLocations) {

        super(active);
        mIncomingCalls = incomingCalls;
        mSimultaneousRingLocations = simultaneousRingLocations;
    }

    @Nullable
    public String getIncomingCalls() {
        return mIncomingCalls;
    }

    @Nullable
    public BroadsoftSimultaneousRingPersonalLocations getSimultaneousRingLocations() {
        return mSimultaneousRingLocations;
    }
}
