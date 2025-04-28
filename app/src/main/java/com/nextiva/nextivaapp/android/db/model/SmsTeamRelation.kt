package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(tableName = DbConstants.TABLE_NAME_SMS_TEAM_RELATION,
    primaryKeys = [DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_MESSAGE_ID, DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_TEAM_ID],
    foreignKeys = [ForeignKey(entity = DbSmsMessage::class,
        parentColumns = [DbConstants.SMS_MESSAGE_COLUMN_NAME_ID],
        childColumns = [DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_MESSAGE_ID],
        onDelete = ForeignKey.CASCADE), ForeignKey(entity = SmsTeam::class,
        parentColumns = [DbConstants.SMS_TEAM_COLUMN_NAME_ID],
        childColumns = [DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_TEAM_ID],
        onDelete = ForeignKey.CASCADE)])
data class SmsTeamRelation(@ColumnInfo(name = DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_MESSAGE_ID) var messageId: Long,
                           @ColumnInfo(name = DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_TEAM_ID) var teamId: Long)