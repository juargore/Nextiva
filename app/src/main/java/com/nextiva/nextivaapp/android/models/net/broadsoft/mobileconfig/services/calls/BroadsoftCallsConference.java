/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftCallsConference extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "xsi-enabled", required = false)
    private String mXsiEnabled;
    @Nullable
    @Element(name = "service-uri", required = false)
    private String mServiceUri;
    @Nullable
    @Element(name = "call-participants", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallParticipants;
    @Nullable
    @Element(name = "subscribe-conference-info", required = false)
    private BroadsoftMobileConfigGeneralSetting mSubscribeConferenceInfo;
    @Nullable
    @Element(name = "do-not-hold-conference-before-refers", required = false)
    private BroadsoftMobileConfigGeneralSetting mDoNotHoldConferenceBeforeRefers;

    public BroadsoftCallsConference() {
    }

    @Nullable
    public String getXsiEnabled() {
        return mXsiEnabled;
    }

    @Nullable
    public String getServiceUri() {
        return mServiceUri;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallParticipants() {
        return mCallParticipants;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getSubscribeConferenceInfo() {
        return mSubscribeConferenceInfo;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getDoNotHoldConferenceBeforeRefers() {
        return mDoNotHoldConferenceBeforeRefers;
    }
}
