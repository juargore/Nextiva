package com.nextiva.nextivaapp.android.models.net.platform.websocket

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebSocketConnectAuthenticationFilters(@SerializedName("include") var includedFilters: ArrayList<WebSocketConnectAuthenticationFilter>?,
                                                 @SerializedName("exclude") var excludedFilters: ArrayList<WebSocketConnectAuthenticationFilter>?) : Serializable