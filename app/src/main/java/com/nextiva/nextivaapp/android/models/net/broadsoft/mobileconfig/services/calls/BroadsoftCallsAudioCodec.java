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
public class BroadsoftCallsAudioCodec implements Serializable {

    @Nullable
    @Attribute(name = "name", required = false)
    private String mName;
    @Nullable
    @Attribute(name = "priority", required = false)
    private String mPriority;
    @Nullable
    @Attribute(name = "payload", required = false)
    private String mPayload;
    @Nullable
    @Attribute(name = "in-band", required = false)
    private String mInBand;

    public BroadsoftCallsAudioCodec() {
    }

    @VisibleForTesting
    public BroadsoftCallsAudioCodec(
            @Nullable String payload,
            @Nullable String priority,
            @Nullable String name,
            @Nullable String inBand) {
        mPayload = payload;
        mPriority = priority;
        mName = name;
        mInBand = inBand;
    }


    @Nullable
    public String getName() {
        return mName;
    }

    @Nullable
    public String getPriority() {
        return mPriority;
    }

    @Nullable
    public String getPayload() {
        return mPayload;
    }

    @Nullable
    public String getInBand() {
        return mInBand;
    }
}
