package com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BroadsoftSuperPresenceLocation(@SerializedName("cnty") var country: String?,
                                          @SerializedName("lclt") var lclt: String?,
                                          @SerializedName("reg") var region: String?,
                                          @SerializedName("tz") var timezone: String?) : Serializable