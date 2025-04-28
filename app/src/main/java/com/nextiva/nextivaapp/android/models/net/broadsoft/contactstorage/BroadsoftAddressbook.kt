package com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Order
import org.simpleframework.xml.Root

@Order(elements = ["contacts", "groups", "favourites"])
@Root(name = "addressbook", strict = false)
data class BroadsoftAddressbook(@field:Attribute(name = "xsi:noNamespaceSchemaLocation", required = false) var noNamespaceSchemaLocation: String? = "",
                                @field:Attribute(name = "schema-version", required = false) var schemaVersion: String? = "",
                                @field:Attribute(name = "xmlns:xsi", required = false) var xsiNamespace: String? = "",
                                @field:Attribute(name = "timestamp", required = false) var timestamp: String? = "",
                                @field:Attribute(name = "version", required = false) var version: String? = "",
                                @field:ElementList(name = "contacts", required = false) var contacts: ArrayList<BroadsoftAddressbookContact>? = ArrayList(),
                                @field:ElementList(name = "groups", required = false) var groups: ArrayList<BroadsoftAddressbookGroup>? = ArrayList(),
                                @field:ElementList(name = "favourites", required = false) var favorites: ArrayList<BroadsoftAddressbookFavorite>? = ArrayList())