/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
class BroadsoftCallsAdaptiveQualityLevel implements Serializable {

    @Nullable
    @Element(name = "network", required = false)
    private BroadsoftCallsNetwork mNetwork;
    @Nullable
    @ElementList(name = "video-codecs", required = false)
    private ArrayList<BroadsoftCallsVideoCodec> mVideoCodecsList;
    @Nullable
    @ElementList(name = "audio-codecs", required = false)
    private ArrayList<BroadsoftCallsAudioCodec> mAudioCodecsList;

    public BroadsoftCallsAdaptiveQualityLevel() {
    }

    @Nullable
    public BroadsoftCallsNetwork getNetwork() {
        return mNetwork;
    }

    @Nullable
    public ArrayList<BroadsoftCallsVideoCodec> getVideoCodecsList() {
        return mVideoCodecsList;
    }

    @Nullable
    public ArrayList<BroadsoftCallsAudioCodec> getAudioCodecsList() {
        return mAudioCodecsList;
    }
}
