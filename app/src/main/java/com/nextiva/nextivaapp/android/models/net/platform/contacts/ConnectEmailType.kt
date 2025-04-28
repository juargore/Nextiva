package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums

/**
 * Use this class to get contacts BackEnd Email type and transform it into Android email type
 *
 * Android uses [Integer] as its type field, and Backend uses [String]
 *
 */
enum class ConnectEmailType(val value: String) {
    Work("work"),
    Home("personal"),
    Mobile("mobile"),
    Other("other");

    val numericType: Int
        get() = when (this) {
            Work -> Enums.Contacts.EmailTypes.WORK_EMAIL
            Home -> Enums.Contacts.EmailTypes.HOME_EMAIL
            Mobile -> Enums.Contacts.EmailTypes.MOBILE_EMAIL
            else -> Enums.Contacts.EmailTypes.OTHER_EMAIL
        }

    val labelIdentifier: Int
        get() = when (this) {
            Work -> R.string.connect_email_type_work
            Home -> R.string.connect_email_type_home
            Mobile -> R.string.connect_email_type_mobile
            else -> R.string.connect_email_type_other
        }

    companion object {
        /**
         * User this function to convert a String that is compatible
         * with Backend email types
         * Note: If you send and invalid value function assigns
         * "Other" as its value
         *
         * @param type possible values: work, home, mobile, other
         * @return [ConnectEmailType] enum class
         */
        fun fromString(type: String?): ConnectEmailType =
            ConnectEmailType.values().firstOrNull() { emailType ->
                emailType.value == type
            } ?: Other

        /**
         * Use this function to convert a Android local phone type into Backend email
         * type.
         *
         * Note: if you send a value that is not in the standard types it will
         * be converted to default value "other"
         *
         * @param type this is the integer type from android email type
         * @return [ConnectEmailType] enum class object
         */
        fun fromIntType(type: Int): ConnectEmailType = when (type) {
            Enums.Contacts.EmailTypes.WORK_EMAIL,
            Enums.Contacts.EmailTypes.CONNECT_PRIMARY_EMAIL -> Work
            Enums.Contacts.EmailTypes.HOME_EMAIL,
            Enums.Contacts.EmailTypes.CONNECT_SECONDARY_EMAIL -> Home
            Enums.Contacts.EmailTypes.MOBILE_EMAIL -> Mobile
            else -> Other
        }
    }
}