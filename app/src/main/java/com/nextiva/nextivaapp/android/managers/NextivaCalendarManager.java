/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;

import org.threeten.bp.Instant;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by adammacdonald on 2/13/18.
 */

@Singleton
public class NextivaCalendarManager implements CalendarManager {

    @Inject
    public NextivaCalendarManager() {
    }

    // --------------------------------------------------------------------------------------------
    // CalendarManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public long getNowMillis() {
        return Instant.now().toEpochMilli();
    }

    @Override
    public Instant getNowInstant() {
        return Instant.now();
    }
    // --------------------------------------------------------------------------------------------
}
