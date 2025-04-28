/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android.models

import android.net.sip.SipAudioCall
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import java.io.Serializable

/**
 * Created by Thaddeus Dannar on 6/14/18.
 */
data class IncomingCall(var sipAddress: String? = null,
                        var sipMessage: String? = null,
                        var sessionId: Int? = null,
                        var participantInfo: ParticipantInfo? = null,
                        var pushNotificationCallInfo: PushNotificationCallInfo? = null) : Serializable
