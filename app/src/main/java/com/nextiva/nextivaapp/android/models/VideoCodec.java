/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;


/**
 * Created by Thaddeus Dannar on 9/7/18.
 */

public class VideoCodec {

    @Nullable
    private String mFramerate;
    @Nullable
    private String mPayload;
    @Nullable
    private String mBitrate;
    @Nullable
    private String mPriority;
    @Nullable
    private String mName;
    @Nullable
    private String mResolution;

    public VideoCodec() {
    }

    @VisibleForTesting
    public VideoCodec(@Nullable String framerate,
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

    public void setFramerate(@Nullable final String framerate) {
        mFramerate = framerate;
    }

    public void setPayload(@Nullable final String payload) {
        mPayload = payload;
    }

    public void setBitrate(@Nullable final String bitrate) {
        mBitrate = bitrate;
    }

    public void setPriority(@Nullable final String priority) {
        mPriority = priority;
    }

    public void setName(@Nullable final String name) {
        mName = name;
    }

    public void setResolution(@Nullable final String resolution) {
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
