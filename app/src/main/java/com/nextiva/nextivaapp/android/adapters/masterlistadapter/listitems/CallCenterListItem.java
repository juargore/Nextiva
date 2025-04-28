/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.Nullable;

/**
 * Created by joedephillipo on 2/22/18.
 */

public class CallCenterListItem extends BaseListItem {

    @Nullable
    private final String mTitle;
    @Nullable
    private final String mSubTitle;
    @Nullable
    private final Boolean mIsJoined;
    @Nullable
    private final Boolean mIsCheckboxEnabled;

    public CallCenterListItem(@Nullable String title, @Nullable String subTitle, @Nullable Boolean checkBox, @Nullable Boolean isCheckboxEnabled) {
        mTitle = title;
        mSubTitle = subTitle;
        mIsJoined = checkBox;
        mIsCheckboxEnabled = isCheckboxEnabled;
    }

    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getSubTitle() {
        return mSubTitle;
    }

    @Nullable
    public Boolean getIsJoined() {
        return mIsJoined;
    }

    @Nullable
    public Boolean getCheckboxEnabled() {
        return mIsCheckboxEnabled;
    }
}
