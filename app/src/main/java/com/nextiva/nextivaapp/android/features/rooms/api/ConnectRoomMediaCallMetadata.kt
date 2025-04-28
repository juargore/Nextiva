package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectRoomMediaCallMetadata(
        @SerializedName("category") var category: String?,
        @SerializedName("mediaCallMeetingId") var mediaCallMeetingId: String?,
        @SerializedName("personalizedMeetingID") var personalizedMeetingID: String?,
        @SerializedName("dialInNumber") var dialInNumber: ArrayList<String>?,
        @SerializedName("HostPin") var hostPin: String?,
        @SerializedName("meetingSettings") var meetingSettings: ConnectRoomMediaCallMetadataSettings?,
): Serializable