package com.nextiva.nextivaapp.android.models.net.broadsoft.notifications

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Order
import org.simpleframework.xml.Root

@Order(elements = ["token", "pushNotificationEvents"])
@Root(name = "deviceToken", strict = false)
data class DeviceToken(@field:Element(name = "token") var token: String? = "",
                       @field:ElementList(name = "pushNotificationEvents", entry = "event") var pushNotificationEvents: ArrayList<String>? = null)
