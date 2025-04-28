package com.nextiva.nextivaapp.android.models.net.platform.messages

import com.google.gson.annotations.SerializedName

data class JobChannel(
    @SerializedName("identifierType") var identifierType: String?,
    @SerializedName("identifiers") var identifiers: ArrayList<String>?)

data class JobChannels(
    @SerializedName("SMS") var SMS: JobChannel?,
    @SerializedName("VOICE") var VOICE: JobChannel?,
    @SerializedName("VOICEMAIL") var VOICEMAIL: JobChannel?)

data class JobModifications(
    @SerializedName("readStatus") var readStatus: String?)

data class SmsMessageBulkAction(
    @SerializedName("id") var id: String?,
    @SerializedName("userId") var userId: String?,
    @SerializedName("corpAccountNumber") var corpAccountNumber: String?,
    @SerializedName("jobType") var jobType: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("createdAt") var createdAt: String?,
    @SerializedName("modifiedAt") var modifiedAt: String?,
    @SerializedName("startedAt") var startedAt: String?,
    @SerializedName("originalRequestId") var originalRequestId: String?,
    @SerializedName("softDelete") var softDelete: Boolean?,
    @SerializedName("hardDelete") var hardDelete: Boolean?,
    @SerializedName("channels") var channels: JobChannels?,
    @SerializedName("modifications") var modifications: JobModifications?,
) {
    companion object {
        const val JOB_TYPE_DELETE = "DELETE"
        const val JOB_TYPE_UPDATE = "UPDATE"
        const val IDENTIFIER_TYPE_GROUP_ID = "GROUP_ID"
        const val IDENTIFIER_TYPE_MESSAGE_ID = "MESSAGE_ID"
        const val STATUS_COMPLETE = "COMPLETE"
    }
}

data class SmsMessageBulkActionList(
    @SerializedName("pageNumber") var pageNumber: Int?,
    @SerializedName("pageSize") var pageSize: Int?,
    @SerializedName("hasNext") var hasNext: Boolean?,
    @SerializedName("data") var data: ArrayList<SmsMessageBulkAction>?)
