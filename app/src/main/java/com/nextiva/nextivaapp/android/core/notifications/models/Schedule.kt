package com.nextiva.nextivaapp.android.core.notifications.models

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.Relation
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.api.DayOfWeek
import com.nextiva.nextivaapp.android.db.model.DbHoliday
import com.nextiva.nextivaapp.android.db.model.DbWorkingHours
import com.nextiva.nextivaapp.android.db.util.DbConstants
import java.io.Serializable

data class Schedule(
    @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_ID) var id: Long?,
    @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_ID) var scheduleId: String?,
    @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_IS_24_7) var is24x7: Boolean?,
    @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_MON_TO_FRI) var monToFri: Boolean?,
    @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_NAME) var name: String?,
    @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_OLD_SCHEDULE_NAME) var oldScheduleName: String?,
    @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_LEVEL) var scheduleLevel: String?,
    @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_IS_DND_SCHEDULE) var isDndSchedule: Boolean,
    @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_PAGE_NUMBER) var pageNumber: Int?,
    @Relation(
        parentColumn = DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_ID,
        entityColumn = DbConstants.WORKING_HOURS_COLUMN_NAME_SCHEDULE_ID,
        entity = DbWorkingHours::class) var workingHours: List<WorkingHour>?,
    @Relation(
        parentColumn = DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_ID,
        entityColumn = DbConstants.HOLIDAYS_COLUMN_NAME_SCHEDULE_ID,
        entity = DbHoliday::class) var holidays: List<DbHoliday>?) : Serializable {

    @Ignore
    constructor() : this(null, null, false, false, null, null, null, false, null, null, null)

    fun getDayString(context: Context): Pair<String, String> {
        return if (monToFri == true) {
            Pair(context.getString(R.string.notification_schedules_weekly_display), "${workingHours?.firstOrNull()?.getTimeDisplay()}")
        } else if (is24x7 == true) {
            Pair(context.getString(R.string.notification_schedules_everyday), context.getString(R.string.notification_schedules_all_day))
        } else {
            getDailyString()
        }
    }

    private fun getDailyString(): Pair<String, String> {
        var dayString = ""
        var hourString = ""

        workingHours?.forEach { workingHour ->
            workingHour.day?.let { day ->
                workingHour.getDisplayName()?.let { displayName ->
                    if (workingHours?.any { it.day == getDayBefore(day) && areHoursTheSame(workingHour, it) } == true &&
                        workingHours?.any { it.day == getDayAfter(day) && areHoursTheSame(workingHour, it) } == true) {
                        if (!dayString.endsWith("-")) {
                            dayString = dayString.removeSuffix(", ")
                            dayString += "-"
                        }

                    } else {
                        if (!dayString.endsWith("-")) {
                            hourString += "${workingHour.getTimeDisplay()}, "
                        }
                        dayString += "${displayName}, "
                    }
                }
            }
        }

        if (doDaysHaveSameHours()) {
            hourString = "${workingHours?.firstOrNull()?.getTimeDisplay()}"
        }

        return Pair(dayString.removeSuffix(", "), hourString.removeSuffix(", "))
    }

    private fun doDaysHaveSameHours(): Boolean {
        val day = workingHours?.firstOrNull()

        workingHours?.forEach {
            if (it.start != day?.start || it.end != day?.end) {
                return false
            }
        }

        return true
    }

    private fun getDayBefore(day: String): String? {
        return when (day) {
            DayOfWeek.MONDAY.name -> DayOfWeek.SUNDAY.name
            DayOfWeek.TUESDAY.name -> DayOfWeek.MONDAY.name
            DayOfWeek.WEDNESDAY.name -> DayOfWeek.TUESDAY.name
            DayOfWeek.THURSDAY.name -> DayOfWeek.WEDNESDAY.name
            DayOfWeek.FRIDAY.name -> DayOfWeek.THURSDAY.name
            DayOfWeek.SATURDAY.name -> DayOfWeek.FRIDAY.name
            else -> null
        }
    }

    private fun getDayAfter(day: String): String? {
        return when (day) {
            DayOfWeek.SUNDAY.name -> DayOfWeek.MONDAY.name
            DayOfWeek.MONDAY.name -> DayOfWeek.TUESDAY.name
            DayOfWeek.TUESDAY.name -> DayOfWeek.WEDNESDAY.name
            DayOfWeek.WEDNESDAY.name -> DayOfWeek.THURSDAY.name
            DayOfWeek.THURSDAY.name -> DayOfWeek.FRIDAY.name
            DayOfWeek.FRIDAY.name -> DayOfWeek.SATURDAY.name
            else -> null
        }
    }

    private fun areHoursTheSame(currentDay: WorkingHour, compareDay: WorkingHour): Boolean {
        return currentDay.start == compareDay.start && currentDay.end == compareDay.end
    }
}