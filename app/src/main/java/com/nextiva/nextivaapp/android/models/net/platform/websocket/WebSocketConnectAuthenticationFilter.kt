package com.nextiva.nextivaapp.android.models.net.platform.websocket

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebSocketConnectAuthenticationFilter(@SerializedName("channel") var channel: String?,
                                                @SerializedName("types") var types: ArrayList<String>?) : Serializable