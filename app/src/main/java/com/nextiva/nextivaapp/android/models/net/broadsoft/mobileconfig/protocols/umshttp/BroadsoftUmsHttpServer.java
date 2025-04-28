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
public class BroadsoftUmsHttpServer implements Serializable {

    @Nullable
    @Attribute(name = "name", required = false)
    private String mName;

    public BroadsoftUmsHttpServer() {
    }

    @Nullable
    public String getName() {
        return mName;
    }
}
