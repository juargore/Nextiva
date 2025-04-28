package com.nextiva.nextivaapp.android.models.net.platform

data class SendMessageResponse(
    var client: String? = null,
    var messageId: String? = null,
    var groupId: String? = null,
    var status_code: Int? = null
)