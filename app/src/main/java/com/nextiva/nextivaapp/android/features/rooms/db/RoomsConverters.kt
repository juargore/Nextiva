package com.nextiva.nextivaapp.android.features.rooms.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.nextiva.nextivaapp.android.features.rooms.api.*

class RoomsConverters {

    @TypeConverter
    fun adminListToJson(value: List<ConnectRoomAdmin>?) = Gson().toJson(value)

    @TypeConverter
    fun adminJsonToList(value: String) = Gson().fromJson(value, Array<ConnectRoomAdmin>::class.java).toList()

    @TypeConverter
    fun memberListToJson(value: List<ConnectRoomMember>?) = Gson().toJson(value)

    @TypeConverter
    fun memberJsonToList(value: String) = Gson().fromJson(value, Array<ConnectRoomMember>::class.java).toList()

    @TypeConverter
    fun connectRoomMediaCallMetadataToJson(value: ConnectRoomMediaCallMetadata?) = Gson().toJson(value)

    @TypeConverter
    fun connectRoomMediaCallMetadataJson(value: String) = Gson().fromJson(value, ConnectRoomMediaCallMetadata::class.java)

    @TypeConverter
    fun reactionListToJson(value: List<Reaction>?) = Gson().toJson(value)

    @TypeConverter
    fun reactionJsonToList(value: String?) = Gson().fromJson(value, Array<Reaction>::class.java)?.toList()

    @TypeConverter
    fun participantListToJson(value: List<Participant>?) = Gson().toJson(value)

    @TypeConverter
    fun participantJsonToList(value: String?) = Gson().fromJson(value, Array<Participant>::class.java)?.toList()

    @TypeConverter
    fun stringListToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    fun stringJsonToList(value: String?) = Gson().fromJson(value, Array<String>::class.java)?.toList()

    @TypeConverter
    fun attachmentListToJson(value: List<ChatMessageAttachment>?) = Gson().toJson(value)

    @TypeConverter
    fun attachmentJsonToList(value: String?) = Gson().fromJson(value, Array<ChatMessageAttachment>::class.java)?.toList()
}
