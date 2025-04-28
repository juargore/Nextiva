package com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BroadsoftSuperPresenceStatusPostBody(@SerializedName("freeText") var freetext: String? = "") : Serializable