package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectRoomMediaCallMetadataSettings(
        @SerializedName("allowBeforeHost") var allowBeforeHost: Boolean?,
        @SerializedName("hostUuids") var hostUuids: ArrayList<String>?
): Serializable