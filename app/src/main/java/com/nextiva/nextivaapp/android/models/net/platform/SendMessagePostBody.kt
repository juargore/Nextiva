package com.nextiva.nextivaapp.android.models.net.platform

data class SendMessagePostBody(
        var destination: List<String>?,
        var message: String,
        var source: String?=null,
        var clientId: String,
        var teamId: String?
)