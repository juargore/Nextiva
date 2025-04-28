/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

import java.io.Serializable;

/**
 * Created by adammacdonald on 3/16/18.
 */

public class Service implements Serializable {

    @Nullable
    @Enums.Service.Type
    private String mType;
    @Nullable
    private String mUri;

    public Service(@Nullable @Enums.Service.Type String type, @Nullable String uri) {
        mType = type;
        mUri = uri;
    }

    @Nullable
    @Enums.Service.Type
    public String getType() {
        return mType;
    }

    public void setType(@Nullable @Enums.Service.Type String type) {
        mType = type;
    }

    @Nullable
    public String getUri() {
        return mUri;
    }

    public void setUri(@Nullable String uri) {
        mUri = uri;
    }
}
