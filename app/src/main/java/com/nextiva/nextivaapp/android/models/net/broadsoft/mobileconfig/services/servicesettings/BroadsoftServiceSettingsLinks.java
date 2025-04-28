/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.servicesettings;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftServiceSettingsLinks extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "service-settings-url", required = false)
    private String mServiceSettingsUrl;
    @Nullable
    @Element(name = "help-url", required = false)
    private String mHelpUrl;
    @Nullable
    @Element(name = "get-access-url", required = false)
    private String mGetAccessUrl;
    @Nullable
    @Element(name = "forgot-password-url", required = false)
    private String mForgotPasswordUrl;

    public BroadsoftServiceSettingsLinks() {
    }

    @Nullable
    public String getServiceSettingsUrl() {
        return mServiceSettingsUrl;
    }

    @Nullable
    public String getHelpUrl() {
        return mHelpUrl;
    }

    @Nullable
    public String getGetAccessUrl() {
        return mGetAccessUrl;
    }

    @Nullable
    public String getForgotPasswordUrl() {
        return mForgotPasswordUrl;
    }
}
