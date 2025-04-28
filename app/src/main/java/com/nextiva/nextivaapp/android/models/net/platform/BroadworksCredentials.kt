package com.nextiva.nextivaapp.android.models.net.platform

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BroadworksCredentials(@SerializedName("username") var username: String? = "",
                                 @SerializedName("password") var password: String? = "") : Serializable