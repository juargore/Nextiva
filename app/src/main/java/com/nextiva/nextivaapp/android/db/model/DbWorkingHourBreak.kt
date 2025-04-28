package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.db.util.DbConstants
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

@Entity(tableName = DbConstants.TABLE_NAME_WORKING_HOUR_BREAKS,
    foreignKeys = [ForeignKey(entity = DbWorkingHours::class,
        parentColumns = [DbConstants.WORKING_HOURS_COLUMN_NAME_DAY_ID],
        childColumns = [DbConstants.WORKING_HOUR_BREAKS_COLUMN_NAME_DAY_ID],
        onDelete = ForeignKey.CASCADE)])
data class DbWorkingHourBreak(@PrimaryKey(autoGenerate = true)
                              @ColumnInfo(DbConstants.WORKING_HOUR_BREAKS_COLUMN_NAME_ID) var id: Long?,
                              @ColumnInfo(DbConstants.WORKING_HOUR_BREAKS_COLUMN_NAME_DAY_ID) var dayId: String,
                              @ColumnInfo(DbConstants.WORKING_HOUR_BREAKS_COLUMN_NAME_NAME) var name: String?,
                              @ColumnInfo(DbConstants.WORKING_HOUR_BREAKS_COLUMN_START) var start: String?,
                              @ColumnInfo(DbConstants.WORKING_HOUR_BREAKS_COLUMN_END) var end: String?) {
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
}