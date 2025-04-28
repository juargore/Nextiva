/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.ums;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.nextiva.nextivaapp.android.constants.Enums;

/**
 * Created by adammacdonald on 3/7/18.
 */

public class BroadsoftUmsBaseResponse {

    @SerializedName("status")
    private BroadsoftUmsStatus mStatus;

    public BroadsoftUmsBaseResponse() {
    }

    public BroadsoftUmsStatus getStatus() {
        return mStatus;
    }

    public boolean isSuccessful() {
        return mStatus != null &&
                (TextUtils.equals(mStatus.getCode(), Enums.Ums.SUCCESS_GET) ||
                        TextUtils.equals(mStatus.getCode(), Enums.Ums.SUCCESS_GET_NO_RESULT) ||
                        TextUtils.equals(mStatus.getCode(), Enums.Ums.SUCCESS_PUT) ||
                        TextUtils.equals(mStatus.getCode(), Enums.Ums.SUCCESS_DELETE));
    }
}
