package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Application
import android.content.Context
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.analytics.events.CallLogEvent
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbCallLogEntry
import com.nextiva.nextivaapp.android.db.model.DbVoicemail
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.interfaces.DatadogManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.net.platform.voice.VoiceMessageState
import com.nextiva.nextivaapp.android.models.net.platform.voice.VoiceMessagesResponse
import com.nextiva.nextivaapp.android.models.net.platform.voice.VoiceMessagesReturn
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailPatchData
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.LogUtil
import io.reactivex.Single
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ConversationApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var netManager: NetManager,
    var schedulerProvider: SchedulerProvider,
    var sessionManager: SessionManager,
    var dbManager: DbManager,
    var sharedPreferencesManager: SharedPreferencesManager,
    val dataDogManager: DatadogManager
) : BaseApiManager(application, logManager), ConversationRepository {

    private val formatterManager = FormatterManager.getInstance()
    private var isRefreshing = false

    override fun fetchVoiceConversationMessages(): Single<VoiceMessagesReturn> {
        return fetchVoiceConversationMessages(1)
    }

    override fun fetchVoiceConversationMessages(pageToFetch: Int): Single<VoiceMessagesReturn> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Will fetch Voice Conversation messages. Page: [$pageToFetch]")

        if (isRefreshing) {
            logManager.logToFile(Enums.Logging.STATE_INFO, "Already fetching Voice Conversation messages. Returning.")

            return Single.never()
        }

        isRefreshing = true

        return netManager.getConversationApi().getAllVoiceConversationMessages(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            pageToFetch,
            getPageSize()
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.body()
            }
            .onErrorReturn { throwable ->
                isRefreshing = false
                logServerResponseError(throwable)
                null
            }
            .map { voiceMessagesResponse ->
                if (voiceMessagesResponse.data != null) {
                    val callLogsList: ArrayList<DbCallLogEntry> = ArrayList()
                    val voicemailsList: ArrayList<DbVoicemail> = ArrayList()

                    voiceMessagesResponse.data!!.forEach { data ->
                        data.voiceMessageItems?.let { voiceMessages ->
                            val splitList = voiceMessages.partition { it.channel == Enums.Messages.Channels.VOICEMAIL }
                            splitList.first.map { it.toDbVoicemail(formatterManager, pageToFetch) }.let { voicemailsList.addAll(it) }
                            splitList.second.map { it.toDbCallLogEntry(formatterManager, pageToFetch) }.let { callLogsList.addAll(it) }
                        }
                    }

                    dbManager.insertCallLogs(callLogsList)
                        .andThen(dbManager.insertVoicemails(voicemailsList))
                        .subscribe(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                isRefreshing = false
                            }

                            override fun onError(e: Throwable) {
                                LogUtil.e("Error saving Conversation messages to database ${e.localizedMessage}")
                                isRefreshing = false
                            }
                        })

                    VoiceMessagesReturn(voiceMessagesResponse.totalCount, pageToFetch)

                } else {
                    isRefreshing = false
                    VoiceMessagesReturn()
                }
            }
            .onErrorReturnItem(VoiceMessagesReturn())
    }

    override fun fetchVoiceConversationMessageForMediator(pageToFetch: Int): Single<VoiceMessagesResponse?> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Will fetch Voice Conversation Message For Mediator. Page [$pageToFetch]")

        return netManager.getConversationApi().getAllVoiceConversationMessages(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            pageToFetch,
            getPageSize()
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.body()
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
    }

    override fun deleteVoicemail(voicemailId: String): Single<Boolean> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Will delete voicemail. Voicemail id: [$voicemailId]")

        return netManager.getConversationApi().deleteMessage(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            sessionManager.userInfo?.comNextivaUseruuid,
            voicemailId
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                    dbManager.deleteVoicemail(voicemailId)
                    true
                } else {
                    logServerParseFailure(response)
                    false
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
    }

    override fun markVoicemailUnread(voicemailId: String): Single<Boolean> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Will mark voicemail unread. Voicemail Id: [$voicemailId]")
        return patchVoicemailReadValue(voicemailId, false)
    }

    override fun markVoicemailRead(voicemailId: String): Single<Boolean> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Will mark voicemail read. Voicemail Id: [$voicemailId]")
        return patchVoicemailReadValue(voicemailId, true)
    }

    private fun patchVoicemailReadValue(voicemailId: String, value: Boolean): Single<Boolean> {
        return netManager.getVoicemailApi().patchVoicemail(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            voicemailId,
            arrayListOf(VoicemailPatchData("replace", "/read", value))
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                    dbManager.patchConversationVoicemailRead(voicemailId, value)
                    true
                } else {
                    logServerParseFailure(response)
                    false
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
    }

    override fun markCallUnread(messageId: String): Single<Boolean> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Will mark call unread. Message Id: [$messageId]")
        return patchMessageState(messageId, "UNREAD")
    }

    override fun markCallRead(messageId: String): Single<Boolean> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Will mark call read. Message Id: [$messageId]")
        return patchMessageState(messageId, "READ")
    }

    private fun patchMessageState(messageId: String, readStatus: String): Single<Boolean> {
        return netManager.getConversationApi().patchCallMessageState(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            arrayListOf(messageId),
            VoiceMessageState(messageId, readStatus, false)
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                    true
                } else {
                    logServerParseFailure(response)
                    false
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
    }

    override fun getChannelMessagesCount(channel: String, readStatus: String): Single<Int> {
        val sessionId = sessionManager.sessionId
        val userId = sessionManager.userInfo?.comNextivaUseruuid
        val corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()

        if (sessionId.isNullOrEmpty() || corpAccountNumber.isEmpty() || userId.isNullOrEmpty()) {
            return Single.just(0)
        }

        return netManager.getConversationApi().getMessagesCount(
            sessionId,
            corpAccountNumber,
            corpAccountNumber,
            userId,
            channel,
            readStatus
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                    response.body() ?: 0
                } else {
                    logManager.logToFile(Enums.Logging.STATE_ERROR, response.body().toString())
                    0
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                0
            }
    }

    override fun deleteMessagesCountCache(channel: String): Single<Void> {
        val corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        val userId = sessionManager.userInfo?.comNextivaUseruuid
        return netManager.getConversationApi().deleteMessagesCountCache(
            sessionManager.sessionId,
            corpAccountNumber,
            corpAccountNumber,
            userId,
            channel
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                    response.body() ?: throw Exception("No response body")
                } else {
                    logServerParseFailure(response)
                    throw Exception("Failed to delete messages count cache")
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                throw Exception("Error deleting messages count cache")
            }
    }

    override fun deleteSmsMessages() {
        val corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        val userId = sessionManager.userInfo?.comNextivaUseruuid

        netManager.getConversationApi().deleteSmsMessages(
            sessionManager.sessionId,
            corpAccountNumber,
            corpAccountNumber,
            userId,
            Enums.Messages.Channels.SMS
        )
            .subscribeOn(schedulerProvider.io())
            .subscribe(object : DisposableSingleObserver<Response<Void>>() {
                override fun onSuccess(t: Response<Void>) {
                    dbManager.deleteAllSmsMessages()
                }

                override fun onError(e: Throwable) {}
            })
    }

    override fun bulkDeleteConversations(bulkDeleteData: BulkActionsConversationData, deleteAudioFiles: (context: Context, messageIds: ArrayList<String>) -> Unit): Single<Boolean> {
        val corpAccount = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        val userId = sessionManager.userInfo?.comNextivaUseruuid.toString()
        return netManager.getConversationApi().bulkDeleteConversations(
            sessionManager.sessionId,
            corpAccountNumber = corpAccount,
            corpAccount = corpAccount,
            patchData = bulkDeleteData,
            userId = userId
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    bulkDeleteData.channels?.VOICE?.identifiers?.let {
                        dbManager.bulkDeleteCallLogs(it)
                    }
                    bulkDeleteData.channels?.VOICEMAIL?.identifiers?.let {
                        dbManager.bulkDeleteVoicemails(it)
                        if (it.isNotEmpty()) {
                            deleteAudioFiles(application, it)
                        }
                    }
                    bulkDeleteData.channels?.SMS?.identifiers?.let { conversations ->
                        conversations.forEach { groupId ->
                            dbManager.deleteMessagesFromConversationByGroupId(groupId)
                        }
                    }
                    logServerSuccess(response)
                    true
                } else {
                    logServerParseFailure(response)
                    false
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
    }

    override fun deleteMessage(bulkDeleteData: BulkActionsConversationData): Single<Boolean> {
        val corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        val userId = sessionManager.userInfo?.comNextivaUseruuid.toString()
        return netManager.getConversationApi().deleteMessage(
            sessionManager.sessionId,
                                corpAccountNumber = corpAccountNumber,
                                corpAcctNumber = corpAccountNumber,
                                request = bulkDeleteData,
                                userId = userId)
                                .subscribeOn(schedulerProvider.io())
                                .map { response ->
                                    if (response.isSuccessful) {
                                        bulkDeleteData.channels?.SMS?.identifiers?.let { messages ->
                                            messages.forEach { messageId ->
                                                dbManager.deleteMessageFromMessageId(messageId)
                                                dbManager.deleteSmsMessageByMessageId(messageId)
                                            }
                                        }
                                        logServerSuccess(response)
                                        return@map true
                                    } else {
                                        logServerParseFailure(response)
                                    }
                                    false
                                }
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    false
                }
    }

    override fun bulkUpdateConversations(bulkDeleteData: BulkActionsConversationData): Single<Boolean> {
        val corpAccount = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        val userId = sessionManager.userInfo?.comNextivaUseruuid.toString()
        return netManager.getConversationApi().bulkUpdateConversations(
            sessionManager.sessionId,
            corpAccountNumber = corpAccount,
            corpAccount = corpAccount,
            patchData = bulkDeleteData,
            userId = userId
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                    true
                } else {
                    logServerParseFailure(response)
                    false
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
    }

    override fun getPageSize(): Int {
        return if (sessionManager.isVoiceLargePageEnabled) 50 else 25
    }

    override fun performDataDogCustomAction(callLogsList: ArrayList<DbCallLogEntry>, voicemailsList: ArrayList<DbVoicemail>) {
        if (sharedPreferencesManager.getBoolean(SharedPreferencesManager.ENABLE_CALL_LOG_DATADOG_EVENT, false)) {
            sharedPreferencesManager.setBoolean(SharedPreferencesManager.ENABLE_CALL_LOG_DATADOG_EVENT, false)
            if (callLogsList.isNotEmpty()) {
                if (dbManager.getCallLogByLogId(callLogsList.last().callLogId) == null) {
                    dataDogManager.performCustomAction(
                        CallLogEvent().gapDetected().name,
                        emptyMap<String, Any>()
                    )
                    return
                }
            }

            if (voicemailsList.isNotEmpty()) {
                if (dbManager.getVoicemailById(voicemailsList.last().messageId) == null) {
                    dataDogManager.performCustomAction(
                        CallLogEvent().gapDetected().name,
                        emptyMap<String, Any>()
                    )
                }
            }
        }
    }
}
