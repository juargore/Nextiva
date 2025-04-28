package com.nextiva.nextivaapp.android.models.net.broadsoft.voicemail

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root
import java.io.Serializable

//<?xml version="1.0" encoding="UTF-8"?>
//<VoiceMessage xmlns="http://schema.broadsoft.com/xsi">
//<messageInfo> <duration>110030</duration> <callingPartyInfo>
//<name>Ken Watson</name>
//<address>tel:0002</address>
//</callingPartyInfo>
//<video/>
//<time>1363014750766</time> <messageId>/v2.0/user/jbloggs@broadworks/voicemessaging
//messages/e5c3a18b-3508-45e7-9577-e8889e4efffb</messageId> </messageInfo>
//<messageMediaContent>
//<description></description> <mediaType>WAV</mediaType> <content>
//BASE 64 content omitted for reasons of space.
//</content> </messageMediaContent>
//</VoiceMessage>

@Root(name = "VoiceMessage", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
data class BroadsoftVoiceMessageDetailsResponse(@field:Element(name = "messageInfo", required = false) var messageInfoList: BroadsoftVoicemailMessageInfo? = null,
                                                @field:Element(name = "messageMediaContent", required = false) var messageMediaContent: BroadsoftVoicemailMessageMediaContent? = null) : Serializable