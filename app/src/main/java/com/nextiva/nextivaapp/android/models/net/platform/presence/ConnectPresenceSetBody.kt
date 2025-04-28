package com.nextiva.nextivaapp.android.models.net.platform.presence

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectPresenceSetBody(@SerializedName("status") var state: Int,
                                  @SerializedName("message") var status: String?,
                                  @SerializedName("userId") var userId: String?,
                                  @SerializedName("expiresAtDateTime") var expiresAtDateTime: String?,
                                  @SerializedName("expiresAtDurationMinutes") var expiresAtDurationMinutes: Int?,
                                  @SerializedName("device") var device: ConnectPresenceDevice) : Serializable