/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.calllogs;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by adammacdonald on 2/15/18.
 */

@Root(name = "callLogsEntry", strict = false)
public class BroadsoftCallLogEntry {

    @Nullable
    @Element(name = "callLogId", required = false)
    private String mCallLogId;
    @Nullable
    @Element(name = "name", required = false)
    private String mName;
    @Nullable
    @Element(name = "time", required = false)
    private String mTime;
    @Nullable
    @Element(name = "countryCode", required = false)
    private String mCountryCode;
    @Nullable
    @Element(name = "phoneNumber", required = false)
    private String mPhoneNumber;

    public BroadsoftCallLogEntry() {
    }

    @VisibleForTesting
    public BroadsoftCallLogEntry(
            @Nullable String callLogId,
            @Nullable String name,
            @Nullable String time,
            @Nullable String countryCode,
            @Nullable String phoneNumber) {

        mCallLogId = callLogId;
        mName = name;
        mTime = time;
        mCountryCode = countryCode;
        mPhoneNumber = phoneNumber;
    }

    @Nullable
    public String getCallLogId() {
        return mCallLogId;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    @Nullable
    public String getTime() {
        return mTime;
    }

    @Nullable
    public String getCountryCode() {
        return mCountryCode;
    }

    @Nullable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }
}
