package com.nextiva.nextivaapp.android.models.net.broadsoft.calls

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "Calls", strict = false)
data class BroadsoftCallsResponseBody(@field:ElementList(name = "call", entry = "call", inline = true, required = false) var calls: ArrayList<BroadsoftCall> = ArrayList())
