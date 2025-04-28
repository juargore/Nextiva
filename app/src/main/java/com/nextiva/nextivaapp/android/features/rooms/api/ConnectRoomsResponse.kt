package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectRoomsResponse(@SerializedName("favoriteInteractionError") var favoriteInteractionError: Boolean?,
                                @SerializedName("rooms") var roomItems: ArrayList<ConnectRoom>?): Serializable

data class ConnectRoomResponse(@SerializedName("room") var room: ConnectRoom?): Serializable