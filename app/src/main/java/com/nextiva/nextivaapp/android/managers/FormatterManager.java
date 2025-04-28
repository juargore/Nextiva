/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import static java.lang.Math.abs;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import kotlin.time.Duration;

/**
 * Created by adammacdonald on 2/9/18.
 */

public class FormatterManager {

    private static DateTimeFormatter sDateFormatter_8601BasicDate;
    private static DateTimeFormatter sDateFormatter_8601BasicDatetime;
    private static DateTimeFormatter sDateFormatter_8601BasicDatetimeTimeZone;
    private static DateTimeFormatter sDateFormatter_8601ExtendedDatetime;
    private static DateTimeFormatter sDateFormatter_8601ExtendedDatetimeTimeZone;
    private static DateTimeFormatter sDateFormatter_8601ExtendedDatetimeTimeZone_v1;
    private static DateTimeFormatter sDateFormatter_8601ExtendedDatetimeTimeZone_v2;
    private static DateTimeFormatter sDateFormatter_8601ExtendedDatetimeTimeZoneTwoMs;
    private static DateTimeFormatter sDateFormatter_8601ExtendedDatetimeTimeZoneThreeMs;
    private static DateTimeFormatter sDateFormatter_8601ExtendedDatetimeTimeZoneNineMs;
    private static DateTimeFormatter sDateFormatter_8601ExtendedDatetimeTimeZoneNoMs;
    private static DateTimeFormatter sDateFormatter_8601ExtendedDatetimeTimeZoneSimple;
    private static DateTimeFormatter sDateFormatter_EMMMMhmma;
    private static DateTimeFormatter sDateFormatter_EMMMMdyyyyhmma;
    private static DateTimeFormatter sDateFormatter_EMMMM;
    private static DateTimeFormatter sDateFormatter_EEEE;
    private static DateTimeFormatter sDateFormatter_EEEEhmma;
    private static DateTimeFormatter sDateFormatter_Todayhmma;
    private static DateTimeFormatter sDateFormatter_Yesterdayhmma;
    private static DateTimeFormatter sDateFormatter_hmma;
    private static DateTimeFormatter sDateFormatter_MMdd;
    private static DateTimeFormatter sDateFormatter_MMMdd;
    private static DateTimeFormatter sDateFormatter_MMMddyyyy;
    private static DateTimeFormatter sDateFormatter_Mdyy;
    private static DateTimeFormatter sDateFormatter_ddMMMyyyy;
    private static DateTimeFormatter sDateFormatter_logZipFilename;

    private static FormatterManager sInstance;

    private FormatterManager() {
    }

    public static FormatterManager getInstance() {
        if (sInstance == null) {
            sInstance = new FormatterManager();
        }

        return sInstance;
    }

    /**
     * Gets the DateTimeFormatter in the ISO 8601 - Basic Date format (yyyyMMdd)
     *
     * @return The formatter with a pattern of yyyyMMdd
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 Wiki</a>
     */
    public DateTimeFormatter getDateFormatter_8601BasicDate() {
        if (sDateFormatter_8601BasicDate == null) {
            sDateFormatter_8601BasicDate = DateTimeFormatter.ofPattern(
                            "yyyyMMdd", Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_8601BasicDate;
    }

    /**
     * Gets the DateTimeFormatter in the ISO 8601 - Basic Date format (MMddyy)
     *
     * @return The formatter with a pattern of MMddyy
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 Wiki</a>
     */
    public DateTimeFormatter getDateFormatter_8601BasicDateShortYear() {
        if (sDateFormatter_8601BasicDate == null) {
            sDateFormatter_8601BasicDate = DateTimeFormatter.ofPattern(
                            "MM/dd/yy", Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_8601BasicDate;
    }

    /**
     * Gets the DateTimeFormatter in the ISO 8601 - Basic Datetime format (yyyyMMddHHmmss.SSS)
     *
     * @return The formatter with a pattern of yyyyMMddHHmmss.SSS
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 Wiki</a>
     */
    public DateTimeFormatter getDateFormatter_8601BasicDatetime() {
        if (sDateFormatter_8601BasicDatetime == null) {
            sDateFormatter_8601BasicDatetime = DateTimeFormatter.ofPattern(
                            "yyyyMMddHHmmss.SSS", Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_8601BasicDatetime;
    }

    /**
     * Gets the DateTimeFormatter in the ISO 8601 - Basic Datetime with time zone format (yyyyMMddHHmmss.SSSXXX)
     *
     * @return The formatter with a pattern of yyyyMMddHHmmss.SSSXXX
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 Wiki</a>
     */
    public DateTimeFormatter getDateFormatter_8601BasicDatetimeTimeZone() {
        if (sDateFormatter_8601BasicDatetimeTimeZone == null) {
            sDateFormatter_8601BasicDatetimeTimeZone = DateTimeFormatter.ofPattern(
                            "yyyyMMddHHmmss.SSSXXX", Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_8601BasicDatetimeTimeZone;
    }

    /**
     * Gets the DateTimeFormatter in the ISO 8601 - Extended Datetime format (yyyy-MM-ddTHH:mm:ss.SSS)
     *
     * @return The formatter with a pattern of yyyy-MM-ddTHH:mm:ss.SSS
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 Wiki</a>
     */
    private DateTimeFormatter getDateFormatter_8601ExtendedDatetime() {
        if (sDateFormatter_8601ExtendedDatetime == null) {
            sDateFormatter_8601ExtendedDatetime = DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_8601ExtendedDatetime;
    }

    /**
     * Gets the DateTimeFormatter in the
     * - Extended Datetime with time zone format (yyyy-MM-ddTHH:mm:ss.SSSXXX)
     *
     * @return The formatter with a pattern of yyyy-MM-ddTHH:mm:ss.SSSXXX
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 Wiki</a>
     */
    public DateTimeFormatter getDateFormatter_8601ExtendedDatetimeTimeZone() {
        if (sDateFormatter_8601ExtendedDatetimeTimeZone == null) {
            sDateFormatter_8601ExtendedDatetimeTimeZone = DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_8601ExtendedDatetimeTimeZone;
    }

    public DateTimeFormatter getDateFormatter_8601ExtendedDatetimeTimeZone_v1() {
        if (sDateFormatter_8601ExtendedDatetimeTimeZone_v1 == null) {
            sDateFormatter_8601ExtendedDatetimeTimeZone_v1 = DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSSS", Locale.getDefault())
                    .withZone(ZoneId.of("UTC"));
        }

        return sDateFormatter_8601ExtendedDatetimeTimeZone_v1;
    }

    public DateTimeFormatter getDateFormatter_8601ExtendedDatetimeTimeZone_v2() {
        if (sDateFormatter_8601ExtendedDatetimeTimeZone_v2 == null) {
            sDateFormatter_8601ExtendedDatetimeTimeZone_v2 = DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSSSZ", Locale.getDefault())
                    .withZone(ZoneId.of("UTC"));
        }

        return sDateFormatter_8601ExtendedDatetimeTimeZone_v2;
    }

    public DateTimeFormatter getDateFormatter_ddMMMyyyy() {
        if (sDateFormatter_ddMMMyyyy == null) {
            sDateFormatter_ddMMMyyyy = DateTimeFormatter.ofPattern(
                            "dd MMM yyyy", Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_ddMMMyyyy;
    }

    public DateTimeFormatter getDateFormatter_MMMdd() {
        if (sDateFormatter_MMMdd == null) {
            sDateFormatter_MMMdd = DateTimeFormatter.ofPattern(
                            "MMM dd", Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_MMMdd;
    }

    public DateTimeFormatter getDateFormatter_MMMddyyyy() {
        if (sDateFormatter_MMMddyyyy == null) {
            sDateFormatter_MMMddyyyy = DateTimeFormatter.ofPattern(
                            "MMM dd yyyy", Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_MMMddyyyy;
    }

    public DateTimeFormatter getDateFormatter_8601ExtendedDatetimeTimeZoneNoMs() {
        if (sDateFormatter_8601ExtendedDatetimeTimeZoneNoMs == null) {
            sDateFormatter_8601ExtendedDatetimeTimeZoneNoMs = DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    .withZone(ZoneId.of("UTC"));
//            sDateFormatter_8601ExtendedDatetimeTimeZoneNoMs.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        return sDateFormatter_8601ExtendedDatetimeTimeZoneNoMs;
    }

    public DateTimeFormatter getDateFormatter_8601ExtendedDatetimeTimeZoneTwoMs() {
        if (sDateFormatter_8601ExtendedDatetimeTimeZoneTwoMs == null) {
            sDateFormatter_8601ExtendedDatetimeTimeZoneTwoMs = DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd'T'HH:mm:ss.SS'Z'", Locale.getDefault())
                    .withZone(ZoneId.of("UTC"));
        }

        return sDateFormatter_8601ExtendedDatetimeTimeZoneTwoMs;
    }

    public DateTimeFormatter getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs(int length) {
        String baseTimeString = "yyyy-MM-dd'T'HH:mm:ss";
        String baseTimeStringSuffix = "'Z'";
        int millisecondLength = length - baseTimeString.length() - baseTimeStringSuffix.length() + 4;

        if (millisecondLength > 0) {
            baseTimeString = baseTimeString.concat(".");

            for (int i = 1; i < millisecondLength; i++) {
                baseTimeString = baseTimeString.concat("S");
            }
        }

        return DateTimeFormatter.ofPattern(
                baseTimeString.concat(baseTimeStringSuffix), Locale.getDefault())
                .withZone(ZoneId.of("UTC"));
    }

    public DateTimeFormatter getDateFormatter_8601ExtendedDatetimeTimeZoneThreeMs() {
        if (sDateFormatter_8601ExtendedDatetimeTimeZoneThreeMs == null) {
            sDateFormatter_8601ExtendedDatetimeTimeZoneThreeMs = DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    .withZone(ZoneId.of("UTC"));
        }

        return sDateFormatter_8601ExtendedDatetimeTimeZoneThreeMs;
    }

    public DateTimeFormatter getDateFormatter_8601ExtendedDatetimeTimeZoneNineMs() {
        if (sDateFormatter_8601ExtendedDatetimeTimeZoneNineMs == null) {
            sDateFormatter_8601ExtendedDatetimeTimeZoneNineMs = DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'", Locale.getDefault())
                    .withZone(ZoneId.of("UTC"));
        }

        return sDateFormatter_8601ExtendedDatetimeTimeZoneNineMs;
    }


    public DateTimeFormatter getDateFormatter_8601ExtendedDatetimeTimeZoneSimple() {
        if (sDateFormatter_8601ExtendedDatetimeTimeZoneSimple == null) {
            sDateFormatter_8601ExtendedDatetimeTimeZoneSimple = DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSSSZ", Locale.getDefault())
                    .withZone(ZoneId.of("UTC"));
        }

        return sDateFormatter_8601ExtendedDatetimeTimeZoneSimple;
    }


    /**
     * Gets the DateTimeFormatter in a human readable format (E, MMMM d, h:mm a) as per
     * the translation in strings.xml
     *
     * @return The formatter with a pattern of (E, MMMM d, h:mm a) short day of the week with hour and min in 12 hour format
     */
    public DateTimeFormatter getDateFormatter_short_day_of_week_month_day_time(@NonNull Context context) {
        if (sDateFormatter_EMMMMhmma == null) {
            sDateFormatter_EMMMMhmma = DateTimeFormatter.ofPattern(
                            context.getString(R.string.date_format_short_day_of_week_month_day_time), Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_EMMMMhmma;
    }

    public DateTimeFormatter getDateFormatter_short_day_of_month_day_time(@NonNull Context context) {
        if (sDateFormatter_EMMMMhmma == null) {
            sDateFormatter_EMMMMhmma = DateTimeFormatter.ofPattern(
                            context.getString(R.string.date_format_short_day_of_month_day_time), Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_EMMMMhmma;
    }

    public DateTimeFormatter getDateFormatter_short_day_of_month_day_year_time(@NonNull Context context) {
        if (sDateFormatter_EMMMMdyyyyhmma == null) {
            sDateFormatter_EMMMMdyyyyhmma = DateTimeFormatter.ofPattern(
                    context.getString(R.string.date_format_short_day_of_month_day_year_time), Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_EMMMMdyyyyhmma;
    }

    /**
     * Gets the DateTimeFormatter in a human readable format (E, MMMM d) as per
     * the translation in strings.xml
     *
     * @return The formatter with a pattern of (E, MMMM d) short day of the week with hour and min in 12 hour format
     */
    public DateTimeFormatter getDateFormatter_short_day_of_week_month_day(@NonNull Context context) {
        if (sDateFormatter_EMMMM == null) {
            sDateFormatter_EMMMM = DateTimeFormatter.ofPattern(
                            context.getString(R.string.date_format_short_day_of_week_month_day), Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_EMMMM;
    }

    /**
     * Gets the DateTimeFormatter in a human readable format (EEEE h:mm a) as per
     * the translation in strings.xml
     *
     * @return The formatter with a pattern of (EEEE h:mm a) full day of the week with hour and min in 12 hour format
     */
    private DateTimeFormatter getDateFormatter_full_day_of_week_hour_min_12_hour(@NonNull Context context) {
        if (sDateFormatter_EEEEhmma == null) {
            sDateFormatter_EEEEhmma = DateTimeFormatter.ofPattern(
                            context.getString(R.string.date_format_full_day_of_week_hour_min_12_hour), Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_EEEEhmma;
    }

    /**
     * Gets the DateTimeFormatter in a human readable format (EEEE h:mm a) as per
     * the translation in strings.xml
     *
     * @return The formatter with a pattern of (EEEE h:mm a) full day of the week with hour and min in 12 hour format
     */
    private DateTimeFormatter getDateFormatter_full_day_of_week(@NonNull Context context) {
        if (sDateFormatter_EEEE == null) {
            sDateFormatter_EEEE = DateTimeFormatter.ofPattern(
                            "EEEE", Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_EEEE;
    }

    /**
     * Gets the DateTimeFormatter in a human readable format denoting the date is today
     * (\'Today\' h:mm a) as per the translation in strings.xml
     *
     * @return The formatter with a pattern of \'Today\' h:mm a
     */
    public DateTimeFormatter getDateFormatter_Todayhmma(@NonNull Context context) {
        if (sDateFormatter_Todayhmma == null) {
            sDateFormatter_Todayhmma = DateTimeFormatter.ofPattern(
                            context.getString(R.string.date_format_today_time_12_hour), Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_Todayhmma;
    }

    /**
     * Gets the DateTimeFormatter in a human readable format denoting the date is yesterday
     * (\'Yesterday\' h:mm a) as per the translation in strings.xml
     *
     * @return The formatter with a pattern of \'Yesterday\' h:mm a
     */
    public DateTimeFormatter getDateFormatter_Yesterdayhmma(@NonNull Context context) {
        if (sDateFormatter_Yesterdayhmma == null) {
            sDateFormatter_Yesterdayhmma = DateTimeFormatter.ofPattern(
                            context.getString(R.string.date_format_yesterday_time_12_hour), Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_Yesterdayhmma;
    }

    /**
     * Gets the DateTimeFormatter in the "hh:mm a" format
     *
     * @return The formatter with a pattern of "h:mm a"
     */
    public DateTimeFormatter getDateFormatter_hmma(@NonNull Context context) {
        if (sDateFormatter_hmma == null) {
            sDateFormatter_hmma = DateTimeFormatter.ofPattern(
                            context.getString(R.string.date_format_short_time_12_hour), Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_hmma;
    }

    /**
     * Gets the DateTimeFormatter in the "M/d/yy" format
     *
     * @return The formatter with a pattern of "M/d/yy"
     */
    public DateTimeFormatter getDateFormatter_Mdyy(@NonNull Context context) {
        if (sDateFormatter_Mdyy == null) {
            sDateFormatter_Mdyy = DateTimeFormatter.ofPattern(
                            context.getString(R.string.date_format_short_month_day_year), Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_Mdyy;
    }

    /**
     * Gets the DateTimeFormatter in the "MM/dd" format
     *
     * @return The formatter with a pattern of "MM/dd"
     */
    public DateTimeFormatter getDateFormatter_MMdd() {
        if (sDateFormatter_MMdd == null) {
            sDateFormatter_MMdd = DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_MMdd;
    }

    /**
     * Gets the DateTimeFormatter in the "%1$s yyyy-MM-dd HH:mm:ss" format
     *
     * @return The formatter with a pattern of "%1$s yyyy-MM-dd HH:mm:ss"
     */
    public DateTimeFormatter getDateFormatter_logZipFilename(@NonNull Context context) {
        if (sDateFormatter_logZipFilename == null) {
            sDateFormatter_logZipFilename = DateTimeFormatter.ofPattern(
                            context.getString(R.string.date_format_log_zip_filename), Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
        }

        return sDateFormatter_logZipFilename;
    }

    /**
     * Gets the Date having been converted from either <br>
     * ISO 8601 - Extended Datetime format (yyyy-MM-ddTHH:mm:ss.SSS) or <br>
     * ISO 8601 - Extended Datetime with time zone format (yyyy-MM-ddTHH:mm:ss.SSSXXX)
     *
     * @param input The date to be parsed in either format
     * @return The date
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601 Wiki</a>
     */
    public Instant parse_8601ExtendedDatetime(@NonNull String input) {
        if (input.contains("Z")) {
            return Instant.from(getDateFormatter_8601ExtendedDatetime().parse(input.replace("Z", "")));
        } else {
            return Instant.from(getDateFormatter_8601ExtendedDatetimeTimeZone().parse(input));
        }
    }

    public Instant getDndStatusExpiresTime(@NonNull String input) {
        try {
            return Instant.from(getDateFormatter_8601ExtendedDatetimeTimeZoneUnknownMs(input.length()).parse(input));
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
    }

    /**
     * Gets the input formatted to the human readable format, determining if the date denotes today or not
     *
     * @param input The date to be formatted
     * @return The date formatted to pattern "Today h:mm a", "Yesterday", "E, MMMM d"
     */
    public String format_humanReadable_short(@NonNull Context context, @NonNull CalendarManager calendarManager, @NonNull Instant input) {
        Instant now = calendarManager.getNowInstant();

        ZoneId zoneId = ZoneId.of(TimeZone.getDefault().getID());

        ZonedDateTime nowZonedDateTime = ZonedDateTime.ofInstant(now, zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime inputZonedDateTime = ZonedDateTime.ofInstant(input, zoneId).toLocalDate().atStartOfDay(zoneId);
        long daysBetween = ChronoUnit.DAYS.between(inputZonedDateTime, nowZonedDateTime);

        if (daysBetween == 0) {
            return getDateFormatter_Todayhmma(context).format(input);

        } else if (daysBetween == 1) {
            return context.getString(R.string.date_format_yesterday);

        } else {
            return getDateFormatter_Mdyy(context).format(input);
        }
    }

    /**
     * Gets the input formatted to the human readable format, determining if the date denotes today or not
     *
     * @param input The date to be formatted
     * @return The date formatted to pattern "h:mm a", "Yesterday", "EEEE",  "MMM dd" or "MMM dd yyyy"
     */
    public String format_humanReadableForMainListItems(@NonNull Context context, @NonNull CalendarManager calendarManager, @NonNull Instant input) {
        Instant now = calendarManager.getNowInstant();

        ZoneId zoneId = ZoneId.of(TimeZone.getDefault().getID());

        ZonedDateTime nowZonedDateTime = ZonedDateTime.ofInstant(now, zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime inputZonedDateTime = ZonedDateTime.ofInstant(input, zoneId).toLocalDate().atStartOfDay(zoneId);
        long daysBetween = ChronoUnit.DAYS.between(inputZonedDateTime, nowZonedDateTime);

        if (daysBetween == 0) {
            return getDateFormatter_hmma(context).format(input);

        } else if (daysBetween == 1) {
            return context.getString(R.string.date_format_yesterday);

        } else if (daysBetween > 1 && daysBetween < 7) {
            return getDateFormatter_full_day_of_week(context).format(input);

        } else {
            return getDateFormatter_ddMMMyyyy().format(input);
        }
    }

    /**
     * Gets the input formatted to the human readable format, determining if the date denotes today or not
     *
     * @param input The date to be formatted
     * @return The date formatted to pattern "h:mm a", "Yesterday", "EEEE",  or "dd MMM yyyy"
     */
    public String format_humanReadableForConnectMainListItems(@NonNull Context context, @NonNull CalendarManager calendarManager, @NonNull Instant input) {
        Instant now = calendarManager.getNowInstant();

        ZoneId zoneId = ZoneId.of(TimeZone.getDefault().getID());

        ZonedDateTime nowZonedDateTime = ZonedDateTime.ofInstant(now, zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime inputZonedDateTime = ZonedDateTime.ofInstant(input, zoneId).toLocalDate().atStartOfDay(zoneId);
        long daysBetween = ChronoUnit.DAYS.between(inputZonedDateTime, nowZonedDateTime);
        boolean isThisYear = inputZonedDateTime.getYear() == nowZonedDateTime.getYear();

        if (daysBetween < 1) {
            return getDateFormatter_hmma(context).format(input)
                    .replaceAll("AM", "am")
                    .replaceAll("PM", "pm");

        } else if (daysBetween == 1) {
            return context.getString(R.string.date_format_yesterday);

        } else if (daysBetween < 7) {
            return getDateFormatter_full_day_of_week(context).format(input);

        } else if (isThisYear) {
            return getDateFormatter_MMMdd().format(input);

        } else {
            return getDateFormatter_MMMddyyyy().format(input);
        }
    }

    /**
     * Gets the input formatted to the human readable format, determining if the date denotes today or not
     *
     * @param input The date to be formatted
     * @return The date formatted to pattern "Today h:mm a", "Yesterday h:mm a", "EEEE h:mm a",  or "E, MMMM d, h:mm a"
     */
    public String format_humanReadable(@NonNull Context context, @NonNull CalendarManager calendarManager, @NonNull Instant input) {
        Instant now = calendarManager.getNowInstant();

        ZoneId zoneId = ZoneId.of(TimeZone.getDefault().getID());

        ZonedDateTime nowZonedDateTime = ZonedDateTime.ofInstant(now, zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime inputZonedDateTime = ZonedDateTime.ofInstant(input, zoneId).toLocalDate().atStartOfDay(zoneId);
        long daysBetween = ChronoUnit.DAYS.between(inputZonedDateTime, nowZonedDateTime);

        if (daysBetween == 0) {
            return getDateFormatter_Todayhmma(context).format(input);

        } else if (daysBetween == 1) {
            return getDateFormatter_Yesterdayhmma(context).format(input);

        } else if (daysBetween > 1 && daysBetween < 7) {
            return getDateFormatter_full_day_of_week_hour_min_12_hour(context).format(input);

        } else {
            return getDateFormatter_short_day_of_week_month_day_time(context).format(input);
        }
    }

    public String format_humanReadableConnectShortTimeStamp(@NonNull Instant input) {
        return getDateFormatter_8601BasicDateShortYear().format(input);
    }

    public String format_humanReadableSmsTimeStamp(@NonNull Context context, @NonNull CalendarManager calendarManager, @NonNull Instant input) {
        Instant now = calendarManager.getNowInstant();

        ZoneId zoneId = ZoneId.of("UTC");

        ZonedDateTime nowZonedDateTime = ZonedDateTime.ofInstant(now, zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime inputZonedDateTime = ZonedDateTime.ofInstant(input, zoneId).toLocalDate().atStartOfDay(zoneId);
        long daysBetween = ChronoUnit.DAYS.between(inputZonedDateTime, nowZonedDateTime);

        if (daysBetween == 0) {
            return getDateFormatter_Todayhmma(context).format(input);

        } else if (daysBetween == 1) {
            return getDateFormatter_Yesterdayhmma(context).format(input);

        } else if (daysBetween > 1 && daysBetween < 7) {
            return getDateFormatter_full_day_of_week_hour_min_12_hour(context).format(input);

        } else {
            return getDateFormatter_short_day_of_week_month_day_time(context).format(input);
        }
    }

    public String format_humanReadableSmsTime(@NonNull Context context, @NonNull Instant input) {
        return getDateFormatter_hmma(context).format(input);
    }

    public String format_humanReadableSmsDate(@NonNull Context context, @NonNull CalendarManager calendarManager, @NonNull Instant input) {
        Instant now = calendarManager.getNowInstant();

        ZoneId zoneId = ZoneId.of("UTC");

        ZonedDateTime nowZonedDateTime = ZonedDateTime.ofInstant(now, zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime inputZonedDateTime = ZonedDateTime.ofInstant(input, zoneId).toLocalDate().atStartOfDay(zoneId);
        long daysBetween = ChronoUnit.DAYS.between(inputZonedDateTime, nowZonedDateTime);

        if (daysBetween == 0) {
            return context.getString(R.string.date_format_today);

        } else if (daysBetween == 1) {
            return context.getString(R.string.date_format_yesterday);

        } else {
            return getDateFormatter_short_day_of_week_month_day(context).format(input);
        }
    }

    public String format_humanReadableDndExpiryDateTime(@NonNull Context context, @NonNull CalendarManager calendarManager, @NonNull Instant input) {
        Instant now = calendarManager.getNowInstant();

        ZoneId zoneId = ZoneId.of(TimeZone.getDefault().getID());

        ZonedDateTime nowZonedDateTime = ZonedDateTime.ofInstant(now, zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime inputZonedDateTime = ZonedDateTime.ofInstant(input, zoneId).toLocalDate().atStartOfDay(zoneId);

        if (inputZonedDateTime.getYear() > nowZonedDateTime.getYear()) {
            return getDateFormatter_short_day_of_month_day_year_time(context).format(input);

        } else if (inputZonedDateTime.getYear() < nowZonedDateTime.getYear()) {
            return null;

        } else if (inputZonedDateTime.getDayOfYear() == nowZonedDateTime.getDayOfYear()) {
            return getDateFormatter_hmma(context).format(input);
        }

        return getDateFormatter_short_day_of_month_day_time(context).format(input);
    }

    public String format_connectHourMinuteTimeStamp(@NonNull Context context, @NonNull Instant input) {
        Calendar dateTime = Calendar.getInstance();
        dateTime.setTimeInMillis(input.toEpochMilli());
        SimpleDateFormat date;

        date = new SimpleDateFormat(context.getString(R.string.date_format_short_time_12_hour), Locale.getDefault());
        return date.format(dateTime.getTime())
                .replaceAll("AM", "am")
                .replaceAll("PM", "pm");
    }

    public String format_humanReadableMessageTimeStamp(@NonNull Context context, @NonNull CalendarManager calendarManager, @NonNull Instant instant) {
        Instant now = calendarManager.getNowInstant();

        ZoneId zoneId = ZoneId.of(TimeZone.getDefault().getID());

        ZonedDateTime nowZonedDateTime = ZonedDateTime.ofInstant(now, zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime inputZonedDateTime = ZonedDateTime.ofInstant(instant, zoneId).toLocalDate().atStartOfDay(zoneId);
        long hoursBetween = ChronoUnit.HOURS.between(inputZonedDateTime, nowZonedDateTime);

        if (hoursBetween < 24) {
            return getDateFormatter_hmma(context).format(instant);
        } else if (hoursBetween < 48) {
            return context.getString(R.string.date_format_yesterday);
        } else {
            return getDateFormatter_Mdyy(context).format(instant);
        }
    }

    /**
     * Gets the input formatted to the human readable format, determining if the date denotes today or not
     *
     * @param input The date to be formatted
     * @return The date formatted to pattern "hh:mm a" for today or "M/d/yy" for any other day
     */
    public String format_HmaOrMdyy(@NonNull Context context, @NonNull CalendarManager calendarManager, @NonNull Instant input) {
        Instant now = calendarManager.getNowInstant();

        if (ChronoUnit.DAYS.between(input, now) == 0) {
            return getDateFormatter_hmma(context).format(input);

        } else {
            return getDateFormatter_Mdyy(context).format(input);
        }
    }

    /**
     * Swap a date from one format to another
     *
     * @param input The date to be changed in pattern fromFormat
     * @return The date formatted to the pattern of toFormat
     */
    public String swapFormats(@Nullable String input, @NonNull DateTimeFormatter fromFormat, @NonNull DateTimeFormatter toFormat) {
        if (TextUtils.isEmpty(input)) {
            return null;
        }

        try {
            return toFormat.format(fromFormat.parse(input));
        } catch (DateTimeException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
    }

    public Instant getVoiceConversationDateTime(String dateTime) {
        if (dateTime != null) {
            return Instant.from(getDateFormatter_8601ExtendedDatetimeTimeZone_v2().parse(dateTime));
        }

        return Instant.now();
    }

    public Instant getRoomsConversationDateTime(String dateTime) {
        if (dateTime != null) {
            try {
                return Instant.from(getDateFormatter_8601ExtendedDatetimeTimeZoneThreeMs().parse(dateTime));
            } catch (Exception e) {
                try {
                    return Instant.from(getDateFormatter_8601ExtendedDatetimeTimeZoneTwoMs().parse(dateTime));
                } catch (Exception e2) {
                    return Instant.from(getDateFormatter_8601ExtendedDatetimeTimeZoneNoMs().parse(dateTime));
                }
            }
        }

        return Instant.now();
    }

    ///Handle datetime changes in the SMS API versions
    public Instant getSentFormatterDateTimeManager(String dateTime) {
        if (dateTime != null) {
            try {
                return Instant.from(getDateFormatter_8601ExtendedDatetimeTimeZone_v1().parse(dateTime));

            } catch (Exception x) {

                //Ignoring Exception as it is caused by there being a possibility that the API is still V2
                try {
                    //V2 format
                    return Instant.from(getDateFormatter_8601ExtendedDatetimeTimeZone_v2().parse(dateTime));

                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        }

        return Instant.now();
    }

    public String format_humanReadableForListItems(@NonNull Context context, @NonNull Long input) {
        Calendar now = Calendar.getInstance();
        Calendar dateTime = Calendar.getInstance();
        dateTime.setTimeInMillis(input);
        SimpleDateFormat date;
        if (now.get(Calendar.YEAR) != dateTime.get(Calendar.YEAR)) {
            date = new SimpleDateFormat(context.getString(R.string.date_format_full_moth_day_year), Locale.getDefault());
            return date.format(dateTime.getTime());
        } else if (now.get(Calendar.DAY_OF_YEAR) - 1 == dateTime.get(Calendar.DAY_OF_YEAR)) {
            return context.getString(R.string.date_format_yesterday);
        } else if (now.get(Calendar.DAY_OF_YEAR) == dateTime.get(Calendar.DAY_OF_YEAR)) {
            date = new SimpleDateFormat(context.getString(R.string.date_format_short_time_12_hour), Locale.getDefault());
            return date.format(dateTime.getTime()).toLowerCase();
        } else if (abs(dateTime.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR)) < 7) {
            date = new SimpleDateFormat(context.getString(R.string.date_format_full_day_of_week), Locale.getDefault());
            return date.format(dateTime.getTime());
        } else {
            date = new SimpleDateFormat(context.getString(R.string.date_format_month_day), Locale.getDefault());
            return date.format(dateTime.getTime());
        }
    }

    public String formatHHmm(@NonNull Context context, Instant instant) {
        return getDateFormatter_hmma(context).format(instant);
    }

    public String format_humanReadableForCallDuration(@NonNull Duration duration, @NonNull Context context){
        return duration.toString()
                .replaceAll("s", context.getString(R.string.time_format_call_duration_seconds))
                .replaceAll("m", context.getString(R.string.time_format_call_duration_minutes))
                .replaceAll("h", context.getString(R.string.time_format_call_duration_hours))
                .replaceAll("d", context.getString(R.string.time_format_call_duration_days));
    }
}