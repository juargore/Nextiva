package com.nextiva.nextivaapp.android.models.net.platform

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LogEntry(@SerializedName("additional") var additional: JsonObject? = null,
                    @SerializedName("level") var level: String? = "",
                    @SerializedName("message") var message: String? = "",
                    @SerializedName("timestamp") var timestamp: Long? = 0L) : Serializable
