/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.rtp;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftRtpSecure extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "mode", required = false)
    private String mMode;
    @Nullable
    @Attribute(name = "rekey-always", required = false)
    private String mRekeyAlways;

    public BroadsoftRtpSecure() {
    }

    @Nullable
    public String getMode() {
        return mMode;
    }

    @Nullable
    public String getRekeyAlways() {
        return mRekeyAlways;
    }
}
