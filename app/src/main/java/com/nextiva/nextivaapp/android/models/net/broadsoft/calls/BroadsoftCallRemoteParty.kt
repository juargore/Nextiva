package com.nextiva.nextivaapp.android.models.net.broadsoft.calls

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "remoteParty", strict = false)
data class BroadsoftCallRemoteParty(@field:Element(name = "name", required = false) var name: String? = "",
                                    @field:Element(name = "address", required = false) var address: String? = "",
                                    @field:Element(name = "userId", required = false) var userId: String? = "",
                                    @field:Element(name = "userDN", required = false) var userDN: BroadsoftUserDN? = null,
                                    @field:Element(name = "callType", required = false) var callType: String = "")