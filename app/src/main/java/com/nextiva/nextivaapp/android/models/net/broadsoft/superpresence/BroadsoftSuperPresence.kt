package com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbPresence
import java.io.Serializable
import java.util.Locale

data class BroadsoftSuperPresence(@SerializedName("fullJid") var fullJid: String?,
                                  @SerializedName("show") var show: String?,
                                  @SerializedName("priority") var priority: Int?,
                                  @SerializedName("location") var location: BroadsoftSuperPresenceLocation?,
                                  @SerializedName("locationText") var locationText: BroadsoftSuperPresenceLocationText?,
                                  @SerializedName("locationSource") var locationSource: String?,
                                  @SerializedName("freeText") var freeText: String?,
                                  @SerializedName("timestamp") var timestamp: Long?,
                                  @SerializedName("isEmpty") var isEmpty: Boolean) : Serializable {

    fun toDbPresence(): DbPresence? {
        val presenceShow = when (show?.toLowerCase(Locale.getDefault())) {
            Enums.Contacts.BroadsoftPresenceState.AVAILABLE -> Enums.Contacts.PresenceStates.AVAILABLE
            Enums.Contacts.BroadsoftPresenceState.AWAY -> Enums.Contacts.PresenceStates.AWAY
            Enums.Contacts.BroadsoftPresenceState.BUSY -> Enums.Contacts.PresenceStates.BUSY
            Enums.Contacts.BroadsoftPresenceState.OFFLINE -> Enums.Contacts.PresenceStates.OFFLINE
            else -> Enums.Contacts.PresenceStates.NONE
        }

        return DbPresence(null, null, null, fullJid, presenceShow, Enums.Contacts.PresenceTypes.AVAILABLE, priority
                ?: -128, freeText, null, null, null)
    }
}