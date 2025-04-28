package com.nextiva.nextivaapp.android.models.net.platform.voice

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbCallLogEntry
import com.nextiva.nextivaapp.android.db.model.DbVoicemail
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.util.CallUtil

data class VoiceMessage(@SerializedName("messageId") var messageId: String?,
                        @SerializedName("channel") var channel: String?,
                        @SerializedName("sent") var sent: String?,
                        @SerializedName("sender") var sender: VoiceMessageParticipant?,
                        @SerializedName("recipients") var recipients: ArrayList<VoiceMessageParticipant>?,
                        @SerializedName("messageState") var messageState: VoiceMessageState?,
                        @SerializedName("callDetails") var callDetails: VoiceMessageCallDetails?,
                        @SerializedName("voicemailDetails") var voicemailDetails: VoiceMessageVoicemailDetails?,
                        @SerializedName("direction") var direction: String?,
                        @SerializedName("eventType") var eventType: String?) {

    fun toDbCallLogEntry(formatterManager: FormatterManager): DbCallLogEntry {
        return toDbCallLogEntry(formatterManager, null)
    }

    fun toDbCallLogEntry(formatterManager: FormatterManager, pageNumber: Int?): DbCallLogEntry {
        val sentEpoch = if (sent.isNullOrEmpty()) {
            null
        } else {
            formatterManager.getVoiceConversationDateTime(sent).toEpochMilli()
        }

        val phoneNumber = if (direction == "INBOUND") {
                sender?.phoneNumber ?: ""
        } else recipients?.firstOrNull()?.phoneNumber ?: ""

        return DbCallLogEntry(
            id = null,
            callLogId = messageId,
            displayName = if (direction == "INBOUND") { sender?.name ?: callDetails?.callerId } else recipients?.firstOrNull()?.name ?: callDetails?.callerId,
            callDateTime = sentEpoch,
            countryCode = CallUtil.getCountryCode(),
            phoneNumber = phoneNumber,
            formattedPhoneNumber = CallUtil.getFormattedNumber(phoneNumber),
            callType = if (direction == "INBOUND") {
                if (callDetails?.answered == true) {
                    Enums.Calls.CallTypes.RECEIVED
                } else {
                    Enums.Calls.CallTypes.MISSED
                }
            } else Enums.Calls.CallTypes.PLACED,
            isRead = if (messageState?.readStatus == "READ") 1 else 0,
            contactId = if (direction == "INBOUND") { sender?.contactId } else if (recipients?.size ?: 0 == 1) recipients?.firstOrNull()?.contactId else null,
            pageNumber = pageNumber,
            callDuration = callDetails?.callDuration ?: 0,
            callStartTime = callDetails?.callStartTime ?: ""
        )
    }

    fun toDbVoicemail(formatterManager: FormatterManager): DbVoicemail {
        return toDbVoicemail(formatterManager, null)
    }

    fun toDbVoicemail(formatterManager: FormatterManager, pageNumber: Int?): DbVoicemail {
        val sentEpoch = if (sent.isNullOrEmpty()) {
            null
        } else {
            formatterManager.getVoiceConversationDateTime(sent).toEpochMilli()
        }

        return DbVoicemail(null,
            voicemailDetails?.duration,
            CallUtil.getStrippedPhoneNumber(sender?.phoneNumber ?: ""),
            sender?.name ?: callDetails?.callerId,
            sender?.contactId,
            sentEpoch,
            messageId,
            messageState?.readStatus == "READ",
            voicemailDetails?.transcript ?: "",
            voicemailDetails?.transcriptRating ?: "",
            CallUtil.getFormattedNumber(sender?.phoneNumber ?: ""),
            messageId,
            null,
            pageNumber,
            voicemailDetails?.callerId)
    }
}