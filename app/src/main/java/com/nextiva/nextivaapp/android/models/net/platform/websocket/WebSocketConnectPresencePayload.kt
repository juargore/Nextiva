package com.nextiva.nextivaapp.android.models.net.platform.websocket

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.constants.Enums
import java.io.Serializable

data class WebSocketConnectPresencePayload(
    @SerializedName("UserID") var userId: String?,
    @SerializedName("Status") private var status: String?,
    @SerializedName("expiresAt") var statusExpiresAt: String?,
    @SerializedName("CustomMessage") var customMessage: String?,
    @SerializedName("isSystemDerivedStatus") var isSystemDerivedStatus: Boolean,
    @SerializedName("Application") var application: String?,
    @SerializedName("MessageKey") var messageKey: String?,
    @SerializedName("inCall") var inCall: Boolean?,
) : Serializable {
    val presenceState: Int
        get() {
            return when (status) {
                "MESSAGE_STATE_ONLINE" -> Enums.Contacts.PresenceStates.CONNECT_ONLINE
                "MESSAGE_STATE_AWAY" -> Enums.Contacts.PresenceStates.CONNECT_AWAY
                "MESSAGE_STATE_BUSY" -> Enums.Contacts.PresenceStates.CONNECT_BUSY
                "MESSAGE_STATE_DND" -> Enums.Contacts.PresenceStates.CONNECT_DND
                else -> Enums.Contacts.PresenceStates.CONNECT_OFFLINE
            }
        }
}