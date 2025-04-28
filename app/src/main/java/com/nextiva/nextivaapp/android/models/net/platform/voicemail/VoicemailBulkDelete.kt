package com.nextiva.nextivaapp.android.models.net.platform.voicemail

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BulkActionsConversationData(
    @SerializedName("jobType") var jobType: String?,
    @SerializedName("isSoftDelete") var isSoftDelete: Boolean?,
    @SerializedName("modifications") var modifications: Modifications?,
    @SerializedName("channels") var channels: Channels?
) : Serializable {
    companion object {
        const val IDENTIFIER_MESSAGE_ID = "MESSAGE_ID"
        const val IDENTIFIER_GROUP_ID = "GROUP_ID"
        const val JOB_TYPE_DELETE = "DELETE"
        const val JOB_TYPE_UPDATE = "UPDATE"
        const val MODIFICATION_STATUS_READ = "READ"
        const val MODIFICATION_STATUS_UNREAD = "UNREAD"
    }
}

data class Modifications(
    @SerializedName("readStatus") var readStatus: String? = null
)
data class Channels(
    @SerializedName("VOICEMAIL") var VOICEMAIL: VOICE? = null,
    @SerializedName("VOICE") var VOICE: VOICE? = null,
    @SerializedName("SMS") var SMS: SMS? = null,
) : Serializable


data class VOICE(
    @SerializedName("identifierType") var identifierType: String?,
    @SerializedName("identifiers") var identifiers: ArrayList<String>?
) : Serializable

data class SMS(
        @SerializedName("identifierType") var identifierType: String?,
        @SerializedName("identifiers") var identifiers: ArrayList<String>?
) : Serializable
