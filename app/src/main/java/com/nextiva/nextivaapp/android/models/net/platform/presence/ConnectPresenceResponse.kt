package com.nextiva.nextivaapp.android.models.net.platform.presence

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.constants.Enums
import java.io.Serializable

data class ConnectPresenceResponse(@SerializedName("uuid") var uuid: String?,
                                   @SerializedName("status", alternate = ["Status"]) var status: Int?,
                                   @SerializedName("CorpAccountNumber", alternate = ["corpAccountNumber"]) var corpAccountNumber: Int?,
                                   @SerializedName("Login", alternate = ["login"]) var login: Boolean?,
                                   @SerializedName("PreviousStatus", alternate = ["previousStatus"]) var previousStatus: Int?,
                                   @SerializedName("Lastseen", alternate = ["lastseen"]) var lastSeen: String?,
                                   @SerializedName("Activity", alternate = ["activity"]) var activity: Int?,
                                   @SerializedName("CurrentActiveDevice", alternate = ["currentActiveDevice"]) var currentActiveDevice: String?,
                                   @SerializedName("InCall", alternate = ["inCall"]) var inCall: Boolean?,
                                   @SerializedName("AutoAway", alternate = ["autoAway"]) var autoAway: Boolean?,
                                   @SerializedName("UserSettingStatus", alternate = ["userSettingStatus"]) var userSettingStatus: Int?,
                                   @SerializedName("userSettingStatusAutoUpdatedAt", alternate = ["UserSettingStatusAutoUpdatedAt"]) var userSettingStatusAutoUpdatedAt: String?,
                                   @SerializedName("userSettingStatusExpiresAt", alternate = ["UserSettingStatusExpiresAt"]) var userSettingStatusExpiresAt: String?,
                                   @SerializedName("CustomMessage", alternate = ["customMessage"]) var customMessage: String?) : Serializable {
    val presenceState: Int
        get() {
            return when (status) {
                Enums.Contacts.ConnectPresenceStates.AUTOMATIC -> Enums.Contacts.PresenceStates.CONNECT_AUTOMATIC
                Enums.Contacts.ConnectPresenceStates.ONLINE -> Enums.Contacts.PresenceStates.CONNECT_ONLINE
                Enums.Contacts.ConnectPresenceStates.ACTIVE -> Enums.Contacts.PresenceStates.CONNECT_ACTIVE
                Enums.Contacts.ConnectPresenceStates.DND -> Enums.Contacts.PresenceStates.CONNECT_DND
                Enums.Contacts.ConnectPresenceStates.AWAY -> Enums.Contacts.PresenceStates.CONNECT_AWAY
                Enums.Contacts.ConnectPresenceStates.BUSY -> Enums.Contacts.PresenceStates.CONNECT_BUSY
                Enums.Contacts.ConnectPresenceStates.BE_RIGHT_BACK -> Enums.Contacts.PresenceStates.CONNECT_BE_RIGHT_BACK
                Enums.Contacts.ConnectPresenceStates.OUT_OF_OFFICE -> Enums.Contacts.PresenceStates.CONNECT_OUT_OF_OFFICE
                else -> Enums.Contacts.PresenceStates.CONNECT_OFFLINE
            }
        }

}