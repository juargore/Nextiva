package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RoomAttachmentResponse(
    @SerializedName("type") var type: String,
    @SerializedName("id") var id: String,
    @SerializedName("roomId") var roomId: String,
    @SerializedName("corpId") var corpId: String,
    @SerializedName("senderId") var senderId: String,
    @SerializedName("mentions") var mentions: ArrayList<String>?,
    @SerializedName("text") var text: String,
    @SerializedName("edited") var edited: Boolean,
    @SerializedName("delivered") var delivered: Boolean,
    @SerializedName("read") var read: Boolean,
    @SerializedName("deleted") var deleted: Boolean,
    @SerializedName("bot") var bot: Boolean,
    @SerializedName("restricted") var restricted: Boolean,
    @SerializedName("timestamp") var timestamp: String?,
    @SerializedName("reactions") var reactions: ArrayList<Reaction>?,
    @SerializedName("participants") var participants: ArrayList<Participant>?,
    @SerializedName("nonMemberMentions") var nonMemberMentions: ArrayList<String>?,
    @SerializedName("hasThread") var hasThread: Boolean,
    @SerializedName("attachments") var attachments: ArrayList<ChatMessageAttachment>?,
    @SerializedName("draft") var draft: Boolean,
) : Serializable
