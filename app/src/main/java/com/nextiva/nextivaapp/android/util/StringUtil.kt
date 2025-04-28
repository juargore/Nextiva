/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android.util

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import com.google.android.gms.common.util.Hex
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Contacts.AddressType
import com.nextiva.nextivaapp.android.constants.Enums.Contacts.SocialMediaType
import com.nextiva.nextivaapp.android.db.model.EmailAddress
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import com.nextiva.nextivaapp.android.util.extensions.redactByPattern
import java.nio.charset.StandardCharsets
import java.util.LinkedList
import java.util.Queue
import java.util.Scanner
import java.util.regex.Pattern

/**
 * Created by joedephillipo on 2/21/18.
 */
object StringUtil {
    @JvmStatic
    fun fromHtml(html: String?): Spanned {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
        return result
    }

    @JvmStatic
    fun getPhoneNumberTypeLabel(context: Context, phoneNumber: PhoneNumber, isRosterContact: Boolean, isConnect: Boolean): String {
        return when (phoneNumber.type) {
            Enums.Contacts.PhoneTypes.PHONE -> context.getString(R.string.phone_type_general)
            Enums.Contacts.PhoneTypes.WORK_PHONE -> if (isConnect) context.getString(R.string.connect_phone_type_work) else context.getString(
                    R.string.phone_type_work
                )
            Enums.Contacts.PhoneTypes.WORK_EXTENSION -> if (isConnect) context.getString(R.string.connect_phone_type_work_extension) else context.getString(
                    R.string.phone_type_work_extension
                )
            Enums.Contacts.PhoneTypes.WORK_MOBILE_PHONE -> context.getString(R.string.phone_type_mobile_work)
            Enums.Contacts.PhoneTypes.WORK_PAGER -> context.getString(R.string.phone_type_pager_work)
            Enums.Contacts.PhoneTypes.WORK_FAX -> context.getString(R.string.phone_type_fax_work)
            Enums.Contacts.PhoneTypes.HOME_PHONE -> {
                when {
                    isConnect -> context.getString(R.string.connect_phone_type_home)
                    isRosterContact -> context.getString(R.string.phone_type_personal)
                    else -> context.getString(R.string.phone_type_home)
                }
            }
            Enums.Contacts.PhoneTypes.MOBILE_PHONE -> if (isConnect) context.getString(R.string.connect_phone_type_mobile) else context.getString(
                    R.string.phone_type_mobile
                )
            Enums.Contacts.PhoneTypes.PAGER -> context.getString(R.string.phone_type_pager)
            Enums.Contacts.PhoneTypes.HOME_FAX -> context.getString(R.string.phone_type_fax_home)
            Enums.Contacts.PhoneTypes.MAIN_PHONE -> if (isConnect) context.getString(R.string.connect_phone_type_main) else context.getString(
                    R.string.phone_type_main
                )
            Enums.Contacts.PhoneTypes.CONFERENCE_PHONE -> context.getString(R.string.phone_type_conference_number)
            Enums.Contacts.PhoneTypes.CUSTOM_PHONE -> {
                if (!TextUtils.isEmpty(phoneNumber.label)) {
                    phoneNumber.label ?: context.getString(R.string.phone_type_general)
                } else {
                    context.getString(R.string.phone_type_custom)
                }
            }
            Enums.Contacts.PhoneTypes.OTHER_PHONE -> if (isConnect) context.getString(R.string.connect_phone_type_other) else context.getString(
                    R.string.phone_type_other
                )
            Enums.Contacts.PhoneTypes.OTHER_FAX -> context.getString(R.string.phone_type_other_fax)
            Enums.Contacts.PhoneTypes.COMPANY_MAIN -> context.getString(R.string.phone_type_company_main)
            Enums.Contacts.PhoneTypes.ASSISTANT -> context.getString(R.string.phone_type_assistant)
            Enums.Contacts.PhoneTypes.CAR -> context.getString(R.string.phone_type_car)
            Enums.Contacts.PhoneTypes.RADIO -> context.getString(R.string.phone_type_radio)
            Enums.Contacts.PhoneTypes.CALLBACK -> context.getString(R.string.phone_type_callback)
            Enums.Contacts.PhoneTypes.ISDN -> context.getString(R.string.phone_type_isdn)
            Enums.Contacts.PhoneTypes.TELEX -> context.getString(R.string.phone_type_telex)
            Enums.Contacts.PhoneTypes.TTY_TDD -> context.getString(R.string.phone_type_tty_tdd)
            Enums.Contacts.PhoneTypes.MMS -> context.getString(R.string.phone_type_mms)
            Enums.Contacts.PhoneTypes.FAX -> context.getString(R.string.connect_phone_type_fax)
            Enums.Contacts.PhoneTypes.ASSISTANT_PHONE -> context.getString(R.string.connect_phone_type_assistant)
            else -> context.getString(R.string.phone_type_general)
        }
    }

    @JvmStatic
    fun getEmailLabel(context: Context, emailAddress: EmailAddress): String? {
        return getEmailLabel(context, emailAddress, false)
    }

    fun getEmailLabel(context: Context, emailAddress: EmailAddress, isConnect: Boolean): String? {
        return when (emailAddress.type) {
            Enums.Contacts.EmailTypes.WORK_EMAIL -> if (isConnect) context.getString(R.string.connect_email_type_work) else context.getString(
                    R.string.email_type_work
                )
            Enums.Contacts.EmailTypes.HOME_EMAIL -> if (isConnect) context.getString(R.string.connect_email_type_home) else context.getString(
                    R.string.email_type_home
                )
            Enums.Contacts.EmailTypes.MOBILE_EMAIL -> if (isConnect) context.getString(R.string.connect_email_type_mobile) else context.getString(
                    R.string.email_type_mobile
                )
            Enums.Contacts.EmailTypes.OTHER_EMAIL -> if (isConnect) context.getString(R.string.connect_email_type_other) else context.getString(
                    R.string.email_type_other
                )
            Enums.Contacts.EmailTypes.CUSTOM_EMAIL -> {
                if (!TextUtils.isEmpty(emailAddress.label)) {
                    emailAddress.label
                } else {
                    if (isConnect) context.getString(R.string.connect_email_type_custom) else context.getString(
                        R.string.email_type_custom
                    )
                }
            }
            Enums.Contacts.EmailTypes.CONNECT_PRIMARY_EMAIL -> context.getString(R.string.connect_email_type_primary)
            Enums.Contacts.EmailTypes.CONNECT_SECONDARY_EMAIL -> context.getString(R.string.connect_email_type_secondary)
            else -> context.getString(R.string.email_type_general)
        }
    }

    fun getConnectDateLabel(context: Context, @Enums.Contacts.DateType.Type type: Int): String {
        return when (type) {
            Enums.Contacts.DateType.BIRTH -> context.getString(R.string.connect_contact_details_birth_date_title)
            Enums.Contacts.DateType.SIGN_UP -> context.getString(R.string.connect_contact_details_sign_up_date_title)
            Enums.Contacts.DateType.NEXT_CONTACT -> context.getString(R.string.connect_contact_details_next_contact_date_title)
            Enums.Contacts.DateType.CANCEL -> context.getString(R.string.connect_contact_details_cancel_date_title)
            else -> context.getString(R.string.connect_contact_details_other_title)
        }
    }

    fun getConnectSocialMediaLabel(context: Context, @SocialMediaType.Type type: Int): String {
        return when (type) {
            SocialMediaType.LINKEDIN -> context.getString(R.string.connect_contact_details_linkedin_title)
            SocialMediaType.TWITTER -> context.getString(R.string.connect_contact_details_twitter_title)
            SocialMediaType.FACEBOOK -> context.getString(R.string.connect_contact_details_facebook_title)
            SocialMediaType.INSTAGRAM -> context.getString(R.string.connect_contact_details_instagram_title)
            SocialMediaType.TELEGRAM -> context.getString(R.string.connect_contact_details_telegram_title)
            SocialMediaType.OTHER -> context.getString(R.string.connect_contact_details_other_title)
            else -> context.getString(R.string.connect_contact_details_other_title)
        }
    }

    fun getConnectAddressLabel(context: Context, @AddressType.Type type: Int): String {
        return when (type) {
            AddressType.HOME -> context.getString(R.string.connect_contact_details_home_address_title)
            AddressType.WORK -> context.getString(R.string.connect_contact_details_work_address_title)
            AddressType.OTHER -> context.getString(R.string.connect_contact_details_other_address_title)
            else -> context.getString(R.string.connect_contact_details_other_address_title)
        }
    }

    @JvmStatic
    fun changesMade(first: String?, second: String?): Boolean {
        return !(TextUtils.isEmpty(first) && TextUtils.isEmpty(second)) && !TextUtils.equals(first, second)
    }

    @JvmStatic
    fun equalsWithNullsAndBlanks(first: String?, second: String?): Boolean {
        return TextUtils.equals(first, second) || (TextUtils.isEmpty(first) && TextUtils.isEmpty(second));
    }

    @JvmStatic
    fun redactApiUrl(url: String?): String {
        return url
            // Remove UDID
            ?.redactByPattern("([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})", "UDID")
            // Remove ImpId param
            ?.redactByPattern("impId=(.*)/i", "IMP_ID")
            ?.redactByPattern("impId=(.*)%2Fi", "IMP_ID")
            // Remove Name param
            ?.redactByPattern("name=(.*)/i", "NAME")
            ?.redactByPattern("name=(.*)%2Fi", "NAME")
            // Remove the user's username
            ?.redactByPattern("(?<=sip:)(.*?)(?=\\@)", "USER_EMAIL")
            ?.redactByPattern("/([^/]*@[^/]*)/", "USER_EMAIL")
            ?.redactByPattern("/([^/]*%40[^/]*)/", "USER_EMAIL")
            // Remove trailing phone number
            ?.redactByPattern("/([0-9" + Constants.VALID_PHONE_SPECIAL_CHARACTERS + "]{7,15})", "PHONE_NUMBER")
            ?.redactByPattern("(?:number|callingPartyAddress|calledPartyAddress|extension|address)=([0-9" + Constants.VALID_PHONE_SPECIAL_CHARACTERS + "]{4,})", "PHONE_NUMBER")
            // Remove all line breaks
            ?.replace("\n", "\\") ?: ""
    }

    @JvmStatic
    fun getStringBetween(input: String, beginning: String, end: String): String {
        if(input.isNotEmpty() &&
            beginning.isNotEmpty() &&
            end.isNotEmpty() &&
            input.contains(beginning) &&
            input.contains(end))
            return input.substring(input.indexOf(beginning) + beginning.length, input.lastIndexOf(end))

        return ""
    }

    @JvmStatic
    fun getStringFromPattern(regexPattern: Pattern, originalString: String?): String {
        originalString?.let { string ->
            val regexMatcher = regexPattern.matcher(string)

            if (regexMatcher.find()) {
                return regexMatcher.group(1) ?: ""
            }
        }

        return ""
    }

    @JvmStatic
    fun getLineContaining(stringToMatch: String, originalString: String): String? {
        val scanner = Scanner(originalString)
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            if (line.contains(stringToMatch)) {
                scanner.close()
                return line
            }
        }
        scanner.close()

        return null
    }

    @JvmStatic
    fun isValidImAddress(imAddress: String?): Boolean {
        imAddress?.nullIfEmpty()?.let { address ->
            if (address.length != address.replace("@".toRegex(), "").length + 1) {
                return false
            }

            val pattern =
                Pattern.compile("^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")
            val matcher = pattern.matcher(address)
            return matcher.find()
        }

        return false
    }

    @JvmStatic
    fun getGroupChatParticipantsString(participantNames: List<String>?, maxWidth: Int, textPaint: Paint?, context: Context): String {
        return getGroupChatParticipantsString(participantNames, null, maxWidth, textPaint, context)
    }

    @JvmStatic
    fun getGroupChatParticipantsString(participantNames: List<String>?, teams: List<SmsTeam>?, maxWidth: Int, textPaint: Paint?, context: Context): String {
        val bounds = Rect()
        val queue: Queue<String> = LinkedList()
        val participantNamesSize = participantNames?.size ?: 0
        val teamsSize = teams?.size ?: 0

        if (participantNamesSize + teamsSize == 1) {
            val uiNameString = participantNames?.firstOrNull()?.nullIfEmpty() ?: teams?.firstOrNull()?.uiName ?: "No name"
            textPaint?.getTextBounds(uiNameString, 0, uiNameString.length, bounds)

            return if (bounds.width() > maxWidth) {
                context.getString(R.string.chat_details_participants, (participantNamesSize + teamsSize).toString())
            } else {
                uiNameString
            }

        } else if (participantNamesSize + teamsSize > 0) {

            if (textPaint != null) {
                val paint = TextPaint()
                try {
                    (participantNames as? MutableList)?.sortWith { s1: String?, s2: String? -> paint.measureText(s1).compareTo(paint.measureText(s2)) }
                    (teams as? MutableList)?.sortWith { team1: SmsTeam, team2: SmsTeam -> paint.measureText(team1.uiName).compareTo(paint.measureText(team2.uiName)) }

                } catch (e: IllegalArgumentException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }

            var nameList = arrayListOf<String>()
            participantNames?.let { nameList.addAll(it) }
            teams?.forEach { team -> team.uiName?.let { nameList.add(it) } }
            queue.addAll(nameList.sorted())

            var counter = queue.size - 1
            val buffer = StringBuffer()
            buffer.append(queue.poll())

            while(!queue.isEmpty()) {
                val add = ", ${queue.peek()}${if(counter - 1 > 0) " ,+ ${counter - 1}" else "" }"
                val temp = buffer.toString() + add

                textPaint?.getTextBounds(temp, 0, temp.length, bounds)
                if(bounds.width() > maxWidth) {
                    buffer.append(", +${counter}")
                    break
                } else {
                    buffer.append(", ${queue.poll()}")
                    counter--
                }
            }

            return buffer.toString()
        }
        return ""
    }

    @JvmStatic
    fun getUserJidFromRoomJid(roomJid: String): String? {
        return try {
            val pFrom = roomJid.indexOf("@")
            val pTo = roomJid.lastIndexOf("-") + 1
            val bytes = Hex.stringToBytes(roomJid.substring(pTo, pFrom))
            String(bytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }
}