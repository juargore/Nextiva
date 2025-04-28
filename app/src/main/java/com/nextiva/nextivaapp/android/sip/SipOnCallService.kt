package com.nextiva.nextivaapp.android.sip

import android.app.Application
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.nextiva.nextivaapp.android.OneActiveCallActivity.Companion.newIntent
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.pjsip.pjsip_lib.sipservice.CallState
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class SipOnCallService : Service() {

    inner class LocalBinder : Binder() {
        val service: SipOnCallService
            get() = this@SipOnCallService
    }

    private val mBinder: IBinder = SipOnCallServiceBinder(WeakReference(this))

    @Inject
    lateinit var mNotificationManager: NotificationManager

    @Inject
    lateinit var mSipManager: PJSipManager

    @Inject
    lateinit var mLogManager: LogManager

    @Inject
    lateinit var mAvatarManager: AvatarManager

    @Inject
    lateinit var mApplication: Application

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent == null) {
            return START_NOT_STICKY
        }

        (mSipManager.getCurrentCall() ?: mSipManager.getActiveCalls()
            ?.firstOrNull { it.state == CallState.TRYING || it.state == CallState.CONNECTED })?.let { sipCall ->
            val participantInfo = sipCall.participantInfoList.firstOrNull()
            val state = sipCall.state.name
            val displayName = if (sipCall.isCallConference) {
                "Conference Call: ${sipCall.participantInfoList.size} members"
            } else {
                sipCall.participantInfoList.firstOrNull()
                    ?.let { it.displayName ?: it.numberToCall?.let { num -> CallUtil.getFormattedNumber(num)} } ?: "Unknown participant"
            }

            prepareAndStartForeground(participantInfo, state, displayName, sipCall.isCallConference)
            return START_STICKY
        }

        return START_NOT_STICKY
    }

    private fun prepareAndStartForeground(
        participantInfo: ParticipantInfo?,
        state: String,
        displayName: String,
        isConference: Boolean
    ) {
        val activeCallScreenIntent = createActiveCallScreenIntent(participantInfo)
        activeCallScreenIntent.putExtra(Constants.EXTRA_OPENED_FROM_NOTIFICATION, true)
        activeCallScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        val pendingIntent = PendingIntent.getActivity(
            mApplication,
            System.currentTimeMillis().toInt(),
            activeCallScreenIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val bitmap = mAvatarManager.getBitmap(
            AvatarInfo.Builder()
            .setDisplayName(if(!isConference) displayName else null)
            .setIconResId(if(isConference) R.drawable.avatar_group else 0)
            .build()
        )

        val notification = mNotificationManager.callNotification(
            bitmap,
            displayName,
            state,
            pendingIntent,
            Enums.Notification.ChannelIDs.CALL
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startForeground(
                    Enums.Notification.TypeIDs.ON_CALL,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL or ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
                )
            } else {
                startForeground(Enums.Notification.TypeIDs.ON_CALL, notification)
            }
        } catch (e: SecurityException) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, e.message)
        }
    }

    override fun onBind(intent: Intent): IBinder? = mBinder

    override fun onDestroy() {
        super.onDestroy()
        mSipManager.setProximityDetection(false)
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
    private fun createActiveCallScreenIntent(
        participantInfo: ParticipantInfo?,
    ): Intent = newIntent(mApplication, participantInfo, null).apply {
        action = Intent.ACTION_MAIN
        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        mLogManager.sipLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success)
    }
}