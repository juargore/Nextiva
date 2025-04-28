/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static org.mockito.ArgumentMatchers.any;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.threeten.bp.DateTimeException;
import org.threeten.bp.Instant;
import org.threeten.bp.Month;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by adammacdonald on 3/22/18.
 */

@PrepareForTest({TextUtils.class, FirebaseCrashlytics.class})
public class FormatManagerTest  {

    private static Context mContext;

    private static Calendar mCalendar;
    private static Instant mInstant;
    private static FormatterManager mFormatterManager;

    protected static final CalendarManager mCalendarManager = Mockito.mock(CalendarManager.class);

    private static final FirebaseCrashlytics mFirebaseCrashlytics = Mockito.mock(FirebaseCrashlytics.class);


    private static ZoneId zoneId = ZoneId.systemDefault();

    @BeforeClass
    public static void global() {

        mContext = Mockito.mock(Context.class);
        Mockito.when(mContext.getString(R.string.date_format_short_day_of_week_month_day_time)).thenReturn("E, MMMM d, h:mm a");
        Mockito.when(mContext.getString(R.string.date_format_full_day_of_week_hour_min_12_hour)).thenReturn("EEEE h:mm a");
        Mockito.when(mContext.getString(R.string.date_format_today_time_12_hour)).thenReturn("'Today' h:mm a");
        Mockito.when(mContext.getString(R.string.date_format_yesterday_time_12_hour)).thenReturn("'Yesterday' h:mm a");
        Mockito.when(mContext.getString(R.string.date_format_short_time_12_hour)).thenReturn("h:mm a");
        Mockito.when(mContext.getString(R.string.date_format_short_month_day_year)).thenReturn("M/dd/yy");
        Mockito.when(mContext.getString(R.string.date_format_log_zip_filename)).thenReturn("'%1$s' yyyy-MM-dd HH:mm:ss");

        MockedStatic<TextUtils> textUtils = Mockito.mockStatic(TextUtils.class);
        textUtils.when(() -> TextUtils.isEmpty(any(CharSequence.class))).thenAnswer((Answer<Boolean>) invocation -> {
            CharSequence a = (CharSequence) invocation.getArguments()[0];
            return !(a != null && a.length() > 0);
        });
        textUtils.when(() -> TextUtils.isEmpty(null)).thenAnswer((Answer<Boolean>) invocation -> true);

        MockedStatic<FirebaseCrashlytics> utilities = Mockito.mockStatic(FirebaseCrashlytics.class);
        utilities.when(FirebaseCrashlytics::getInstance).thenReturn(mFirebaseCrashlytics);

        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, 2008);
        mCalendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
        mCalendar.set(Calendar.DATE, 23);
        mCalendar.set(Calendar.HOUR_OF_DAY, 11);
        mCalendar.set(Calendar.MINUTE, 3);
        mCalendar.set(Calendar.SECOND, 25);
        mCalendar.set(Calendar.MILLISECOND, 123);
        mCalendar.setTimeZone(TimeZone.getTimeZone(zoneId.getId()));

        mInstant = Instant.from(ZonedDateTime.of(2008, Month.SEPTEMBER.getValue(), 23, 11, 3, 25, 123000000, zoneId));
        mInstant.atZone(zoneId);

        mFormatterManager = FormatterManager.getInstance();

        Locale.setDefault(Locale.US);
    }

    @Before
    public void setup() {
        Mockito.when(mCalendarManager.getNowMillis()).thenReturn(1222193005000L);
        Mockito.when(mCalendarManager.getNowInstant()).thenReturn(ZonedDateTime.of(2008, Month.SEPTEMBER.getValue(), 23, 11, 3, 25, 123000000, zoneId).toInstant());
    }

    @Test
    public void getInstance_returnsSameInstance() {
        FormatterManager formatterManager1 = FormatterManager.getInstance();
        FormatterManager formatterManager2 = FormatterManager.getInstance();

        assertEquals(formatterManager1, formatterManager2);
    }

    @Test
    public void getDateFormatter_8601BasicDate_returnsCorrectFormatter() {
        assertEquals("20080923", mFormatterManager.getDateFormatter_8601BasicDate().format(mInstant));
    }

    @Test
    public void getDateFormatter_8601BasicDatetime_returnsCorrectFormatter() {
        assertEquals("20080923110325.123", mFormatterManager.getDateFormatter_8601BasicDatetime().format(mInstant));
    }

    @Test
    public void getDateFormatter_8601BasicDatetimeTimeZone_returnsCorrectFormatter() {

        String test_zone = DateTimeFormatter.ofPattern("XXX").withZone(ZoneId.systemDefault()).format(mInstant);
        String expected = String.format("20080923110325.123%s", test_zone);

        assertEquals(expected, mFormatterManager.getDateFormatter_8601BasicDatetimeTimeZone().format(mInstant));
    }

    @Test
    public void getDateFormatter_8601ExtendedDatetimeTimeZone_returnsCorrectFormatter() {
        String test_zone = DateTimeFormatter.ofPattern("XXX").withZone(ZoneId.systemDefault()).format(mInstant);
        String expected = String.format("2008-09-23T11:03:25.123%s", test_zone);
        assertEquals(expected, mFormatterManager.getDateFormatter_8601ExtendedDatetimeTimeZone().format(mInstant));
    }

    @Test
    public void getDateFormatter_8601ExtendedDatetimeTimeZoneNoMs_returnsCorrectFormatter() {
        String testInstant = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("GMT-0")).format(mInstant);
        assertEquals(testInstant, mFormatterManager.getDateFormatter_8601ExtendedDatetimeTimeZoneNoMs().format(mInstant));
    }

    @Test
    public void getDateFormatter_EMMMMdhmma_returnsCorrectFormatter() {
        assertEquals("Tue, September 23, 11:03 AM", mFormatterManager.getDateFormatter_short_day_of_week_month_day_time(mContext).format(mInstant));
    }

    @Test
    public void getDateFormatter_Todayhmmssa_returnsCorrectFormatter() {
        assertEquals("Today 11:03 AM", mFormatterManager.getDateFormatter_Todayhmma(mContext).format(mInstant));
    }

    @Test
    public void getDateFormatter_Yesterdayhmmssa_returnsCorrectFormatter() {
        assertEquals("Yesterday 11:03 AM", mFormatterManager.getDateFormatter_Yesterdayhmma(mContext).format(mInstant));
    }

    @Test
    public void getDateFormatter_hmma_returnsCorrectFormatter() {
        assertEquals("11:03 AM", mFormatterManager.getDateFormatter_hmma(mContext).format(mInstant));
    }

    @Test
    public void getDateFormatter_Mdyy_returnsCorrectFormatter() {
        assertEquals("9/23/08", mFormatterManager.getDateFormatter_Mdyy(mContext).format(mInstant));
    }

    @Test
    public void getDateFormatter_logZipFilename_returnsCorrectFormatter() {
        assertEquals("%1$s 2008-09-23 11:03:25", mFormatterManager.getDateFormatter_logZipFilename(mContext).format(mInstant));
    }

    @Test
    public void parse8601ExtendedDatetime_withTimezone_returnsCorrectly() {
        try {
            String test_zone = DateTimeFormatter.ofPattern("XXX").withZone(ZoneId.systemDefault()).format(mInstant);
            String expected = String.format("2008-09-23T11:03:15.123%s", test_zone);
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(mFormatterManager.parse_8601ExtendedDatetime(expected), zoneId);

            assertEquals(2008, zonedDateTime.getYear());
            assertEquals(Month.SEPTEMBER, zonedDateTime.getMonth());
            assertEquals(23, zonedDateTime.getDayOfMonth());
            assertEquals(11, zonedDateTime.getHour());
            assertEquals(3, zonedDateTime.getMinute());
            assertEquals(15, zonedDateTime.getSecond());
            assertEquals(123000000, zonedDateTime.getNano());

        } catch (DateTimeException e) {
            fail();
        }
    }

    @Test
    public void parse8601ExtendedDatetime_withoutTimezone_returnsCorrectly() {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(mFormatterManager.parse_8601ExtendedDatetime("2008-09-23T11:03:15.123Z"), ZoneId.systemDefault());

            assertEquals(2008, zonedDateTime.getYear());
            assertEquals(Month.SEPTEMBER, zonedDateTime.getMonth());
            assertEquals(23, zonedDateTime.getDayOfMonth());
            assertEquals(11, zonedDateTime.getHour());
            assertEquals(3, zonedDateTime.getMinute());
            assertEquals(15, zonedDateTime.getSecond());
            assertEquals(123000000, zonedDateTime.getNano());

        } catch (DateTimeException e) {
            fail();
        }
    }

    @Test
    public void parse8601ExtendedDatetime_invalidFormat_throwsException() {
        try {
            Instant instant = mFormatterManager.parse_8601ExtendedDatetime("123");
            fail();

        } catch (DateTimeException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void formatHumanReadable_todayDate_formatsCorrectly() {
        Instant input = ZonedDateTime.of(2008, Month.SEPTEMBER.getValue(), 23, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Today 11:03 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));
    }

    @Test
    public void formatHumanReadable_yesterdayNormalDate_formatsCorrectly() {
        Instant input = ZonedDateTime.of(2008, Month.SEPTEMBER.getValue(), 22, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Yesterday 11:03 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));
    }

    @Test
    public void formatHumanReadable_yesterdayNewYearsDate_formatsCorrectly() {
        PowerMockito.when(mCalendarManager.getNowInstant()).thenAnswer((Answer<Instant>) invocation -> ZonedDateTime.of(2009, Month.JANUARY.getValue(), 1, 11, 3, 25, 123000000, zoneId).toInstant());

        Instant input = ZonedDateTime.of(2008, Month.DECEMBER.getValue(), 31, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Yesterday 11:03 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));
    }

    @Test
    public void formatHumanReadable_yesterdayNewLeapYearsDate_formatsCorrectly() {
        PowerMockito.when(mCalendarManager.getNowInstant()).thenAnswer((Answer<Instant>) invocation -> ZonedDateTime.of(2017, Month.JANUARY.getValue(), 1, 11, 3, 25, 123000000, zoneId).toInstant());

        Instant input = ZonedDateTime.of(2016, Month.DECEMBER.getValue(), 31, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Yesterday 11:03 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));
    }

    @Test
    public void formatHumanReadable_lastWeekDates_formatsCorrectly() {
        Mockito.when(mCalendarManager.getNowInstant()).thenAnswer((Answer<Instant>) invocation -> ZonedDateTime.of(2017, Month.JANUARY.getValue(), 4, 11, 3, 25, 123000000, zoneId).toInstant());

        Instant input = ZonedDateTime.of(2017, Month.JANUARY.getValue(), 2, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Monday 11:03 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));

        input = ZonedDateTime.of(2017, Month.JANUARY.getValue(), 1, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Sunday 11:03 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));

        input = ZonedDateTime.of(2016, Month.DECEMBER.getValue(), 31, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Saturday 11:03 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));

        input = ZonedDateTime.of(2016, Month.DECEMBER.getValue(), 30, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Friday 11:03 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));

        input = ZonedDateTime.of(2016, Month.DECEMBER.getValue(), 29, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Thursday 11:03 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));

        input = ZonedDateTime.of(2016, Month.DECEMBER.getValue(), 28, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Wed, December 28, 11:03 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));

        Mockito.when(mCalendarManager.getNowInstant()).thenAnswer((Answer<Instant>) invocation -> ZonedDateTime.of(2018, Month.JUNE.getValue(), 15, 10, 20, 25, 123000000, zoneId).toInstant());

        input = ZonedDateTime.of(2018, Month.JUNE.getValue(), 8, 13, 46, 50, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Fri, June 8, 1:46 PM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));

        input = ZonedDateTime.of(2018, Month.JUNE.getValue(), 8, 9, 26, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Fri, June 8, 9:26 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));
    }

    @Test
    public void formatHumanReadable_regularDate_formatsCorrectly() {
        Instant input = ZonedDateTime.of(2006, Month.DECEMBER.getValue(), 31, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("Sun, December 31, 11:03 AM", mFormatterManager.format_humanReadable(mContext, mCalendarManager, input));
    }

    @Test
    public void formatHmaOrMdyy_todayDate_formatsCorrectly() {
        Instant input = ZonedDateTime.of(2008, Month.SEPTEMBER.getValue(), 23, 11, 3, 25, 123000000, zoneId).toInstant();

        assertEquals("11:03 AM", mFormatterManager.format_HmaOrMdyy(mContext, mCalendarManager, input));
    }

    @Test
    public void formatHmaOrMdyy_regularDate_formatsCorrectly() {
        Instant input = ZonedDateTime.of(2008, Month.FEBRUARY.getValue(), 23, 11, 3, 25, 123000000, zoneId).toInstant();
        input.atZone(zoneId);

        assertEquals("2/23/08", mFormatterManager.format_HmaOrMdyy(mContext, mCalendarManager, input));
    }

    @Test
    public void swapFormats_emptyInput_returnsNull() {
        assertNull(mFormatterManager.swapFormats(
                null,
                mFormatterManager.getDateFormatter_8601BasicDatetime(),
                mFormatterManager.getDateFormatter_8601BasicDate()));
    }

    @Test
    public void swapFormats_invalidInput_returnsNull() {
        assertNull(mFormatterManager.swapFormats(
                "12345",
                mFormatterManager.getDateFormatter_8601BasicDatetime(),
                mFormatterManager.getDateFormatter_8601BasicDate()));

    }

    @Test
    public void swapFormats_validInput_returnsCorrectly() {
        assertEquals("20180125",
                     mFormatterManager.swapFormats(
                             "20180125161317.123",
                             mFormatterManager.getDateFormatter_8601BasicDatetime(),
                             mFormatterManager.getDateFormatter_8601BasicDate()));
    }

    @Test
    public void getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs_formatsNoMs() {
        String timeToTest = "2023-03-14T09:44:46Z";

        Instant instant = Instant.from(mFormatterManager.getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs(timeToTest.length()).parse(timeToTest));
        assertEquals(1678787086000L, instant.toEpochMilli());
    }

    @Test
    public void getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs_formatsOneMs() {
        String timeToTest = "2023-03-14T09:44:46.0Z";

        Instant instant = Instant.from(mFormatterManager.getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs(timeToTest.length()).parse(timeToTest));
        assertEquals(1678787086000L, instant.toEpochMilli());
    }

    @Test
    public void getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs_formatsThreeMs() {
        String timeToTest = "2023-03-14T09:44:46.000Z";

        Instant instant = Instant.from(mFormatterManager.getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs(timeToTest.length()).parse(timeToTest));
        assertEquals(1678787086000L, instant.toEpochMilli());
    }

    @Test
    public void getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs_formatsFiveMs() {
        String timeToTest = "2023-03-14T09:44:46.00000Z";

        Instant instant = Instant.from(mFormatterManager.getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs(timeToTest.length()).parse(timeToTest));
        assertEquals(1678787086000L, instant.toEpochMilli());
    }

    @Test
    public void getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs_formatsEightMs() {
        String timeToTest = "2023-03-14T09:44:46.00000000Z";

        Instant instant = Instant.from(mFormatterManager.getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs(timeToTest.length()).parse(timeToTest));
        assertEquals(1678787086000L, instant.toEpochMilli());
    }
}
