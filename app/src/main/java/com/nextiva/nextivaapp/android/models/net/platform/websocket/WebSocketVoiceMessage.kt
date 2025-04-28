package com.nextiva.nextivaapp.android.models.net.platform.websocket

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebSocketVoiceMessage(@SerializedName("conferenceId") var conferenceId: String?,
                                 @SerializedName("hostUserUuid") var hostUserUuid: String?,
                                 @SerializedName("participants") var participants: ArrayList<WebSocketVoiceMessageParticipant>?): Serializable