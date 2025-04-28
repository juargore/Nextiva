package com.nextiva.nextivaapp.android.core.analytics.events

import com.nextiva.nextivaapp.android.core.analytics.interfaces.AnalyticEvent

class MessagingEvent : AnalyticEvent {
    override var name: String = ""
    override var properties: Map<String, Any>? = null

    fun incomingSMS(): MessagingEvent {
        name = "incoming_sms"
        return this
    }

    fun outgoingSMS(): MessagingEvent {
        name = "outgoing_sms"
        return this
    }
}