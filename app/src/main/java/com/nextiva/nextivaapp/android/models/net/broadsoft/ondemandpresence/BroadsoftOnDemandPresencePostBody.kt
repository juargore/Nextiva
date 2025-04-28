package com.nextiva.nextivaapp.android.models.net.broadsoft.ondemandpresence

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BroadsoftOnDemandPresencePostBody(@SerializedName("jids") var jids: ArrayList<String>? = ArrayList()) : Serializable