package com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Order
import org.simpleframework.xml.Root

@Order(attributes = ["id", "q", "display_name"])
@Root(name = "group", strict = false)
data class BroadsoftAddressbookGroup(@field:Attribute(name = "id", required = false) var id: String? = null,
                                     @field:Attribute(name = "q", required = false) var position: Int? = null,
                                     @field:Attribute(name = "display_name", required = false) var displayName: String? = null,
                                     @field:ElementList(name = "member", inline = true, required = false) var members: ArrayList<BroadsoftGroupMember>? = ArrayList())