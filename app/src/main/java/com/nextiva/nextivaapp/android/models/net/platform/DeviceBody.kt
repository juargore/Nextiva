package com.nextiva.nextivaapp.android.models.net.platform

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DeviceBody(@SerializedName("deviceId") var deviceId: String? = "",
                      @SerializedName("firebaseRegistrationToken") var firebaseRegistrationToken: String? = "",
                      @SerializedName("projectId") var projectId: String? = "",
                      @SerializedName("deviceType") var deviceType: String? = "",
                      @SerializedName("deviceOs") var deviceOs: String? = "") : Serializable