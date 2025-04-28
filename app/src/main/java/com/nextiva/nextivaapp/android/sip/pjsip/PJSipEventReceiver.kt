package com.nextiva.nextivaapp.android.sip.pjsip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.net.buses.RxBus
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.util.extensions.parcelable
import com.nextiva.nextivaapp.android.util.extensions.parcelableArrayList
import com.nextiva.nextivaapp.android.util.extensions.serializable
import com.nextiva.pjsip.pjsip_lib.sipservice.BroadcastEventEmitter
import com.nextiva.pjsip.pjsip_lib.sipservice.CallReconnectionState
import com.nextiva.pjsip.pjsip_lib.sipservice.CodecPriority
import com.nextiva.pjsip.pjsip_lib.sipservice.Logger
import com.nextiva.pjsip.pjsip_lib.sipservice.MediaState
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import com.nextiva.pjsip.pjsip_lib.sipservice.RtpStreamStats
import com.nextiva.pjsip.pjsip_lib.sipservice.SipService
import com.nextiva.pjsip.pjsip_lib.sipservice.SipServiceConstants
import com.nextiva.pjsip.pjsip_lib.sipservice.metrics.CallMetrics
import com.nextiva.pjsip.pjsip_lib.sipservice.metrics.CallStats

class PJSipEventReceiver : BroadcastReceiver(), SipServiceConstants {
    private var receiverContext: Context? = null

    private var onAccountIdLiveData: MutableLiveData<String?> = MutableLiveData()
    var onRegistrationChangedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var onCallStateChangedLiveData: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    var onCallMediaStateChangedLiveData: MutableLiveData<Pair<Int, Boolean>> = MutableLiveData()
    var isStackStartedLiveData: MutableLiveData<Boolean?> = MutableLiveData()
    var onIncomingCallLiveData: MutableLiveData<ParticipantInfo> = MutableLiveData()
    var onCallDisconnectedLiveData: MutableLiveData<CallStats> = MutableLiveData()
    var onCallMetricsLiveData: MutableLiveData<CallMetrics?> = MutableLiveData()

    var postRegistrationCallAction: (() -> Unit)? = null

    var isRegistered: Boolean = false
    var isStackStarted: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {

        //save context internally for convenience in subclasses, which can get it with
        //getReceiverContext method
        receiverContext = context
        val action = intent.action

        when (action) {
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.REGISTRATION) -> {
                val stateCode = intent.getIntExtra(SipServiceConstants.PARAM_REGISTRATION_CODE, -1)
                onRegistration(intent.getStringExtra(SipServiceConstants.PARAM_ACCOUNT_ID), stateCode)
            }
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.INCOMING_CALL) -> {
                onIncomingCall(
                    intent.getIntExtra(SipServiceConstants.PARAM_CALL_ID, -1),
                    intent.getStringExtra(SipServiceConstants.PARAM_DISPLAY_NAME),
                    intent.getStringExtra(SipServiceConstants.PARAM_REMOTE_URI),
                    intent.getStringExtra(SipServiceConstants.PARAM_TRACKING_ID)
                )
            }
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_STATE) -> {
                val callState = intent.getIntExtra(SipServiceConstants.PARAM_CALL_STATE, -1)
                val callStatus = intent.getIntExtra(SipServiceConstants.PARAM_CALL_STATUS, -1)
                onCallState(
                    intent.getIntExtra(SipServiceConstants.PARAM_CALL_ID, -1),
                    callState, callStatus,
                    intent.getLongExtra(SipServiceConstants.PARAM_CONNECT_TIMESTAMP, -1)
                )
            }
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_MEDIA_STATE) -> {
                onCallMediaState(
                    intent.getIntExtra(SipServiceConstants.PARAM_CALL_ID, -1),
                    intent.serializable<MediaState>(SipServiceConstants.PARAM_MEDIA_STATE_KEY),
                    intent.getBooleanExtra(SipServiceConstants.PARAM_MEDIA_STATE_VALUE, false)
                )
            }
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.OUTGOING_CALL) -> {
                onOutgoingCall(
                    intent.getIntExtra(SipServiceConstants.PARAM_CALL_ID, -1),
                    intent.getStringExtra(SipServiceConstants.PARAM_NUMBER)
                )
            }
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.STACK_STATUS) -> onStackStatus(intent.getBooleanExtra(SipServiceConstants.PARAM_STACK_STARTED, false))
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CODEC_PRIORITIES) -> onReceivedCodecPriorities(intent.parcelableArrayList<CodecPriority>(SipServiceConstants.PARAM_CODEC_PRIORITIES_LIST))
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CODEC_PRIORITIES_SET_STATUS) -> onCodecPrioritiesSetStatus(intent.getBooleanExtra(SipServiceConstants.PARAM_SUCCESS, false))
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.MISSED_CALL) -> onMissedCall(intent.getStringExtra(SipServiceConstants.PARAM_DISPLAY_NAME), intent.getStringExtra(SipServiceConstants.PARAM_REMOTE_URI))
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_STATS) -> {
                val callStatus = intent.getIntExtra(SipServiceConstants.PARAM_CALL_STATUS, -1)
                onCallStats(
                    intent.getIntExtra(SipServiceConstants.PARAM_CALL_ID, -1),
                    intent.getIntExtra(SipServiceConstants.PARAM_CALL_STATS_DURATION, 0),
                    intent.getStringExtra(SipServiceConstants.PARAM_CALL_STATS_AUDIO_CODEC), callStatus,
                    intent.parcelable(SipServiceConstants.PARAM_CALL_STATS_RX_STREAM),
                    intent.parcelable(SipServiceConstants.PARAM_CALL_STATS_TX_STREAM))
            }
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_RECONNECTION_STATE) -> onCallReconnectionState(intent.serializable(SipServiceConstants.PARAM_CALL_RECONNECTION_STATE))
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.SILENT_CALL_STATUS) -> {
                onSilentCallStatus(intent.getBooleanExtra(SipServiceConstants.PARAM_SILENT_CALL_STATUS, false),
                    intent.getStringExtra(SipServiceConstants.PARAM_NUMBER))
            }
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.NOTIFY_TLS_VERIFY_STATUS_FAILED) -> onTlsVerifyStatusFailed()
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_UPDATED) -> { }
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_DISCONNECTED) -> {
                onCallDisconnected(intent.getBooleanExtra(SipServiceConstants.PARAM_IS_INCOMING_CALL, false),
                    intent.getDoubleExtra(SipServiceConstants.PARAM_TIME_ELAPSED, 0.0),
                    intent.serializable<CallMetrics>(SipServiceConstants.PARAM_CALL_METRICS))
            }
            BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_METRICS) -> onCallMetrics(intent.serializable<CallMetrics>(SipServiceConstants.PARAM_CALL_METRICS))
        }
    }

    /**
     * Register this broadcast receiver.
     * It's recommended to register the receiver in Activity's onResume method.
     *
     * @param context context in which to register this receiver
     */
    fun register(context: Context) {
        Logger.info(LOG_TAG, "Registering receiver: $this from context: $context")
        val intentFilter = IntentFilter()
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.REGISTRATION))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.INCOMING_CALL))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_STATE))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_MEDIA_STATE))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.OUTGOING_CALL))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.STACK_STATUS))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CODEC_PRIORITIES))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CODEC_PRIORITIES_SET_STATUS))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.MISSED_CALL))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_STATS))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_RECONNECTION_STATE))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.SILENT_CALL_STATUS))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.NOTIFY_TLS_VERIFY_STATUS_FAILED))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_UPDATED))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_DISCONNECTED))
        intentFilter.addAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_METRICS))
        context.registerReceiver(this, intentFilter, RECEIVER_EXPORTED)
    }

    /**
     * Unregister this broadcast receiver.
     * It's recommended to unregister the receiver in Activity's onPause method.
     *
     * @param context context in which to unregister this receiver
     */
    fun unregister(context: Context) {
        try {
            Logger.info(LOG_TAG, "Unregistering BER: $this from context: $context")
            context.unregisterReceiver(this)
        } catch (e: Exception) {
            Logger.error(LOG_TAG, "Error while unregistering BER", e)
        }
    }

    private fun onRegistration(accountID: String?, registrationStateCode: Int) {
        Logger.debug(LOG_TAG, "onRegistration - registrationStateCode: $registrationStateCode")
        onAccountIdLiveData.value = accountID
        onRegistrationChangedLiveData.value = registrationStateCode == 200
        isRegistered = registrationStateCode == 200
        RxBus.publish(RxEvents.SipRegisterFinished(isRegistered, System.currentTimeMillis()))

        if (registrationStateCode == 200) {
            postRegistrationCallAction?.let { it() }
        }
    }

    private fun onIncomingCall(callID: Int, displayName: String?, remoteUri: String?, trackingId: String?) {
        Logger.debug(LOG_TAG, "onIncomingCall - callID: $callID, displayName: $displayName, remoteUri: $remoteUri")
        var numberToCall = remoteUri

        remoteUri?.let {
            if (remoteUri.contains("@")) {
                numberToCall = remoteUri.split("@").firstOrNull()
            }
        }

        onIncomingCallLiveData.value = ParticipantInfo(
            sessionId = callID,
            displayName = displayName,
            numberToCall = numberToCall,
            trackingId = trackingId
        )
    }

    private fun onCallState(callID: Int, callStateCode: Int, callStatusCode: Int, connectTimestamp: Long) {
        onCallStateChangedLiveData.value = Pair(callID, callStateCode)
        Logger.debug(
            LOG_TAG, "onCallState - callID: " + callID +
                    ", callStateCode: " + callStateCode +
                    ", callStatusCode: " + callStatusCode +
                    ", connectTimestamp: " + connectTimestamp
        )
    }

    private fun onCallMediaState(callID: Int, stateType: MediaState?, stateValue: Boolean) {
        onCallMediaStateChangedLiveData.value = Pair(callID, stateValue)
        Logger.debug(LOG_TAG, "onCallMediaState - callID: " + callID +
                ", mediaStateType: " + stateType!!.name +
                ", mediaStateValue: " + stateValue
        )
    }

    private fun onOutgoingCall(callID: Int, number: String?) {
        Logger.debug(LOG_TAG, "onOutgoingCall - callID: $callID, number: $number")
    }

    private fun onStackStatus(started: Boolean) {
        Logger.debug(LOG_TAG, "SIP service stack " + if (started) "started" else "stopped")
        isStackStarted = started
        isStackStartedLiveData.value = started
        isStackStartedLiveData.value = null
        RxBus.publish(RxEvents.SipDeregisterFinished(!started))

        if (!started) {
            receiverContext?.stopService(Intent(receiverContext, SipService::class.java))
        }
    }

    private fun onReceivedCodecPriorities(codecPriorities: ArrayList<CodecPriority>?) {
        Logger.debug(LOG_TAG, "Received codec priorities")
        for (codec in codecPriorities!!) {
            Logger.debug(LOG_TAG, codec.toString())
        }
    }

    private fun onCodecPrioritiesSetStatus(success: Boolean) {
        Logger.debug(LOG_TAG, "Codec priorities " + if (success) "successfully set" else "set error")
    }

    private fun onMissedCall(displayName: String?, uri: String?) {
        Logger.debug(LOG_TAG, "Missed call from ${displayName.orEmpty()} ${uri.orEmpty()}")
    }

    private fun onCallStats(
        callID: Int,
        duration: Int,
        audioCodec: String?,
        callStatusCode: Int,
        rx: RtpStreamStats?,
        tx: RtpStreamStats?) {
        Logger.debug(LOG_TAG, "Call Stats sent $callID $duration $audioCodec $callStatusCode $rx $tx")
    }

    private fun onCallReconnectionState(state: CallReconnectionState?) {
        Logger.debug(LOG_TAG, "Call reconnection state " + state!!.name)
    }

    private fun onSilentCallStatus(success: Boolean, number: String?) {
        Logger.debug(LOG_TAG, "Success: $success for silent call: $number")
    }

    private fun onTlsVerifyStatusFailed() {
        Logger.debug(LOG_TAG, "TlsVerifyStatusFailed")
    }

    private fun onCallDisconnected(isIncomingCall: Boolean, timeElapsed: Double, metrics: CallMetrics?) {
        onCallDisconnectedLiveData.value = CallStats(isIncomingCall, timeElapsed, metrics)
        onCallMetricsLiveData.value = null
    }

    private fun onCallMetrics(callMetrics: CallMetrics?) {
        callMetrics?.let { onCallMetricsLiveData.value = it }
    }

    companion object {
        private const val LOG_TAG = "SipServiceBR"
    }
}