package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectRoomAdmin( @SerializedName("userUuid") var userUuid: String?) : Serializable {
}