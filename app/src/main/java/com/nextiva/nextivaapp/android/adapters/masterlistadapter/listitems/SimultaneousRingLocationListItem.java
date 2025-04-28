/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation;

public class SimultaneousRingLocationListItem extends DetailItemViewListItem {

    @NonNull
    private final SimultaneousRingLocation mLocation;

    public SimultaneousRingLocationListItem(
            @NonNull SimultaneousRingLocation location,
            @NonNull String title,
            @Nullable String subTitle) {

        super(title, subTitle, 0, 0, true, false);
        mLocation = location;
    }

    @NonNull
    public SimultaneousRingLocation getLocation() {
        return mLocation;
    }

}
