/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigExternalLinkSetting;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigLocalAddressBookSearchSetting;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigLoginInfoDialogSearchSetting;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigSilentCallVibrationSearchSetting;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigTabsList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftMobileConfigSettings implements Serializable {

    @Nullable
    @Element(name = "local-address-book-search", required = false)
    private BroadsoftMobileConfigLocalAddressBookSearchSetting mLocalAddressBookSearch;
    @Nullable
    @Element(name = "show-welcome-dialog", required = false)
    private BroadsoftMobileConfigGeneralSetting mShowWelcomeDialog;
    @Nullable
    @Element(name = "login-informational-dialog", required = false)
    private BroadsoftMobileConfigLoginInfoDialogSearchSetting mLoginInformationalDialog;
    @Nullable
    @Element(name = "silent-call-vibration", required = false)
    private BroadsoftMobileConfigSilentCallVibrationSearchSetting mSilentCallVibration;
    @Nullable
    @Element(name = "dialing-service-callback-validation", required = false)
    private BroadsoftMobileConfigGeneralSetting mDialingServiceCallbackValidation;
    @Nullable
    @Element(name = "dialing-service-mobility-location-override", required = false)
    private BroadsoftMobileConfigGeneralSetting mDialingServiceMobilityLocationOverride;
    @Nullable
    @Element(name = "tabs", required = false)
    private BroadsoftMobileConfigTabsList mTabsList;
    @Nullable
    @Element(name = "external-link", required = false)
    private BroadsoftMobileConfigExternalLinkSetting mExternalLink;

    public BroadsoftMobileConfigSettings() {
    }

    @Nullable
    public BroadsoftMobileConfigLocalAddressBookSearchSetting getLocalAddressBookSearch() {
        return mLocalAddressBookSearch;
    }

    public void setLocalAddressBookSearch(@Nullable BroadsoftMobileConfigLocalAddressBookSearchSetting localAddressBookSearch) {
        mLocalAddressBookSearch = localAddressBookSearch;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getShowWelcomeDialog() {
        return mShowWelcomeDialog;
    }

    public void setShowWelcomeDialog(@Nullable BroadsoftMobileConfigGeneralSetting showWelcomeDialog) {
        mShowWelcomeDialog = showWelcomeDialog;
    }

    @Nullable
    public BroadsoftMobileConfigLoginInfoDialogSearchSetting getLoginInformationalDialog() {
        return mLoginInformationalDialog;
    }

    public void setLoginInformationalDialog(@Nullable BroadsoftMobileConfigLoginInfoDialogSearchSetting loginInformationalDialog) {
        mLoginInformationalDialog = loginInformationalDialog;
    }

    @Nullable
    public BroadsoftMobileConfigSilentCallVibrationSearchSetting getSilentCallVibration() {
        return mSilentCallVibration;
    }

    public void setSilentCallVibration(@Nullable BroadsoftMobileConfigSilentCallVibrationSearchSetting silentCallVibration) {
        mSilentCallVibration = silentCallVibration;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getDialingServiceCallbackValidation() {
        return mDialingServiceCallbackValidation;
    }

    public void setDialingServiceCallbackValidation(@Nullable BroadsoftMobileConfigGeneralSetting dialingServiceCallbackValidation) {
        mDialingServiceCallbackValidation = dialingServiceCallbackValidation;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getDialingServiceMobilityLocationOverride() {
        return mDialingServiceMobilityLocationOverride;
    }

    public void setDialingServiceMobilityLocationOverride(@Nullable BroadsoftMobileConfigGeneralSetting dialingServiceMobilityLocationOverride) {
        mDialingServiceMobilityLocationOverride = dialingServiceMobilityLocationOverride;
    }

    @Nullable
    public BroadsoftMobileConfigTabsList getTabsList() {
        return mTabsList;
    }

    public void setTabsList(@Nullable BroadsoftMobileConfigTabsList tabsList) {
        mTabsList = tabsList;
    }

    @Nullable
    public BroadsoftMobileConfigExternalLinkSetting getExternalLink() {
        return mExternalLink;
    }

    public void setExternalLink(@Nullable BroadsoftMobileConfigExternalLinkSetting externalLink) {
        mExternalLink = externalLink;
    }
}
