/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import com.nextiva.nextivaapp.android.BasePowerMockTest;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.temporal.ChronoField;

import java.io.IOException;

/**
 * Created by adammacdonald on 3/23/18.
 */

@RunWith(PowerMockRunner.class)
public class NextivaCalendarManagerTest extends BasePowerMockTest {

    private CalendarManager mCalendarManager;

    @Override
    public void setup() throws IOException {
        super.setup();
        mCalendarManager = new NextivaCalendarManager();
    }

    @Test
    public void getNowMillis_returnsCorrectValue() {
        long nowMillis = Instant.now().toEpochMilli();
        long testMillis = mCalendarManager.getNowMillis();

        assertTrue(testMillis >= nowMillis - 999);
    }

    @Test
    public void getNowInstant_returnsCorrectValue() {
        Instant now = Instant.now();
        Instant test = mCalendarManager.getNowInstant();

        LocalDateTime nowLocalDateTime = LocalDateTime.ofInstant(now, ZoneId.of("GMT"));
        LocalDateTime testLocalDateTime = LocalDateTime.ofInstant(test, ZoneId.of("GMT"));

        assertEquals(nowLocalDateTime.get(ChronoField.YEAR), testLocalDateTime.get(ChronoField.YEAR));
        assertEquals(nowLocalDateTime.get(ChronoField.MONTH_OF_YEAR), testLocalDateTime.get(ChronoField.MONTH_OF_YEAR));
        assertEquals(nowLocalDateTime.get(ChronoField.DAY_OF_MONTH), testLocalDateTime.get(ChronoField.DAY_OF_MONTH));
        assertEquals(nowLocalDateTime.get(ChronoField.HOUR_OF_DAY), testLocalDateTime.get(ChronoField.HOUR_OF_DAY));
        assertEquals(nowLocalDateTime.get(ChronoField.MINUTE_OF_HOUR), testLocalDateTime.get(ChronoField.MINUTE_OF_HOUR));
        assertEquals(nowLocalDateTime.get(ChronoField.SECOND_OF_MINUTE), testLocalDateTime.get(ChronoField.SECOND_OF_MINUTE));
    }
}
