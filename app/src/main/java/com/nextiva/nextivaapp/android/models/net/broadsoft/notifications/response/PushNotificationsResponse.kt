package com.nextiva.nextivaapp.android.models.net.broadsoft.notifications.response

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "PushNotificationRegistrations", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
data class PushNotificationsResponse(@field:ElementList(entry = "pushNotificationRegistration", inline = true, required = false)
                                     var pushNotificationRegistrations: ArrayList<PushNotificationsResponseDetails>? = null) : Serializable
