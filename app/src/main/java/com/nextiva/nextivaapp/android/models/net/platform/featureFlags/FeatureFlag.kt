package com.nextiva.nextivaapp.android.models.net.platform.featureFlags

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FeatureFlag(@SerializedName("name") var name: String?,
                       @SerializedName("is_enabled") var isEnabled: Boolean?,
                       var isManuallyDisabled: Boolean?) : Serializable