/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import org.threeten.bp.Instant;

/**
 * Created by adammacdonald on 2/13/18.
 */

public interface CalendarManager {

    long getNowMillis();

    Instant getNowInstant();
}
