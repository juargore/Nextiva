/*
 * Copyright (c) 2024. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.mocks;

import com.nextiva.nextivaapp.android.di.modules.TestNetModule;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Created by Thaddeus Dannar on 6/12/24.
 */
public class HiltTestNextivaApplication extends TestNextivaApplication {
    private MockWebServer mMockWebServer;

    @Override
    public void onCreate() {
        super.onCreate();
        TestNetModule testNetModule = new TestNetModule();
        mMockWebServer = testNetModule.getMockWebServer();
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
