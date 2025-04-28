/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/22/18.
 */

@Root
public class BroadsoftSipTimers implements Serializable {

    @Nullable
    @Element(name = "T1", required = false)
    private String mTOneTimer;
    @Nullable
    @Element(name = "T2", required = false)
    private String mTTwoTimer;
    @Nullable
    @Element(name = "T3", required = false)
    private String mTThreeTimer;
    @Nullable
    @Element(name = "T4", required = false)
    private String mTFourTimer;

    public BroadsoftSipTimers() {
    }

    @Nullable
    public String getTOneTimer() {
        return mTOneTimer;
    }

    @Nullable
    public String getTTwoTimer() {
        return mTTwoTimer;
    }

    @Nullable
    public String getTThreeTimer() {
        return mTThreeTimer;
    }

    @Nullable
    public String getTFourTimer() {
        return mTFourTimer;
    }
}
