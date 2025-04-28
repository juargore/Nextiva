package com.nextiva.nextivaapp.android.models.net.broadsoft.calls

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Call", strict = false)
data class BroadsoftCallDetails(@field:Element(name = "callId", required = false) var callId: String? = "",
                                @field:Element(name = "extTrackingId", required = false) var externalTrackingId: String? = "",
                                @field:Element(name = "personality", required = false) var personality: String? = "",
                                @field:Element(name = "state", required = false) var state: String? = "",
                                @field:Element(name = "remoteParty", required = false) var remoteParty: BroadsoftCallRemoteParty? = null,
                                @field:Element(name = "endpoint", required = false) var endpoint: BroadsoftCallEndpoint? = null,
                                @field:Element(name = "appearance", required = false) var appearance: String? = "",
                                @field:Element(name = "startTime", required = false) var startTime: String? = "",
                                @field:Element(name = "answerTime", required = false) var answerTime: String? = "")