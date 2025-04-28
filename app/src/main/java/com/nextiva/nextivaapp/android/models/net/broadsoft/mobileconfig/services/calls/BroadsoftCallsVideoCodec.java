/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftCallsVideoCodec implements Serializable {

    @Nullable
    @Attribute(name = "framerate", required = false)
    private String mFramerate;
    @Nullable
    @Attribute(name = "payload", required = false)
    private String mPayload;
    @Nullable
    @Attribute(name = "bitrate", required = false)
    private String mBitrate;
    @Nullable
    @Attribute(name = "priority", required = false)
    private String mPriority;
    @Nullable
    @Attribute(name = "name", required = false)
    private String mName;
    @Nullable
    @Attribute(name = "resolution", required = false)
    private String mResolution;

    public BroadsoftCallsVideoCodec() {
    }


    @VisibleForTesting
    public BroadsoftCallsVideoCodec(
            @Nullable String framerate,
            @Nullable String payload,
            @Nullable String bitrate,
            @Nullable String priority,
            @Nullable String name,
            @Nullable String resolution) {
        mFramerate = framerate;
        mPayload = payload;
        mBitrate = bitrate;
        mPriority = priority;
        mName = name;
        mResolution = resolution;

    }

    @Nullable
    public String getResolution() {
        return mResolution;
    }

    @Nullable
    public String getFramerate() {
        return mFramerate;
    }

    @Nullable
    public String getBitrate() {
        return mBitrate;
    }

    @Nullable
    public String getPayload() {
        return mPayload;
    }

    @Nullable
    public String getPriority() {
        return mPriority;
    }

    @Nullable
    public String getName() {
        return mName;
    }
}
