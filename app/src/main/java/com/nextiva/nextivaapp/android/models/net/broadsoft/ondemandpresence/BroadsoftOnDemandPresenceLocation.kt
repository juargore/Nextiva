package com.nextiva.nextivaapp.android.models.net.broadsoft.ondemandpresence

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BroadsoftOnDemandPresenceLocation(@SerializedName("cntry") var country: String? = "",
                                             @SerializedName("city") var city: String? = "",
                                             @SerializedName("reg") var region: String? = "",
                                             @SerializedName("tz") var timezone: String? = "",
                                             @SerializedName("loctext") var locationText: String? = "") : Serializable