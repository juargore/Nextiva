/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;

/**
 * Created by joedephillipo on 2/22/18.
 */

public class LocalSettingListItem extends DetailItemViewListItem {

    @SharedPreferencesManager.SettingsKey
    private final String mSettingKey;

    public LocalSettingListItem(@SharedPreferencesManager.SettingsKey String settingKey, @NonNull String title, @Nullable String subTitle) {
        super(title, subTitle, 0, 0, true, false);
        mSettingKey = settingKey;
    }

    public LocalSettingListItem(@SharedPreferencesManager.SettingsKey String settingKey, @NonNull String title, @Nullable String subTitle, @NonNull Boolean isClickable) {
        super(title, subTitle, 0, 0, isClickable, false);
        mSettingKey = settingKey;
    }

    @SharedPreferencesManager.SettingsKey
    public String getSettingKey() {
        return mSettingKey;
    }

// --Commented out by Inspection START (2019-11-18 12:26):
//    public void setSettingKey(@SharedPreferencesManager.SettingsKey String settingKey) {
//        mSettingKey = settingKey;
//    }
// --Commented out by Inspection STOP (2019-11-18 12:26)
}
