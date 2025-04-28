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

@Root(name = "CallForwardingNoAnswer", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
public class BroadsoftCallForwardingNoAnswerServiceSettings extends BroadsoftGeneralServiceSettings {

    @Nullable
    @Element(name = "numberOfRings", required = false)
    private Integer mNumberOfRings;
    @Nullable
    @Element(name = "forwardToPhoneNumber", required = false)
    @Convert(value = NilValueStringConverter.class)
    private String mForwardToPhoneNumber;

    public BroadsoftCallForwardingNoAnswerServiceSettings() {
    }

    public BroadsoftCallForwardingNoAnswerServiceSettings(@Nullable Boolean active, @Nullable Integer numberOfRings, @Nullable String forwardToPhoneNumber) {
        super(active);
        mNumberOfRings = numberOfRings;
        mForwardToPhoneNumber = forwardToPhoneNumber;
    }

    @Nullable
    public Integer getNumberOfRings() {
        return mNumberOfRings;
    }

    @Nullable
    public String getForwardToPhoneNumber() {
        return mForwardToPhoneNumber;
    }
}
