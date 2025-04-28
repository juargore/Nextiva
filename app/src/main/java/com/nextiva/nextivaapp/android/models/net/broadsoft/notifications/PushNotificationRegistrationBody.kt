package com.nextiva.nextivaapp.android.models.net.broadsoft.notifications

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Order
import org.simpleframework.xml.Root

@Order(elements = ["applicationId", "applicationVersion", "registrationId", "deviceOSType", "deviceOSVersion", "deviceTokenList"])
@Root(name = "PushNotificationRegistration", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
data class PushNotificationRegistrationBody(@field:Element(name = "applicationId") var applicationId: String?,
                                            @field:Element(name = "applicationVersion") var applicationVersion: String?,
                                            @field:Element(name = "registrationId") var registrationId: String?,
                                            @field:Element(name = "deviceOSType") var deviceOsType: String?,
                                            @field:Element(name = "deviceOSVersion") var deviceOsVersion: String?,
                                            @field:ElementList(name = "deviceTokenList") var deviceTokenList: ArrayList<DeviceToken>?)
