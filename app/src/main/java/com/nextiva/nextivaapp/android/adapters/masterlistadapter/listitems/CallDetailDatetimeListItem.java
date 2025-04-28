/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

/**
 * Created by joedephillipo on 2/22/18.
 */

public class CallDetailDatetimeListItem extends BaseListItem {

    @Enums.Calls.CallTypes.Type
    private final String mCallType;
    @Nullable
    private final String mSubTitle;

    public CallDetailDatetimeListItem(@Enums.Calls.CallTypes.Type String callType, @Nullable String subTitle) {
        mCallType = callType;
        mSubTitle = subTitle;
    }

    public String getCallType() {
        return mCallType;
    }

    @Nullable
    public String getSubTitle() {
        return mSubTitle;
    }
}
