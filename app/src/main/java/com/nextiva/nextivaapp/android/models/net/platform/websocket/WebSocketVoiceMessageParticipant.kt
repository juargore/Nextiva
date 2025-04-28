package com.nextiva.nextivaapp.android.models.net.platform.websocket

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebSocketVoiceMessageParticipant(@SerializedName("userUuid") var userUuid: String?,
                                            @SerializedName("contactId") var contactId: String?,
                                            @SerializedName("extTrackingId") var extTrackingId: String?,
                                            @SerializedName("displayName") var displayName: String?,
                                            @SerializedName("phoneNumber") var phoneNumber: String?,
                                            @SerializedName("phoneNumberType") var phoneNumberType: String?,
                                            @SerializedName("contactType") var contactType: String?,
                                            @SerializedName("startTime") var startTime: Double?,
                                            @SerializedName("endTime") var endTime: Double?,
                                            @SerializedName("noteId") var noteId: String?): Serializable