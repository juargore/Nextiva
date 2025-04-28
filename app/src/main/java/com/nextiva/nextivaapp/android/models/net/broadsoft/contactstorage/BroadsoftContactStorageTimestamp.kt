package com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(name = "timestamp", strict = false)
data class BroadsoftContactStorageTimestamp(@field:Attribute(name = "xmlns", required = false) var namespace: String = "bsft-private-storage",
                                            @field:Text var value: String? = null) {
    constructor(timestamp: String) :
            this("bsft-private-storage", timestamp)
}