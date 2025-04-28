/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftVoicemailProtocol implements Serializable {

    @Nullable
    @Element(name = "center-number", required = false)
    private String mVoicemailCenterNumber;

    public BroadsoftVoicemailProtocol() {
    }

    @VisibleForTesting
    public BroadsoftVoicemailProtocol(@Nullable String voicemailCenterNumber) {
        mVoicemailCenterNumber = voicemailCenterNumber;
    }

    @Nullable
    public String getVoicemailCenterNumber() {
        return mVoicemailCenterNumber;
    }
}
