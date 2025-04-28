/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by joedephillipo on 2/14/18.
 */

@Root
public class BroadsoftProfileAdditionalDetails {

    @Nullable
    @Element(name = "emailAddress", required = false)
    private String mEmailAddress;
    @Nullable
    @Element(name = "location", required = false)
    private String mLocation;
    @Nullable
    @Element(name = "impId", required = false)
    private String mImpId;

    public BroadsoftProfileAdditionalDetails() {
    }

    public BroadsoftProfileAdditionalDetails(@Nullable String emailAddress, @Nullable String impId) {
        mEmailAddress = emailAddress;
        mImpId = impId;
    }

    @Nullable
    public String getEmailAddress() {
        return mEmailAddress;
    }

    @Nullable
    public String getLocation() {
        return mLocation;
    }

    @Nullable
    public String getImpId() {
        return mImpId;
    }
}
