package com.nextiva.nextivaapp.android.models.net.broadsoft.voicemail

import org.simpleframework.xml.Element
import java.io.Serializable

data class BroadsoftVoicemailMessageInfo(@field:Element(name = "duration", required = false) var duration: Int? = null,
                                         @field:Element(name = "callingPartyInfo", required = false) var callingPartyInfo: BroadsoftVoicemailCallingPartyInfo? = null,
                                         @field:Element(name = "time", required = false) var time: String? = null,
                                         @field:Element(name = "messageId", required = false) var messageId: String? = null,
                                         @field:Element(name = "read", required = false) var read: Void? = null) : Serializable