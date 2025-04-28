package com.nextiva.nextivaapp.android.models.net.platform

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LogSubmit(@SerializedName("metadata") var metadata: LogMetadata? = null,
                        @SerializedName("logs") var logs: ArrayList<LogEntry>) : Serializable