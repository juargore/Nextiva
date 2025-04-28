/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

/**
 * Created by joedephillipo on 2/22/18.
 */

public class CallDetailListItem extends DetailItemViewListItem {

    @Enums.Calls.DetailViewTypes.Type
    private final int mViewType;

    public CallDetailListItem(
            @Enums.Calls.DetailViewTypes.Type int viewType,
            @NonNull String title,
            @Nullable String subTitle,
            @DrawableRes int actionButtonOne,
            @DrawableRes int actionButtonTwo,
            boolean isLongClickable) {

        super(title, subTitle, actionButtonOne, actionButtonTwo, false, isLongClickable);
        mViewType = viewType;
    }

    @Enums.Calls.DetailViewTypes.Type
    public int getViewType() {
        return mViewType;
    }
}
