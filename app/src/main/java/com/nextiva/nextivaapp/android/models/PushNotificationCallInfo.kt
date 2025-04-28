package com.nextiva.nextivaapp.android.models

import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import java.io.Serializable

data class PushNotificationCallInfo(val retrievalNumber: String, val callId: String?, val trackingId: String?, var participantInfo: ParticipantInfo?, var codecs: ArrayList<AudioCodec>?) : Serializable