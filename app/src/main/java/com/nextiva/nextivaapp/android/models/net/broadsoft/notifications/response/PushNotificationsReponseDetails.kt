package com.nextiva.nextivaapp.android.models.net.broadsoft.notifications.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import java.io.Serializable

data class PushNotificationsResponseDetails(@field:Element(name = "applicationId", required = false) var applicationId: String? = null,
                                            @field:Element(name = "applicationVersion", required = false) var applicationVersion: String? = null,
                                            @field:Element(name = "registrationId", required = false) var registrationId: String? = null,
                                            @field:Element(name = "deviceOSType", required = false) var deviceOsType: String? = null,
                                            @field:Element(name = "deviceOSVersion", required = false) var deviceOsVersion: String? = null,
                                            @field:ElementList(name = "deviceTokenList", entry = "deviceToken", required = false) var deviceTokenList: ArrayList<PushNotificationsResponseDeviceToken>? = null) : Serializable