package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectRoomRecentActivity(
        @SerializedName("type") var type: String?,
        @SerializedName("timestamp") var timestamp: String?
): Serializable