package com.nextiva.nextivaapp.android.models.net.broadsoft.accessdevices

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BroadsoftAllowTerminationPutBody(@SerializedName("allowTermination") var allowTermination: Boolean): Serializable