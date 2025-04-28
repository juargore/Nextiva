/*
 * Copyright (c) 2024. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.mocks;

import com.nextiva.nextivaapp.android.NextivaApplication;
import com.nextiva.nextivaapp.android.di.modules.TestNetModule;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Created by Thaddeus Dannar on 6/13/24.
 */
public class CustomTestNextivaApplication extends NextivaApplication {
    private MockWebServer mMockWebServer;

    @Override
    public void onCreate() {
        super.onCreate();
        TestNetModule testNetModule = new TestNetModule();
        mMockWebServer = testNetModule.getMockWebServer();
    }

    public MockWebServer getMockWebServer() {
        return mMockWebServer;
    }
}
