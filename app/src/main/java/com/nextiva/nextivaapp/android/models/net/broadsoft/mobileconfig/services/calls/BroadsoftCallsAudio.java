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
public class BroadsoftCallsAudio extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "audio-quality-enhancements", required = false)
    private BroadsoftCallsQualityEnhancements mAudioQualityEnhancements;
    @Nullable
    @ElementList(name = "codecs", required = false)
    private ArrayList<BroadsoftCallsAudioCodec> mAudioCodecsList;

    public BroadsoftCallsAudio() {
    }

    @VisibleForTesting
    public BroadsoftCallsAudio(@Nullable ArrayList<BroadsoftCallsAudioCodec> audioCodecsList) {
        mAudioCodecsList = audioCodecsList;
    }

    @Nullable
    public BroadsoftCallsQualityEnhancements getAudioQualityEnhancements() {
        return mAudioQualityEnhancements;
    }

    @Nullable
    public ArrayList<BroadsoftCallsAudioCodec> getAudioCodecsList() {
        return mAudioCodecsList;
    }
}
