package com.nextiva.nextivaapp.android.models.net.broadsoft.voicemail

import android.telephony.PhoneNumberUtils
import com.nextiva.nextivaapp.android.db.model.DbVoicemail
import com.nextiva.nextivaapp.android.util.CallUtil
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root
import java.io.Serializable
import java.util.Locale

//<?xml version="1.0" encoding="UTF-8"?>
//<VoiceMessagingMessages xmlns="http://schema.broadsoft.com/xsi">
//<messageInfoList>
//<messageInfo>
//<duration>14580</duration>
//<callingPartyInfo>
//<name>Joe2 DePhillipo2</name>
//<userId>joetest2</userId>
//<address>tel:2918</address>
//</callingPartyInfo>
//<time>1600733992928</time>
//<messageId>/v2.0/user/joetest1@nextiva.com/voicemessagingmessages/8c9f49e3-cd6c-4891-b628-cb0aa6fa7810</messageId>
//</messageInfo>
//</messageInfoList>
//</VoiceMessagingMessages>

@Root(name = "VoiceMessagingMessages", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
data class BroadsoftVoicemailListResponse(
        @field:Element(
                name = "messageInfoList",
                required = false
        ) var messageInfoList: BroadsoftVoicemailMessageInfoList? = null
) : Serializable {
    fun getVoicemails(): ArrayList<DbVoicemail> {
        val voicemailList: ArrayList<DbVoicemail> = ArrayList()

        messageInfoList?.messageInfoList?.let { messageInfoList ->
            for (messageInfo in messageInfoList) {

                if (messageInfo.messageId?.startsWith("/") == true) {
                    messageInfo.messageId?.let { messageId ->
                        messageInfo.messageId = messageId.replaceFirst("/", "")
                    }
                }

                val phoneNumber = messageInfo.callingPartyInfo?.address?.let {
                    CallUtil.getStrippedPhoneNumber(
                            it
                    )
                }
                voicemailList.add(
                    DbVoicemail(
                        null,
                        messageInfo.duration,
                        phoneNumber,
                        messageInfo.callingPartyInfo?.name,
                        messageInfo.callingPartyInfo?.userId,
                        messageInfo.time?.toLong(),
                        messageInfo.messageId,
                        messageInfo.read != null,
                        null,
                        null,
                        PhoneNumberUtils.formatNumber(phoneNumber?.let {
                            CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(
                                it
                            )
                        }, Locale.getDefault().country),
                        messageInfo.messageId,
                        null,
                        null,
                        null
                    )
                )
            }
        }

        return voicemailList
    }
}