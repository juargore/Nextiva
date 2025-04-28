package com.nextiva.nextivaapp.android.models.net.broadsoft.calls

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "endpoint", strict = false)
data class BroadsoftCallEndpoint(@field:Element(name = "addressOfRecord", required = false) var addressOfRecord: String? = "")