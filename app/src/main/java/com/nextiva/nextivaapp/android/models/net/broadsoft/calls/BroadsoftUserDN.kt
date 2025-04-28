package com.nextiva.nextivaapp.android.models.net.broadsoft.calls

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(name = "userDN", strict = false)
data class BroadsoftUserDN(@field:Attribute(name = "countryCode", required = false) var countryCode: String? = "",
                           @field:Text(required = false) var number: String? = "")