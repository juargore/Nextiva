package com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(name = "communication", strict = false)
data class BroadsoftContactCommunication(@field:Attribute(name = "type", required = false) var type: String? = null,
                                         @field:Attribute(name = "pin1", required = false) var pinOne: String? = null,
                                         @field:Attribute(name = "pin2", required = false) var pinTwo: String? = null,
                                         @field:Text(required = false) var value: String? = null) {
    constructor(type: String, value: String) :
            this(type, null, null, value)
}