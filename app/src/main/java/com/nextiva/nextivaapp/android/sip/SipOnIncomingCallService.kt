package com.nextiva.nextivaapp.android.sip

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.AudioManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.pjsip.pjsip_lib.sipservice.CallState
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class SipOnIncomingCallService : Service() {

    @Inject
    lateinit var mAudioManager: AudioManager

    @Inject
    lateinit var mNotificationManager: NotificationManager

    @Inject
    lateinit var mSipManager: PJSipManager

    @Inject
    lateinit var mLogManager: LogManager

    @Inject
    lateinit var mSharedPreferencesManager: SharedPreferencesManager

    private val mBinder: IBinder = SipOnIncomingCallServiceBinder(WeakReference(this))


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        //startForeground should always be call as soon as possible. It must be called before the 5 seconds limit.

        mSipManager.incomingCall?.let { mIncomingCall ->
            mNotificationManager.getIncomingCallNotification(this, mIncomingCall)
                ?.let { incomingCallNotification ->

                    incomingCallNotification.flags =
                        incomingCallNotification.flags or Notification.FLAG_INSISTENT //THIS SHOULD BE RESEARCHED as I don't believe it's needed and might cause ring or vibrate to not stop.

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        startForeground(
                            Enums.Notification.TypeIDs.INCOMING_CALL,
                            incomingCallNotification,
                            FOREGROUND_SERVICE_TYPE_PHONE_CALL
                        )
                    } else {
                        startForeground(
                            Enums.Notification.TypeIDs.INCOMING_CALL,
                            incomingCallNotification
                        )
                    }
                }

            handleIncomingCallNotification()
            mLogManager.sipLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success)
        } ?: run {
            stopSelf()
        }

        return START_REDELIVER_INTENT
    }

    private fun handleIncomingCallNotification() {
        val nativePhoneCallState = mSharedPreferencesManager.getString(
            SharedPreferencesManager.NATIVE_PHONE_CALL_STATE,
            TelephonyManager.EXTRA_STATE_IDLE
        ) ?: ""
        val isNativeCallStateIdle = nativePhoneCallState == TelephonyManager.EXTRA_STATE_IDLE

        val isCallActive = mSipManager.getActiveCalls()?.firstOrNull() {
            it.state == CallState.CONNECTED
        }?.let { true } ?: false

        mAudioManager.startRingTone(isCallActive || !isNativeCallStateIdle);

        if (mSipManager.isCallActive()) {
            mLogManager.logToFile(Enums.Logging.STATE_INFO, "Incoming Call with live call.");
        } else {
            mLogManager.logToFile(Enums.Logging.STATE_INFO, "Incoming Call");
        }
    }

    override fun onUnbind(intent: Intent): Boolean {
        stopSelf()
        stopRingtone()
        return false
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRingtone()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun stopRingtone() {
        mAudioManager.stopRingTone()
    }
}