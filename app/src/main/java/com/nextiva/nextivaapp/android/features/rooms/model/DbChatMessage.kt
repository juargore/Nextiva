package com.nextiva.nextivaapp.android.features.rooms.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.features.rooms.api.ChatMessage
import com.nextiva.nextivaapp.android.features.rooms.api.ChatMessageAttachment
import com.nextiva.nextivaapp.android.features.rooms.api.Participant
import com.nextiva.nextivaapp.android.features.rooms.api.Reaction
import com.nextiva.nextivaapp.android.features.rooms.db.DbConstants
import com.nextiva.nextivaapp.android.managers.FormatterManager
import org.threeten.bp.Instant

@Entity(tableName = DbConstants.TABLE_NAME_CHAT_MESSAGES)
data class DbChatMessage(
    @PrimaryKey @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_ID) var id: String,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_TYPE) var type: String?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_ROOM_ID) var roomId: String?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_CORP_ID) var corpId: String?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_SENDER_ID) var senderId: String?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_MENTIONS) var mentions: List<String>?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_TEXT) var text: String?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_EDITED) var edited: Boolean?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_DELIVERED) var delivered: Boolean?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_READ) var read: Boolean?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_TIMESTAMP) var timestamp: Instant?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_REACTIONS) var reactions: List<Reaction>?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_PARTICIPANTS) var participants: List<Participant>?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_NON_MEMBER_MENTIONS) var nonMemberMentions: List<String>?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_HAS_THREAD) var hasThread: Boolean?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_PARENT_MESSAGE_ID) var parentMessageId: String?,
    @ColumnInfo(name = DbConstants.CHAT_MESSAGE_COLUMN_NAME_ATTACHMENTS) var attachments: List<ChatMessageAttachment>?,
) {

    companion object {
        const val UNSENT_FLAG = "-UNSENT"
    }

    @Ignore
    constructor(chatMessage: ChatMessage) : this(
        id = chatMessage.id,
        type = chatMessage.type,
        roomId = chatMessage.roomId,
        corpId = chatMessage.corpId,
        senderId = chatMessage.senderId,
        mentions = chatMessage.mentions,
        text = chatMessage.text,
        edited = chatMessage.edited,
        delivered = chatMessage.delivered,
        read = chatMessage.read,
        timestamp = Instant.from(FormatterManager.getInstance().getRoomsConversationDateTime(chatMessage.timestamp)),
        reactions = chatMessage.reactions,
        participants = chatMessage.participants,
        nonMemberMentions = chatMessage.nonMemberMentions,
        hasThread = chatMessage.hasThread,
        parentMessageId = chatMessage.parentMessageId,
        attachments = chatMessage.attachments
    )

}