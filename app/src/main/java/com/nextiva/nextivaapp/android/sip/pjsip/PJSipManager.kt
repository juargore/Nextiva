package com.nextiva.nextivaapp.android.sip.pjsip

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.BuildConfig
import com.nextiva.nextivaapp.android.IncomingCallActivity
import com.nextiva.nextivaapp.android.OneActiveCallActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.AppStates.AppState
import com.nextiva.nextivaapp.android.constants.Enums.AudioDevices.AudioDevice
import com.nextiva.nextivaapp.android.constants.Enums.Calls.CallTypes
import com.nextiva.nextivaapp.android.core.analytics.events.CallsEvent
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.managers.AudioDeviceManager
import com.nextiva.nextivaapp.android.managers.NetworkManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.PjSipManagerI
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager.PENDO_LAST_SURVEY_SHOW_TIME
import com.nextiva.nextivaapp.android.models.IncomingCall
import com.nextiva.nextivaapp.android.models.PushNotificationCallInfo
import com.nextiva.nextivaapp.android.sip.SipOnCallService
import com.nextiva.nextivaapp.android.sip.SipOnCallServiceBinder
import com.nextiva.nextivaapp.android.sip.SipOnIncomingCallService
import com.nextiva.nextivaapp.android.sip.SipOnIncomingCallServiceBinder
import com.nextiva.nextivaapp.android.util.ApplicationUtil
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.pjsip.pjsip_lib.sipservice.CallState
import com.nextiva.pjsip.pjsip_lib.sipservice.Logger
import com.nextiva.pjsip.pjsip_lib.sipservice.Logger.LoggerDelegate
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import com.nextiva.pjsip.pjsip_lib.sipservice.SipAccountData
import com.nextiva.pjsip.pjsip_lib.sipservice.SipAccountTransport
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import com.nextiva.pjsip.pjsip_lib.sipservice.SipConnectionStatus
import com.nextiva.pjsip.pjsip_lib.sipservice.SipService
import com.nextiva.pjsip.pjsip_lib.sipservice.SipServiceCommand
import com.nextiva.pjsip.pjsip_lib.sipservice.metrics.CallMetrics
import com.nextiva.pjsip.pjsip_lib.sipservice.metrics.CallStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PJSipManager @Inject constructor(
    var application: Application,
    var configManager: ConfigManager,
    var logManager: LogManager,
    var avatarManager: AvatarManager,
    var dbManager: DbManagerKt,
    var sharedPreferencesManager: SharedPreferencesManager,
    var callManagementRepository: CallManagementRepository,
    var notificationManager: NotificationManager,
    var sessionManager: SessionManager,
    var audioDeviceManager: AudioDeviceManager,
    var analyticsManager: AnalyticsManager,
    var connectionStateManager: ConnectionStateManager,
    networkManager: NetworkManager
) : PjSipManagerI {

    private val pendoTrackCallsOnCount = 10

    private var mProximityWakeLock: PowerManager.WakeLock? = null
    private var eventReceiver = PJSipEventReceiver()
    private var mAudioManager: AudioManager? = null

    var activeCallLiveData: MutableLiveData<SipCall?> = MutableLiveData()
    var activeCallDurationLiveData: MutableLiveData<String> = MutableLiveData()
    var passiveCallLiveData: MutableLiveData<SipCall?> = MutableLiveData()
    var isStackStartedLiveData: MutableLiveData<Boolean?> = eventReceiver.isStackStartedLiveData
    var sipConnectionStatusLiveData: MutableLiveData<SipConnectionStatus> = MutableLiveData()

    private var currentCallId = -1
    private val timerHandler = Handler(Looper.getMainLooper())

    var incomingCall: IncomingCall? = null
    var pushCallInfo: PushNotificationCallInfo? = null
    var incomingCallActivity: IncomingCallActivity? = null
    var isCallQueued = false

    private val loggerDelegate = object : LoggerDelegate {
        override fun error(tag: String?, message: String?) {
            logManager.sipLogToFile(Enums.Logging.STATE_FAILURE, "PJSIP - $tag - $message")
        }

        override fun error(tag: String?, message: String?, exception: Throwable?) {
            logManager.sipLogToFile(Enums.Logging.STATE_FAILURE, "PJSIP - $tag - $message")
            exception?.let { FirebaseCrashlytics.getInstance().recordException(it) }
        }

        override fun warning(tag: String?, message: String?) {
            logManager.sipLogToFile(Enums.Logging.STATE_ERROR, "PJSIP - $tag - $message")
        }

        override fun info(tag: String?, message: String?) {
            logManager.sipLogToFile(Enums.Logging.STATE_INFO, "PJSIP - $tag - $message")
        }

        override fun debug(tag: String?, message: String?) {
            // Going to keep this one commented out... it's insanely busy and will make our log files too big.
            // logManager.sipLogToFile(Enums.Logging.STATE_INFO, "PJSIP - $tag - $message")
        }
    }

    private var onCallStateChangedObserver = Observer<Pair<Int, Int>> {
        updateCalls()
    }

    private var onCallMediaStateChangedObserver = Observer<Pair<Int, Boolean>> {
        updateCalls()
    }

    private var onIncomingCallObserver = Observer<ParticipantInfo> {
        MainScope().launch(Dispatchers.IO) {
            if (it.contactId == null) {
                val contact = async { dbManager.getContactFromPhoneNumberInThread(it.numberToCall ?: "") }.await()

                contact.value?.getParticipantInfo(it.numberToCall)?.let { newParticipantInfo ->
                    newParticipantInfo.sessionId = it.sessionId
                    showIncomingCall(newParticipantInfo)
                } ?: kotlin.run { showIncomingCall(it) }

            } else {
                showIncomingCall(it)
            }
        }
    }

    private var onStackStartedObserver = Observer<Boolean?> {
        if (it == false) {
            val service = Intent(application, SipOnCallService::class.java)
            application.stopService(service)
            isCallQueued = false
            sipConnectionStatusLiveData.value = SipConnectionStatus.GOOD
            audioDeviceManager.close()

            MainScope().launch(Dispatchers.IO) {
                if (isServiceRunning(SipOnCallService::class.java.simpleName) && sipOnCallService?.get() != null && isOnCallServiceBound) {
                    application.unbindService(onCallServiceConnection)
                    isOnCallServiceBound = false
                }
            }
        } else if (it == true) {
            audioDeviceManager.start()
        }
    }

    private var onCallDisconnectedObserver = Observer<CallStats> {
        trackCallEvent(if (it.isIncoming) CallTypes.RECEIVED else CallTypes.PLACED, it)
    }

    private var onCallMetricsObserver = Observer<CallMetrics?> {
        it?.let {
            val sipCall = getActiveCalls()?.firstOrNull { call -> call.id == it.callId }

            sipConnectionStatusLiveData.value = when {
                !connectionStateManager.isInternetConnected -> {
                    if (sipCall?.hasLostMetricsBeenTracked == false) {
                        sipCall.hasLostMetricsBeenTracked = true
                        analyticsManager.trackEvent(CallsEvent().lostConnection(it))
                    }

                    SipConnectionStatus.LOST
                }
                it.hasConcerningJitter(40F) || it.hasConcerningPacketLoss(5F) -> {
                    if (sipCall?.hasPoorMetricsBeenTracked == false) {
                        sipCall.hasPoorMetricsBeenTracked = true
                        analyticsManager.trackEvent(CallsEvent().poorConnection(it))
                    }

                    SipConnectionStatus.POOR
                }
                else -> SipConnectionStatus.GOOD
            }
        }
    }

    init {
        Logger.setLoggerDelegate(loggerDelegate)
        networkManager.initialize {
            if(sipService?.getAllCalls()?.isNotEmpty().orFalse()) {
                reconnectCall(application)
            }
        }

        MainScope().launch(Dispatchers.Main) {
            eventReceiver.onCallStateChangedLiveData.observeForever(onCallStateChangedObserver)
            eventReceiver.onCallMediaStateChangedLiveData.observeForever(onCallMediaStateChangedObserver)
            eventReceiver.isStackStartedLiveData.observeForever(onStackStartedObserver)
            eventReceiver.onIncomingCallLiveData.observeForever(onIncomingCallObserver)
            eventReceiver.onCallDisconnectedLiveData.observeForever(onCallDisconnectedObserver)
            eventReceiver.onCallMetricsLiveData.observeForever(onCallMetricsObserver)

            connectionStateManager.setSipManager(this@PJSipManager)
            mAudioManager = application.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }
    }

    private fun setupBinder() {
        if (!isBound) {
            Intent(application, SipService::class.java).also { intent ->
                MainScope().launch(Dispatchers.IO) {
                    application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                }
            }

            eventReceiver.register(application)
        }
    }

    private fun isSilentMode(): Boolean {
        val ringerMode = mAudioManager?.ringerMode
        return ringerMode == AudioManager.RINGER_MODE_SILENT
    }

    fun registerAndMakeCall(participantInfo: ParticipantInfo, retrievalNumber: String? = null) {
        isCallQueued = true

        if (eventReceiver.isRegistered && eventReceiver.isStackStarted) {
            makeCall(retrievalNumber ?: CallUtil.cleanPhoneNumber(participantInfo.numberToCall ?: ""), participantInfo)

        } else {
            eventReceiver.postRegistrationCallAction = {
                makeCall(retrievalNumber ?: CallUtil.cleanPhoneNumber(participantInfo.numberToCall ?: ""), participantInfo)
                eventReceiver.postRegistrationCallAction = null
            }

            registerAccount()
        }
    }

    fun registerAccount() {
        configManager.mobileConfig?.sip?.let { sipConfig ->
            setupBinder()
            val accountData = SipAccountData()
                .setAuthUsername(sipConfig.authorizationUsername)
                .setProxy(sipConfig.proxyDomain)
                .setUsername(sipConfig.username)
                .setPassword(sipConfig.password)
                .setRealm("*")
                .setPort(sipConfig.proxyPort.toLong())
                .setHost(configManager.mobileConfig?.sip?.domain)
                .setTransport(SipAccountTransport.TCP)

            SipServiceCommand.setAccount(application,
                accountData,
                sipConfig.domain,
                sipConfig.authorizationUsername,
                sipConfig.password,
                sessionManager.echoCancellation?.toIntOrNull(),
                sessionManager.aecAggressiveness?.toIntOrNull(),
                sessionManager.isNoiseSuppressionEnabled,
                sessionManager.enabledAudioCodecs,
                BuildConfig.VERSION_NAME)
        }
    }

    fun makeCall(sipUri: String, participantInfo: ParticipantInfo) {
        SipServiceCommand.makeCall(application, sipUri, participantInfo)
    }

    fun holdCall(callId: Int, lockOnHold: Boolean? = null) {
        SipServiceCommand.setCallHold(application, callId, true, lockOnHold)
    }

    fun unholdCall(callId: Int, lockOnHold: Boolean? = null) {
        SipServiceCommand.setCallHold(application, callId, false, lockOnHold)
    }

    fun muteCall(callId: Int) {
        SipServiceCommand.setCallMute(application, callId, true)
    }

    fun unmuteCall(callId: Int) {
        SipServiceCommand.setCallMute(application, callId, false)
    }

    fun playDtmfTone(callId: Int?, tone: String) {
        if (!isSilentMode()) {
            playDtmfToneSound(callId, tone)
        }
        callId?.let {
            SipServiceCommand.sendDTMF(application, it, tone)
        }
    }

    fun swapCalls() {
        SipServiceCommand.swapCalls(application)
    }

    fun blindTransfer(callId: Int, to: String) {
        SipServiceCommand.transferCall(application, callId, to)
    }

    fun completeWarmTransfer() {
        SipServiceCommand.completeWarmTransfer(application)
    }

    fun acceptIncomingCall(callId: Int, participantInfo: ParticipantInfo) {
        getCurrentCall()?.id?.let { holdCall(it) }
        SipServiceCommand.acceptIncomingCall(application, callId, participantInfo)
        stopIncomingCall()
    }

    fun endCall(callId: Int) {
        SipServiceCommand.hangUpCall(application, callId)
    }

    fun stopStackIfNecessary() {
        if (incomingCall == null) {
            sipService?.stopStackIfNecessary()
        }
    }

    private fun reconnectCall(context: Context) {
        SipServiceCommand.reconnectCall(context)
        Log.d("PJSipManager", "PJSipManager : Reconnecting call... ")
    }

    fun getActiveCalls(): ArrayList<SipCall>? {
        return sipService?.getAllCalls()
    }

    fun getCurrentCall(): SipCall? {
        return sipService?.getAllCalls()?.firstOrNull { it.isCurrent }
    }

    private fun getPassiveCall(): SipCall? {
        return sipService?.getAllCalls()?.firstOrNull { !it.isCurrent }
    }

    fun setEcOptions(options: Long) {
        sipService?.setEcOptions(options)
    }

    private fun updateCalls() {
        incomingCall?.sessionId?.let { incomingSessionId ->
            val matchingCall = getActiveCalls()?.firstOrNull { it.id == incomingSessionId }

            if (matchingCall == null) {
                stopIncomingCall()
            }
        }

        activeCallLiveData.value = getCurrentCall()
        passiveCallLiveData.value = getPassiveCall()

        activeCallLiveData.value?.let {
            if(it.state == CallState.TRYING || it.state == CallState.CONNECTED) {
                (application.getSystemService(Context.AUDIO_SERVICE) as AudioManager).mode = AudioManager.MODE_IN_COMMUNICATION
                showOngoingCallNotifications()
            }
        }

        stopStackIfNecessary()

        getCurrentCall()?.let { currentCall ->
            currentCallId = currentCall.id
            timerHandler.post(object : Runnable {
                override fun run() {
                    getCurrentCall()?.let {
                        activeCallDurationLiveData.value = it.getTimeElapsed().ifEmpty {
                            when (it.state) {
                                CallState.TRYING -> application.getString(R.string.in_call_calling)
                                else -> ""
                            }
                        }

                        timerHandler.postDelayed(this, 1000)
                    }
                }
            })

        } ?: kotlin.run {
            currentCallId = -1
        }
    }

    fun updateActivePassiveWithSipCall(sipCall: SipCall) {
        if(activeCallLiveData.value?.trackingId == sipCall.trackingId) {
            activeCallLiveData.postValue(sipCall)

            sipOnCallService?.let {
                showOngoingCallNotifications()
            }

        } else if(passiveCallLiveData.value?.trackingId == sipCall.trackingId) {
            passiveCallLiveData.postValue(sipCall)
        }
    }

    fun isPresentCallConference() = activeCallLiveData.value?.isCallConference.orFalse()

    fun isPassiveCallConference() = passiveCallLiveData.value?.isCallConference.orFalse()

    fun tearDown() {
        sipService?.stopStackIfNecessary()
    }

    fun isCallActive(): Boolean {
        return sipService?.getAllCalls()?.isNotEmpty() ?: false
    }

    fun isRegistered(): Boolean {
        return eventReceiver.onRegistrationChangedLiveData.value ?: false
    }

    private fun playDtmfToneSound(callId: Int?, tone: String) {
        val dtmfDuration = 160

        val number = when (tone) {
            "A" -> ToneGenerator.TONE_DTMF_A
            "B" -> ToneGenerator.TONE_DTMF_B
            "C" -> ToneGenerator.TONE_DTMF_C
            "D" -> ToneGenerator.TONE_DTMF_D
            "*" -> ToneGenerator.TONE_DTMF_S
            "#" -> ToneGenerator.TONE_DTMF_P
            ";", "," -> {
                SystemClock.sleep(160L)
                -1
            }
            else -> {
                try {
                    Integer.parseInt(tone)

                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    logManager.sipLogToFile(Enums.Logging.STATE_ERROR, e.javaClass.simpleName)
                    -1
                }
            }
        }

        val toneGenerator = ToneGenerator(if (callId == null) 0 else AudioManager.STREAM_VOICE_CALL, ToneGenerator.MAX_VOLUME)
        toneGenerator.startTone(number, dtmfDuration)
        toneGenerator.stopTone()
    }

    fun setProximityDetection(@AppState appState: Int) {
        LogUtil.d("Proxy setProximityDetection state: $appState")
        when (appState) {
            Enums.AppStates.ACTIVE_FORGROUND, Enums.AppStates.PAUSED, Enums.AppStates.STOPPED -> setProximityDetection(isCallActive())
            Enums.AppStates.DESTORYED -> setProximityDetection(false)
        }
    }

    fun setProximityDetection(turnOnProximitySensor: Boolean) {
        LogUtil.d("Proxy setProximityDetection sensor: $turnOnProximitySensor")
        if (mProximityWakeLock == null) {
            val tag = "SipManager"
            mProximityWakeLock = (application.getSystemService() as? PowerManager)?.newWakeLock(
                PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                "$tag:PROXIMITY_SCREEN_OFF_WAKE_LOCK"
            )
        }
        if (mProximityWakeLock != null) {
            if (turnOnProximitySensor) {
                if (mProximityWakeLock?.isHeld != true) {
                    mProximityWakeLock?.acquire(10 * Constants.ONE_MINUTE_IN_MILLIS /*10 minutes*/)
                }
            } else {
                while (mProximityWakeLock?.isHeld == true) {
                    mProximityWakeLock?.release()
                }
            }
        }
    }

    fun showIncomingCall(participantInfo: ParticipantInfo) {
        participantInfo.isIncomingCall = true
        incomingCall = IncomingCall(participantInfo = participantInfo)
        incomingCall?.sessionId = participantInfo.sessionId

        if (!isIncomingCallServiceBound) {
            val service = Intent(application, SipOnIncomingCallService::class.java)
            service.putExtra(Constants.Calls.PARAMS_INCOMING_CALL, incomingCall)

            try {
                application.startService(service)
            } catch (e: NullPointerException) {
                logManager.sipLogToFile(Enums.Logging.STATE_ERROR, e.javaClass.simpleName)
                LogUtil.d(e.toString())
                FirebaseCrashlytics.getInstance().recordException(e)

                } catch (e: IllegalStateException) {
                    logManager.sipLogToFile(Enums.Logging.STATE_ERROR, e.javaClass.simpleName)
                    LogUtil.d(e.toString())
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

            if (ApplicationUtil.isAppInForeground(application)) {
                bringIncomingCallToFront()
            }
            MainScope().launch(Dispatchers.IO) {
                application.bindService(service, onIncomingCallServiceConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    fun showIncomingCallPushNotification(pushCallInfo: PushNotificationCallInfo) {
        pushCallInfo.participantInfo?.isIncomingCall = true

        incomingCall = IncomingCall(pushNotificationCallInfo = pushCallInfo)

        if (!isIncomingCallServiceBound) {
            val service = Intent(application, SipOnIncomingCallService::class.java)
            service.putExtra(Constants.Calls.PARAMS_INCOMING_CALL, incomingCall)
            service.putExtra(Constants.Calls.PARAMS_PARTICIPANT_INFO, pushCallInfo)

            try {
                application.startService(service)

            } catch (e: NullPointerException) {
                logManager.sipLogToFile(Enums.Logging.STATE_ERROR, e.javaClass.simpleName)
                LogUtil.d(e.toString())
                FirebaseCrashlytics.getInstance().recordException(e)

            } catch (e: IllegalStateException) {
                logManager.sipLogToFile(Enums.Logging.STATE_ERROR, e.javaClass.simpleName)
                LogUtil.d(e.toString())
                FirebaseCrashlytics.getInstance().recordException(e)
            }

            if (ApplicationUtil.isAppInForeground(application)) {
                bringIncomingCallToFront()
            }

            MainScope().launch(Dispatchers.IO) {
                application.bindService(service, onIncomingCallServiceConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private fun bringIncomingCallToFront() {
        incomingCall?.let {
            val componentName = ComponentName(application, IncomingCallActivity::class.java.name)
            val intent = IncomingCallActivity.newIntent(application, it).setComponent(componentName)
            application.startActivity(intent)
        }
    }

    fun stopIncomingCall(killIncomingCall: Boolean = true) {
        sipIncomingCallService?.get()?.stopRingtone()
        incomingCallActivity?.let { incomingCallActivity ->
            if (incomingCallActivity.isTaskRoot && !isCallActive()) {
                try {
                    incomingCallActivity.finishAndRemoveTask()
                } catch (e: NullPointerException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                sipService?.stopStackIfNecessary()

            } else {
                try {
                    if (!incomingCallActivity.isFinishing) {
                        incomingCallActivity.finish()
                    }

                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    logManager.logToFile(Enums.Logging.STATE_ERROR, "Incoming call attempting to stop: " + Log.getStackTraceString(e))
                }
            }

            this.incomingCallActivity = null
        }

        MainScope().launch(Dispatchers.IO) {
            try {
                sipIncomingCallService?.get()?.let {
                    if (isIncomingCallServiceBound) {
                        application.unbindService(onIncomingCallServiceConnection)
                        isIncomingCallServiceBound = false
                    }
                }

                val service = Intent(application, SipOnIncomingCallService::class.java)
                application.stopService(service)
            } catch (e: Exception) {
                logManager.sipLogToFile(Enums.Logging.STATE_ERROR, e.javaClass.simpleName)
                LogUtil.d(e.toString())
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        if (killIncomingCall) {
            incomingCall = null
            pushCallInfo = null
        }
    }

    fun rejectIncomingCall(incomingCall: IncomingCall) {
        if (incomingCall.sessionId == null) {
            notificationManager.cancelNotification(Enums.Notification.TypeIDs.INCOMING_CALL)
            sipIncomingCallService?.get()?.stopRingtone()
        }

        val isCallDeclineEnabled = sharedPreferencesManager.getBoolean(SharedPreferencesManager.USER_SETTINGS_DEVICES_POLICIES_CALL_DECLINE_ENABLED, false)

        if (!isCallActive()) {
            if (isCallDeclineEnabled) {
                pushCallInfo?.callId?.let { callManagementRepository.rejectCall(it) }
            }
        }

        incomingCall.sessionId?.let { endCall(it) }
        stopIncomingCall()

        Handler(Looper.getMainLooper()).postDelayed({
            sipService?.stopStackIfNecessary()
        }, 2000)
    }

    fun answerIncomingCall(activity: Activity?, incomingCall: IncomingCall) {
        val activityToUse =
            if (activity == null && incomingCallActivity != null) incomingCallActivity else activity

        getCurrentCall()?.let { currentCall ->
            swapCalls()

            if (currentCall.state == CallState.INCOMING || currentCall.state == CallState.CLOSED) {
                getCurrentCall()?.let { muteCall(it.id) }
            }
        }

        val sessionId = incomingCall.sessionId ?: incomingCall.participantInfo?.sessionId
        val call = getActiveCalls()?.firstOrNull { it.id == sessionId }

        if (call != null) {
            //TODO: Set current call call type to received?

            try {
                incomingCall.participantInfo?.let { participantInfo ->
                    sessionId?.let { sessionId ->
                        acceptIncomingCall(sessionId, participantInfo)
                    }
                }

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }

            incomingCall.pushNotificationCallInfo?.participantInfo?.let {
                showActiveCallScreen(
                    activityToUse,
                    it
                )
            }

        } else {
            incomingCall.pushNotificationCallInfo?.let { startCallFromPushNotificationCallInfo(it) }
            pushCallInfo?.participantInfo?.let { showActiveCallScreen(activityToUse, it) }
        }
    }

    private fun startCallFromPushNotificationCallInfo(pushCallInfo: PushNotificationCallInfo) {
        pushCallInfo.participantInfo?.let { makeCall(pushCallInfo.retrievalNumber, it) }
    }

    private fun showActiveCallScreen(activity: Activity?, participantInfo: ParticipantInfo) {
        if (activity != null) {
            activity.startActivity(createActiveCallScreenIntent(participantInfo))

        } else {
            val intent = createActiveCallScreenIntent(participantInfo)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(intent)
        }
    }

    fun muteRingtone() {
        sipIncomingCallService?.get()?.stopRingtone()
    }

    private fun showOngoingCallNotifications() {
        Handler(Looper.getMainLooper()).postDelayed({
            val service = Intent(application, SipOnCallService::class.java)

            try {
                application.startService(service)
                MainScope().launch(Dispatchers.IO) {
                    application.bindService(service, onCallServiceConnection, Context.BIND_AUTO_CREATE)
                }

            } catch (e: NullPointerException) {
                logManager.sipLogToFile(Enums.Logging.STATE_ERROR, e.javaClass.simpleName)
                LogUtil.d(e.toString())
                FirebaseCrashlytics.getInstance().recordException(e)

            } catch (e: IllegalStateException) {
                logManager.sipLogToFile(Enums.Logging.STATE_ERROR, e.javaClass.simpleName)
                LogUtil.d(e.toString())
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }, 400)
    }

    private fun createActiveCallScreenIntent(participantInfo: ParticipantInfo): Intent {
        return OneActiveCallActivity.newIntent(application, participantInfo, null).apply {
            action = Intent.ACTION_MAIN
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
    }

    // Service Management

    private var sipService: SipService? = null
    private var sipOnCallService: WeakReference<SipOnCallService>? = null
    private var sipIncomingCallService: WeakReference<SipOnIncomingCallService>? = null
    private var isBound = false
    private var isOnCallServiceBound = false
    private var isIncomingCallServiceBound = false

    private val onIncomingCallServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            try {
                sipIncomingCallService = (service as? SipOnIncomingCallServiceBinder)?.getInstance()
                isIncomingCallServiceBound = true
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                logManager.logToFile(Enums.Logging.STATE_ERROR, e.javaClass.simpleName)
                logManager.logToFile(Enums.Logging.STATE_INFO, GsonUtil.getJSON(e))
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            sipIncomingCallService = null
            isIncomingCallServiceBound = false
        }

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
            sipIncomingCallService = null
            isIncomingCallServiceBound = false
        }

        override fun onNullBinding(name: ComponentName?) {
            super.onNullBinding(name)
            sipIncomingCallService = null
            isIncomingCallServiceBound = false
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            (service as? SipService.LocalBinder)?.let { binder ->
                sipService = binder.service
                isBound = true
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            sipService = null
            isBound = false

            val service = Intent(application, SipOnCallService::class.java)
            application.stopService(service)
            audioDeviceManager.close()

            if (isServiceRunning(SipOnCallService::class.java.simpleName) && sipOnCallService?.get() != null && isOnCallServiceBound) {
                MainScope().launch(Dispatchers.IO) {
                    application.unbindService(onCallServiceConnection)
                    isOnCallServiceBound = false
                }
            }
        }

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
            sipService = null
            isBound = false
        }

        override fun onNullBinding(name: ComponentName?) {
            super.onNullBinding(name)
            sipService = null
            isBound = false
        }
    }

    private val onCallServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            sipOnCallService = (service as? SipOnCallServiceBinder)?.getInstance()
            isOnCallServiceBound = true
        }

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
            sipOnCallService = null
            isOnCallServiceBound = false
        }

        override fun onNullBinding(name: ComponentName?) {
            super.onNullBinding(name)
            sipOnCallService = null
            isOnCallServiceBound = false
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            sipOnCallService = null
            isOnCallServiceBound = false
        }
    }

    fun isServiceRunning(serviceClassName: String): Boolean {
        val activityManager = application.baseContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val services = activityManager?.getRunningServices(Integer.MAX_VALUE)

        services?.forEach { service ->
            if (service.service.className.contains(serviceClassName)) {
                return true
            }
        }

        return false
    }

    fun setAudioDevice(device: AudioDevice) {
        audioDeviceManager.setAudioDevice(device)
    }

    fun toggleSpeaker() {
        audioDeviceManager.toggleSpeaker()
    }

    fun isPhoneSpeakerOn() = audioDeviceManager.getCurrentAudioDev() == AudioDevice.SPEAKER_PHONE

    fun getBluetoothName() = audioDeviceManager.getBluetoothName()

    fun getDivertedCallId() = sipService?.getDivertedCallId()

    fun clearDivertedCallId() {
        sipService?.clearDivertedCallId()
    }

    private fun trackCallEvent(callType: String, callStats: CallStats) {
        val event = CallsEvent()

        when (callType) {
            CallTypes.RECEIVED -> analyticsManager.trackEvent(event.incoming(callStats))
            CallTypes.PLACED -> analyticsManager.trackEvent(event.outgoing(callStats))
        }

        // duration in milliseconds for 7 days
        val sevenDaysMillis = 7 * 24 * 60 * 60 * 1000L
        val currentTime = System.currentTimeMillis()

        // get the last survey execution time from shared preferences
        val lastExecutionTime = sharedPreferencesManager.getLong(PENDO_LAST_SURVEY_SHOW_TIME, 0L)

        // check if at least 7 days have passed since the last survey
        if (currentTime - lastExecutionTime >= sevenDaysMillis) {
            // show survey to the user
            analyticsManager.trackEvent(CallsEvent().surveyUserForCall())

            // update the last survey execution time
            sharedPreferencesManager.setLong(PENDO_LAST_SURVEY_SHOW_TIME, currentTime)
        } else {
            LogUtil.i("Survey not shown: 7 days have not yet passed since the last survey.")
        }
    }
}
