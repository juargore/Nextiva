package com.nextiva.nextivaapp.android.models

import java.io.Serializable

data class SmsConversation(var groupBy: String?,
                            var messageList: ArrayList<SmsMessage>?): Serializable {
    constructor() : this(null, null)
}