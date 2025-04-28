/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftCallsNetwork implements Serializable {

    @Nullable
    @Attribute(name = "type", required = false)
    private String mType;

    public BroadsoftCallsNetwork() {
    }

    @Nullable
    public String getType() {
        return mType;
    }
}
