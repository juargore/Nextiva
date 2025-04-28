package com.nextiva.nextivaapp.android.models.net.platform

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.db.model.DbParticipant
import com.nextiva.nextivaapp.android.models.net.platform.messages.SmsTeamPayload
import com.nextiva.nextivaapp.android.util.CallUtil
import java.util.UUID

data class Participant (
    var email: String? = null,
    var name: String? = null,
    @SerializedName("phoneNumber") var serverNumber: String?,
    var teamUuids: List<String>? = null,
    var type: String? = null,
    var userUuid: String? = null,
    var teams: List<SmsTeamPayload>? = null){

    val phoneNumber: String?
        get() {
            return CallUtil.getStrippedPhoneNumber(serverNumber ?: "")
        }

    fun getDbParticipant(groupId: String): DbParticipant {
        if (userUuid.isNullOrEmpty()) {
            userUuid = UUID.randomUUID().toString()
        }
        return DbParticipant(null, name, email, phoneNumber, teamUuids?.joinToString(separator = ","), userUuid, groupId)
    }
}