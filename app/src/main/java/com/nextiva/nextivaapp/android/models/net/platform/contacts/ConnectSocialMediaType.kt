package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.nextiva.nextivaapp.android.constants.Enums

/**
 * Use this class to get BackEnd social media type and transform it into Android social media type
 *
 * Android uses [Integer] as its type field, and Backend uses [String]
 */
enum class ConnectSocialMediaType(val value: String) {
    Facebook("facebook"),
    Instagram("instagram"),
    LinkedIn("linkedin"),
    Telegram("telegram"),
    Twitter("twitter"),
    Other("other");

    val numericType: Int
        get() = when (this) {
            Facebook -> Enums.Contacts.SocialMediaType.FACEBOOK
            Instagram -> Enums.Contacts.SocialMediaType.INSTAGRAM
            LinkedIn -> Enums.Contacts.SocialMediaType.LINKEDIN
            Telegram -> Enums.Contacts.SocialMediaType.TELEGRAM
            Twitter -> Enums.Contacts.SocialMediaType.TWITTER
            else -> Enums.Contacts.SocialMediaType.OTHER
        }

    companion object {
        /**
         * User this function to convert a String that is compatible
         * with Backend social media type
         *
         * Note: If you send and invalid value function assigns
         * "Other" as its value
         *
         * @param type possible values: facebook, instagram, linked, telegram, twitter, other
         * @return [ConnectSocialMedia] enum class
         */
        fun fromString(type: String?): ConnectSocialMediaType =
            ConnectSocialMediaType.values().firstOrNull() { emailType ->
                emailType.value == type
            } ?: Other

        /**
         * Use this function to convert a Android social media type into Backend social media type.
         *
         * Note: if you send a value that is not in the backend supported type it will
         * be converted to default value "other"
         *
         * @param type this is the integer type from android social media type
         * @return [ConnectSocialMedia] enum class object
         */
        fun fromIntType(type: Int): ConnectSocialMediaType = when (type) {
            Enums.Contacts.SocialMediaType.FACEBOOK -> Facebook
            Enums.Contacts.SocialMediaType.INSTAGRAM -> Instagram
            Enums.Contacts.SocialMediaType.LINKEDIN -> LinkedIn
            Enums.Contacts.SocialMediaType.TELEGRAM -> Telegram
            Enums.Contacts.SocialMediaType.TWITTER -> Twitter
            else -> Other
        }
    }
}
