package com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "member", strict = false)
data class BroadsoftGroupMember(@field:Attribute(name = "id", required = false) var id: String? = null)