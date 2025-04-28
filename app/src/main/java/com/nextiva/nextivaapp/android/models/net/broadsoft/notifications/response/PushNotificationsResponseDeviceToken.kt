package com.nextiva.nextivaapp.android.models.net.broadsoft.notifications.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import java.io.Serializable

data class PushNotificationsResponseDeviceToken(@field:Element(name = "token", required = false) var token: String? = "",
                                                @field:ElementList(name = "pushNotificationEvents", entry = "event", required = false) var pushNotificationEvents: ArrayList<String>? = null) : Serializable