package com.nextiva.nextivaapp.android.models.net.broadsoft.ums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BroadsoftSendChatMessageResponse(@SerializedName("status") var status: BroadsoftUmsStatus?,
                                            @SerializedName("msgid") var msgId: String? = "") : Serializable