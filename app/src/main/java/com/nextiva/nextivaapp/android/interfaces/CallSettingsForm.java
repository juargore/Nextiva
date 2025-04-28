/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.nextiva.nextivaapp.android.constants.Enums;

public interface CallSettingsForm<T> {

    T getFormCallSettings();

    void validateForm(ValidateCallSettingCallBack<T> callBack);

    void saveForm();

    void deleteForm();

    @StringRes
    int getFormTitleResId();

    @StringRes
    int getHelpTextResId();

    @NonNull
    @Enums.Analytics.ScreenName.Screen
    String getAnalyticScreenName();

    interface ValidateCallSettingCallBack<T> {
        void onSaveCallSettings(@Enums.CallSettings.FormType String formType, @NonNull T callSettings);
    }
}
