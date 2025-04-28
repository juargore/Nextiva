package com.nextiva.nextivaapp.android.models.net.sip

import com.google.gson.annotations.SerializedName

data class SipHost(@SerializedName("bwUserId") var bwUserId: String? = "",
    @SerializedName("userId") var userId: String? = "")