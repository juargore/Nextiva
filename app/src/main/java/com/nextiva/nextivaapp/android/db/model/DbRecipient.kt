package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(tableName = DbConstants.TABLE_NAME_RECIPIENT,
        primaryKeys = [DbConstants.RECIPIENT_COLUMN_NAME_MESSAGE_ID, DbConstants.RECIPIENT_COLUMN_NAME_PARTICIPANT_ID],
        foreignKeys = [ForeignKey(entity = DbSmsMessage::class,
                parentColumns = [DbConstants.SMS_MESSAGE_COLUMN_NAME_ID],
                childColumns = [DbConstants.RECIPIENT_COLUMN_NAME_MESSAGE_ID],
                onDelete = ForeignKey.CASCADE), ForeignKey(entity = DbParticipant::class,
                parentColumns = [DbConstants.PARTICIPANT_COLUMN_NAME_ID],
                childColumns = [DbConstants.RECIPIENT_COLUMN_NAME_PARTICIPANT_ID],
                onDelete = ForeignKey.CASCADE)])
data class DbRecipient(
        @ColumnInfo(name = DbConstants.RECIPIENT_COLUMN_NAME_MESSAGE_ID) var messageId: Long,
        @ColumnInfo(name = DbConstants.RECIPIENT_COLUMN_NAME_PARTICIPANT_ID) var participantId: Long)