package com.nextiva.nextivaapp.android.managers

import android.app.Application
import android.text.TextUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonSyntaxException
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.NextivaApplication
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.features.messaging.view.ConversationActivity
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.api.ChatMessage
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectMyRoomResponse
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.model.DbChatMessage
import com.nextiva.nextivaapp.android.managers.apimanagers.SipApiManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.*
import com.nextiva.nextivaapp.android.models.net.platform.Data
import com.nextiva.nextivaapp.android.models.net.platform.Message
import com.nextiva.nextivaapp.android.models.net.platform.SelectiveCallRejection
import com.nextiva.nextivaapp.android.models.net.platform.messages.JobChannel
import com.nextiva.nextivaapp.android.models.net.platform.messages.SmsMessageBulkAction
import com.nextiva.nextivaapp.android.models.net.platform.messages.SmsMessageBulkActionList
import com.nextiva.nextivaapp.android.models.net.platform.voice.VoiceMessage
import com.nextiva.nextivaapp.android.models.net.platform.websocket.*
import com.nextiva.nextivaapp.android.models.net.sip.SipCallDetails
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import io.reactivex.Observable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import okhttp3.*
import okio.ByteString
import org.json.JSONException
import org.json.JSONObject
import org.threeten.bp.Instant
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NextivaWebSocketManager @Inject constructor(
    var application: Application,
    var sessionManager: SessionManager,
    var dbManager: DbManager,
    val sharedPreferencesManager: SharedPreferencesManager,
    var roomsDbManager: RoomsDbManager,
    var logManager: LogManager,
    var connectionStateManager: ConnectionStateManager,
    var analyticsManager: AnalyticsManager,
    var blockingNumberManager: BlockingNumberManager,
    var schedulerProvider: SchedulerProvider,
    var sipManager: PJSipManager,
    var sipApiManager: SipApiManager,
    var smsManagementRepository: SmsManagementRepository,
    var conversationRepository: ConversationRepository
) : WebSocketManager, WebSocketListener() {

    private var client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var sessionId: String? = null
    private var shouldClose = false
    private var retryCount = 0
    private val maxRetryCount = 10

    private var allSavedTeams: List<SmsTeam>? = null

    private val formatterManager = FormatterManager.getInstance()

    init {
        connectionStateManager.setWebSocketManager(this)
        client.dispatcher.maxRequests = 1
        allSavedTeams = sessionManager.allTeams
    }

    override fun setup() {
        try {
            if (!connectionStateManager.isConnectWebSocketConnected && client.connectionPool.connectionCount() == 0) {
                sessionManager.accountInformation?.domainName?.let { domainName ->
                    val restApiUrl = "https://$domainName/rest-api/realtime-data-stream-orchestration/v2/socket/connect"
                    val request = sessionManager.sessionId?.let {
                        Request.Builder()
                            .header("x-api-key", it)
                            .header("nextiva-context-corpAcctNumber",
                                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
                            )
                            .url(restApiUrl)
                            .post(FormBody.Builder().build())
                            .build()
                    }

                    if (request != null) {
                        client.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                logManager.logToFile(Enums.Logging.STATE_ERROR, "Failed to get WebSocket connection details: ${e.message}")
                                retry()
                            }

                            override fun onResponse(call: Call, response: Response) {
                                response.body?.let { responseBody ->
                                    val responseData = responseBody.string()
                                    try {
                                        if (response.header("Content-Type")?.contains("application/json") == true) {
                                            val jsonObject = JSONObject(responseData)
                                            val websocketToken = jsonObject.getString("websocket_token")
                                            val corpAccountNumber = jsonObject.getInt("CorpAccountNumber")
                                            connectToWebSocket(domainName, websocketToken, corpAccountNumber)
                                        } else {
                                            // Log the unexpected response for debugging
                                            logManager.logToFile(Enums.Logging.STATE_ERROR, "Unexpected response format: $responseData")
                                            retry()
                                        }
                                    } catch (e: JSONException) {
                                        logManager.logToFile(Enums.Logging.STATE_ERROR, "Failed to parse WebSocket connection details: ${e.message}")
                                        retry()
                                    }
                                } ?: run {
                                    logManager.logToFile(Enums.Logging.STATE_ERROR, "Empty response body")
                                    retry()
                                }
                            }
                        })
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun connectToWebSocket(domainName: String, websocketToken: String, corpAccountNumber: Int) {
        val webSocketUrl = "wss://$domainName/public/realtime-data-stream-orchestration/socket/connect"
        val request = Request.Builder()
            .url(webSocketUrl)
            .build()

        webSocket = client.newWebSocket(request, this)

        val sendJson = GsonUtil.getJSON(
            mapOf(
                "websocketToken" to websocketToken,
                "nextiva-context-corpAcctNumber" to corpAccountNumber
            )
        )

        logManager.logToFile(Enums.Logging.STATE_INFO, "Will send authentication request.")
        webSocket?.send(sendJson)
    }

    override fun stopConnection() {
        shouldClose = true
        webSocket?.cancel()
        connectionStateManager.postIsConnectWebSocketConnected(false)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Websocket opened.")
        connectionStateManager.postIsConnectWebSocketConnected(true)
        shouldClose = false
        retryCount = 0
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Incoming message: $text")

        if (isValidJson(text)) {
            GsonUtil.getObject(WebSocketConnectMessage::class.java, text)?.let { message ->
                when (message.application) {
                    Enums.Platform.WebSocketApplications.APPLICATION_CONTACT -> updateConnectPresenceSession(message)
                    Enums.Platform.WebSocketApplications.APPLICATION_CONVERSATION -> handleConversationMessage(message)
                    Enums.Platform.WebSocketApplications.APPLICATION_CHAT -> handleChatMessage(message)
                    Enums.Platform.WebSocketApplications.APPLICATION_VOICE_CALL -> handleVoiceCallMessage(message)
                    Enums.Platform.WebSocketApplications.APPLICATION_VOICE_SETTINGS -> handleVoiceSettingMessage(message)
                    else -> setSessionId(text)
                }
            } ?: setSessionId(text)
        } else {
            logManager.logToFile(Enums.Logging.STATE_INFO, "Invalid JSON format: $text")
        }
    }

    private fun isValidJson(text: String): Boolean {
        return try {
            JSONObject(text)
            true
        } catch (e: JSONException) {
            false
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Incoming byte array message")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        webSocket.close(code, reason)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Websocket will close: $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        logManager.logToFile(if (TextUtils.equals(t.localizedMessage, "Socket Closed")) Enums.Logging.STATE_INFO else Enums.Logging.STATE_ERROR, "Websocket failed: ${t.localizedMessage}")
        connectionStateManager.postIsConnectWebSocketConnected(false)
        retry()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Websocket closed: $reason")
        connectionStateManager.postIsConnectWebSocketConnected(false)
        retry()
    }

    private fun retry() {
        if (!shouldClose && retryCount < maxRetryCount) {
            retryCount += 1
            logManager.logToFile(Enums.Logging.STATE_INFO, "Attempting to reconnect - retry number: $retryCount")
            setup()
        }
    }

    private fun setSessionId(text: String) {
        try {
            val json = JSONObject(text)

            if (json.optBoolean("success", false) && json.has("sessionId")) {
                val mSessionId = json.getString("sessionId")
                if (mSessionId.isNotEmpty()) {
                    sessionId = mSessionId
                    if (sessionManager.isCommunicationsBulkActionsUpdateEnabled) {
                        fetchBulkActionsList(0)
                    }
                }
            }
        } catch (e: JsonSyntaxException) {
            logManager.logToFile(Enums.Logging.STATE_ERROR, "Invalid JSON format: ${e.message}")
        } catch (e: Exception) {
            logManager.logToFile(Enums.Logging.STATE_ERROR, "Error parsing incoming message: ${e.message}")
        }
    }

    private fun fetchBulkActionsList(page: Int) {
        if (lastBulkActionRefreshTimestamp > 0) {
            smsManagementRepository.checkForDeletedItems(Instant.ofEpochSecond(lastBulkActionRefreshTimestamp), page)
                .subscribe(object : DisposableSingleObserver<SmsMessageBulkActionList?>() {
                    override fun onSuccess(bulkActions: SmsMessageBulkActionList) {
                        Observable.just(true)
                            .subscribeOn(schedulerProvider.io())
                            .subscribe {
                                bulkActions.data?.forEach { bulkAction ->
                                    smsManagementRepository.onMessageConversationBulk(bulkAction)
                                    onMessageVoiceBulk(bulkAction)
                                }
                            }

                        if (bulkActions.hasNext == true) {
                            fetchBulkActionsList(page + 1)
                        } else {
                            lastBulkActionRefreshTimestamp = Instant.now().epochSecond
                        }
                    }

                    override fun onError(e: Throwable) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                })
        } else {
            lastBulkActionRefreshTimestamp = Instant.now().epochSecond
        }
    }

    private fun handleConversationMessage(message: WebSocketConnectMessage) {
        when (message.type) {
            Enums.Platform.WebSocketMessageTypes.MESSAGE_BULK_ACTION -> {
                GsonUtil.getObject(SmsMessageBulkAction::class.java, message.payload)?.let { bulkAction ->
                    smsManagementRepository.onMessageConversationBulk(bulkAction)
                    onMessageVoiceBulk(bulkAction)
                }
            }
            else -> onMessageConversation(message)
        }
        sessionManager.updateNotificationsCount(conversationRepository, application)
    }

    private fun handleChatMessage(message: WebSocketConnectMessage) {
        when (message.type) {
            Enums.Platform.WebSocketMessageTypes.MESSAGE_CHAT_REGULAR -> {
                GsonUtil.getObject(Array<ChatMessage>::class.java, message.payload)?.let { chatMessageList ->
                    chatMessageList.forEach { chatMessage ->
                        if (chatMessage.deleted) {
                            roomsDbManager.deleteChatMessage(chatMessage.roomId, chatMessage.id)
                            roomsDbManager.modifyUnreadMessageCountByRoomId(-1, chatMessage.roomId)
                        } else if (chatMessage.edited) {
                            roomsDbManager.updateRoomChatMessage(DbChatMessage(chatMessage))
                                .subscribe(object : DisposableCompletableObserver() {
                                    override fun onComplete() {}
                                    override fun onError(e: Throwable) {
                                        FirebaseCrashlytics.getInstance().recordException(e)
                                    }
                                })
                            roomsDbManager.modifyUnreadMessageCountByRoomId(1, chatMessage.roomId)
                        } else {
                            roomsDbManager.saveRoomChatMessages(arrayListOf(chatMessage))
                                .subscribe(object : DisposableCompletableObserver() {
                                    override fun onComplete() {}
                                    override fun onError(e: Throwable) {
                                        FirebaseCrashlytics.getInstance().recordException(e)
                                    }
                                })
                            if (!chatMessage.read) {
                                roomsDbManager.modifyUnreadMessageCountByRoomId(1, chatMessage.roomId)
                            }
                        }
                    }
                }
            }
            Enums.Platform.WebSocketMessageTypes.MESSAGE_ROOM_ADD_MEMBER -> {
                GsonUtil.getObject(ConnectMyRoomResponse::class.java, message.payload)?.let { roomResponse ->
                    roomResponse.room?.let { room ->
                        roomsDbManager.getMyRoomInThread()?.roomId?.let { myRoomId ->
                            if (room.id == myRoomId) {
                                room.type = RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM.value
                            }
                        }
                        roomsDbManager.saveRooms(arrayListOf(room))
                            .subscribe(object : DisposableCompletableObserver() {
                                override fun onComplete() {}
                                override fun onError(e: Throwable) {
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                }
                            })
                    }
                }
            }
            Enums.Platform.WebSocketMessageTypes.MESSAGE_ROOM_REMOVE_MEMBER -> {
                GsonUtil.getObject(ConnectMyRoomResponse::class.java, message.payload)?.let { roomResponse ->
                    roomResponse.room?.let { room ->
                        var isMyRoom = false
                        roomsDbManager.getMyRoomInThread()?.roomId?.let { myRoomId ->
                            if (room.id == myRoomId) {
                                isMyRoom = true
                                room.type = RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM.value
                            }
                        }

                        val ourUuid = sessionManager.userInfo?.comNextivaUseruuid
                        val isCurrentUserMember = room.members?.firstOrNull { it.userUuid == ourUuid } != null
                        if (isMyRoom || isCurrentUserMember) {
                            roomsDbManager.saveRooms(arrayListOf(room))
                                .subscribe(object : DisposableCompletableObserver() {
                                    override fun onComplete() {}
                                    override fun onError(e: Throwable) {
                                        FirebaseCrashlytics.getInstance().recordException(e)
                                    }
                                })
                        } else {
                            room.id?.let { id ->
                                roomsDbManager.deleteRoom(id)
                            }
                        }
                    }
                }
            }
            Enums.Platform.WebSocketMessageTypes.MESSAGE_ROOM_CREATE -> {
                GsonUtil.getObject(ConnectMyRoomResponse::class.java, message.payload)?.let { roomResponse ->
                    roomResponse.room?.let { room ->
                        roomsDbManager.saveRooms(arrayListOf(room))
                            .subscribe(object : DisposableCompletableObserver() {
                                override fun onComplete() {}
                                override fun onError(e: Throwable) {
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                }
                            })
                    }
                }
            }
            else -> {
                return
            }
        }
    }

    private fun handleVoiceSettingMessage(message: WebSocketConnectMessage) {
        GsonUtil.getObject(SelectiveCallRejection::class.java, message.payload)?.let { payload ->
            val numbersList = payload.currentSelectiveCallRejectionConditions
                ?.flatMap { it.numbers.orEmpty() }
                ?: emptyList()
            blockingNumberManager.refreshListFromWebSocket(numbersList)
        }
    }

    private fun handleVoiceCallMessage(message: WebSocketConnectMessage) {
        GsonUtil.getObject(WebSocketVoiceMessage::class.java, message.payload)?.let { voiceMessage ->
            voiceMessage.conferenceId?.let { conferenceId ->
                sipManager.getActiveCalls()?.firstOrNull { it.conferenceId == conferenceId }?.let { sipCall ->
                    val participants: ArrayList<ParticipantInfo> = ArrayList()

                    voiceMessage.participants?.forEach { participant ->
                        val contact = dbManager.getConnectContactFromPhoneNumberInThread(participant.phoneNumber).value

                        participants.add(
                            ParticipantInfo(
                                numberToCall = participant.phoneNumber ?: "",
                                displayName = contact?.uiName ?: participant.displayName ?: participant.phoneNumber?.let { num -> CallUtil.getFormattedNumber(num) } ?: "",
                                trackingId = participant.extTrackingId,
                                contactId = contact?.userId,
                                hasLeftNWay = participant.endTime != null
                            )
                        )
                    }

                    if (participants.any { !it.hasLeftNWay }) {
                        sipCall.participantInfoList = participants
                        sipManager.updateActivePassiveWithSipCall(sipCall)
                    } else {
                        sipApiManager.getActiveCalls()
                            .observeOn(schedulerProvider.ui())
                            .subscribe(object : DisposableSingleObserver<ArrayList<SipCallDetails>?>() {
                                override fun onSuccess(activeCalls: ArrayList<SipCallDetails>) {
                                    activeCalls.firstOrNull { activeCall ->
                                        sipCall.participantInfoList.firstOrNull { it.trackingId == activeCall.extTrackingId } != null
                                    }?.let { activeCall ->
                                        sipCall.isCallConference = false
                                        sipCall.conferenceId = null
                                        sipCall.trackingId = activeCall.extTrackingId.orEmpty()
                                        sipCall.participantInfoList.removeAll { it.trackingId != activeCall.extTrackingId }
                                        sipManager.updateActivePassiveWithSipCall(sipCall)
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                }
                            })
                    }
                }
            }
        }
    }

    private fun onMessageVoiceBulk(bulkAction: SmsMessageBulkAction) {
        if (bulkAction.status != SmsMessageBulkAction.STATUS_COMPLETE) return

        bulkAction.jobType?.let { jobType ->
            when (jobType) {
                SmsMessageBulkAction.JOB_TYPE_DELETE -> {
                    processJobChannel(
                        channel = bulkAction.channels?.VOICE,
                        processMessage = { messageId ->
                            dbManager.database.runInTransaction {
                                dbManager.deleteCallLogByCallLogId(messageId).subscribe()
                            }
                        })
                    processJobChannel(
                        channel = bulkAction.channels?.VOICEMAIL,
                        processMessage = { messageId ->
                            dbManager.database.runInTransaction {
                                dbManager.deleteVoicemail(messageId)
                            }
                        })
                }
                SmsMessageBulkAction.JOB_TYPE_UPDATE -> {
                    bulkAction.modifications?.readStatus?.let { readStatus ->
                        when (readStatus) {
                            Enums.SMSMessages.ReadStatus.READ -> {
                                processJobChannel(
                                    channel = bulkAction.channels?.VOICE,
                                    processMessage = { messageId ->
                                        dbManager.database.runInTransaction {
                                            dbManager.markCallLogEntryRead(messageId).subscribe()
                                        }
                                    })
                                processJobChannel(
                                    channel = bulkAction.channels?.VOICEMAIL,
                                    processMessage = { messageId ->
                                        dbManager.database.runInTransaction {
                                            dbManager.markVoicemailRead(messageId)
                                        }
                                    })
                            }
                            Enums.SMSMessages.ReadStatus.UNREAD -> {
                                processJobChannel(
                                    channel = bulkAction.channels?.VOICE,
                                    processMessage = { messageId ->
                                        dbManager.database.runInTransaction {
                                            dbManager.markCallLogEntryUnread(messageId).subscribe()
                                        }
                                    })
                                processJobChannel(
                                    channel = bulkAction.channels?.VOICEMAIL,
                                    processMessage = { messageId ->
                                        dbManager.database.runInTransaction {
                                            dbManager.markVoicemailUnread(messageId)
                                        }
                                    })
                            }
                            else -> {}
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun processJobChannel(channel: JobChannel?, processGroup: ((String) -> (Unit))? = null, processMessage: ((String) -> (Unit))? = null) {
        channel?.identifierType?.let { identifierType ->
            when (identifierType) {
                SmsMessageBulkAction.IDENTIFIER_TYPE_GROUP_ID -> {
                    channel.identifiers?.let { identifiers ->
                        processGroup?.let { processJob ->
                            identifiers.forEach { groupId ->
                                processJob(groupId)
                            }
                        }
                    }
                }
                SmsMessageBulkAction.IDENTIFIER_TYPE_MESSAGE_ID -> {
                    channel.identifiers?.let { identifiers ->
                        processMessage?.let { processJob ->
                            identifiers.forEach { messageId ->
                                processJob(messageId)
                            }
                        }
                    }
                }
                else -> {}
            }
            conversationRepository.fetchVoiceConversationMessages().subscribe()
        }
    }

    private fun onMessageConversation(message: WebSocketConnectMessage) {
        GsonUtil.getObject(VoiceMessage::class.java, message.payload)?.let { voiceMessage ->
            voiceMessage.eventType?.let { eventType ->
                when (eventType) {
                    Enums.Platform.WebSocketMessageEvents.MESSAGE_CREATED -> {
                        when (voiceMessage.channel) {
                            Enums.Platform.ConversationChannels.VOICE,
                            Enums.Platform.ConversationChannels.VOICEMAIL -> onVoiceMessageCreated(voiceMessage)

                            Enums.Platform.ConversationChannels.SMS -> onSmsMessageCreated(message)
                        }
                    }

                    Enums.Platform.WebSocketMessageEvents.MESSAGE_UPDATED -> {
                        when (message.type) {
                            Enums.Platform.WebSocketMessageTypes.MESSAGE -> onVoiceMessageUpdated(voiceMessage)

                            Enums.Platform.WebSocketMessageTypes.MESSAGE_STATUS -> {
                                if (TextUtils.equals(voiceMessage.messageState?.readStatus, Enums.SMSMessages.ReadStatus.READ)) {
                                    dbManager.updateReadStatusForMessageId(voiceMessage.messageId)
                                } else {
                                    onVoiceMessageStatusUpdated(voiceMessage)
                                }
                            }
                        }
                    }

                    Enums.Platform.WebSocketMessageEvents.MESSAGE_DELETED -> {
                        when (voiceMessage.channel) {
                            Enums.Platform.ConversationChannels.VOICE,
                            Enums.Platform.ConversationChannels.VOICEMAIL -> deleteVoiceMessage(voiceMessage, message.messageUuid)
                        }
                    }
                }
            }
        }
    }

    private fun onVoiceMessageCreated(voiceMessage: VoiceMessage) {
        dbManager.database.runInTransaction {
            if (voiceMessage.channel == Enums.Messages.Channels.VOICEMAIL) {
                dbManager.deleteCallLogByCallLogIdInThread(voiceMessage.messageId)
                dbManager.insertVoicemailsInThread(arrayListOf(voiceMessage.toDbVoicemail(formatterManager)))
                sessionManager.tempIncreaseChannelMessagesCount(Enums.Messages.Channels.VOICE, -1, application)

            } else {
                dbManager.deleteVoicemail(voiceMessage.messageId)
                dbManager.insertCallLogsInThread(arrayListOf(voiceMessage.toDbCallLogEntry(formatterManager)))
                sessionManager.tempIncreaseChannelMessagesCount(Enums.Messages.Channels.VOICEMAIL, -1, application)
            }
        }
    }

    private fun onSmsMessageCreated(message: WebSocketConnectMessage) {
        GsonUtil.getObject(Message::class.java, message.payload)?.let { smsMessage ->
            markMessageReadIfInConversation(smsMessage)

            val telephoneNumber = sessionManager.userDetails?.telephoneNumber?.let { CallUtil.getCountryCode() + it } ?: ""
            val data = Data(messages = listOf(smsMessage))

            dbManager.saveSmsMessages(listOf(data),
                telephoneNumber,
                Enums.SMSMessages.SentStatus.SUCCESSFUL, sessionManager.currentUser?.userUuid, allSavedTeams)
                .observeOn(schedulerProvider.io())
                .subscribe(object : DisposableCompletableObserver() {
                    override fun onComplete() { }
                    override fun onError(e: Throwable) { FirebaseCrashlytics.getInstance().recordException(e) }
                })
        }
    }

    private fun markMessageReadIfInConversation(message: Message) {
        (application as? NextivaApplication)?.let { application ->
            (application.currentActivity as? ConversationActivity)?.let { activity ->
                if (message.getConversationId() == activity.currentConversationId) {
                    message.messageState?.readStatus = Enums.SMSMessages.ReadStatus.READ
                    activity.updateReadStatus()
                }
            }
        }
    }

    private fun deleteVoiceMessage(voiceMessage: VoiceMessage, messageUuid: String?) {
        messageUuid?.let {
            dbManager.database.runInTransaction {
                if (voiceMessage.channel == Enums.Messages.Channels.VOICEMAIL) {
                    dbManager.deleteVoicemail(messageUuid)
                } else {
                    dbManager.deleteCallLogByCallLogIdInThread(messageUuid)
                }
            }
        }
    }

    private fun onVoiceMessageUpdated(voiceMessage: VoiceMessage) {
        dbManager.database.runInTransaction {
            if (voiceMessage.channel == Enums.Messages.Channels.VOICEMAIL) {
                dbManager.insertVoicemailsInThread(arrayListOf(voiceMessage.toDbVoicemail(formatterManager)))

                if (voiceMessage.callDetails?.voicemailRead == true) {
                    dbManager.markVoicemailRead(voiceMessage.callDetails?.voicemailId)
                    sessionManager.tempIncreaseChannelMessagesCount(Enums.Messages.Channels.VOICEMAIL, -1, application)

                } else {
                    dbManager.markVoicemailUnread(voiceMessage.callDetails?.voicemailId)
                    sessionManager.tempIncreaseChannelMessagesCount(Enums.Messages.Channels.VOICEMAIL, 1, application)
                }

            } else {
                // A MESSAGE_UPDATED comes through when a voicemail is deleted. Since a missed call will remain it comes through as MESSAGE_UPDATED.
                dbManager.deleteVoicemail(voiceMessage.messageId)
                dbManager.insertCallLogsInThread(arrayListOf(voiceMessage.toDbCallLogEntry(formatterManager)))
                sessionManager.tempIncreaseChannelMessagesCount(Enums.Messages.Channels.VOICEMAIL, -1, application)
            }
        }
    }

    private fun onVoiceMessageStatusUpdated(voiceMessage: VoiceMessage) {
        dbManager.database.runInTransaction {
            if (voiceMessage.channel == Enums.Messages.Channels.VOICEMAIL) {
                if (voiceMessage.callDetails?.voicemailRead == true) {
                    dbManager.markVoicemailRead(voiceMessage.callDetails?.voicemailId)
                    sessionManager.tempIncreaseChannelMessagesCount(Enums.Messages.Channels.VOICEMAIL, -1, application)

                } else {
                    dbManager.markVoicemailUnread(voiceMessage.callDetails?.voicemailId)
                    sessionManager.tempIncreaseChannelMessagesCount(Enums.Messages.Channels.VOICEMAIL, 1, application)
                }

            } else {
                dbManager.markCallLogEntryRead(voiceMessage.messageId)
                sessionManager.tempIncreaseChannelMessagesCount(Enums.Messages.Channels.VOICE, -1, application)
            }
        }
    }

    private fun updateConnectPresenceSession(message: WebSocketConnectMessage) {
        GsonUtil.getObject(WebSocketConnectPresencePayload::class.java, message.payload)?.let { presence ->
            dbManager.updatePresence(presence)

            if (presence.userId == sessionManager.userInfo?.comNextivaUseruuid) {
                sessionManager.setConnectUserPresence(
                    DbPresence(null, presence.userId, presence.presenceState, presence.customMessage, presence.statusExpiresAt, false),
                    presence.isSystemDerivedStatus
                )
            }
        }
    }

    private var lastBulkActionRefreshTimestamp: Long
        get() = sharedPreferencesManager.getLong(SharedPreferencesManager.LAST_SMS_BULK_ACTION_REFRESHED_TIMESTAMP, 0L)
        set(timestamp) { sharedPreferencesManager.setLong(SharedPreferencesManager.LAST_SMS_BULK_ACTION_REFRESHED_TIMESTAMP, timestamp) }
}
