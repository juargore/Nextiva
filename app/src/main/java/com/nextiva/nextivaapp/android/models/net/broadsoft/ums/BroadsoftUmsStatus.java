/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.ums;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.nextiva.nextivaapp.android.constants.Enums;

import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 3/7/18.
 */

@Root(name = "status", strict = false)
public class BroadsoftUmsStatus implements Serializable {

    @Nullable
    @SerializedName("type")
    private String mType;
    @Nullable
    @Enums.Ums.StatusCodeType
    @SerializedName("code")
    private String mCode;
    @Nullable
    @SerializedName("message")
    private String mMessage;

    public BroadsoftUmsStatus() {
    }

    @Nullable
    public String getType() {
        return mType;
    }

    @Nullable
    @Enums.Ums.StatusCodeType
    public String getCode() {
        return mCode;
    }

    @Nullable
    public String getMessage() {
        return mMessage;
    }
}
