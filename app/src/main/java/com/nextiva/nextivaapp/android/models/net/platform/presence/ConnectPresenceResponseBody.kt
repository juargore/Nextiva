package com.nextiva.nextivaapp.android.models.net.platform.presence

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectPresenceResponseBody(@SerializedName("statusList") var statusList: ArrayList<ConnectPresenceResponse>?,
                                       @SerializedName("total") var total: Int?) : Serializable