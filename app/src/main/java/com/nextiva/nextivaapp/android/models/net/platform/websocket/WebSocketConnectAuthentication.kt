package com.nextiva.nextivaapp.android.models.net.platform.websocket

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebSocketConnectAuthentication(@SerializedName("nextiva-context-corpAcctNumber") var corporateAccountNumber: Int?,
                                          @SerializedName("sessionId") var sessionId: String?,
                                          @SerializedName("filters") var filters: WebSocketConnectAuthenticationFilters?) : Serializable