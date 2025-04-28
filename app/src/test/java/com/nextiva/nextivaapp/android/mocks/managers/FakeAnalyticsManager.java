/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.mocks.managers;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.core.analytics.interfaces.AnalyticEvent;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;

import javax.inject.Inject;

public class FakeAnalyticsManager implements AnalyticsManager {

    @Inject
    public FakeAnalyticsManager() {
    }

    @Override
    public void logScreenView(@NonNull String screenName) {

    }

    @Override
    public void logEvent(@NonNull String screenName, @NonNull String event) {

    }

    @Override
    public void trackEvent(AnalyticEvent event) {

    }
}
