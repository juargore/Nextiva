/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.models.mobileConfig.MobileConfig;
import com.nextiva.nextivaapp.android.util.GsonUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by adammacdonald on 2/24/18.
 */

@Singleton
public class NextivaConfigManager implements ConfigManager {

    private final DbManager mDbManager;
    private MobileConfig mMobileConfig;

    @Inject
    public NextivaConfigManager(DbManager dbManager) {
        mDbManager = dbManager;
    }

    // --------------------------------------------------------------------------------------------
    // ConfigManager Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public void clearCache() {
        clearSipCache();
    }

    private void clearSipCache() {
        setNextivaAnywhereEnabled(false);
        setRemoteOfficeEnabled(false);
    }

    @Nullable
    @Override
    public MobileConfig getMobileConfig() {
        if(mMobileConfig == null)
            mMobileConfig = GsonUtil.getObject(MobileConfig.class, mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.MOBILE_CONFIG));

        return mMobileConfig;
    }

    @Override
    public void setMobileConfig(@Nullable MobileConfig mobileConfig) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.MOBILE_CONFIG, GsonUtil.getJSON(mobileConfig));
        mMobileConfig = mobileConfig;
    }


    @Nullable
    public String getFullMobileConfigForTesting() {
        return mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.COPY_OF_MOBILE_CONFIG_FOR_TESTING);
    }

    @Override
    public void setFullMobileConfigForTesting(@Nullable String fullMobileConfigForTesting) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.COPY_OF_MOBILE_CONFIG_FOR_TESTING, GsonUtil.getJSON(fullMobileConfigForTesting));
    }

    @Override
    public boolean getNextivaAnywhereEnabled() {
        try {
            return Integer.parseInt(mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.NEXTIVA_ANYWHERE_ENABLED)) == 1;
        } catch (NumberFormatException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            return false;
        }
    }

    @Override
    public void setNextivaAnywhereEnabled(boolean enabled) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.NEXTIVA_ANYWHERE_ENABLED, enabled ? "1" : "0");
    }

    @Override
    public boolean getRemoteOfficeEnabled() {
        try {
            return Integer.parseInt(mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.REMOTE_OFFICE_ENABLED)) == 1;
        } catch (NumberFormatException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            return false;
        }
    }

    @Override
    public void setRemoteOfficeEnabled(boolean enabled) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.REMOTE_OFFICE_ENABLED, enabled ? "1" : "0");
    }
    // --------------------------------------------------------------------------------------------
}
