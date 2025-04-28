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

public class CallDetailPhoneNumberListItem extends CallDetailListItem {

    public CallDetailPhoneNumberListItem(
            @NonNull String title,
            @Nullable String subTitle,
            @DrawableRes int actionButtonOne,
            @DrawableRes int actionButtonTwo) {

        super(Enums.Calls.DetailViewTypes.PHONE_NUMBER, title, subTitle, actionButtonOne, actionButtonTwo, true);
    }
}
