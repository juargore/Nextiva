/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.listeners;

import android.content.Intent;

import androidx.annotation.Nullable;

public interface SetCallSettingsListener {

    void onFormUpdated();

    void onCallSettingsRetrieved(@Nullable Intent data);

    void onCallSettingsSaved(@Nullable Intent data);

    void onCallSettingsDeleted(@Nullable Intent data);
}
