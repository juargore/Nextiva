package com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BroadsoftSuperPresenceLocationText(@SerializedName("text") var text: String?,
                                              @SerializedName("tz") var timezone: String?) : Serializable