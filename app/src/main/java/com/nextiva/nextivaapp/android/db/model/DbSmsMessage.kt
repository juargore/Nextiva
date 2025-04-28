package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.db.util.DbConstants
import org.threeten.bp.Instant

@Entity(tableName = DbConstants.TABLE_NAME_SMS_MESSAGE)
data class DbSmsMessage(@PrimaryKey(autoGenerate = true)
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_ID) var id: Long?,
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_MESSAGE_ID) var messageId: String,
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_CHANNEL) var channel: String?,
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_BODY) var body: String?,
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_PREVIEW) var preview: String?,
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_SENT) var sent: Instant?,
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_PRIORITY) var priority: String?,
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_GROUP_VALUE) var groupValue: String?,
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_IS_SENDER) var isSender: Boolean?,
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_SENT_STATUS) var sentStatus: Int?,
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_GROUP_ID) var groupId: String?,
                        @ColumnInfo(name = DbConstants.SMS_MESSAGE_COLUMN_NAME_CONVERSATION_ID) var conversationId: String?)