package com.nextiva.nextivaapp.android.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.nextiva.nextivaapp.android.core.notifications.api.UserScheduleResponse
import com.nextiva.nextivaapp.android.core.notifications.models.Schedule
import com.nextiva.nextivaapp.android.db.model.DbHoliday
import com.nextiva.nextivaapp.android.db.model.DbSchedule
import com.nextiva.nextivaapp.android.db.model.DbWorkingHourBreak
import com.nextiva.nextivaapp.android.db.model.DbWorkingHours
import kotlinx.coroutines.flow.Flow

@Dao
interface SchedulesDao {
    @Query("SELECT IFNULL(MAX(page_number), 0) FROM schedules")
    fun getLastPageFetched(): Int

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """SELECT schedules.*  
                    FROM schedules ORDER BY LOWER(name)"""
    )
    fun getSchedulesPagingSource(): PagingSource<Int, Schedule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSchedules(schedule: List<DbSchedule>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSchedule(schedule: DbSchedule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkingHours(workingHour: List<DbWorkingHours>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkingHourBreaks(workingHourBreak: List<DbWorkingHourBreak>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHolidays(holidays: List<DbHoliday>)

    @Transaction
    fun insertSchedules(isRefresh: Boolean, schedules: ArrayList<UserScheduleResponse>, pageNumber: Int?): String {
        val dndScheduleId = getDndScheduleId()
        val schedulesList: ArrayList<DbSchedule> = ArrayList()
        val workingHoursList: ArrayList<DbWorkingHours> = ArrayList()
        val workingHourBreaksList: ArrayList<DbWorkingHourBreak> = ArrayList()
        val holidaysList: ArrayList<DbHoliday> = ArrayList()

        if (isRefresh) {
            deleteSchedules()
            deleteWorkingHours()
            deleteWorkingHourBreaks()
            deleteHolidays()
        }

        schedules.forEach { schedule ->
            val dbSchedule = schedule.toDbSchedule(dndScheduleId == schedule.id, 0)

            dbSchedule.scheduleId?.let { scheduleId ->
                if (getScheduleById(scheduleId) != null) {
                    updateSchedule(scheduleId, dbSchedule.is24x7, dbSchedule.monToFri, dbSchedule.name, dbSchedule.oldScheduleName, dbSchedule.scheduleLevel, dbSchedule.isDndSchedule)
                } else {
                    schedulesList.add(schedule.toDbSchedule(dndScheduleId == schedule.id, pageNumber))
                }
            }

            schedule.id?.let { scheduleId ->
                deleteWorkingHourBreaksByScheduleId(scheduleId)
                deleteHolidaysByScheduleId(scheduleId)

                schedule.getDaysList().forEach { workingHour ->
                    workingHour.day?.let { day ->
                        val dbWorkingHour = getWorkingHourByIdAndDay(workingHour.scheduleId, day)

                        if (dbWorkingHour != null) {
                            workingHour.start?.let { start ->
                                workingHour.end?.let { end ->
                                    updateWorkingHour(workingHour.scheduleId, day, start, end)
                                }
                            }

                        } else {
                            workingHoursList.add(workingHour)
                        }

                        workingHour.breaks?.forEach { workingHourBreaksList.add(it) }
                    }
                }

                schedule.getDbHolidays().forEach { holiday ->
                    holidaysList.add(holiday)
                }
            }
        }

        insertSchedules(schedulesList)
        insertWorkingHours(workingHoursList)
        insertWorkingHourBreaks(workingHourBreaksList)
        insertHolidays(holidaysList)

        return if (schedules.size > 1) "" else schedules.firstOrNull()?.id ?: ""
    }

    @Query("DELETE FROM schedules")
    fun deleteSchedules()

    @Query("DELETE FROM schedules WHERE schedule_id = :scheduleId")
    fun deleteSchedule(scheduleId: String)

    @Query("DELETE FROM working_hours")
    fun deleteWorkingHours()

    @Query("DELETE FROM working_hour_breaks")
    fun deleteWorkingHourBreaks()

    @Query("DELETE FROM holidays")
    fun deleteHolidays()

    @Query("DELETE FROM holidays WHERE schedule_id = :scheduleId")
    fun deleteHolidaysByScheduleId(scheduleId: String)

    @Query("DELETE FROM working_hour_breaks WHERE day_id LIKE '%' || :scheduleId || '%'")
    fun deleteWorkingHourBreaksByScheduleId(scheduleId: String)

    @Query("UPDATE schedules SET is_24_7 = :is24x7, mon_to_fri = :monToFri, name = :name, old_schedule_name = :oldScheduleName, schedule_level = :scheduleLevel, is_dnd_schedule = :isDndSchedule WHERE schedule_id = :scheduleId")
    fun updateSchedule(scheduleId: String, is24x7: Boolean?, monToFri: Boolean?, name: String?, oldScheduleName: String?, scheduleLevel: String?, isDndSchedule: Boolean)

    @Query("UPDATE working_hours SET start = :start, 'end' = :end WHERE schedule_id = :scheduleId AND day = :day")
    fun updateWorkingHour(scheduleId: String, day: String, start: String, end: String)

    @Query("SELECT * FROM schedules WHERE schedule_id = :scheduleId")
    fun getScheduleById(scheduleId: String): DbSchedule?

    @Query("SELECT schedule_id FROM schedules WHERE is_dnd_schedule = 1")
    fun getDndScheduleId(): String?

    @Query("SELECT * FROM working_hours WHERE schedule_id = :scheduleId AND day = :day")
    fun getWorkingHourByIdAndDay(scheduleId: String, day: String): DbWorkingHours?

    @Query("SELECT * FROM schedules WHERE is_dnd_schedule = 1")
    fun getDndScheduleFlow(): Flow<Schedule?>

    @Query("UPDATE schedules SET is_dnd_schedule = CASE WHEN schedule_id = :scheduleId THEN 1 ELSE 0 END")
    fun setDndScheduleSelected(scheduleId: String)

    @Query("UPDATE schedules SET is_dnd_schedule = 0")
    fun deleteDndSchedules()

    @Query("SELECT count(*) FROM schedules where name = :scheduleName")
    fun isScheduleNameInUse(scheduleName: String): Int
}