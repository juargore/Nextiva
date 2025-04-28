/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.umshttp;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/22/18.
 */

@Root
public class BroadsoftUmsHttpDomain implements Serializable {

    @Nullable
    @Attribute(name = "address", required = false)
    private String mAddress;

    public BroadsoftUmsHttpDomain() {
    }

    @Nullable
    public String getAddress() {
        return mAddress;
    }
}
