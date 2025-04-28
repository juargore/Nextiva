package com.nextiva.nextivaapp.android.models.net.platform

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LogMetadata(@SerializedName("appName") var appName: String? = null,
                       @SerializedName("additional") var additional: JsonObject? = null) : Serializable