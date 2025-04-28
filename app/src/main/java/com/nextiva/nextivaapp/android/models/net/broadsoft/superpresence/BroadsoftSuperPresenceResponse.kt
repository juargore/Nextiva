package com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsStatus
import java.io.Serializable

data class BroadsoftSuperPresenceResponse(@SerializedName("status") var responseStatus: BroadsoftUmsStatus?,
                                          @SerializedName("superPresence") var superPresence: BroadsoftSuperPresence?) : Serializable