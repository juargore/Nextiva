package com.nextiva.nextivaapp.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nextiva.nextivaapp.android.models.IncomingCall
import com.nextiva.nextivaapp.android.receivers.IncomingCallNotificationBroadcastReceiver

class TrampolineRestrictingActivity : AppCompatActivity() {
    val INCOMING_CALL = "incoming_call"

    companion object {
        fun newIntent(context: Context, incomingCall: IncomingCall, action: String): Intent {
            return Intent(context, TrampolineRestrictingActivity::class.java).putExtra(IncomingCallNotificationBroadcastReceiver.INCOMING_CALL, incomingCall).setAction(action)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent != null && intent.action != null && intent.hasExtra(INCOMING_CALL)) {
            val incomingCall = intent.getSerializableExtra(IncomingCallNotificationBroadcastReceiver.INCOMING_CALL) as IncomingCall
            val broadcastFiringIntent = IncomingCallNotificationBroadcastReceiver.newIntent(this, incomingCall)
            broadcastFiringIntent.action = intent.action
            this.sendBroadcast(broadcastFiringIntent)
            finish()
        }
    }
}