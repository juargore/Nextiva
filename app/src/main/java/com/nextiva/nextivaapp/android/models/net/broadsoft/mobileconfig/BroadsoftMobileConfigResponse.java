/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftMobileConfigResponse implements Serializable {

    @Nullable
    @Attribute(name = "version", required = false)
    private String mVersion;
    @Nullable
    @Element(name = "name", required = false)
    private String mConfigName;
    @Nullable
    @Element(name = "services", required = false)
    private BroadsoftMobileConfigServices mConfigServices;
    @Nullable
    @Element(name = "protocols", required = false)
    private BroadsoftMobileConfigProtocols mConfigProtocols;
    @Nullable
    @Element(name = "settings", required = false)
    private BroadsoftMobileConfigSettings mConfigSettings;

    public BroadsoftMobileConfigResponse() {
    }

    @VisibleForTesting
    public BroadsoftMobileConfigResponse(@Nullable BroadsoftMobileConfigProtocols configProtocols) {
        mConfigProtocols = configProtocols;
    }

    @VisibleForTesting
    public BroadsoftMobileConfigResponse(@Nullable BroadsoftMobileConfigServices configServices) {
        mConfigServices = configServices;
    }

    @Nullable
    public String getVersion() {
        return mVersion;
    }

    @Nullable
    public String getConfigName() {
        return mConfigName;
    }

    @Nullable
    public BroadsoftMobileConfigServices getConfigServices() {
        return mConfigServices;
    }

    @Nullable
    public BroadsoftMobileConfigProtocols getConfigProtocols() {
        return mConfigProtocols;
    }

    @Nullable
    public BroadsoftMobileConfigSettings getConfigSettings() {
        return mConfigSettings;
    }
}
