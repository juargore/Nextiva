package com.nextiva.nextivaapp.android.db.model

import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.util.DbConstants


@Entity(tableName = DbConstants.TABLE_NAME_MESSAGE_STATE,
        foreignKeys = [ForeignKey(entity = DbSmsMessage::class,
                parentColumns = [DbConstants.SMS_MESSAGE_COLUMN_NAME_ID],
                childColumns = [DbConstants.MESSAGE_STATE_COLUMN_NAME_SMS_ID],
                onDelete = ForeignKey.CASCADE)])

class DbMessageState(@PrimaryKey
                     @ColumnInfo(name = DbConstants.MESSAGE_STATE_COLUMN_NAME_SMS_ID) var smsId: Long,
                     @ColumnInfo(name = DbConstants.MESSAGE_STATE_COLUMN_NAME_MESSAGE_ID) var messageId: String?,
                     @ColumnInfo(name = DbConstants.MESSAGE_STATE_COLUMN_NAME_PRIORITY) var priority: String?,
                     @ColumnInfo(name = DbConstants.MESSAGE_STATE_COLUMN_NAME_READ_STATUS) var readStatus: String?,
                     @ColumnInfo(name = DbConstants.MESSAGE_STATE_COLUMN_NAME_DELETED) var isDeleted: Boolean?) {
    fun isRead(): Boolean {
        return TextUtils.equals(readStatus, Enums.SMSMessages.ReadStatus.READ)
    }
}