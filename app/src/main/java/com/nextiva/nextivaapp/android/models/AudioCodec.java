/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by Thaddeus Dannar on 9/7/18.
 */

public class AudioCodec implements Serializable {

    @Nullable
    private String mName;
    @Nullable
    private String mPriority;
    @Nullable
    private String mPayload;
    @Nullable
    private String mInBand;

    public AudioCodec() {
    }

    public AudioCodec(@Nullable String name) {
        mName = name;
    }

    public AudioCodec(
            @Nullable String name,
            @Nullable String priority,
            @Nullable String payload,
            @Nullable String inBand) {
        mName = name;
        mPriority = priority;
        mPayload = payload;
        mInBand = inBand;
    }

    public void setName(@Nullable final String name) {
        mName = name;
    }

    public void setPriority(@Nullable final String priority) {
        mPriority = priority;
    }

    public void setPayload(@Nullable final String payload) {
        mPayload = payload;
    }

    public void setInBand(@Nullable final String inBand) {
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
