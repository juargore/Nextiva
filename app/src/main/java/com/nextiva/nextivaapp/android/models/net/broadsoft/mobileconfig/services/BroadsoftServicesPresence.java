/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftServicesPresence implements Serializable {

    @Nullable
    @Element(name = "presence-rules", required = false)
    private BroadsoftMobileConfigGeneralSetting mPresenceRules;
    @Nullable
    @Element(name = "server-presence-aggregation", required = false)
    private BroadsoftMobileConfigGeneralSetting mServerPresenceAggregation;

    public BroadsoftServicesPresence() {
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getPresenceRules() {
        return mPresenceRules;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getServerPresenceAggregation() {
        return mServerPresenceAggregation;
    }
}
