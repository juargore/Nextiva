package com.nextiva.nextivaapp.android.models.net.broadsoft.voicemail

import org.simpleframework.xml.Element
import java.io.Serializable

data class BroadsoftVoicemailCallingPartyInfo(@field:Element(name = "name", required = false) var name: String? = null,
                                              @field:Element(name = "userId", required = false) var userId: String? = null,
                                              @field:Element(name = "address", required = false) var address: String? = null) : Serializable