/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftSipKeepalive extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "timeout-sec", required = false)
    private String mTimeoutSec;

    public BroadsoftSipKeepalive() {
    }

    @VisibleForTesting
    public BroadsoftSipKeepalive(@Nullable String timeoutSec, String enabled) {
        mTimeoutSec = timeoutSec;
        mIsEnabled = enabled;
    }


    @Nullable
    public String getTimeoutSec() {
        return mTimeoutSec;
    }
}
