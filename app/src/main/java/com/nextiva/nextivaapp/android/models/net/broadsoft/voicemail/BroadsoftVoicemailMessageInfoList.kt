package com.nextiva.nextivaapp.android.models.net.broadsoft.voicemail

import org.simpleframework.xml.ElementList
import java.io.Serializable

data class BroadsoftVoicemailMessageInfoList(@field:ElementList(entry = "messageInfo", inline = true, required = false)
                                             var messageInfoList: ArrayList<BroadsoftVoicemailMessageInfo>? = null) : Serializable