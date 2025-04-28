package com.nextiva.nextivaapp.android.models.net.broadsoft.ondemandpresence

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbPresence
import java.io.Serializable
import java.util.Locale

data class BroadsoftOnDemandPresence(@SerializedName("jid") var jid: String? = "",
                                     @SerializedName("show") var show: String? = "",
                                     @SerializedName("pri") var priority: Int? = -128,
                                     @SerializedName("text") var status: String? = "",
                                     @SerializedName("loc") var location: BroadsoftOnDemandPresenceLocation?) : Serializable {

    fun toDbPresence(): DbPresence {
        val dbPresence = DbPresence()

        dbPresence.jid = jid
        dbPresence.type = Enums.Contacts.PresenceTypes.AVAILABLE

        priority?.let {
            dbPresence.priority = it
        }

        dbPresence.status = status

        dbPresence.state = when (show?.toLowerCase(Locale.getDefault())) {
            AVAILABLE -> Enums.Contacts.PresenceStates.AVAILABLE
            AWAY -> Enums.Contacts.PresenceStates.AWAY
            BUSY -> Enums.Contacts.PresenceStates.BUSY
            OFFLINE -> Enums.Contacts.PresenceStates.OFFLINE
            else -> Enums.Contacts.PresenceStates.NONE
        }

        return dbPresence
    }

    companion object {
        const val AVAILABLE = "available"
        const val AWAY = "away"
        const val BUSY = "dnd"
        const val OFFLINE = "offline"
    }
}