package com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BroadsoftSuperPresenceShowPostBody(@SerializedName("show") var show: String? = "",
                                              @SerializedName("priority") var priority: Int? = -128) : Serializable