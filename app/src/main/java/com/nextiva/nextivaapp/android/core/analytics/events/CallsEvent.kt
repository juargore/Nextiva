package com.nextiva.nextivaapp.android.core.analytics.events

import com.nextiva.nextivaapp.android.core.analytics.interfaces.AnalyticEvent
import com.nextiva.pjsip.pjsip_lib.sipservice.metrics.CallMetrics
import com.nextiva.pjsip.pjsip_lib.sipservice.metrics.CallStats

class CallsEvent : AnalyticEvent {
    override var name: String = ""
    override var properties: Map<String, Any>? = null

    private val disconnectedMetricPrefix = "[DISCONNECTED]"

    fun surveyUserForCall(): CallsEvent {
        name = "survey_user_for_call"
        return this
    }

    fun incoming(callStats: CallStats): CallsEvent {
        name = "incoming_call"
        properties = if (callStats.metrics?.rawMetrics != null && callStats.metrics?.rawMetrics?.startsWith(disconnectedMetricPrefix) != true) {
            mapOf("duration" to callStats.timeElapsed,
                "inbound_mos" to "${callStats.metrics?.getAverageInboundMOS()}",
                "outbound_mos" to "${callStats.metrics?.getAverageOutboundMOS()}",
                "inbound_jitter" to "${callStats.metrics?.rxData?.jitter?.formatted()}",
                "outbound_jitter" to "${callStats.metrics?.txData?.jitter?.formatted()}",
                "inbound_loss_period" to "${callStats.metrics?.rxData?.lossPeriod?.formatted()}",
                "outbound_loss_period" to "${callStats.metrics?.txData?.lossPeriod?.formatted()}",
                "inbound_packets_lost" to "${callStats.metrics?.rxData?.packetLoss}",
                "outbound_packets_lost" to "${callStats.metrics?.rxData?.packetLoss}")

        } else {
            mapOf("duration" to callStats.timeElapsed)
        }

        return this
    }

    fun outgoing(callStats: CallStats): CallsEvent {
        name = "outgoing_call"
        properties = if (callStats.metrics?.rawMetrics != null && callStats.metrics?.rawMetrics?.startsWith(disconnectedMetricPrefix) != true) {
            mapOf("duration" to callStats.timeElapsed,
                "inbound_mos" to "${callStats.metrics?.getAverageInboundMOS()}",
                "outbound_mos" to "${callStats.metrics?.getAverageOutboundMOS()}",
                "inbound_jitter" to "${callStats.metrics?.rxData?.jitter?.formatted()}",
                "outbound_jitter" to "${callStats.metrics?.txData?.jitter?.formatted()}",
                "inbound_loss_period" to "${callStats.metrics?.rxData?.lossPeriod?.formatted()}",
                "outbound_loss_period" to "${callStats.metrics?.txData?.lossPeriod?.formatted()}",
                "inbound_packets_lost" to "${callStats.metrics?.rxData?.packetLoss}",
                "outbound_packets_lost" to "${callStats.metrics?.rxData?.packetLoss}")

        } else {
            mapOf("duration" to callStats.timeElapsed)
        }

        return this
    }

    fun lostConnection(metrics: CallMetrics): CallsEvent {
        name = "lost_connection_shown"

        if (metrics.rawMetrics != null && metrics.rawMetrics?.startsWith(disconnectedMetricPrefix) != true) {
            properties = mapOf("inbound_mos" to "${metrics.getAverageInboundMOS()}",
                "outbound_mos" to "${metrics.getAverageOutboundMOS()}",
                "inbound_jitter" to "${metrics.rxData?.jitter?.formatted()}",
                "outbound_jitter" to "${metrics.txData?.jitter?.formatted()}",
                "inbound_loss_period" to "${metrics.rxData?.lossPeriod?.formatted()}",
                "outbound_loss_period" to "${metrics.txData?.lossPeriod?.formatted()}",
                "inbound_packets_lost" to "${metrics.rxData?.packetLoss}",
                "outbound_packets_lost" to "${metrics.rxData?.packetLoss}")
        }

        return this
    }

    fun poorConnection(metrics: CallMetrics): CallsEvent {
        name = "poor_connection_shown"

        if (metrics.rawMetrics != null && metrics.rawMetrics?.startsWith(disconnectedMetricPrefix) != true) {
            properties = mapOf("inbound_mos" to "${metrics.getAverageInboundMOS()}",
                "outbound_mos" to "${metrics.getAverageOutboundMOS()}",
                "inbound_jitter" to "${metrics.rxData?.jitter?.formatted()}",
                "outbound_jitter" to "${metrics.txData?.jitter?.formatted()}",
                "inbound_loss_period" to "${metrics.rxData?.lossPeriod?.formatted()}",
                "outbound_loss_period" to "${metrics.txData?.lossPeriod?.formatted()}",
                "inbound_packets_lost" to "${metrics.rxData?.packetLoss}",
                "outbound_packets_lost" to "${metrics.rxData?.packetLoss}")
        }

        return this
    }
}