/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.rtp;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftRtpCallQualityReporting implements Serializable {

    @Nullable
    @Attribute(name = "audio-enabled", required = false)
    private String mAudioEnabled;
    @Nullable
    @Attribute(name = "video-enabled", required = false)
    private String mVideoEnabled;
    @Nullable
    @Element(name = "service-uri", required = false)
    private String mServiceUri;
    @Nullable
    @Element(name = "local-group", required = false)
    private String mLocalGroup;

    public BroadsoftRtpCallQualityReporting() {
    }

    @Nullable
    public String getAudioEnabled() {
        return mAudioEnabled;
    }

    @Nullable
    public String getVideoEnabled() {
        return mVideoEnabled;
    }

    @Nullable
    public String getServiceUri() {
        return mServiceUri;
    }

    @Nullable
    public String getLocalGroup() {
        return mLocalGroup;
    }
}
