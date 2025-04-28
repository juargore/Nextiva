package com.nextiva.nextivaapp.android.models.net.sip

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SipCallDetails(@SerializedName("audioStateInfo") var audioStateInfo: String? = "",
                          @SerializedName("corpAcctNbr") var corpAcctNbr: String? = "",
                          @SerializedName("extTrackingId") var extTrackingId: String? = "",
                          @SerializedName("ownerUserId") var ownerUserId: String? = "",
                          @SerializedName("phoneNbr") var phoneNbr: String? = "") : Serializable