package com.nextiva.nextivaapp.android.core.notifications.utils

import com.nextiva.nextivaapp.android.NextivaApplication
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.notifications.api.UiDaySchedule
import com.nextiva.nextivaapp.android.core.notifications.models.Schedule
import com.nextiva.nextivaapp.android.db.DbManager
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class ScheduleValidator @Inject constructor
    (private val app: NextivaApplication) {

    @Inject
    lateinit var dbManager: DbManager

    companion object {
        private const val SCHEDULE_NAME_MAX_CHAR_LIMIT = 40
    }

    fun validateScheduleName(scheduleName: String, selectedSchedule: Schedule?): String {
        if (scheduleName.isEmpty()) {
            return Enums.ScheduleValidationErrorType.EMPTY_SCHEDULE_NAME
        }

        if (scheduleName.length > SCHEDULE_NAME_MAX_CHAR_LIMIT) {
            return Enums.ScheduleValidationErrorType.MAX_SCHEDULE_NAME_CHAR_LIMIT_REACHED
        }

        if ((selectedSchedule == null || !selectedSchedule.name.equals(scheduleName)) && isScheduleNameInUse(
                scheduleName
            )
        ) {
            return Enums.ScheduleValidationErrorType.SCHEDULE_NAME_IN_USE
        }
        return Enums.ScheduleValidationErrorType.SCHEDULE_VALIDATION_ERROR_NONE
    }

    fun validateBreaks(daySchedule: UiDaySchedule?): Boolean {
        var isValidated = true
        daySchedule?.breaks?.filter { uiStartEndTimes -> uiStartEndTimes.startTime.isNotEmpty() && uiStartEndTimes.endTime.isNotEmpty() }
            ?.map { uiStartEndTimes ->
                if (uiStartEndTimes.getFormattedEndTime()
                        ?.before(uiStartEndTimes.getFormattedStartTime()) == true
                ) {
                    uiStartEndTimes.validationErrorTypeList.add(Enums.ScheduleValidationErrorType.END_TIME_BEFORE_START_TIME)
                    isValidated = false
                } else {
                    uiStartEndTimes.validationErrorTypeList.remove(Enums.ScheduleValidationErrorType.END_TIME_BEFORE_START_TIME)
                }
            }
        return isValidated
    }

    fun validateHours(daySchedule: UiDaySchedule?): Boolean {
        return daySchedule?.hours?.let { uiStartEndTimes ->
            val startTime = uiStartEndTimes.getFormattedStartTime()
            val endTime = uiStartEndTimes.getFormattedEndTime()
            if (startTime != null && endTime != null) {
                if (endTime.before(startTime)) {
                    uiStartEndTimes.isEndTimeError = true
                    uiStartEndTimes.validationErrorText =
                        app.getString(R.string.notification_create_schedule_validation_error_end_time_before_start)
                    false
                } else {
                    uiStartEndTimes.isEndTimeError = false
                    uiStartEndTimes.validationErrorText = ""
                    true
                }
            } else {
                false
            }
        } ?: false
    }

    fun validateSameHours(daySchedule: UiDaySchedule?): Boolean {
        return daySchedule?.hours?.let { uiStartEndTimes ->
            val startTime = uiStartEndTimes.getFormattedStartTime()
            val endTime = uiStartEndTimes.getFormattedEndTime()
            if (startTime != null && endTime != null) {
                uiStartEndTimes.isEndTimeError = false
                if (endTime == startTime) {
                    uiStartEndTimes.isStartTimeError = true
                    uiStartEndTimes.validationErrorText =
                        app.getString(R.string.notification_create_schedule_validation_error_start_date_time_before_end_date_time)
                    false
                } else {
                    uiStartEndTimes.isStartTimeError = false
                    uiStartEndTimes.isEndTimeError = false
                    true
                }
            } else {
                false
            }
        } ?: false
    }

    fun validateBreaksWithHours(daySchedule: UiDaySchedule?): Boolean {
        if (daySchedule == null) {
            // If the day schedule or hours are null, return false
            return false
        }

        val uiStartEndTimes = daySchedule.hours
        if (uiStartEndTimes.startTime.isEmpty() || uiStartEndTimes.endTime.isEmpty()) {
            // If the start or end time is empty, return false
            return false
        }

        var isValidated = true
        var isStartTimeError = false
        var isEndTimeError = false
        daySchedule.breaks.filter { it.startTime.isNotEmpty() && it.endTime.isNotEmpty() }
            .forEach { uiBreakTimes ->

                if (uiBreakTimes.getFormattedStartTime()
                        ?.before(uiStartEndTimes.getFormattedStartTime()) == true
                ) {
                    isValidated = false
                    isStartTimeError = true
                    uiBreakTimes.validationErrorTypeList.add(Enums.ScheduleValidationErrorType.BREAK_TIMES_START_TIME_NOT_IN_RANGE)
                } else {
                    uiBreakTimes.validationErrorTypeList.remove(Enums.ScheduleValidationErrorType.BREAK_TIMES_START_TIME_NOT_IN_RANGE)
                }

                if (uiBreakTimes.getFormattedEndTime()
                        ?.after(uiStartEndTimes.getFormattedEndTime()) == true
                ) {
                    isValidated = false
                    isEndTimeError = true
                    uiBreakTimes.validationErrorTypeList.add(Enums.ScheduleValidationErrorType.BREAK_TIMES_END_TIME_NOT_IN_RANGE)
                } else {
                    uiBreakTimes.validationErrorTypeList.remove(Enums.ScheduleValidationErrorType.BREAK_TIMES_END_TIME_NOT_IN_RANGE)
                }

                if (isStartTimeError && isEndTimeError) {
                    uiBreakTimes.validationErrorTypeList.add(Enums.ScheduleValidationErrorType.BREAK_TIMES_START_END_TIME_NOT_IN_RANGE)
                } else {
                    uiBreakTimes.validationErrorTypeList.remove(Enums.ScheduleValidationErrorType.BREAK_TIMES_START_END_TIME_NOT_IN_RANGE)
                }
            }

        return isValidated
    }

    fun validateBreaksOverlapping(daySchedule: UiDaySchedule?): Boolean {
        daySchedule?.breaks?.filter { it.startTime.isNotEmpty() && it.endTime.isNotEmpty() }
            ?.let { breakTimes ->
                val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                var hasOverlap = false
                breakTimes.forEach { it.validationErrorTypeList.remove(Enums.ScheduleValidationErrorType.BREAK_TIMES_OVERLAPPING) }
                if (breakTimes.size > 1) {
                    breakTimes.forEachIndexed { i, b1 ->
                        breakTimes.drop(i + 1).forEach { b2 ->
                            if (dateFormat.parse(b1.startTime)
                                    ?.before(dateFormat.parse(b2.endTime)) == true && dateFormat.parse(
                                    b2.startTime
                                )?.before(dateFormat.parse(b1.endTime)) == true
                            ) {
                                hasOverlap = true
                                b1.validationErrorTypeList.add(Enums.ScheduleValidationErrorType.BREAK_TIMES_OVERLAPPING)
                                b2.validationErrorTypeList.add(Enums.ScheduleValidationErrorType.BREAK_TIMES_OVERLAPPING)
                            }
                        }
                    }
                }
                return !hasOverlap
            }

        return true
    }

    private fun isScheduleNameInUse(scheduleName: String): Boolean {
        return dbManager.isScheduleNameInUse(scheduleName)
    }

}