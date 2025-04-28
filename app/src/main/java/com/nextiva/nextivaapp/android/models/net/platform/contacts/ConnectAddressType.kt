package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.nextiva.nextivaapp.android.constants.Enums

/**
 * Use this class to get BackEnd address type and transform it into Android address type
 *
 * Android uses [Integer] as its type field, and Backend uses [String]
 */
enum class ConnectAddressType(val value: String) {
    Work("work"),
    Home("home"),
    Shipping("shipping"),
    Billing("billing"),
    Other("other");

    val numericType: Int
        get() = when (this) {
            Work -> Enums.Contacts.AddressType.WORK
            Home -> Enums.Contacts.AddressType.HOME
            Shipping -> Enums.Contacts.AddressType.SHIPPING
            Billing -> Enums.Contacts.AddressType.BILLING
            else -> Enums.Contacts.AddressType.OTHER
        }

    companion object {
        fun fromString(type: String?): ConnectAddressType =
            ConnectAddressType.values().firstOrNull { emailType ->
                emailType.value == type
            } ?: Other


        fun fromIntType(type: Int): ConnectAddressType = when (type) {
            Enums.Contacts.AddressType.WORK -> Work
            Enums.Contacts.AddressType.HOME -> Home
            Enums.Contacts.AddressType.SHIPPING -> Shipping
            Enums.Contacts.AddressType.BILLING -> Billing
            else -> Other
        }
    }
}