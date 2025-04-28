package com.nextiva.nextivaapp.android.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Created by Thaddeus Dannar on 2/17/21.
 */
@AndroidEntryPoint
class PhoneCallStateBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sipManager: PJSipManager

    @Inject
    lateinit var logManager: LogManager

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager



    override fun onReceive(context: Context, intent: Intent) {
       val callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        if(callState is String)
            sharedPreferencesManager.setString(SharedPreferencesManager.NATIVE_PHONE_CALL_STATE, callState)

//        sipManager.phoneState = callState
//            callState?.let { callState ->
//            when(callState){
//                TelephonyManager.EXTRA_STATE_IDLE -> {
//                    logManager.logToFile(Enums.Logging.STATE_INFO, " PHONE STATE: IDLE")
//                }
//                TelephonyManager.EXTRA_STATE_RINGING -> {
//                    logManager.logToFile(Enums.Logging.STATE_INFO, " PHONE STATE: RINGING")
//                }
//                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
//                    logManager.logToFile(Enums.Logging.STATE_INFO, " PHONE STATE: OFFHOOK")
//                    val sessionId = sipManager.activeCallSessionId
//                    if (sessionId > 0) {
//                        sipManager.holdInBackground(sessionId, true)
//                    }
//                }
//                else -> logManager.logToFile(Enums.Logging.STATE_INFO, " PHONE STATE not known: $callState")
//            }
//
//        }
    }

}