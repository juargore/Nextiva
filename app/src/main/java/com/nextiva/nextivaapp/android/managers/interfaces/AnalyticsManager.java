/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.core.analytics.interfaces.AnalyticEvent;

/**
 * Created by adammacdonald on 2/28/18.
 */

public interface AnalyticsManager {

    void logScreenView(@Enums.Analytics.ScreenName.Screen @NonNull String screenName);

    void logEvent(@Enums.Analytics.ScreenName.Screen @NonNull String screenName,
                  @Enums.Analytics.EventName.Event @NonNull String event);

    void trackEvent(AnalyticEvent event);
}
