package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums.Contacts.PhoneTypes

/**
 * Use this class to get contacts BackEnd Phone type and transform it into Android phone type
 * specially since Backend doesn't have a "work_extension" type.
 *
 * Android uses [Integer] as its type field, and Backend uses [String]
 */
enum class ConnectPhoneType(val value: String) {
    Work("work"),
    Home("home"),
    Personal("personal"),
    Assistant("assistant"),
    Mobile("mobile"),
    Other("other");

    val numericType: Int
        get() = when (this) {
            Work -> PhoneTypes.WORK_PHONE
            Home -> PhoneTypes.HOME_PHONE
            Mobile -> PhoneTypes.MOBILE_PHONE
            else -> PhoneTypes.OTHER_PHONE
        }

    val labelIdentifier: Int
        get() = when (this) {
            Work -> R.string.connect_phone_type_work
            Home -> R.string.connect_phone_type_home
            Mobile -> R.string.connect_phone_type_mobile
            else -> R.string.connect_phone_type_other
        }

    companion object {

        /**
         * User this function to convert a String that is compatible
         * with Backend phone types
         *
         * Note: If you send and invalid value function assigns
         * "Other" as its value
         *
         * @param type possible values: work, home, personal, assistant, mobile, other
         * @return [ConnectPhoneType] enum class
         */
        fun fromString(type: String?): ConnectPhoneType =
            ConnectPhoneType.values().firstOrNull { phoneType ->
                phoneType.value == type
            } ?: Other

        /**
         * Use this function to convert a Android phone type into Backend phone type.
         *
         * Note: if you send a value that is not in the backend supported type it will
         * be converted to default value "other"
         *
         * @param type this is the integer type from android phone type
         * @return [ConnectPhoneType] enum class object
         */
        fun fromIntType(type: Int): ConnectPhoneType = when (type) {
            PhoneTypes.WORK_EXTENSION, PhoneTypes.WORK_PHONE -> Work
            PhoneTypes.HOME_PHONE -> Home
            PhoneTypes.WORK_MOBILE_PHONE, PhoneTypes.MOBILE_PHONE -> Mobile
            else -> Other
        }
    }
}