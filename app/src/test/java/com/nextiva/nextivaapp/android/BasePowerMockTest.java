/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android;

import androidx.annotation.CallSuper;

import com.nextiva.nextivaapp.android.di.modules.TestNetModule;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

/**
 * Created by adammacdonald on 3/23/18.
 */

@RunWith(PowerMockRunner.class)
public abstract class BasePowerMockTest {

    protected MockWebServer mMockWebServer;

    @Before
    @CallSuper
    public void setup() throws IOException {
        TestNetModule testNetModule = new TestNetModule();
        mMockWebServer = testNetModule.getMockWebServer();
    }
}
