package com.nextiva.nextivaapp.android.models.net.broadsoft.ondemandpresence

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsStatus
import java.io.Serializable

data class BroadsoftOnDemandPresenceResponse(@SerializedName("status") var responseStatus: BroadsoftUmsStatus?,
                                             @SerializedName("users") var presences: ArrayList<BroadsoftOnDemandPresence>?) : Serializable
