/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BaseBroadsoftBroadWorksAnywhereLocation;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by adammacdonald on 3/16/18.
 */

@Root(strict = false)
public class BroadsoftBroadWorksAnywhereLocationResponse extends BaseBroadsoftBroadWorksAnywhereLocation {

    @Nullable
    @Element(name = "description", required = false)
    private String mDescription;

    public BroadsoftBroadWorksAnywhereLocationResponse() {
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }
}
