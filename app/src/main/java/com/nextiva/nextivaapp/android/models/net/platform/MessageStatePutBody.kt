package com.nextiva.nextivaapp.android.models.net.platform

data class MessageStatePutBody(
        var deleted: Boolean?,
        var messageId: String?,
        var priority: String?,
        var readStatus: String?
)
