package com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "favourite", strict = false)
data class BroadsoftAddressbookFavorite(@field:Attribute(name = "id", required = false) var id: String? = null,
                                        @field:Attribute(name = "q", required = false) var q: String? = null)