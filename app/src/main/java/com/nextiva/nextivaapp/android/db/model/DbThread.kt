package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(tableName = DbConstants.TABLE_NAME_THREADS,
        foreignKeys = [ForeignKey(entity = DbSmsMessage::class,
                parentColumns = [DbConstants.SMS_MESSAGE_COLUMN_NAME_ID],
                childColumns = [DbConstants.THREAD_COLUMN_NAME_MESSAGE_ID],
                onDelete = ForeignKey.CASCADE)])
data class DbThread(@PrimaryKey(autoGenerate = true)
                    @ColumnInfo(name = DbConstants.THREAD_COLUMN_NAME_ID) var id: Long?,
                    @ColumnInfo(name = DbConstants.THREAD_COLUMN_NAME_MESSAGE_ID) var messageId: String?,
                    @ColumnInfo(name = DbConstants.THREAD_COLUMN_NAME_THREAD_ID) var threadId: String?)