/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.xml.converters.NilValueStringConverter;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

/**
 * Created by adammacdonald on 3/21/18.
 */

@Root(name = "CallForwardingAlways", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
public class BroadsoftCallForwardingAlwaysServiceSettings extends BroadsoftGeneralServiceSettings {

    @Nullable
    @Element(name = "ringSplash", required = false)
    private Boolean mRingSplash;
    @Nullable
    @Element(name = "forwardToPhoneNumber", required = false)
    @Convert(value = NilValueStringConverter.class)
    private String mForwardToPhoneNumber;

    public BroadsoftCallForwardingAlwaysServiceSettings() {
    }

    public BroadsoftCallForwardingAlwaysServiceSettings(@Nullable Boolean active, @Nullable Boolean ringSplash, @Nullable String forwardToPhoneNumber) {
        mActive = active;
        mRingSplash = ringSplash;
        mForwardToPhoneNumber = forwardToPhoneNumber;
    }

    @Nullable
    public Boolean getRingSplash() {
        return mRingSplash;
    }

    @Nullable
    public String getForwardToPhoneNumber() {
        return mForwardToPhoneNumber;
    }
}
