package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(
    tableName = DbConstants.TABLE_NAME_MEETINGS,
    primaryKeys = [
        DbConstants.MEETING_COLUMN_NAME_CALENDAR_ID,
        DbConstants.MEETING_COLUMN_NAME_NAME,
        DbConstants.MEETING_COLUMN_NAME_START_TIME,
        DbConstants.MEETING_COLUMN_NAME_CREATED_BY]
)

data class DbMeeting(
    @ColumnInfo(name = DbConstants.MEETING_COLUMN_NAME_CALENDAR_ID) var calendarId: Int,
    @ColumnInfo(name = DbConstants.MEETING_COLUMN_NAME_NAME) var name: String,
    @ColumnInfo(name = DbConstants.MEETING_COLUMN_NAME_START_TIME) var startTime: Long,
    @ColumnInfo(name = DbConstants.MEETING_COLUMN_NAME_CREATED_BY) var createdBy: String,
    @ColumnInfo(name = DbConstants.MEETING_COLUMN_NAME_MEETING_INFO) var meetingInfo: String
)