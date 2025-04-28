package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.core.notifications.api.DayOfWeek
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(tableName = DbConstants.TABLE_NAME_WORKING_HOURS,
    indices = [Index(value = [DbConstants.WORKING_HOURS_COLUMN_NAME_DAY_ID], unique = true)],
        foreignKeys = [ForeignKey(entity = DbSchedule::class,
        parentColumns = [DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_ID],
        childColumns = [DbConstants.WORKING_HOURS_COLUMN_NAME_SCHEDULE_ID],
        onDelete = ForeignKey.CASCADE)])
data class DbWorkingHours(@PrimaryKey(autoGenerate = true)
                          @ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_ID) var id: Long?,
                          @ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_SCHEDULE_ID) var scheduleId: String,
                          @ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_DAY_ID) var dayId: String,
                          @ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_DAY) var day: String?,
                          @ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_END) var end: String?,
                          @ColumnInfo(DbConstants.WORKING_HOURS_COLUMN_NAME_START) var start: String?) {

    @Ignore
    constructor(scheduleId: String, day: String?, end: String?, start: String?, breaks: List<DbWorkingHourBreak>?): this(null, scheduleId, "${day}${scheduleId}", day, end, start) {
        this.breaks = breaks
    }

    @Ignore
    var breaks: List<DbWorkingHourBreak>? = null

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
}