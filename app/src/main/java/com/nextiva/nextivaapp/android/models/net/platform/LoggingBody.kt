package com.nextiva.nextivaapp.android.models.net.platform

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoggingBody(@SerializedName("") var logs: List<LogEntry>? = null) : Serializable