package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetChatMessageResponse(@SerializedName("Page") var pageInfo: Page,
                                  @SerializedName("Data") var messageItems: ArrayList<ChatMessage>,
         ) : Serializable

data class Page(@SerializedName("first") var firstPage: String,
                @SerializedName("prev") var previousPage: String,
                @SerializedName("next") var nextPage: String,
                @SerializedName("last") var lastPage: String
) : Serializable

data class ChatMessage(@SerializedName("type") var type: String,
                       @SerializedName("id") var id: String,
                       @SerializedName("roomId") var roomId: String,
                       @SerializedName("corpId") var corpId: String,
                       @SerializedName("senderId") var senderId: String,
                       @SerializedName("mentions") var mentions: ArrayList<String>,
                       @SerializedName("text") var text: String,
                       @SerializedName("edited") var edited: Boolean,
                       @SerializedName("delivered") var delivered: Boolean,
                       @SerializedName("read") var read: Boolean,
                       @SerializedName("deleted") var deleted: Boolean,
                       @SerializedName("timestamp") var timestamp: String?,
                       @SerializedName("reactions") var reactions: ArrayList<Reaction>,
                       @SerializedName("participants") var participants: ArrayList<Participant>,
                       @SerializedName("nonMemberMentions") var nonMemberMentions: ArrayList<String>,
                       @SerializedName("hasThread") var hasThread: Boolean,
                       @SerializedName("parentMessageId") var parentMessageId: String?,
                       @SerializedName("attachments") var attachments: ArrayList<ChatMessageAttachment>?
) : Serializable {

    constructor(id: String, roomId: String, senderId: String, text: String) : this(
        type = "",
        id = id,
        roomId = roomId,
        corpId = "",
        senderId = senderId,
        mentions = arrayListOf(),
        text = text,
        edited = false,
        delivered = false,
        read = false,
        deleted = false,
        timestamp = null,
        reactions = arrayListOf(),
        participants = arrayListOf(),
        nonMemberMentions = arrayListOf(),
        hasThread = false,
        parentMessageId = null,
        attachments = null
    )

}

data class Reaction(@SerializedName("reactionId") var reactionId: String,
                @SerializedName("userIds") var userIds: ArrayList<String>
) : Serializable

data class Participant(@SerializedName("id") var id: String,
                       @SerializedName("read") var read: Boolean,
                       @SerializedName("delivered") var delivered: Boolean,
                       @SerializedName("sender") var sender: Boolean,
                       @SerializedName("readTimestamp") var readTimestamp: String
) : Serializable

enum class ChatMessageType { HUMAN, NON_MEMBER_MENTION }
