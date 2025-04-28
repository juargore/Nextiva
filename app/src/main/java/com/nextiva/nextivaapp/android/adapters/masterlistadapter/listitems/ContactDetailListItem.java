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

public class ContactDetailListItem extends DetailItemViewListItem {

    @Enums.Contacts.DetailViewTypes.Type
    private final int mViewType;

    public ContactDetailListItem(
            @Enums.Contacts.DetailViewTypes.Type int viewType,
            @NonNull String title,
            @Nullable String subTitle,
            @DrawableRes int actionButtonOne,
            @DrawableRes int actionButtonTwo,
            boolean isLongClickable) {

        super(title, subTitle, actionButtonOne, actionButtonTwo, false, isLongClickable);
        mViewType = viewType;
    }

    @Enums.Contacts.DetailViewTypes.Type
    public int getViewType() {
        return mViewType;
    }

// --Commented out by Inspection START (2019-11-18 12:26):
//    public void setViewType(@Enums.Contacts.DetailViewTypes.Type int viewType) {
//        mViewType = viewType;
//    }
// --Commented out by Inspection STOP (2019-11-18 12:26)
}
