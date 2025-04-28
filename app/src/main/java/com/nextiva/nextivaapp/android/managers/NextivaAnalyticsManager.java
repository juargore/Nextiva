/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.core.analytics.interfaces.AnalyticEvent;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import sdk.pendo.io.Pendo;

/**
 * Created by Thaddeus Dannar on 2/5/18.
 */

@Singleton
public class NextivaAnalyticsManager implements AnalyticsManager {

    private final FirebaseAnalytics mFirebaseAnalytics;

    private final Application mApplication;
    private final LogManager mLogManager;


    @Inject
    public NextivaAnalyticsManager(Application application, LogManager logManager) {
        mApplication = application;
        mLogManager = logManager;
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(application);


    }

    // --------------------------------------------------------------------------------------------
    // AnalyticsManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void logScreenView(@Enums.Analytics.ScreenName.Screen @NonNull String screenName) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, mApplication.getString(R.string.log_message_analytic_screen_view, screenName));

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Enums.Analytics.EventName.SCREEN_VIEW);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    @Override
    public void logEvent(@NonNull String screenName, @NonNull String event) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, mApplication.getString(R.string.log_message_analytic_event, screenName, event));

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, event);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    @Override
    public void trackEvent(AnalyticEvent event) {
        Pendo.track(event.getName(), event.getProperties());
    }
    // --------------------------------------------------------------------------------------------
}
