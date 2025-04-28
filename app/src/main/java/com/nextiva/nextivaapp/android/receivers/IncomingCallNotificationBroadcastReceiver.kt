package com.nextiva.nextivaapp.android.receivers

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.models.IncomingCall
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by Thaddeus Dannar on 2020-01-02.
 */
@AndroidEntryPoint
class IncomingCallNotificationBroadcastReceiver : BroadcastReceiver() {
    @JvmField
    @Inject
    var mNotificationManager: NotificationManager? = null
    @JvmField
    @Inject
    var mSipManager: PJSipManager? = null
    @JvmField
    @Inject
    var mApplication: Application? = null
    @JvmField
    @Inject
    var mLogManager: LogManager? = null
    override fun onReceive(context: Context, intent: Intent) {
        if (!intent.hasExtra(INCOMING_CALL)) {
            return
        }

        val incomingCall = intent.getSerializableExtra(INCOMING_CALL) as IncomingCall?
        if (intent.action != null) {
            when (intent.action) {
                DECLINE_CALL -> {
                    mLogManager!!.logToFile(Enums.Logging.STATE_INFO, "DECLINE_CALL")
                    declineCall(incomingCall)
                }

                ANSWER_AUDIO_CALL -> {
                    mLogManager!!.logToFile(Enums.Logging.STATE_INFO, "ANSWER_AUDIO_CALL")
                    answerAudioCall(incomingCall)
                }
            }
        }
    }

    private fun answerAudioCall(incomingCall: IncomingCall?) {
        if (mSipManager != null) {
            if (incomingCall?.pushNotificationCallInfo?.participantInfo != null) {
                mLogManager?.logToFile(Enums.Logging.STATE_INFO, "Answer Audio Call: " + GsonUtil.getJSON(incomingCall.pushNotificationCallInfo?.participantInfo))
                mSipManager?.answerIncomingCall(null, incomingCall)

            } else if ((incomingCall?.sessionId != null || incomingCall?.participantInfo?.sessionId != null) && incomingCall.participantInfo != null) {
                (incomingCall.sessionId ?: incomingCall.participantInfo?.sessionId)?.let { sessionId ->
                    incomingCall.participantInfo?.let { mSipManager?.acceptIncomingCall(sessionId, it) }
                }

            } else {
                mSipManager?.stopIncomingCall()
            }
        }
    }

    private fun declineCall(incomingCall: IncomingCall?) {
        try {
            if (incomingCall != null) {
                mSipManager?.rejectIncomingCall(incomingCall);

                incomingCall.pushNotificationCallInfo?.participantInfo?.let { participantInfo ->
                    mLogManager?.logToFile(Enums.Logging.STATE_INFO, "Decline Call: " + GsonUtil.getJSON(participantInfo))
                }
            }

            mSipManager?.stopIncomingCall();
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            LogUtil.d("Error in decline call: " + GsonUtil.getJSON(e))
        }

        if (mSipManager?.isCallActive() != true) {
            mNotificationManager?.cancelNotification(Enums.Notification.TypeIDs.ON_CALL)
            mNotificationManager?.cancelNotification(Enums.Notification.TypeIDs.INCOMING_CALL)
            mNotificationManager?.cancelNotification(Enums.Notification.TypeIDs.SIP_AUDIO_STATS)
        }
    }

    companion object {
        const val INCOMING_CALL = "incoming_call"
        const val ANSWER_AUDIO_CALL = "answer_audio_call"
        const val DECLINE_CALL = "decline_call"
        @JvmStatic
        fun newIntent(context: Context?, incomingCall: IncomingCall?): Intent {
            return Intent(context, IncomingCallNotificationBroadcastReceiver::class.java).putExtra(
                INCOMING_CALL, incomingCall
            )
        }
    }
}