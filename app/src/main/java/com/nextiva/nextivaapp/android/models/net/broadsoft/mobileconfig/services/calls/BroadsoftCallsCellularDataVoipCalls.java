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
public class BroadsoftCallsCellularDataVoipCalls implements Serializable {

    @Nullable
    @Attribute(name = "state", required = false)
    private String mState;

    public BroadsoftCallsCellularDataVoipCalls() {
    }

    @Nullable
    public String getState() {
        return mState;
    }
}
