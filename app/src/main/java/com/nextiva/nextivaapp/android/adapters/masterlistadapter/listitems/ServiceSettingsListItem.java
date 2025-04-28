/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.ServiceSettings;

/**
 * Created by joedephillipo on 2/22/18.
 */

public class ServiceSettingsListItem extends DetailItemViewListItem {

    @NonNull
    private final ServiceSettings mServiceSettings;

    public ServiceSettingsListItem(@NonNull ServiceSettings serviceSettings, @NonNull String title, @Nullable String subTitle) {
        super(title, subTitle, 0, 0, true, false);
        mServiceSettings = serviceSettings;
    }

    @NonNull
    public ServiceSettings getServiceSettings() {
        return mServiceSettings;
    }

}
