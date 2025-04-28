package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Application
import android.util.Log
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.mediators.SmsConversationRemoteMediator
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.mediators.SmsMessageRemoteMediator
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.net.platform.BulkUpdateUserMessageState
import com.nextiva.nextivaapp.android.models.net.platform.GenerateGroupIdPostBody
import com.nextiva.nextivaapp.android.models.net.platform.MessageState
import com.nextiva.nextivaapp.android.models.net.platform.MessageStatePutBody
import com.nextiva.nextivaapp.android.models.net.platform.SendMessagePostBody
import com.nextiva.nextivaapp.android.models.net.platform.SendMessageResponse
import com.nextiva.nextivaapp.android.models.net.platform.SmsMessages
import com.nextiva.nextivaapp.android.models.net.platform.UserMessageState
import com.nextiva.nextivaapp.android.models.net.platform.messages.JobChannel
import com.nextiva.nextivaapp.android.models.net.platform.messages.SmsMessageBulkAction
import com.nextiva.nextivaapp.android.models.net.platform.messages.SmsMessageBulkActionList
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import io.reactivex.Single
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import okhttp3.MultipartBody
import org.threeten.bp.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SmsApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var netManager: NetManager,
    var schedulerProvider: SchedulerProvider,
    var sessionManager: SessionManager,
    var dbManager: DbManager
) : BaseApiManager(application, logManager), SmsManagementRepository {

    var isFetchingMessages = false
    private val smsServerCatchUpDelaySeconds: Long = 3

    override fun getSmsConversations(): Single<Boolean> {
        return getSmsConversations(1)
    }

    override fun getSmsConversationsForMediator(pageNumber: Int): Single<SmsMessages?> {
        if (isFetchingMessages) {
            return Single.never()
        }

        isFetchingMessages = true

        return netManager.getMessagesApi().getSmsConversations(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            SmsMessageRemoteMediator.PAGE_SIZE,
            SmsMessageRemoteMediator.PAGE_SIZE,
            pageNumber
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                isFetchingMessages = false
                response.body()
            }
            .onErrorReturn { throwable ->
                isFetchingMessages = false
                logServerResponseError(throwable)
                SmsMessages()
            }
    }

    override fun getSmsConversations(pageToFetch: Int): Single<Boolean> {
        if (isFetchingMessages) {
            return Single.never()
        }

        isFetchingMessages = true

        return netManager.getMessagesApi().getSmsConversations(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            SmsMessageRemoteMediator.PAGE_SIZE,
            SmsMessageRemoteMediator.PAGE_SIZE,
            pageToFetch
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
            .map { smsMessages ->
                if (smsMessages.data != null) {
                    smsMessages.data!!.forEach { data ->
                        val lastMessage = data.messages?.lastOrNull()
                        if (lastMessage?.messageState?.isRead() == true) {
                            dbManager.updateReadStatusForConversationId(lastMessage.getConversationId())
                        }
                    }

                    dbManager.saveSmsMessages(
                        smsMessages.data,
                        CallUtil.getCountryCode() + sessionManager.userDetails?.telephoneNumber,
                        Enums.SMSMessages.SentStatus.SUCCESSFUL,
                        sessionManager.currentUser?.userUuid,
                        sessionManager.allTeams
                    )
                        .subscribe(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                isFetchingMessages = false
                            }

                            override fun onError(e: Throwable) {
                                isFetchingMessages = false
                            }
                        })

                    true
                } else {
                    isFetchingMessages = false
                    false
                }
            }
            .onErrorReturnItem(false)
    }

    override fun getSmsConversation(groupId: String, conversationId: String, shouldMarkMessagesRead: Boolean): Single<Int> {
        return getSmsConversation(1, groupId, conversationId, shouldMarkMessagesRead)
    }

    override fun getSmsConversationForMediator(pageNumber: Int, groupId: String?): Single<SmsMessages?> {
        return netManager.getMessagesApi().getSmsConversation(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            "groupId:$groupId,channel:SMS",
            SmsConversationRemoteMediator.PAGE_SIZE,
            pageNumber
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
                SmsMessages()
            }
    }

    override fun getSmsConversation(pageNumber: Int, groupId: String, conversationId: String, shouldMarkMessagesRead: Boolean): Single<Int> {
        var totalCount = Int.MAX_VALUE

        return netManager.getMessagesApi().getSmsConversation(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            "groupId:$groupId,channel:SMS",
            SmsConversationRemoteMediator.PAGE_SIZE,
            pageNumber
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    totalCount = response.body()?.totalCount ?: Int.MAX_VALUE
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.body()
            }
            .map { smsMessages ->
                if (smsMessages.data != null) {
                    sessionManager.userDetails?.telephoneNumber?.let { telephoneNumber ->
                        dbManager.saveSmsMessages(
                            smsMessages.data,
                            CallUtil.getCountryCode() + telephoneNumber,
                            Enums.SMSMessages.SentStatus.SUCCESSFUL,
                            sessionManager.currentUser?.userUuid,
                            sessionManager.allTeams
                        )
                            .subscribe(object : DisposableCompletableObserver() {
                                override fun onComplete() {
                                    if (shouldMarkMessagesRead) {
                                        val messageStateList: ArrayList<MessageState?> = ArrayList()
                                        smsMessages.data!!.forEach { data ->
                                            data.messages?.forEach { message ->
                                                messageStateList.add(message.messageState)
                                            }
                                        }
                                        updateMessagesAsRead(messageStateList, conversationId)
                                    }
                                }

                                override fun onError(e: Throwable) {}
                            })
                    }

                    totalCount
                } else {
                    totalCount
                }
            }
            .onErrorReturnItem(totalCount)
    }

    override fun updateMessagesAsRead(messageStateList: List<MessageState?>, conversationId: String) {
        val messageIds: List<String> = messageStateList.map { it?.messageId ?: "" }
        val userMessageState = UserMessageState(true)
        if (messageIds.isNotEmpty()) {
            bulkUpdateMessageReadStatus(BulkUpdateUserMessageState(messageIds, userMessageState))
                .subscribe(object : DisposableSingleObserver<Boolean>() {
                    override fun onSuccess(success: Boolean) {
                        if (success) {
                            dbManager.updateReadStatusForConversationId(conversationId)
                        }
                    }

                    override fun onError(e: Throwable) {
                        LogUtil.e("Failed updating user message read status. ${e.localizedMessage}")
                    }
                })
        }
    }

    override fun getSmsMessageWithMessageId(messageId: String): Single<SmsMessages> {
        return netManager.getMessagesApi().getMessageFromMessageId(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            "messageId:$messageId"
        )
            .delay(smsServerCatchUpDelaySeconds, TimeUnit.SECONDS)
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.body() ?: SmsMessages()
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                SmsMessages()
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun sendSmsMessage(smsPostBody: SendMessagePostBody): Single<SendMessageResponse> {
        return netManager.getMessagesApi().sendSmsMessages(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            smsPostBody
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.body() ?: SendMessageResponse(null, response.body()?.messageId, response.body()?.groupId, response.code())
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                SendMessageResponse()
            }
            .observeOn(schedulerProvider.ui())
    }

    override suspend fun generateGroupId(body: GenerateGroupIdPostBody): String? {
        try {
            Log.d("SmsApiManager", "[generateGroupId]: $body")

            val response = netManager.getMessagesApi().generateGroupId(
                sessionId = sessionManager.sessionId,
                corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                body = body
            )

            if (response.isSuccessful) {
                logServerSuccess(response)
                return response.body()?.groupId
            } else {
                logServerParseFailure(response)
            }
        } catch (e: Exception) {
            logServerResponseError(Throwable(e.message.orEmpty()))
        }
        return null
    }

    override fun checkApiHealthByMessageId(messageId: String): Single<Boolean> {
        return netManager.getMessagesApi().getMessageStateFromMessageId(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(), arrayOf(messageId)
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
            .observeOn(schedulerProvider.ui())
    }

    override fun checkV2ApiHealthByMessageId(messageId: String): Single<Boolean> {
        return netManager.getMessagesApi().getMessageFromMessageIdV2(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(), messageId
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
            .observeOn(schedulerProvider.ui())
    }

    override fun updateMessageReadStatus(messageStatePostBody: MessageStatePutBody, messageId: String): Single<Boolean> {
        val userMessageState = UserMessageState()
        userMessageState.read = true

        return netManager.getMessagesApi().updateReadStatus(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            messageStatePostBody,
            messageId
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
                LogUtil.d("updateMessageReadStatus Error throwable: " + GsonUtil.getJSON(throwable))
                false
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun updateUserMessageReadStatus(userMessageState: UserMessageState, messageId: String): Single<Boolean> {
        return netManager.getMessagesApi().updateUserMessageState(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            userMessageState,
            messageId
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
                LogUtil.d("updateMessageReadStatus Error throwable: " + GsonUtil.getJSON(throwable))
                false
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun bulkUpdateMessageReadStatus(bulkUpdateUserMessageState: BulkUpdateUserMessageState): Single<Boolean> {
        return netManager.getMessagesApi().updateUserMessageState(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            bulkUpdateUserMessageState
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
                LogUtil.d("bulkUpdateMessageReadStatus Error throwable: " + GsonUtil.getJSON(throwable))
                false
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun checkForDeletedItems(createdAfter: Instant, page: Int): Single<SmsMessageBulkActionList?> {
        val dateFormatter = FormatterManager.getInstance().dateFormatter_8601ExtendedDatetimeTimeZoneThreeMs
        return netManager.getMessagesApi().checkForDeletedItems(
            sessionId = sessionManager.sessionId,
            corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            corpAcctNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            userId = sessionManager.userInfo?.comNextivaUseruuid.toString(),
            createdAfter = dateFormatter.format(createdAfter),
            page = page
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                    response.body()
                } else {
                    logServerParseFailure(response)
                    null
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun onMessageConversationBulk(bulkAction: SmsMessageBulkAction) {
        if (bulkAction.status != SmsMessageBulkAction.STATUS_COMPLETE) {
            return
        }
        bulkAction.jobType?.let { jobType ->
            when (jobType) {
                SmsMessageBulkAction.JOB_TYPE_DELETE -> {
                    processJobChannel(
                        channel = bulkAction.channels?.SMS,
                        processGroup = { groupId ->
                            dbManager.database.runInTransaction {
                                dbManager.deleteMessagesByGroupId(groupId)
                            }
                        })
                }
                SmsMessageBulkAction.JOB_TYPE_UPDATE -> {
                    bulkAction.modifications?.readStatus?.let { readStatus ->
                        when (readStatus) {
                            Enums.SMSMessages.ReadStatus.READ -> {
                                processJobChannel(
                                    channel = bulkAction.channels?.SMS,
                                    processGroup = { groupId ->
                                        dbManager.database.runInTransaction {
                                            dbManager.updateReadStatusForGroupId(groupId)
                                        }
                                    },
                                    processMessage = { messageId ->
                                        dbManager.database.runInTransaction {
                                            dbManager.updateReadStatusForMessageId(messageId)
                                        }
                                    })
                            }
                            Enums.SMSMessages.ReadStatus.UNREAD -> {
                                processJobChannel(
                                    channel = bulkAction.channels?.SMS,
                                    processGroup = { groupId ->
                                        dbManager.database.runInTransaction {
                                            dbManager.updateUnreadStatusForGroupId(groupId)
                                        }
                                    },
                                    processMessage = { messageId ->
                                        dbManager.database.runInTransaction {
                                            dbManager.updateUnreadStatusForMessageId(messageId)
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
        }
    }

    override fun sendMmsMessage(
        mmsPostBody: MultipartBody.Part,
        destination: String,
        message: String,
        source: String?,
        clientId: String,
        teams: List<SmsTeam>?
    ): Single<SendMessageResponse> {
        val ourTeams: ArrayList<SmsTeam> = ArrayList()
        sessionManager.usersTeams.forEach { team ->
            if (teams?.any { it.teamId == team.teamId } == true) {
                ourTeams.add(team)
            }
        }

        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("destination", destination)
        builder.addFormDataPart("message", message)
        builder.addFormDataPart("clientId", clientId)
        builder.addPart(mmsPostBody)

        if (!source.isNullOrEmpty()) {
            builder.addFormDataPart("source", source)
        }

        ourTeams.firstOrNull()?.teamId?.let { teamId ->
            if (teamId.isNotEmpty()) {
                builder.addFormDataPart("teamId", teamId)
            }
        }

        return netManager.getMessagesApi().sendMms(
            builder.build(),
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.body() ?: SendMessageResponse(null, response.body()?.messageId, response.body()?.groupId, response.code())
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                SendMessageResponse()
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun getUsersTeams(): Single<RxEvents.BaseResponseEvent> {
        return netManager.getMessagesApi().getTeams(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            withVoice = true,
            withMembers = true,
            userId = sessionManager.userInfo?.comNextivaUseruuid.toString()
        )
            .flatMap teamsFlatMap@{ response ->
                val phoneNumbers: ArrayList<String> = ArrayList()

                response.body()?.data?.let { data ->
                    data.forEach { team ->
                        team.phoneNumbers?.forEach { phoneNumber ->
                            if (phoneNumber.isPrimary == true) {
                                phoneNumber.number?.let { number ->
                                    phoneNumbers.add(number)
                                }
                            }
                        }
                    }

                    if (phoneNumbers.isNotEmpty()) {
                        return@teamsFlatMap netManager.getMessagesApi().getTeamPhoneNumberInformation(
                            sessionManager.sessionId,
                            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                            true,
                            phoneNumbers)
                            .map {
                                response.body()?.data?.forEach { team ->
                                    val phoneNumber = team.phoneNumbers?.firstOrNull { it.isPrimary == true }?.number ?: team.phoneNumbers?.firstOrNull()?.number

                                    it.body()?.firstOrNull { it.phoneNumber == phoneNumber }?.let { phoneInformation ->
                                        team.smsEnabled = phoneInformation.metadata?.smsEnabled ?: false
                                    }
                                }

                                response
                            }
                    }
                }

                Single.just(response)

            }
            .map { response ->
                if (response.isSuccessful && response.body() != null) {
                    val ourTeams: ArrayList<SmsTeam> = ArrayList()
                    val allTeams: ArrayList<SmsTeam> = ArrayList()
                    val ourUuid = sessionManager.userInfo?.comNextivaUseruuid

                    response.body()?.data?.map { teamResponse ->
                        var phoneNumber = teamResponse.phoneNumbers?.firstOrNull { it.isPrimary == true }?.number ?: teamResponse.phoneNumbers?.firstOrNull()?.number

                        if (phoneNumber?.length == 10) {
                            phoneNumber = CallUtil.getCountryCode() + phoneNumber
                        }

                        SmsTeam(null, teamResponse.id, teamResponse.name, phoneNumber, teamResponse.legacyId, teamResponse.members, teamResponse.smsEnabled)
                    }?.forEach { team ->
                        if (team.members?.firstOrNull { it.id == ourUuid } != null) {
                            ourTeams.add(team)
                        }

                        allTeams.add(team)
                    }

                    sessionManager.setUsersTeams(ourTeams)
                    sessionManager.setAllTeams(allTeams)

                    logServerSuccess(response)
                    RxEvents.BaseResponseEvent(true)
                } else {
                    logServerParseFailure(response)
                    RxEvents.BaseResponseEvent(false)
                }
            }
            .onErrorReturn {
                RxEvents.BaseResponseEvent(false)
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun testAttachmentApi(): Single<Boolean> {
        val domain = "https://${sessionManager.accountInformation.domainName}/"
        return netManager.getAttachmentApi(domain).testAttachmentApi(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            arrayListOf(
                Enums.Attachment.EntityType.CHAT_UPLOAD,
                Enums.Attachment.EntityType.LINK_MESSAGE,
                Enums.Attachment.EntityType.MMS_MESSAGE,
                Enums.Attachment.EntityType.SMS_MESSAGE
            )
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
            .observeOn(schedulerProvider.ui())
    }

    override fun getPageSize(): Int {
        return SmsMessageRemoteMediator.PAGE_SIZE
    }

    override fun setIsFetchingMessages(isFetching: Boolean) {
        isFetchingMessages = isFetching
    }

    override fun getNextConversationListPage(): Int {
        val currentItemCount = dbManager.currentConversationListCount
        return (currentItemCount / SmsMessageRemoteMediator.PAGE_SIZE) + 1
    }

    override fun getNextConversationPage(groupId: String): Int {
        val currentItemCount = dbManager.getCurrentConversationCount(groupId)
        return (currentItemCount / SmsConversationRemoteMediator.PAGE_SIZE) + 1
    }
}
