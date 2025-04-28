package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(tableName = DbConstants.TABLE_NAME_MESSAGES)
data class DbMessage(@PrimaryKey(autoGenerate = true)
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_ID) var id: Long?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_FROM) var from: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_TO) var to: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_BODY) var body: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_MESSAGE_ID) var messageId: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_TIMESTAMP) var timestamp: Long?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_IS_SENDER) var isSender: Boolean?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_IS_READ) var isRead: Boolean?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_GUEST_FIRST) var guestFirst: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_GUEST_LAST) var guestLast: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_LANGUAGE) var language: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_PARTICIPANT) var participant: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_THREAD_ID) var threadId: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_MEMBERS) var members: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_TYPE) @Enums.Chats.ConversationTypes.Type var type: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_CHAT_WITH) var chatWith: String?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_SENT_STATUS) var sentStatus: Int?,
                     @ColumnInfo(name = DbConstants.MESSAGES_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?) {
    @Ignore
    constructor() : this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
}