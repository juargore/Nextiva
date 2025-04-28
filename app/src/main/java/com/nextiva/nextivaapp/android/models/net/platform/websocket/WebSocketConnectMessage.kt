package com.nextiva.nextivaapp.android.models.net.platform.websocket

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebSocketConnectMessage(@SerializedName("corp_account") var corporateAccountNumber: Int?,
                                   @SerializedName("recipient_user_uuids") var recipientUserUuids: ArrayList<String>?,
                                   @SerializedName("message_uuid") var messageUuid: String?,
                                   @SerializedName("application") var application: String?,
                                   @SerializedName("payload") var payload: String?,
                                   @SerializedName("type") var type: String?,
                                   @SerializedName("creation_time") var creationTime: String?): Serializable