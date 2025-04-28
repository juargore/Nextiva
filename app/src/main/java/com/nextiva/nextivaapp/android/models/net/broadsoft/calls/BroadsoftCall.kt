package com.nextiva.nextivaapp.android.models.net.broadsoft.calls

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "call", strict = false)
data class BroadsoftCall(@field:Attribute(name = "inConference", required = false) var inConference: String? = "false",
                         @field:Element(name = "callId", required = false) var callId: String? = "",
                         @field:Element(name = "uri", required = false) var uri: String? = "") {
    val isConferenceCall: Boolean
        get() {
            inConference?.let {
                return inConference == "true"
            }

            return false
        }
}