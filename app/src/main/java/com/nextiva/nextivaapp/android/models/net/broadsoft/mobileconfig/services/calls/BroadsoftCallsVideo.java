/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftCallsVideo extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "video-quality-enhancements", required = false)
    private BroadsoftCallsQualityEnhancements mVideoQualityEnhancements;
    @Nullable
    @ElementList(name = "codecs", required = false)
    private ArrayList<BroadsoftCallsVideoCodec> mVideoCodecsList;
    @Nullable
    @Element(name = "fir-sip-info", required = false)
    private BroadsoftMobileConfigGeneralSetting mFirSipInfo;

    public BroadsoftCallsVideo() {
    }

    @VisibleForTesting
    public BroadsoftCallsVideo(@Nullable ArrayList<BroadsoftCallsVideoCodec> videoCodecsList) {
        mVideoCodecsList = videoCodecsList;
    }

    @Nullable
    public BroadsoftCallsQualityEnhancements getVideoQualityEnhancements() {
        return mVideoQualityEnhancements;
    }

    @Nullable
    public ArrayList<BroadsoftCallsVideoCodec> getVideoCodecsList() {
        return mVideoCodecsList;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getFirSipInfo() {
        return mFirSipInfo;
    }
}
