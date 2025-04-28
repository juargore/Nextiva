package com.nextiva.nextivaapp.android.models.net.broadsoft.ums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BroadsoftSetVCardBody(@SerializedName("vcard") var vcardData: String? = "",
                                 @SerializedName("udid") var udid: String? = "") : Serializable