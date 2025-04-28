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
public class BroadsoftCallsRejectWithXsi implements Serializable {

    @Nullable
    @Attribute(name = "mode", required = false)
    private String mMode;
    @Nullable
    @Attribute(name = "declineReason", required = false)
    private String mDeclineReason;

    public BroadsoftCallsRejectWithXsi() {
    }

    @Nullable
    public String getMode() {
        return mMode;
    }

    @Nullable
    public String getDeclineReason() {
        return mDeclineReason;
    }
}