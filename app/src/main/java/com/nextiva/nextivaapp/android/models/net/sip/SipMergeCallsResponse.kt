package com.nextiva.nextivaapp.android.models.net.sip

import com.google.gson.annotations.SerializedName

data class SipMergeCallsResponse(@SerializedName("callDetails") var callDetails: ArrayList<SipCallDetails>,
                                 @SerializedName("createdTime") var createdTime: String? = "",
                                 @SerializedName("host") var host: SipHost? = null,
                                 @SerializedName("id") var id: String? = "",
                                 @SerializedName("updatedTime") var updatedTime: String? = "")