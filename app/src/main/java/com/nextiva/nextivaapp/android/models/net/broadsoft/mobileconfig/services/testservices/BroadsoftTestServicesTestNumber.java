/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.testservices;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root(name = "test-number", strict = false)
class BroadsoftTestServicesTestNumber implements Serializable {

    @Nullable
    @Attribute(name = "language", required = false)
    private String mLanguage;
    @Nullable
    @Attribute(name = "number", required = false)
    private String mNumber;

    public BroadsoftTestServicesTestNumber() {
    }

    @Nullable
    public String getLanguage() {
        return mLanguage;
    }

    @Nullable
    public String getNumber() {
        return mNumber;
    }
}
