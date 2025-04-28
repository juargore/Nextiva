package com.nextiva.nextivaapp.android.fcm

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nextiva.nextivaapp.android.ConnectMainActivity
import com.nextiva.nextivaapp.android.ConnectMainActivity.Companion.newIntent
import com.nextiva.nextivaapp.android.NextivaApplication
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Notification.TypeIDs
import com.nextiva.nextivaapp.android.core.analytics.events.MessagingEvent
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.features.messaging.view.ConversationActivity
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.api.ChatMessage
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.view.RoomConversationActivity.Companion.newIntent
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformNotificationOrchestrationServiceRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.AudioManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.DatadogManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AudioCodec
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.PushNotificationCallInfo
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.UserDetails
import com.nextiva.nextivaapp.android.models.net.broadsoft.calls.BroadsoftCallDetails
import com.nextiva.nextivaapp.android.models.net.platform.SmsMessages
import com.nextiva.nextivaapp.android.net.buses.RxEvents.CallUpdatedEvent
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.StringUtil.getStringBetween
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.StringReader
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Arrays
import java.util.Locale
import java.util.Objects
import java.util.Random
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class NextivaFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var sipManager: PJSipManager
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var dbManager: DbManagerKt
    @Inject
    lateinit var roomsDbManager: RoomsDbManager
    @Inject
    lateinit var avatarManager: AvatarManager
    @Inject
    lateinit var logManager: LogManager
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var audioManager: AudioManager
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var smsManagementRepository: SmsManagementRepository
    @Inject
    lateinit var callManagementRepository: CallManagementRepository
    @Inject
    lateinit var conversationRepository: ConversationRepository
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var platformNOSRepository: PlatformNotificationOrchestrationServiceRepository
    @Inject
    lateinit var datadogManager: DatadogManager

    private var updateNotificationCountHandler = Handler(Looper.getMainLooper())
    private var updateNotificationCountRunnable = Runnable {
        sessionManager.updateNotificationsCount(conversationRepository, applicationContext)
    }
    private var updateNotificationCountDelay = 2 * Constants.ONE_SECOND_IN_MILLIS

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        logManager.sipLogToFile(Enums.Logging.STATE_INFO, "Message Received: $remoteMessage")

        if (sessionManager.getUserDetails() != null) {
            if (!TextUtils.isEmpty(remoteMessage.data["type"])) {
                LogUtil.d("FCM Notification Incoming Call", remoteMessage.data["type"])
                logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - Push Notification Received TYPE: " + remoteMessage.data["type"])

                when (Objects.requireNonNull(remoteMessage.data["type"])) {
                    Enums.PushNotifications.EventTypes.NEW_CALL -> {
                        if (sessionManager.allowTermination) {
                            logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - handleIncomingCallNotification()")
                            handleIncomingCallNotification(remoteMessage)
                        } else {
                            logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - Ignoring call due to allowTermination")
                        }
                    }
                    Enums.PushNotifications.EventTypes.CALL_UPDATED -> {
                        logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - handleCallUpdatedNotification()")
                        handleCallUpdatedNotification(remoteMessage)
                    }
                    Enums.PushNotifications.EventTypes.RING_SPLASH -> showRingSplashNotification()
                    Enums.PushNotifications.EventTypes.MESSAGE_WAITING_INDICATOR -> showNewVoicemailCountNotification(remoteMessage)
                    Enums.PushNotifications.EventTypes.SMS -> handleSMSNotification(remoteMessage)
                    Enums.PushNotifications.EventTypes.CHAT -> handleConnectChatNotification(remoteMessage)
                }
            }

            notificationManager.showSIPStateNotification(sipManager)

        } else if (sessionManager.getUserDetails() == null) {
            LogUtil.e("FCM Notification", "We got a push when we were logged out!")
            //userRepository.unregisterForPushNotifications().subscribe()
        }

        logManager.sipLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success)
    }

    override fun handleIntent(intent: Intent) {
        val titleTag = "gcm.notification.title"
        val bodyTag = "gcm.notification.body"
        val statusTag = "status"
        val messageIdTag = "messageId"
        val senderIdTag = "from"
        val senderUserIdTag = "senderUserId"
        val senderPhoneNumberTag = "senderPhoneNumber"
        val roomIdTag = "roomId"
        val bundle = intent.extras
        val tag = bundle?.getString(TYPE_TAG) ?: ""

        when (tag) {
            Enums.PushNotifications.EventTypes.SMS -> handleSmsNotification(bundle?.getString(senderPhoneNumberTag), bundle?.getString(senderUserIdTag), bundle?.getString(messageIdTag))
            Enums.PushNotifications.EventTypes.PRESENCE -> handlePresenceNotification(bundle?.getString(titleTag), bundle?.getString(bodyTag), bundle?.getString(statusTag))
            Enums.PushNotifications.EventTypes.CHAT -> handleConnectChatNotification(
                bundle?.getString(messageIdTag),
                bundle?.getString(roomIdTag),
                bundle?.getString(senderIdTag),
                bundle?.getString(bodyTag),
                bundle?.getString(titleTag))
            else -> super.handleIntent(intent)
        }
    }

    private fun handleSMSNotification(remoteMessage: RemoteMessage) {
        val messageIdTag = "messageId"
        val senderUserIdTag = "senderUserId"
        val senderPhoneNumberTag = "senderPhoneNumber"

        if (!TextUtils.isEmpty(remoteMessage.getData()[messageIdTag])) {
            handleSmsNotification(
                remoteMessage.getData()[senderPhoneNumberTag],
                remoteMessage.getData()[senderUserIdTag],
                remoteMessage.getData()[messageIdTag]
            )
        }
    }

    private fun handleConnectChatNotification(remoteMessage: RemoteMessage) {
        val titleTag = "title"
        val bodyTag = "body"
        val messageIdTag = "messageId"
        val senderUserIdTag = "senderUserId"
        val roomIdTag = "roomId"

        remoteMessage.data[messageIdTag]?.nullIfEmpty()?.let { messageId ->
            handleConnectChatNotification(
                messageId,
                remoteMessage.data[roomIdTag] ?: "",
                remoteMessage.data[senderUserIdTag] ?: "",
                remoteMessage.data[bodyTag] ?: "",
                remoteMessage.data[titleTag] ?: ""
            )
        }
    }

    private fun handleIncomingCallNotification(remoteMessage: RemoteMessage) {
        val appearanceTag = "appearance"
        val callingNumberTag = "callingNumber"
        val senderTag = "sender"
        val callIdTag = "callId"
        val sdpTag = "sdp"
        val assertedIdTag = "passertedIdentity"
        val trackingId = "extTrackingId"

        logManager.sipLogToFile(Enums.Logging.STATE_INFO, "Push Incoming Call Notification")
        logManager.sipLogToFile(Enums.Logging.STATE_INFO, "Call Contains Calling Number: " + !TextUtils.isEmpty(remoteMessage.getData()[callingNumberTag]))
        logManager.sipLogToFile(Enums.Logging.STATE_INFO, "Call Contains Calling Number: " + !TextUtils.isEmpty(remoteMessage.getData()[callingNumberTag]))
        logManager.sipLogToFile(Enums.Logging.STATE_INFO, "Call Contains Appearance: " + !TextUtils.isEmpty(remoteMessage.getData()[appearanceTag]))
        logManager.sipLogToFile(Enums.Logging.STATE_INFO, "Call Contains Sender: " + !TextUtils.isEmpty(remoteMessage.getData()[senderTag]))
        logManager.sipLogToFile(Enums.Logging.STATE_INFO, "Call Contains Call Id: " + !TextUtils.isEmpty(remoteMessage.getData()[callIdTag]))
        logManager.sipLogToFile(Enums.Logging.STATE_INFO, "Call Contains SDP: " + !TextUtils.isEmpty(remoteMessage.getData()[sdpTag]))

        val pm = getSystemService(POWER_SERVICE) as PowerManager
        val featureAccessCode = "#0322"
        var senderValue = ""
        val callingNumber = if (remoteMessage.data[callingNumberTag]?.contains("sip:") == true) {
            getStringBetween(remoteMessage.data[callingNumberTag] ?: "", "sip:", "@")
        } else remoteMessage.getData()[callingNumberTag] ?: ""

        logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - CallingNumber: $callingNumber")

        MainScope().launch(Dispatchers.IO) {
            val contact = dbManager.getContactFromPhoneNumberInThread(callingNumber).value
            val sdp = remoteMessage.data[sdpTag]?.nullIfEmpty() ?: ""

            logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - Contact: $contact || sdp: $sdp")

            if (remoteMessage.getData().contains(senderTag)) {
                logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - Sender: ${remoteMessage.getData()[senderTag] ?: ""}")
            }

            remoteMessage.getData()[senderTag]?.nullIfEmpty()?.let { sender ->
                if (sender.contains("@")) {
                    senderValue = when {
                        sender.contains("sip:") && sender.contains("@anonymous.invalid") -> getStringBetween(
                            sender,
                            "sip:",
                            "@anonymous.invalid"
                        )

                        !TextUtils.isEmpty(remoteMessage.getData()[assertedIdTag]) -> {
                            if (remoteMessage.getData()[assertedIdTag]?.contains("@") == true) {
                                remoteMessage.getData()[assertedIdTag]
                                    ?.split("@".toRegex())
                                    ?.dropLastWhile { it.isEmpty() }
                                    ?.toTypedArray()?.get(0) ?: ""

                            } else {
                                remoteMessage.getData()[assertedIdTag] ?: ""
                            }
                        }

                        contact?.uiName?.nullIfEmpty() != null -> contact.uiName ?: ""
                        sender.contains("sip:") && !TextUtils.isEmpty(
                            getStringBetween(
                                sender,
                                "sip:",
                                "@"
                            )
                        ) -> {
                            PhoneNumberUtils.formatNumber(
                                getStringBetween(sender, "sip:", "@"),
                                Locale.getDefault().country
                            )
                        }

                        else -> sender
                    }
                } else {
                    if (sender.contains("UNASSIGNED") &&
                        !TextUtils.isEmpty(
                            PhoneNumberUtils.formatNumber(
                                callingNumber,
                                Locale.getDefault().country
                            )
                        )
                    ) {
                        senderValue = sender.replace(
                            "UNASSIGNED",
                            contact?.uiName ?: PhoneNumberUtils.formatNumber(
                                callingNumber,
                                Locale.getDefault().country
                            )
                        )
                    }

                    contact?.uiName?.nullIfEmpty()?.let { uiName ->
                        if (sender.split(" - ".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray().isNotEmpty()) {
                            val callerIdName =
                                sender.split(" - ".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()[sender.split(" - ".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray().size - 1]
                            senderValue = senderValue.replace(callerIdName, uiName)
                        }
                    }
                }

                callingNumber.nullIfEmpty()?.let {
                    if (senderValue.isEmpty()) {
                        senderValue = sender.takeIf { it.isNotBlank() } ?: contact?.uiName ?: it
                    }
                }
            }

            contact?.uiName?.nullIfEmpty()?.let { uiName ->
                senderValue = if (senderValue.contains(" - ") && senderValue.split(" - ".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray().isNotEmpty()
                ) {
                    val callerIdName = senderValue.split(" - ".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()[senderValue.split(" - ".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray().size - 1]
                    senderValue.replace(callerIdName, uiName)

                } else {
                    uiName
                }
            }

            val callId = remoteMessage.getData()[callIdTag]
            val extTrackingId = remoteMessage.getData().getOrDefault(trackingId, "")
            LogUtil.d("Call Tracking Id: $extTrackingId")
            logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - Call Tracking Id: $extTrackingId")

            val participantInfo = ParticipantInfo(
                numberToCall = callingNumber,
                dialingServiceType = Enums.Service.DialingServiceTypes.VOIP,
                callType = Enums.Sip.CallTypes.VOICE,
                displayName = senderValue.nullIfEmpty() ?: contact?.uiName ?: callingNumber,
                trackingId = extTrackingId,
                contactId = contact?.userId
            )

            withContext(Dispatchers.Main) {
                val dTemp: DisposableSubscriber<BroadsoftCallDetails?> =
                    object : DisposableSubscriber<BroadsoftCallDetails?>() {
                        override fun onNext(broadsoftCallDetails: BroadsoftCallDetails?) {
                            logManager.sipLogToFile(
                                Enums.Logging.STATE_INFO,
                                "Push Incoming Call Timer: Running"
                            )
                            LogUtil.d(
                                "Request Call Info Result: " + GsonUtil.getJSON(
                                    broadsoftCallDetails
                                )
                            )

                            if (broadsoftCallDetails?.state != null && !this.isDisposed) {
                                if (!TextUtils.equals(broadsoftCallDetails.state, "Alerting")) {
                                    if (sipManager.incomingCall != null) {
                                        logManager.sipLogToFile(
                                            Enums.Logging.STATE_INFO,
                                            "${getTime()} - Push Incoming Call Timer: Out of sync with server! Hanging up."
                                        )
                                        hangupIncomingCall(true, callId, extTrackingId, false)
                                    }

                                    dispose()
                                    logManager.sipLogToFile(
                                        Enums.Logging.STATE_INFO,
                                        "${getTime()} - Push Incoming Call Timer: Call no longer ringing. Disposing."
                                    )
                                } else {
                                    logManager.sipLogToFile(
                                        Enums.Logging.STATE_INFO,
                                        "Push Incoming Call ActiveCallInformation state: " + broadsoftCallDetails.state
                                    )
                                }
                            } else if (!this.isDisposed) {
                                dispose()
                                logManager.sipLogToFile(
                                    Enums.Logging.STATE_INFO,
                                    "${getTime()} - Push Incoming Call Timer: Data is empty or null. Disposing."
                                )
                            }
                        }

                        override fun onError(t: Throwable) {
                            logManager.sipLogToFile(
                                Enums.Logging.STATE_INFO,
                                "${getTime()} - Push Incoming Call Timer: onError!  Disposing. " + t.localizedMessage
                            )

                            if (!this.isDisposed) {
                                FirebaseCrashlytics.getInstance().recordException(t)
                                logManager.sipLogToFile(
                                    Enums.Logging.STATE_INFO,
                                    "${getTime()} - hangupIncomingCall(true, $callId, $extTrackingId, false)"
                                )
                                hangupIncomingCall(true, callId, extTrackingId, false)
                                dispose()
                            }
                        }

                        override fun onComplete() {
                            logManager.sipLogToFile(
                                Enums.Logging.STATE_INFO,
                                "Push Incoming Call Timer: Completed"
                            )
                        }
                    }

                if (!sipManager.isRegistered() || sipManager.incomingCall == null) {
                    val pushNotificationCallInfo = PushNotificationCallInfo(
                        featureAccessCode + remoteMessage.getData()[appearanceTag],
                        remoteMessage.getData()[callIdTag],
                        extTrackingId,
                        participantInfo,
                        getCodecsFromSdp(sdp)
                    )
                    sipManager.pushCallInfo = pushNotificationCallInfo
                    if (!sipManager.isCallActive() || !pm.isInteractive) {
                        logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - sipManager.registerAccount()")
                        sipManager.registerAccount()
                    }

                    notificationManager.showSIPStateNotification(sipManager)
                    sipManager.showIncomingCallPushNotification(pushNotificationCallInfo)
                    logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - showIncomingCallPushNotification = $pushNotificationCallInfo")

                } else if (sipManager.isRegistered() && sipManager.incomingCall != null) {
                    val pInfo = sipManager.incomingCall?.participantInfo
                    val pTracking = pInfo?.trackingId

                    if (extTrackingId != pTracking) {
                        return@withContext
                    }

                    hangupIncomingCall(true, callId, null, true)

                    val pushNotificationCallInfo = PushNotificationCallInfo(
                        featureAccessCode + remoteMessage.getData()[appearanceTag],
                        remoteMessage.getData()[callIdTag],
                        extTrackingId,
                        participantInfo,
                        getCodecsFromSdp(sdp)
                    )
                    sipManager.pushCallInfo = pushNotificationCallInfo
                    notificationManager.showSIPStateNotification(sipManager)
                    sipManager.showIncomingCallPushNotification(pushNotificationCallInfo)
                    logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - showIncomingCallPushNotification = $pushNotificationCallInfo")
                }

                disposable?.dispose()
                disposable = dTemp
                callManagementRepository.getActiveCallInformation(remoteMessage.getData()[callIdTag])
                    .repeatWhen { completion: Flowable<Any?> ->
                        completion.delay(2, TimeUnit.SECONDS)
                    }
                    .subscribe(disposable)
            }
        }
    }

    private fun getCodecsFromSdp(sdp: String?): ArrayList<AudioCodec>? {
        val codecs = ArrayList<AudioCodec>()
        val reader = BufferedReader(StringReader(sdp))
        var line: String
        try {
            while (reader.readLine().also { line = it } != null) {
                if (line.startsWith("a=rtpmap:")) {
                    codecs.add(
                        AudioCodec(
                            line.substring(line.indexOf(" ") + 1, line.indexOf("/")),
                            null,
                            null,
                            null
                        )
                    )
                }
            }

        } catch (e: Exception) {
            LogUtil.e("FCM Notification", "Error parsing incoming call codecs.")
            logManager.sipLogToFile(Enums.Logging.STATE_INFO, "${getTime()} - Error parsing incoming call codecs.")
            return null
        }

        return codecs
    }

    fun hangupIncomingCall(callWasMissed: Boolean, callId: String?, trackingId: String?, isDivertedCall: Boolean) {
        val pushCallInfo = sipManager.pushCallInfo
        val pushCallId = pushCallInfo?.callId
        if (!isDivertedCall && callId != pushCallId) {
            return
        }

        audioManager.stopRingTone()
        var killIncomingCall = true
        sipManager.incomingCall?.participantInfo?.let { participantInfo ->
            val incomingCallTrackingId = participantInfo.trackingId
            val incomingCallDivertedCallId = sipManager.getDivertedCallId()
            if (incomingCallTrackingId == trackingId && incomingCallDivertedCallId == trackingId || isDivertedCall) {
                killIncomingCall = false
                sipManager.clearDivertedCallId()
            }
        }

        sipManager.stopIncomingCall(killIncomingCall)

        if (callWasMissed) {
            notificationManager.cancelNotification(TypeIDs.INCOMING_CALL)
            if (!sipManager.isCallActive()) {
                sipManager.tearDown()
            }
        }
    }

    private fun handleConnectChatNotification(
        messageId: String?,
        roomId: String?,
        senderId: String?,
        body: String?,
        title: String?
    ) {
        var strippedBody = body
        val bodySenderDelimiter = ": "
        val bodyComponents = body
            ?.split(bodySenderDelimiter.toRegex())
            ?.dropLastWhile { it.isEmpty() }
            ?.toTypedArray()

        if (!bodyComponents.isNullOrEmpty()) {
            strippedBody = java.lang.String.join(bodySenderDelimiter, *Arrays.copyOfRange(bodyComponents, 1, bodyComponents.size))
        }

        val message = ChatMessage(
            messageId ?: "",
            roomId ?: "",
            senderId ?: "",
            strippedBody ?: "")

        roomsDbManager.saveRoomChatMessages(ArrayList(listOf(message)))
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    roomId?.let { roomsDbManager.modifyUnreadMessageCountByRoomId(1, roomId) }
                    showNewConnectChatMessageNotification(messageId, roomId, body, title)
                }

                override fun onError(e: Throwable) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    LogUtil.e("FCM Notification", "Failed saving chat messages")
                }
            })
    }

    private fun showNewConnectChatMessageNotification(
        messageId: String?,
        roomId: String?,
        body: String?,
        title: String?
    ) {
        var finalBody = body?.replace("&quot;", "\"")
            ?.replace("&apos;", "'")
            ?.replace("&gt;", ">")
            ?.replace("&lt;", "<")
            ?.replace("&amp;", "&")

        val bodyComponents = body?.split(":".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        if (bodyComponents?.size == 2 && bodyComponents[1].trim { it <= ' ' }.isEmpty()) {
            finalBody += application.getString(R.string.Connect_sms_media_message)
        }

        roomId?.let {
            roomsDbManager.getRoomCompletable(roomId)
                .subscribe(object : DisposableSingleObserver<DbRoom?>() {
                    override fun onSuccess(dbRoom: DbRoom) {
                        val avatarInfoBuilder = AvatarInfo.Builder().isConnect(true)
                        val displayName = application.getString(R.string.my_status_my_room)

                        if (dbRoom.typeEnum() == RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM) {
                            avatarInfoBuilder
                                .setDisplayName(sessionManager.userDetails?.fullName?.nullIfEmpty() ?: displayName)
                                .setFontAwesomeIconResId(R.string.fa_user)

                        } else {
                            avatarInfoBuilder
                                .setDisplayName(dbRoom.name)
                                .setFontAwesomeIconResId(R.string.fa_door_open)
                                .setFontAwesomeFontResId(R.font.fa_regular_400)
                                .setAlwaysShowIcon(true)
                        }

                        val i = Intent(newIntent(application, roomId, title ?: ""))
                        notificationManager.showNotification(
                            application,
                            Enums.Notification.ChannelIDs.CHAT,
                            NotificationCompat.CATEGORY_MESSAGE,
                            TypeIDs.NEW_CHAT_MESSAGE_NOTIFICATION_ID,
                            messageId,
                            Random().nextInt(),
                            finalBody,
                            title,
                            i,
                            roomId,
                            avatarManager.getBitmap(avatarInfoBuilder.build())
                        )
                    }

                    override fun onError(e: Throwable) {}
                })
        }
    }

    private fun showRingSplashNotification() {
        val ringSplashNotification = notificationManager.simpleNotification(
            getString(R.string.notifications_channel_ring_splash_name),
            getString(R.string.notifications_channel_ring_splash_description),
            Enums.Notification.ChannelIDs.RING_SPLASH
        )

        notificationManager.showNotification(TypeIDs.RING_SPLASH, ringSplashNotification)
    }

    private fun handleCallUpdatedNotification(remoteMessage: RemoteMessage?) {
        val callIdTag = "callId"
        val reasonTag = "reason"
        val trackingIdTag = "extTrackingId"
        LogUtil.d("Incoming Call handleCallUpdatedNotification  CANCEL CALL!!!")

        remoteMessage?.data?.get(callIdTag)?.nullIfEmpty()?.let { callId ->
            remoteMessage.data[reasonTag]?.nullIfEmpty()?.let { reason ->
                remoteMessage.data[trackingIdTag]?.nullIfEmpty()?.let { trackingId ->
                    notificationManager.showMissedCallNotification(
                        CallUpdatedEvent(
                            callId,
                            trackingId,
                            !TextUtils.equals(reason, Enums.PushNotifications.CallUpdatedEventTypes.CALL_ANSWERED_ELSEWHERE)
                        ),
                        this
                    )

                    hangupIncomingCall(
                        remoteMessage.getData()[reasonTag] == Enums.PushNotifications.CallUpdatedEventTypes.CALL_ANSWERED_ELSEWHERE ||
                                remoteMessage.getData()[reasonTag] == Enums.PushNotifications.CallUpdatedEventTypes.CALL_ANSWERED_ELSEWHERE ||
                                remoteMessage.getData()[reasonTag] == Enums.PushNotifications.CallUpdatedEventTypes.CALL_ABANDONED,
                        callId,
                        trackingId,
                        false
                    )
                }
            }
        }
    }

    private fun showNewVoicemailCountNotification(remoteMessage: RemoteMessage?) {
        val newVoicemailTag = "newVM"
        dbManager.expireVoiceConversationMessagesCache()
        sessionManager.updateNotificationsCount(conversationRepository, applicationContext)

        remoteMessage?.data?.get(newVoicemailTag)?.nullIfEmpty()?.let { newVoicemail ->
            if (!TextUtils.equals(remoteMessage.getData()[newVoicemailTag], "0")) {
                try {
                    LogUtil.d("FCM Notification", "Handling notification.")
                    val i = Intent(newIntent(this, Enums.Platform.ViewsToShow.CALLS_VOICEMAIL))

                    sessionManager.updateNotificationsCount(conversationRepository, applicationContext)
                    if (newVoicemail.toInt() != sessionManager.getNewVoicemailMessagesCount()) {
                        notificationManager.cancelNotification(TypeIDs.MWI_NOTIFICATION_ID)
                        notificationManager.showNotification(
                            application,
                            Enums.Notification.ChannelIDs.VOICEMAIL,
                            TypeIDs.MWI_NOTIFICATION_ID,
                            REQUEST_CODE,
                            application.getString(R.string.push_notification_mwi_body, "" + newVoicemail),
                            application.getString(R.string.push_notification_mwi_title),
                            i,
                            null
                        )
                    }
                } catch (e: NumberFormatException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    logManager.sipLogToFile(Enums.Logging.STATE_ERROR, e.toString())
                }

            } else {
                notificationManager.cancelNotification(TypeIDs.MWI_NOTIFICATION_ID)
            }

            updateNotificationCountHandler.postDelayed(updateNotificationCountRunnable, updateNotificationCountDelay)
        }
    }

    private fun handleSmsNotification(senderPhoneNumber: String?, senderUuid: String?, messageId: String?) {
        checkUserDetailsTelephoneNumber(sessionManager.getUserDetails())

        messageId?.let {
            smsManagementRepository.getSmsMessageWithMessageId(messageId)
                .subscribe(object : DisposableSingleObserver<SmsMessages?>() {
                    override fun onSuccess(smsMessages: SmsMessages) {
                        saveSmsMessage(smsMessages, senderPhoneNumber, senderUuid, messageId)
                    }

                    override fun onError(e: Throwable) {
                        LogUtil.e("FCM Notification", "Failed getting SMS message.")
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                })
        }
    }

    private fun saveSmsMessage(smsMessages: SmsMessages, senderPhoneNumber: String?, senderUuid: String?, messageId: String?) {
        if (sessionManager.userDetails != null && sessionManager.currentUser != null) {
            markMessagesReadIfInConversation(smsMessages)

            MainScope().launch(Dispatchers.IO) {
                dbManager.saveSmsMessages(
                    smsMessages.data,
                    CallUtil.getCountryCode() + sessionManager.userDetails?.telephoneNumber,
                    Enums.SMSMessages.SentStatus.SUCCESSFUL,
                    sessionManager.currentUser.userUuid,
                    sessionManager.allTeams
                )

                withContext(Dispatchers.Main) {
                    analyticsManager.trackEvent(MessagingEvent().incomingSMS())
                    datadogManager.performCustomAction(MessagingEvent().incomingSMS().name, emptyMap<String, Any>())
                    showSmsNotification(messageId, senderPhoneNumber, senderUuid)
                }
            }

        } else {
            LogUtil.e("FCM Notification", "Failed saving SMS message.  User Details is null.")
        }
    }

    private fun markMessagesReadIfInConversation(smsMessages: SmsMessages) {
        ((application as? NextivaApplication)?.currentActivity as? ConversationActivity)?.let { conversationActivity ->
            val conversationId = conversationActivity.currentConversationId
            var foundConversation = false

            if (!TextUtils.isEmpty(conversationId)) {
                smsMessages.data?.forEach { data ->
                    data.messages?.forEach { message ->
                        if (TextUtils.equals(message.getConversationId(), conversationId) && message.messageState != null) {
                            foundConversation = true
                            message.messageState?.readStatus = Enums.SMSMessages.ReadStatus.READ
                        }
                    }
                }
            }

            if (foundConversation) {
                conversationActivity.updateReadStatus()
            }
        }
    }

    private fun showSmsNotification(messageId: String?, senderPhoneNumber: String?, senderUuid: String?) {
        messageId?.let {
            MainScope().launch(Dispatchers.IO) {
                val smsMessage = dbManager.getSmsMessageByMessageId(messageId)

                if (smsMessage != null) {
                    val participantNumbers = ArrayList<String>()
                    var ourNumber = ""
                    sessionManager.userDetails?.let { ourNumber = CallUtil.getCountryCode() + it.telephoneNumber }

                    if (!TextUtils.isEmpty(smsMessage.groupValue) &&
                        !TextUtils.isEmpty(Objects.requireNonNull<String?>(smsMessage.groupValue)
                            .trim { it <= ' ' })) {

                        for (number in (smsMessage.groupValue ?: "").trim { it <= ' ' }
                            .split(",".toRegex())
                            .dropLastWhile { it.isEmpty() }
                            .toTypedArray()) {
                            if (!ourNumber.contains(number)) {
                                participantNumbers.add(number)
                            }
                        }
                    }

                    val contact = when {
                        !TextUtils.isEmpty(senderPhoneNumber) -> senderPhoneNumber?.let { number -> dbManager.getContactFromPhoneNumberInThread(number).value }
                        !TextUtils.isEmpty(senderUuid) -> senderUuid?.let { uuid -> dbManager.getConnectContactFromUuidInThread(uuid) }
                        else -> null
                    }

                    val conversationDetails = SmsConversationDetails(
                        smsMessage,
                        ourNumber,
                        sessionManager.currentUser?.userUuid ?: ""
                    )

                    val i = ConversationActivity.newIntent(
                        application,
                        conversationDetails,
                        false,
                        Enums.Chats.ConversationTypes.SMS,
                        Enums.Chats.ChatScreens.CONVERSATION
                    )

                    withContext(Dispatchers.Main) {
                        notificationManager.showNotification(
                            application,
                            notificationManager.getPresentChannelIdFor(Enums.Notification.ChannelIDs.SMS),
                            null,
                            TypeIDs.NEW_SMS_NOTIFICATION_ID,
                            smsMessage.messageId,
                            Random().nextInt(),
                            if (smsMessage.body?.isNotEmpty() == true) smsMessage.body else smsMessage.preview,
                            contact?.uiName ?: senderPhoneNumber,
                            i,
                            null,
                            avatarManager.getBitmap(contact?.avatarInfo ?: AvatarInfo.Builder().build()),
                            sessionManager.getNewVoicemailMessagesCount()
                        )

                        sessionManager.updateNotificationsCount(conversationRepository, applicationContext)
                    }
                }
            }
        }
    }

    private fun handlePresenceNotification(title: String?, body: String?, status: String?) {
        sessionManager.connectUserPresence?.let {
            if (TextUtils.isDigitsOnly(status)) {
                it.setConnectState(status?.toIntOrNull() ?: 0)
            }
            sessionManager.setConnectUserPresence(it, sessionManager.isConnectUserPresenceAutomatic())

        }

        notificationManager.showNotification(
            application,
            Enums.Notification.ChannelIDs.PRESENCE,
            TypeIDs.PRESENCE_STATUS_UPDATED_NOTIFICATION_ID,
            REQUEST_CODE,
            body,
            title,
            Intent(this, ConnectMainActivity::class.java),
            null
        )
    }

    private fun checkUserDetailsTelephoneNumber(userDetails: UserDetails?) {
        when {
            userDetails == null -> Exception("showSmsNotification: UserDetails is null")
            userDetails.telephoneNumber == null -> Exception("showSmsNotification: UserDetails Telephone Number is null")
            userDetails.telephoneNumber?.isEmpty() == true -> Exception("showSmsNotification: UserDetails Telephone Number is empty")
            else -> null
        }?.let { FirebaseCrashlytics.getInstance().recordException(it) }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sessionManager.userDetails?.let {
            platformNOSRepository.registerForSmsPushNotifications(token)
            userRepository.registerForPushNotifications(token)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { logManager.logToFile(Enums.Logging.STATE_INFO, "Success registering for push notifications on background") },
                    { error -> logManager.logToFile(Enums.Logging.STATE_FAILURE, "Error registering for push notifications on background: $error") }
                )
        }
    }

    companion object {
        const val REQUEST_CODE = 1
        private const val TYPE_TAG = "type"
        var disposable: DisposableSubscriber<BroadsoftCallDetails?>? = null
    }

    private fun getTime(): String {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}