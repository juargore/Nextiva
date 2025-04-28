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

@Root(name = "CallLogs", strict = false)
public class BroadsoftAllCallLogsResponse {

    @Nullable
    @ElementList(name = "placed", required = false)
    private ArrayList<BroadsoftCallLogEntry> mPlacedCallLogs;
    @Nullable
    @ElementList(name = "missed", required = false)
    private ArrayList<BroadsoftCallLogEntry> mMissedCallLogs;
    @Nullable
    @ElementList(name = "received", required = false)
    private ArrayList<BroadsoftCallLogEntry> mReceivedCallLogs;

    public BroadsoftAllCallLogsResponse() {
    }

    public BroadsoftAllCallLogsResponse(
            @Nullable ArrayList<BroadsoftCallLogEntry> placedCallLogs,
            @Nullable ArrayList<BroadsoftCallLogEntry> missedCallLogs,
            @Nullable ArrayList<BroadsoftCallLogEntry> receivedCallLogs) {

        mPlacedCallLogs = placedCallLogs;
        mMissedCallLogs = missedCallLogs;
        mReceivedCallLogs = receivedCallLogs;
    }

    @Nullable
    public ArrayList<BroadsoftCallLogEntry> getPlacedCallLogs() {
        return mPlacedCallLogs;
    }

    @Nullable
    public ArrayList<BroadsoftCallLogEntry> getMissedCallLogs() {
        return mMissedCallLogs;
    }

    @Nullable
    public ArrayList<BroadsoftCallLogEntry> getReceivedCallLogs() {
        return mReceivedCallLogs;
    }
}
