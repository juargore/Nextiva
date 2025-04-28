package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(indices = [Index(value = [DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_ID], unique = true)],
    tableName = DbConstants.TABLE_NAME_SCHEDULES)
data class DbSchedule(@PrimaryKey(autoGenerate = true)
                      @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_ID) var id: Long?,
                      @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_ID) var scheduleId: String?,
                      @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_IS_24_7) var is24x7: Boolean?,
                      @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_MON_TO_FRI) var monToFri: Boolean?,
                      @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_NAME) var name: String?,
                      @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_OLD_SCHEDULE_NAME) var oldScheduleName: String?,
                      @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_LEVEL) var scheduleLevel: String?,
                      @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_IS_DND_SCHEDULE) var isDndSchedule: Boolean = false,
                      @ColumnInfo(DbConstants.SCHEDULES_COLUMN_NAME_PAGE_NUMBER) var pageNumber: Int?)