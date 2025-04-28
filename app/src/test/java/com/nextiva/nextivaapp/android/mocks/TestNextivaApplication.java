/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.mocks;

import com.nextiva.nextivaapp.android.NextivaApplication;
import com.nextiva.nextivaapp.android.TestNextivaComponent;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import java.io.IOException;


public class TestNextivaApplication extends NextivaApplication {
    private MockWebServer mMockWebServer;
    private TestNextivaComponent nextivaComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mMockWebServer = new MockWebServer();
        try {
            mMockWebServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setupCrashlytics() {
        // Do nothing
    }

    @Override
    protected void setupAndroidThreeTen() {
        // Do nothing
    }

    @Override
    protected void setupPendo() {
        // Do nothing
    }

    public MockWebServer getMockWebServer() {
        return mMockWebServer;
    }
}