package com.nextiva.nextivaapp.android.core.notifications.models

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.nextiva.nextivaapp.android.core.notifications.api.DayOfWeek
import com.nextiva.nextivaapp.android.db.model.DbWorkingHourBreak
import com.nextiva.nextivaapp.android.db.util.DbConstants
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

data class WorkingHour(@ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_ID) var id: Long?,
                       @ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_SCHEDULE_ID) var scheduleId: String,
                       @ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_DAY_ID) var dayId: String,
                       @ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_DAY) var day: String?,
                       @ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_END) var end: String?,
                       @ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_START) var start: String?,
                       @Relation(parentColumn = DbConstants.WORKING_HOURS_COLUMN_NAME_DAY_ID,
                           entityColumn = DbConstants.WORKING_HOUR_BREAKS_COLUMN_NAME_DAY_ID,
                           entity = DbWorkingHourBreak::class) var breaks: List<DbWorkingHourBreak>?) {

    fun getDisplayName(): String? {
        return when (day) {
            DayOfWeek.SUNDAY.name -> "Sun"
            DayOfWeek.MONDAY.name -> "Mon"
            DayOfWeek.TUESDAY.name -> "Tue"
            DayOfWeek.WEDNESDAY.name -> "Wed"
            DayOfWeek.THURSDAY.name -> "Thu"
            DayOfWeek.FRIDAY.name -> "Fri"
            DayOfWeek.SATURDAY.name -> "Sat"
            else -> null
        }
    }

    fun getStartTimeDisplay(): String {
        var startTime = ""

        start?.let { start ->
            val dateFormat = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

            LocalTime.parse(start)?.let {
                startTime = dateFormat.format(it)
                    .replace("AM", "am")
                    .replace("PM", "pm")
            }
        }

        return startTime
    }

    fun getEndTimeDisplay(): String {
        var endTime = ""

        end?.let { start ->
            val dateFormat = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

            LocalTime.parse(start)?.let {
                endTime = dateFormat.format(it)
                    .replace("AM", "am")
                    .replace("PM", "pm")
            }
        }

        return endTime
    }

    fun getTimeDisplay(): String {
        return "${getStartTimeDisplay().removePrefix("0")} - ${getEndTimeDisplay().removePrefix("0")}"
            .replace(":00", "")
    }
}