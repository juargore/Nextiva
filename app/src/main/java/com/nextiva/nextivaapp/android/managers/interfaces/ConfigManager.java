/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.mobileConfig.MobileConfig;

/**
 * Created by adammacdonald on 2/24/18.
 */

public interface ConfigManager {

    void clearCache();


    @Nullable
    MobileConfig getMobileConfig();

    void setMobileConfig(@Nullable MobileConfig mobileConfig);

    @SuppressWarnings("unused")
    @Nullable
    String getFullMobileConfigForTesting();

    void setFullMobileConfigForTesting(@Nullable String fullMobileConfigForTesting);

    boolean getNextivaAnywhereEnabled();

    void setNextivaAnywhereEnabled(boolean enabled);

    boolean getRemoteOfficeEnabled();

    void setRemoteOfficeEnabled(boolean enabled);


}
