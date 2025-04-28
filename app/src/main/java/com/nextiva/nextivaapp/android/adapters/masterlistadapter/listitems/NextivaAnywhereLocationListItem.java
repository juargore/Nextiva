/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;

public class NextivaAnywhereLocationListItem extends DetailItemViewListItem {

    @NonNull
    private NextivaAnywhereLocation mLocation;

    public NextivaAnywhereLocationListItem(
            @NonNull NextivaAnywhereLocation location,
            @NonNull String title,
            @Nullable String subTitle) {

        super(title, subTitle, 0, 0, true, false);
        mLocation = location;
    }

    @NonNull
    public NextivaAnywhereLocation getLocation() {
        return mLocation;
    }

    public void setLocation(@NonNull NextivaAnywhereLocation location) {
        mLocation = location;
    }
}
