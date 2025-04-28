package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.core.notifications.api.Holiday
import com.nextiva.nextivaapp.android.core.notifications.api.HolidayType
import com.nextiva.nextivaapp.android.core.notifications.api.HourOfWeek
import com.nextiva.nextivaapp.android.core.notifications.api.Interval
import com.nextiva.nextivaapp.android.core.notifications.api.Repeat
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(tableName = DbConstants.TABLE_NAME_HOLIDAYS,
    foreignKeys = [ForeignKey(entity = DbSchedule::class,
        parentColumns = [DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_ID],
        childColumns = [DbConstants.HOLIDAYS_COLUMN_NAME_SCHEDULE_ID],
        onDelete = ForeignKey.CASCADE)])
data class DbHoliday(@PrimaryKey(autoGenerate = true)
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_ID) var id: Long?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_SCHEDULE_ID) var scheduleId: String,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_DAY) var day: Int?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_DAY_OF_WEEK) var dayOfWeek: String?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_START_DATE) var startDate: String?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_END_DATE) var endDate: String?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_START_HOUR) var startHour: String?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_END_HOUR) var endHour: String?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_TYPE) var type: String?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_MONTH) var month: String?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_NAME) var name: String?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_OCCURRENCE) var occurrence: String?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_RECURRENCE_RANGE_END_DATE) var recurrenceRangeEndDate: String?,
                     @ColumnInfo(DbConstants.HOLIDAYS_COLUMN_NAME_RECURRENCE_RANGE_NUMBER) var numberOfRecurrences: String?) {

    fun toHoliday(): Holiday {
        return Holiday(day,
            dayOfWeek,
            type?.let { HolidayType.valueOf(it) },
            month,
            name,
            occurrence,
            Interval(startHour == null && endHour == null,
                if (startHour == null && endHour == null) null else HourOfWeek(startHour ?: "", endHour ?: "")
            ),
            Repeat(true, null),
            startDate)
    }
}