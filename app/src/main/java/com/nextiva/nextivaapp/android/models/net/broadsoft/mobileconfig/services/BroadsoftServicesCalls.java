/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsAdaptiveQuality;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsAudio;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsAutoRecovery;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsCellularDataVoipCalls;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsConference;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsExtendedCallControl;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsRejectWithXsi;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsTransferCall;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsVideo;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftServicesCalls implements Serializable {

    @Nullable
    @Attribute(name = "type", required = false)
    private String mType;
    @Nullable
    @Element(name = "record", required = false)
    private BroadsoftMobileConfigGeneralSetting mRecord;
    @Nullable
    @Element(name = "reject-with-486", required = false)
    private BroadsoftMobileConfigGeneralSetting mRejectWith486;
    @Nullable
    @Element(name = "reject-with-xsi", required = false)
    private BroadsoftCallsRejectWithXsi mRejectWithXsi;
    @Nullable
    @Element(name = "extended-call-control", required = false)
    private BroadsoftCallsExtendedCallControl mExtendedCallControl;
    @Nullable
    @Element(name = "transfer-call", required = false)
    private BroadsoftCallsTransferCall mTransferCall;
    @Nullable
    @Element(name = "transfer-to-circuit-switch", required = false)
    private BroadsoftMobileConfigGeneralSetting mTransferToCircuitSwitch;
    @Nullable
    @Element(name = "security-classification", required = false)
    private BroadsoftMobileConfigGeneralSetting mSecurityClassification;
    @Nullable
    @Element(name = "audio", required = false)
    private BroadsoftCallsAudio mAudio;
    @Nullable
    @Element(name = "video", required = false)
    private BroadsoftCallsVideo mVideo;
    @Nullable
    @Element(name = "conference", required = false)
    private BroadsoftCallsConference mConference;
    @Nullable
    @Element(name = "adaptive-quality", required = false)
    private BroadsoftCallsAdaptiveQuality mAdaptiveQuality;
    @Nullable
    @Element(name = "call-pull", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallPull;
    @Nullable
    @Element(name = "call-park", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallPark;
    @Nullable
    @Element(name = "voip-mode", required = false)
    private BroadsoftMobileConfigGeneralSetting mVoipMode;
    @Nullable
    @Element(name = "cellular-data-voip-calls", required = false)
    private BroadsoftCallsCellularDataVoipCalls mCellularDataVoipCalls;
    @Nullable
    @Element(name = "auto-recovery", required = false)
    private BroadsoftCallsAutoRecovery mAutoRecovery;

    public BroadsoftServicesCalls() {
    }


    @VisibleForTesting
    public BroadsoftServicesCalls(@Nullable BroadsoftCallsAudio audio, @Nullable BroadsoftCallsVideo video, String rejectWith486) {
        mAudio = audio;
        mVideo = video;

        mRejectWith486 = new BroadsoftMobileConfigGeneralSetting(rejectWith486);
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getRecord() {
        return mRecord;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getRejectWith486() {
        return mRejectWith486;
    }

    @Nullable
    public BroadsoftCallsRejectWithXsi getRejectWithXsi() {
        return mRejectWithXsi;
    }

    @Nullable
    public BroadsoftCallsExtendedCallControl getExtendedCallControl() {
        return mExtendedCallControl;
    }

    @Nullable
    public BroadsoftCallsTransferCall getTransferCall() {
        return mTransferCall;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getTransferToCircuitSwitch() {
        return mTransferToCircuitSwitch;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getSecurityClassification() {
        return mSecurityClassification;
    }

    @Nullable
    public BroadsoftCallsAudio getAudio() {
        return mAudio;
    }

    @Nullable
    public BroadsoftCallsVideo getVideo() {
        return mVideo;
    }

    @Nullable
    public BroadsoftCallsConference getConference() {
        return mConference;
    }

    @Nullable
    public BroadsoftCallsAdaptiveQuality getAdaptiveQuality() {
        return mAdaptiveQuality;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallPull() {
        return mCallPull;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallPark() {
        return mCallPark;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getVoipMode() {
        return mVoipMode;
    }

    @Nullable
    public BroadsoftCallsCellularDataVoipCalls getCellularDataVoipCalls() {
        return mCellularDataVoipCalls;
    }

    @Nullable
    public BroadsoftCallsAutoRecovery getAutoRecovery() {
        return mAutoRecovery;
    }
}
