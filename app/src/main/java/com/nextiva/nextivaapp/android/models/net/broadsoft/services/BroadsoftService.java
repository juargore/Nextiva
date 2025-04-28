/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by adammacdonald on 3/16/18.
 */

@Root(name = "service", strict = false)
public class BroadsoftService {

    @Nullable
    @Enums.Service.Type
    @Element(name = "name", required = false)
    private String mName;
    @Nullable
    @Element(name = "uri", required = false)
    private String mUri;

    public BroadsoftService() {
    }

    public BroadsoftService(@Nullable String name, @Nullable String uri) {
        mName = name;
        mUri = uri;
    }

    @Nullable
    @Enums.Service.Type
    public String getName() {
        return mName;
    }

    @Nullable
    public String getUri() {
        return mUri;
    }
}
