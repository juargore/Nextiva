package com.nextiva.nextivaapp.android.core.analytics.events

import com.nextiva.nextivaapp.android.core.analytics.interfaces.AnalyticEvent

class CallLogEvent : AnalyticEvent {
    override var name: String = ""
    override var properties: Map<String, Any>? = null

    fun gapDetected(): CallLogEvent {
        name = "call_log_gap_detected"
        return this
    }

    fun xBroadworksCorrelationHeader(): CallLogEvent {
        name = "x_broadworks_correlation"
        return this
    }
}