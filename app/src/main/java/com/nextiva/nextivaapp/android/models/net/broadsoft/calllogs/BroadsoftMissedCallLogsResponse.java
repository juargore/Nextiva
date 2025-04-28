/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.calllogs;

import androidx.annotation.Nullable;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by adammacdonald on 2/15/18.
 */

@Root(name = "missed", strict = false)
public class BroadsoftMissedCallLogsResponse {

    @Nullable
    @ElementList(name = "callLogsEntry", required = false, inline = true)
    private ArrayList<BroadsoftCallLogEntry> mMissedCallLogs;

    public BroadsoftMissedCallLogsResponse() {
    }

    public BroadsoftMissedCallLogsResponse(@Nullable ArrayList<BroadsoftCallLogEntry> missedCallLogs) {
        mMissedCallLogs = missedCallLogs;
    }

    @Nullable
    public ArrayList<BroadsoftCallLogEntry> getMissedCallLogs() {
        return mMissedCallLogs;
    }
}
