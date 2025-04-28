package com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root

@Root(name = "contact_storage", strict = false)
@Namespace(reference = "bsft-private-storage")
data class BroadsoftContactStorage(@field:Element(name = "addressbook", required = false) var addressbook: BroadsoftAddressbook? = BroadsoftAddressbook())