package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectRoomFavoriteBody(@SerializedName("member") var connectRoomFavoriteMemberBody: ConnectRoomFavoriteMemberBody?) : Serializable {
}