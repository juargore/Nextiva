package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(tableName = DbConstants.TABLE_NAME_SENDER,
        foreignKeys = [ForeignKey(entity = DbSmsMessage::class,
                parentColumns = [DbConstants.SMS_MESSAGE_COLUMN_NAME_ID],
                childColumns = [DbConstants.SENDER_COLUMN_NAME_MESSAGE_ID],
                onDelete = ForeignKey.CASCADE), ForeignKey(entity = DbParticipant::class, parentColumns = [DbConstants.PARTICIPANT_COLUMN_NAME_ID], childColumns = [DbConstants.SENDER_COLUMN_NAME_PARTICIPANT_ID], onDelete = ForeignKey.CASCADE)],
        primaryKeys = [DbConstants.SENDER_COLUMN_NAME_MESSAGE_ID, DbConstants.SENDER_COLUMN_NAME_PARTICIPANT_ID])
data class DbSender(
        @ColumnInfo(name = DbConstants.SENDER_COLUMN_NAME_MESSAGE_ID) var messageId: Long,
        @ColumnInfo(name = DbConstants.SENDER_COLUMN_NAME_PARTICIPANT_ID) var participantId: Long)
