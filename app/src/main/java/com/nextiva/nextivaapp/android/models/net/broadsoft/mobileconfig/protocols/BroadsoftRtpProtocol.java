/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.rtp.BroadsoftRtpCallQualityReporting;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.rtp.BroadsoftRtpSecure;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftRtpProtocol implements Serializable {

    @Nullable
    @Element(name = "secure", required = false)
    private BroadsoftRtpSecure mSecure;
    @Nullable
    @Element(name = "call-quality-reporting", required = false)
    private BroadsoftRtpCallQualityReporting mCallQualityReporting;
    @Nullable
    @Element(name = "preferred-audio-port-start", required = false)
    private String mmPreferredAudioPortStart;
    @Nullable
    @Element(name = "preferred-audio-port-end", required = false)
    private String mmPreferredAudioPortEnd;
    @Nullable
    @Element(name = "preferred-video-port-start", required = false)
    private String mmPreferredVideoPortStart;
    @Nullable
    @Element(name = "preferred-video-port-end", required = false)
    private String mPreferredVideoPortEnd;
    @Nullable
    @Element(name = "mtu", required = false)
    private String mMtu;

    public BroadsoftRtpProtocol() {
    }

    @Nullable
    public BroadsoftRtpSecure getSecure() {
        return mSecure;
    }

    @Nullable
    public BroadsoftRtpCallQualityReporting getCallQualityReporting() {
        return mCallQualityReporting;
    }

    @Nullable
    public String getMmPreferredAudioPortStart() {
        return mmPreferredAudioPortStart;
    }

    @Nullable
    public String getMmPreferredAudioPortEnd() {
        return mmPreferredAudioPortEnd;
    }

    @Nullable
    public String getMmPreferredVideoPortStart() {
        return mmPreferredVideoPortStart;
    }

    @Nullable
    public String getPreferredVideoPortEnd() {
        return mPreferredVideoPortEnd;
    }

    @Nullable
    public String getMtu() {
        return mMtu;
    }
}
