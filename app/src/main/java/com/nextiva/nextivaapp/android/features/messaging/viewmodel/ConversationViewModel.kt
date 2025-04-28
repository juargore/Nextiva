package com.nextiva.nextivaapp.android.features.messaging.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BottomSheetMenuListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.mediators.SmsConversationRemoteMediator
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Notification.TypeIDs.NEW_SMS_NOTIFICATION_ID
import com.nextiva.nextivaapp.android.constants.Enums.SMSMessages.FooterMessageType
import com.nextiva.nextivaapp.android.constants.Enums.SMSMessages.SMSCampaignStatus
import com.nextiva.nextivaapp.android.core.analytics.events.MessagingEvent
import com.nextiva.nextivaapp.android.core.common.ui.PendingMessageData
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbMessageState
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.DatadogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager.IS_USER_SUPER_ADMIN
import com.nextiva.nextivaapp.android.models.CurrentUser
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.models.net.platform.Attachment
import com.nextiva.nextivaapp.android.models.net.platform.BulkUpdateUserMessageState
import com.nextiva.nextivaapp.android.models.net.platform.Data
import com.nextiva.nextivaapp.android.models.net.platform.GenerateGroupIdPostBody
import com.nextiva.nextivaapp.android.models.net.platform.Message
import com.nextiva.nextivaapp.android.models.net.platform.MessageState
import com.nextiva.nextivaapp.android.models.net.platform.Participant
import com.nextiva.nextivaapp.android.models.net.platform.SendMessagePostBody
import com.nextiva.nextivaapp.android.models.net.platform.SendMessageResponse
import com.nextiva.nextivaapp.android.models.net.platform.SmsMessages
import com.nextiva.nextivaapp.android.models.net.platform.UserMessageState
import com.nextiva.nextivaapp.android.models.net.platform.messages.SmsTeamPayload
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.Channels
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.SMS
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.Event
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.MessageUtil
import com.nextiva.nextivaapp.android.util.extensions.isNull
import com.nextiva.nextivaapp.android.util.extensions.notNull
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.util.extensions.orTrue
import com.nextiva.nextivaapp.android.util.extensions.orZero
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class ConversationViewModel @Inject constructor(
    application: Application,
    private val schedulerProvider: SchedulerProvider,
    private val dbManager: DbManager,
    private val smsManagementRepository: SmsManagementRepository,
    private val calendarManager: CalendarManager,
    private val sessionManager: SessionManager,
    private val connectionStateManager: ConnectionStateManager,
    private val analyticsManager: AnalyticsManager,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val notificationManager: NotificationManager,
    private val dataDogManager: DatadogManager,
    private val conversationRepository: ConversationRepository
) : BaseViewModel(application) {

    val formatterManager: FormatterManager = FormatterManager.getInstance()

    var isCallOptionsDisabled: Boolean? = null
    var chatType: String? = null
    var membersUiNames: ArrayList<String> = ArrayList()
    var conversationDetails: SmsConversationDetails? = null
    var isNewChat: Boolean = false
    var membersUiNamesWithParticipant: MutableMap<String, String> = mutableMapOf()
    var currentUser: CurrentUser? = null
    var allSavedTeams: List<SmsTeam>? = null
    var ourTeams: List<SmsTeam>? = null
    var userNumber: String? = null
    var draftMessage: SmsMessage? = null
    var isSmsEnabled: Boolean = false
    var isSmsLicenseEnabled: Boolean = false
    var isTeamSmsLicenseEnabled: Boolean = false
    var comesFromNewMessage: Boolean = false
    var isUserAdmin: Boolean = false
    var tempMessageIdSent: String = ""
    private var actualMessageIdSent = ""
    private var smsCampaignStatus: String = ""
    private var isSMSCampaignFeatureEnabled: Boolean = false
    private var inProgressMmsMessages: ArrayList<String> = ArrayList()
    private var contactForContactAction: NextivaContact? = null
    private var alreadySentPendingMessage: Boolean = false

    private val _groupId: MutableStateFlow<String?> = MutableStateFlow(null)
    val groupId: StateFlow<String?> = _groupId

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    companion object {
        const val FIVE_MIN = 300
    }

    val participantsList: List<SmsParticipant>
        get() {
            return conversationDetails?.getParticipantsList() ?: ArrayList()
        }

    val isTeamConversation: Boolean
        get() {
            return conversationDetails?.getAllTeams()?.isNotEmpty() == true
        }

    var sendingPhoneNumber: MutableLiveData<SendingPhoneNumber> = MutableLiveData()
    var uiNameTextChangedLiveData: MutableLiveData<NextivaContact?> = MutableLiveData()
    var draftMessageLiveData: MutableLiveData<SmsMessage?> = MutableLiveData()
    var showNoInternetLiveData: MutableLiveData<Void?> = MutableLiveData()
    var showProgressDialog: MutableLiveData<Void?> = MutableLiveData()
    var dismissProgressBarDialog: MutableLiveData<Void?> = MutableLiveData()
    var processedCallInfoMutableLiveData = MutableLiveData<ParticipantInfo>()
    var maxRateExceededLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var pendingMessage: MutableLiveData<Event<PendingMessageData>> = MutableLiveData()
    var messageDeleteResultLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var smsMessageListItemDeleted: MutableLiveData<SmsMessageListItem> = MutableLiveData(null)
    var userRepoApiSmsStartedLiveData: MutableLiveData<Int> = MutableLiveData()
    private val conversationDetailsLiveData = MutableLiveData<SmsConversationDetails?>()

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    val smsListItemsLiveData: Flow<PagingData<BaseListItem>> = _groupId.flatMapLatest { _group_id ->
            Pager(
                PagingConfig(
                    pageSize = SmsConversationRemoteMediator.PAGE_SIZE,
                    prefetchDistance = 25,
                    initialLoadSize = SmsConversationRemoteMediator.PAGE_SIZE * 2
                ),
                pagingSourceFactory = {
                    dbManager.getSmsConversationPagingSource(_group_id)
                },
                remoteMediator = SmsConversationRemoteMediator(
                    smsManagementRepository,
                    dbManager,
                    sessionManager,
                    conversationDetails?.groupId,
                    conversationDetails?.getConversationId()
                )
            ).flow.map { pagingData ->
                pagingData.filter { model ->
                    if (model.sentStatus == Enums.SMSMessages.SentStatus.DRAFT) {
                        draftMessage = draftMessage ?: model
                        draftMessageLiveData.value = model
                    }
                    model.sentStatus != Enums.SMSMessages.SentStatus.DRAFT
                }
            }
            .map { filteredData ->
                filteredData.map { model ->

                    val timeStamp = model.sent?.toEpochMilli() ?: 0L

                    var presence: DbPresence? = null
                    userNumber?.let {
                        model.getUserIdForPresence()?.let { userId ->
                            presence = dbManager.getPresenceFromContactTypeIdInThread(userId)
                        }
                    }

                    model.messageId?.let { messageTag ->
                        notificationManager.cancelNotificationByTag(messageTag, NEW_SMS_NOTIFICATION_ID)
                    }

                    determineIfMessageIsFromSender(model)

                    SmsMessageListItem(
                        model,
                        Enums.Chats.MessageBubbleTypes.END,
                        formatterManager.format_humanReadableSmsTimeStamp(
                            application,
                            calendarManager,
                            Instant.ofEpochMilli(timeStamp)
                        ),
                        showTimeSeparator = false,
                        showHumanReadableTime = false,
                        formatterManager.format_humanReadableSmsDate(
                            application,
                            calendarManager,
                            Instant.ofEpochMilli(timeStamp)
                        ),
                        formatterManager.format_humanReadableSmsTime(
                            application,
                            Instant.ofEpochMilli(timeStamp)
                        ).lowercase(Locale.getDefault()),
                        presence
                    ) as BaseListItem

                }.insertSeparators { before: BaseListItem?, after: BaseListItem? ->
                    val now = Instant.now()
                    val beforeListItem = before as? SmsMessageListItem
                    val afterListItem = after as? SmsMessageListItem
                    val beforeTimeStamp = beforeListItem?.data?.sent ?: now
                    val afterTimeStamp = afterListItem?.data?.sent ?: now
                    val willInsertSeparator = after == null || !isSameDay(afterTimeStamp, beforeTimeStamp) && (beforeTimeStamp != now && afterTimeStamp != now)

                    if (willInsertSeparator) {
                        beforeListItem?.showHumanReadableTime = true
                        return@insertSeparators MessageHeaderListItem(
                            formatterManager.format_humanReadableSmsTimeStamp(
                                application,
                                calendarManager,
                                beforeTimeStamp
                            ),
                            formatterManager.format_humanReadableSmsDate(
                                application,
                                calendarManager,
                                beforeTimeStamp
                            )
                        )
                    }

                    null
                }
            }
            .cachedIn(viewModelScope)
    }.flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)


    private fun determineIfMessageIsFromSender(model: SmsMessage) {
        model.sender?.forEach { participant ->
            if (participant.contact == null || participant.contact == NextivaContact()) {
                val nContact = dbManager.getConnectContactFromPhoneNumberInThread(participant.phoneNumber).value
                participant.contact = nContact
            }
        }
        if (tempMessageIdSent.isNotEmpty() || actualMessageIdSent.isNotEmpty()) {
            // validation for the rest of messages (already sent)
            model.isSender = senderNameEqualsToUserName(model)
            // validation of the current message being sent
            if (tempMessageIdSent == model.messageId || actualMessageIdSent == model.messageId) {
                model.isSender = true
            }
        } else {
            // no message is being sent -> general validation to all messages
            model.isSender = senderNameEqualsToUserName(model)
        }
    }

    private fun senderNameEqualsToUserName(model: SmsMessage): Boolean {
        val userName = sessionManager.userDetails?.fullName
        val senderName = model.sender?.firstOrNull()?.name ?: userName
        val nextivaContact = model.sender?.firstOrNull()?.contact
        return if (nextivaContact != null) {
            userName == senderName
        } else {
            model.body?.startsWith(userName?.substringBefore(" ").orEmpty()) == true
        }
    }

    private fun isSameDay(instantA: Instant, instantB: Instant): Boolean {
        val zoneId = ZoneId.systemDefault()
        val instantADateTime = ZonedDateTime.ofInstant(instantA, zoneId).toLocalDate().atStartOfDay(zoneId)
        val instantBDateTime = ZonedDateTime.ofInstant(instantB, zoneId).toLocalDate().atStartOfDay(zoneId)
        val daysBetween = ChronoUnit.DAYS.between(instantADateTime, instantBDateTime)

        return daysBetween.toInt() == 0
    }

    private fun setBubbleTypes(
        before: SmsMessageListItem?,
        after: SmsMessageListItem?,
        isSender: Boolean,
        willInsertSeparator: Boolean
    ) {
        before?.bubbleType?.let { beforeBubbleType ->
            after?.let { after ->
                when (beforeBubbleType) {
                    Enums.Chats.MessageBubbleTypes.END -> {
                        when {
                            willInsertSeparator || !isSender -> before.bubbleType = Enums.Chats.MessageBubbleTypes.SINGLE
                            else -> after.bubbleType = Enums.Chats.MessageBubbleTypes.MIDDLE
                        }
                    }
                    Enums.Chats.MessageBubbleTypes.MIDDLE -> {
                        when {
                            willInsertSeparator || !isSender -> before.bubbleType = Enums.Chats.MessageBubbleTypes.START
                            else -> after.bubbleType = Enums.Chats.MessageBubbleTypes.MIDDLE
                        }
                    }
                }
            } ?: kotlin.run {
                before.bubbleType = if (beforeBubbleType == Enums.Chats.MessageBubbleTypes.END) {
                    Enums.Chats.MessageBubbleTypes.SINGLE
                } else {
                    Enums.Chats.MessageBubbleTypes.START
                }
            }
        }
    }

    fun groupMessagesByTime(
        itemsList: List<BaseListItem>,
        timeWindowInSeconds: Int = FIVE_MIN
    ) {
        var prev: BaseListItem? = null
        var groupTime = 0L
        itemsList.reversed().let { list ->
            list.forEachIndexed { index, item ->
                (item as? SmsMessageListItem)?.let { message ->
                    val prevName = (prev as? SmsMessageListItem)?.data?.sender?.firstOrNull()?.let { sender ->
                        sender.phoneNumber?.ifBlank { sender.uiName }
                    }

                    val messageName = message.data.sender?.firstOrNull()?.let { sender ->
                        sender.phoneNumber?.ifBlank{ sender.uiName }
                    }

                    if (groupTime == 0L ||
                        message.data.sent?.epochSecond.orZero() > groupTime ||
                        prev is MessageHeaderListItem ||
                        (prev as? SmsMessageListItem)?.data?.isSender != message.data.isSender ||
                        prevName != messageName
                    ) {
                        groupTime = message.data.sent?.epochSecond.orZero() + timeWindowInSeconds
                        message.showHumanReadableTime = true
                    }

                    setReversedBubbleTypes(
                        prev = prev as? SmsMessageListItem,
                        message = message,
                        next = list.getOrNull(index + 1) as? SmsMessageListItem,
                        fromSameUser = prevName == messageName
                    )
                }
                prev = item
            }
        }
    }

    private fun setReversedBubbleTypes(
        prev: SmsMessageListItem?,
        message: SmsMessageListItem,
        next: SmsMessageListItem?,
        fromSameUser: Boolean
    ) {
        if (prev.notNull()) {
            when (prev?.bubbleType) {
                Enums.Chats.MessageBubbleTypes.START -> {
                    if (!fromSameUser || message.showHumanReadableTime) {
                        prev.bubbleType = Enums.Chats.MessageBubbleTypes.SINGLE
                    } else {
                        message.bubbleType = Enums.Chats.MessageBubbleTypes.MIDDLE
                    }
                }

                Enums.Chats.MessageBubbleTypes.MIDDLE -> {
                    if (!fromSameUser || message.showHumanReadableTime) {
                        prev.bubbleType = Enums.Chats.MessageBubbleTypes.END
                    } else if(next.notNull()) {
                        message.bubbleType = Enums.Chats.MessageBubbleTypes.MIDDLE
                    }
                }
            }
        }

        if(next.isNull()) {
            if(!fromSameUser || message.showHumanReadableTime) {
                message.bubbleType = Enums.Chats.MessageBubbleTypes.SINGLE
            } else {
                message.bubbleType = Enums.Chats.MessageBubbleTypes.END
            }
        }
    }

    fun setup(bundle: Bundle, isInit: Boolean = false) {
        isCallOptionsDisabled = bundle.getBoolean(Constants.Chats.PARAMS_IS_CALL_OPTIONS_DISABLED)
        chatType = bundle.getString(Constants.Chats.PARAMS_CHAT_TYPE)
        isNewChat = bundle.getBoolean(Constants.Chats.PARAMS_IS_NEW_CHAT)
        currentUser = sessionManager.currentUser
        ourTeams = sessionManager.usersTeams
        allSavedTeams = sessionManager.allTeams
        isSmsEnabled = sessionManager.isSmsEnabled
        isSmsLicenseEnabled = sessionManager.isSmsLicenseEnabled
        isTeamSmsLicenseEnabled = sessionManager.isTeamSmsLicenseEnabled
        userNumber = sessionManager.userDetails?.telephoneNumber
        isSMSCampaignFeatureEnabled = sessionManager.isSmsCampaignValidationEnabled
        isUserAdmin = sharedPreferencesManager.getBoolean(IS_USER_SUPER_ADMIN, false)
        smsCampaignStatus = sharedPreferencesManager.getString(SharedPreferencesManager.SMS_CAMPAIGN_STATUS, "").orEmpty()

        if (chatType == Enums.Chats.ConversationTypes.SMS) {
            setupSms(bundle, isInit)
        }

        getUiNameToDisplay()
    }

    private fun setupSms(bundle: Bundle, isInit: Boolean = false) {
        bundle.getString(Constants.Chats.PARAMS_SMS_CONVERSATION_DETAILS)?.let { detailsString ->
            conversationDetails = GsonUtil.getObject(SmsConversationDetails::class.java, detailsString)
            conversationDetails?.isNewChat = isNewChat
            conversationDetails?.isInit = true
            viewModelScope.launch { _groupId.emit(conversationDetails?.groupId) }
        }

        if (pendingMessage.value.isNull() && !alreadySentPendingMessage) {
            (bundle.getSerializable(Constants.Chats.PARAMS_PENDING_MESSAGE_DATA) as? PendingMessageData)?.let { data ->
                alreadySentPendingMessage = true
                comesFromNewMessage = true
                pendingMessage.postValue(Event(data))
            }
        }

        sessionManager.usersTeams?.let { conversationDetails?.userTeams = it }
        sessionManager.allTeams?.let { conversationDetails?.allSavedTeams = it }

        conversationDetails?.isTeamSmsEnabled = isTeamSmsLicenseEnabled

        if (sendingPhoneNumber.value == null || isInit) {
            if (isTeamSmsLicenseEnabled) {
                val ourConversationTeam = conversationDetails?.userTeams
                    ?.sortedBy { it.teamId }
                    ?.filter { it.smsEnabled != false }
                    ?.firstOrNull { team ->
                        conversationDetails?.teams?.map { it.teamId }?.contains(team.legacyId) == true ||
                                conversationDetails?.teams?.map { it.teamId }?.contains(team.teamId) == true }

                if (ourConversationTeam != null) {
                    setSenderPhoneNumber(ourConversationTeam.teamPhoneNumber ?: "", ourConversationTeam.teamName, null)

                } else {
                    setSenderPhoneNumber(getUserTelephoneNumber())
                }

            } else {
                setSenderPhoneNumber(getUserTelephoneNumber())
            }
        }

        bundle.getString(Constants.Chats.PARAMS_MESSAGE_ID)?.let { messageId ->
            smsManagementRepository.getSmsMessageWithMessageId(Objects.requireNonNull(messageId))
                .subscribe(object : DisposableSingleObserver<SmsMessages?>() {
                    override fun onSuccess(smsMessages: SmsMessages) {
                        if (sessionManager.userDetails != null && !TextUtils.isEmpty(sessionManager.userDetails?.telephoneNumber)) {
                            dbManager.saveSmsMessages(
                                smsMessages.data,
                                getUserTelephoneNumber(),
                                Enums.SMSMessages.SentStatus.SUCCESSFUL,
                                currentUser?.userUuid,
                                allSavedTeams).subscribe(object : DisposableCompletableObserver() {
                                override fun onComplete() {
                                    updateReadStatus()
                                    sessionManager.updateNotificationsCount(conversationRepository, application)
                                }

                                override fun onError(e: Throwable) {
                                    LogUtil.e("Failed saving SMS message with messageId in conversation. ${e.localizedMessage}")
                                }
                            })
                        }
                    }

                    override fun onError(e: Throwable) {
                        LogUtil.e("Splash Activity", "Failed getting SMS message.")
                    }
                })
        }

        participantsList.firstOrNull()?.let { participant ->
            mCompositeDisposable.add(
                dbManager.getContactFromPhoneNumber(participant.phoneNumber)
                    .subscribe { dbResponse: DbResponse<NextivaContact?>? ->
                        if (dbResponse != null && dbResponse.value != null) {
                            contactForContactAction = dbResponse.value
                        }
                    })
        }
    }

    fun getContactInSingleUserSms(onContactReturned: (NextivaContact?) -> Unit) {
        if (membersUiNames.size == 1) {

            dbManager.getContactFromUIName(membersUiNames.first())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : DisposableSingleObserver<NextivaContact>() {
                    override fun onSuccess(contact: NextivaContact) {
                        contact.dbId?.let {
                            onContactReturned(contact)

                        } ?: kotlin.run {
                            onContactReturned(null)
                        }
                    }

                    override fun onError(e: Throwable) {
                        onContactReturned(null)
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                })
        } else {
            onContactReturned(null)
        }
    }

    fun contactAction(action: Int?) {
        var contactToUse = contactForContactAction

        //Simulate contact if it doesn't exist.
        if (contactToUse == null) {
            contactToUse = NextivaContact("")
            contactToUse.contactType = Enums.Contacts.ContactTypes.UNKNOWN
        }

        val participantInfo = contactToUse.getParticipantInfo(null)
        participantInfo.callType = action

        if (chatType == Enums.Chats.ConversationTypes.SMS) {
            val number =
                CallUtil.removeCountryCodeFromFormattedPhoneNumber(participantsList.firstOrNull()?.phoneNumber)
            participantInfo.displayName = membersUiNames.firstOrNull()
            participantInfo.numberToCall = number
        }

        processedCallInfoMutableLiveData.value = participantInfo
    }

    private fun getMultiPartBody(activity: FragmentActivity, fileName: String): MultipartBody.Part {
        val f = File(activity.cacheDir, fileName)
        val mimeType = MessageUtil.getMimeType(f.toUri(), application)
        val requestFile: RequestBody = RequestBody.create(
            mimeType?.toMediaTypeOrNull(),
            f
        )

        return MultipartBody.Part.createFormData("file", fileName, requestFile)
    }

    private fun prependUsernameIfTeamMessage(message:String): String {
        var messageText = message
        var teamSender: String

        if (sendingPhoneNumber.value?.team != null) {
            sessionManager.userDetails?.firstName.let { firstName ->
                sessionManager.userDetails?.lastName?.first().let { lastInitial ->
                    teamSender = "$firstName $lastInitial: "
                    messageText = teamSender + message
                }
            }
        }

        return messageText
    }

    fun sendSmsMessage(message: String, finishedCallback: (Int?) -> Unit) {
        val uploadedDate = getUploadedDate()
        val uuid: String = UUID.randomUUID().toString()
        val messageList: ArrayList<Message> = ArrayList()
        val messageText = prependUsernameIfTeamMessage(message)
        tempMessageIdSent = uuid

        messageList.add(
            Message(
                null, messageText, "SMS", uuid, uuid, null, "NORMAL",
                getRecipientList(), getSenderObject(), uploadedDate, null, null, conversationDetails?.groupValue, getMessageStateObject(uuid), true, Enums.SMSMessages.SentStatus.PENDING,
                conversationDetails?.getAllTeams()?.map { team -> SmsTeamPayload(team.teamName, team.teamId, team.teamPhoneNumber, team.legacyId) }, conversationDetails?.groupId ?: uuid
            )
        )

        val data = Data(conversationDetails?.groupValue, messageList)

        dbManager.saveSendMessage(data, getUserTelephoneNumber(), Enums.SMSMessages.SentStatus.PENDING, currentUser?.userUuid, allSavedTeams, null)
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    data.messages?.get(0)?.messageId?.let {
                        sendSmsRequest(getSmsSendPostBody(messageText, uuid), it, finishedCallback)
                    }
                }

                override fun onError(e: Throwable) {
                    finishedCallback(null)
                }
            })
        getUiNameToDisplay()
    }

    private fun getSmsSendPostBody(message: String, clientId: String): SendMessagePostBody {
        val teams = conversationDetails?.getAllTeams()?.sortedBy { it.teamId }
        val sendingTeamId = sendingPhoneNumber.value?.team?.teamId
        var destinationNumbers = conversationDetails?.getDestinationNumbers()
        val source = sendingPhoneNumber.value?.phoneNumber ?: getUserTelephoneNumber()

        if (sendingTeamId != null && destinationNumbers != null) {
            destinationNumbers = destinationNumbers.filter { it != source }
        }
        return SendMessagePostBody(
            destination = if (destinationNumbers.isNullOrEmpty()) arrayListOf(
                teams?.firstOrNull()?.teamPhoneNumber ?: getUserTelephoneNumber()
            ) else destinationNumbers,
            message = message,
            source = source.ifEmpty { null },
            clientId = clientId,
            teamId = sendingTeamId
        )
    }

    private fun sendSmsRequest(smsPostBody: SendMessagePostBody, tempMessageId: String, finishedCallback: ((Int?) -> Unit)? = null) {
        showProgressDialog.value = null
        mCompositeDisposable.add(
            smsManagementRepository
                .sendSmsMessage(smsPostBody)
                .subscribe { sendMessageResponse: SendMessageResponse? ->
                    if (sendMessageResponse != null && sendMessageResponse.status_code == 202) {
                        conversationDetails?.groupId = sendMessageResponse.groupId
                        viewModelScope.launch {  _groupId.emit(sendMessageResponse.groupId) }

                        updateMessageIdAndSentStatus(tempMessageId, sendMessageResponse.messageId, Enums.SMSMessages.SentStatus.SUCCESSFUL, conversationDetails?.groupId)
                        sendMessageResponse.messageId?.let {
                            dismissProgressBarDialog.value = null

                            sendingPhoneNumber.value?.let { sendingPhoneNumber ->
                                conversationDetails?.updateSendingPhoneNumber(sendingPhoneNumber, null)
                                setSenderPhoneNumber(sendingPhoneNumber.phoneNumber, sendingPhoneNumber.team?.teamName, null, true)
                            }
                        }
                        trackEvent(MessagingEvent().outgoingSMS())
                    } else {
                        dismissProgressBarDialog.value = null
                        maxRateExceededLiveData.value = sendMessageResponse != null && sendMessageResponse.status_code == 409
                        updateMessageSentStatus(Enums.SMSMessages.SentStatus.FAILED, tempMessageId)
                    }

                    if (finishedCallback != null) {
                        finishedCallback(sendMessageResponse?.status_code)
                    }

                    inProgressMmsMessages.remove(tempMessageId)
                })
    }

    private fun trackEvent(event: MessagingEvent) {
        analyticsManager.trackEvent(event)
        dataDogManager.performCustomAction(event.name, emptyMap<String, Any>())
    }

    fun onResendSmsMessage(message: SmsMessage) {
        if (connectionStateManager.isInternetConnected) {
            if(!inProgressMmsMessages.contains(message.messageId)) {
                message.messageId?.let { inProgressMmsMessages.add(it) }

                if (!TextUtils.isEmpty(message.body)) {
                    message.messageId?.let { messageId ->
                        dbManager.updateSentStatus(message.messageId, Enums.SMSMessages.SentStatus.PENDING)
                        sendSmsRequest(getSmsSendPostBody(message.body!!, messageId), messageId)
                    }
                }
            }
        } else {
            showNoInternetLiveData.value = null
        }
    }

    fun sendMmsRequest(fbody: MultipartBody.Part, destination: String, source: String?, message: String, tempMessageId: String, finishedCallback: ((Int?) -> Unit)? = null) {
        showProgressDialog.value = null
        mCompositeDisposable.add(
            smsManagementRepository
                .sendMmsMessage(fbody, destination, message, source, tempMessageId, conversationDetails?.getAllTeams())
                .subscribe { sendMmsResponse: SendMessageResponse ->
                    dismissProgressBarDialog.value = null
                    if (sendMmsResponse.status_code == 202) {
                        conversationDetails?.groupId = sendMmsResponse.groupId
                        viewModelScope.launch { _groupId.emit(sendMmsResponse.groupId) }
                        updateMessageIdAndSentStatus(tempMessageId, sendMmsResponse.messageId, Enums.SMSMessages.SentStatus.SUCCESSFUL, conversationDetails?.groupId)
                        trackEvent(MessagingEvent().outgoingSMS())
                    } else {
                        maxRateExceededLiveData.value = sendMmsResponse.status_code == 409
                        updateMessageSentStatus(Enums.SMSMessages.SentStatus.FAILED, tempMessageId)
                    }

                    finishedCallback?.let { it(sendMmsResponse.status_code) }
                    inProgressMmsMessages.remove(tempMessageId)
                })
    }

    fun sendMmsMessage(imageUri: Uri, fbody: MultipartBody.Part, message: String, fileName: String, contentType: String, contentData: ByteArray?, audioDuration: Long, finishedCallback: (Int?) -> Unit) {
        val uploadedDate = getUploadedDate()
        val uuid: String = UUID.randomUUID().toString()
        val messageList: ArrayList<Message> = ArrayList()
        val messageText = prependUsernameIfTeamMessage(message)
        tempMessageIdSent = uuid

        messageList.add(
            Message(
                getAttachmentsList(imageUri, contentType, fileName, contentData, uploadedDate, audioDuration), messageText, "SMS", uuid, uuid, null, "NORMAL",
                getRecipientList(), getSenderObject(), uploadedDate, null, null, conversationDetails?.groupValue, getMessageStateObject(uuid), true, Enums.SMSMessages.SentStatus.PENDING,
                conversationDetails?.getAllTeams()?.map { team -> SmsTeamPayload(team.teamName, team.teamId, team.teamPhoneNumber, team.legacyId) }, conversationDetails?.groupId
            )
        )

        val data = Data(conversationDetails?.groupValue, messageList)

        dbManager.saveSendMessage(data, getUserTelephoneNumber(), Enums.SMSMessages.SentStatus.PENDING, currentUser?.userUuid, allSavedTeams, null)
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    val source = getUserTelephoneNumber()
                    data.messages?.get(0)?.messageId?.let { messageId ->
                        val teams = conversationDetails?.getAllTeams()?.sortedBy { it.teamId }
                        val destinationNumbers = conversationDetails?.getDestinationNumbers()
                        sendMmsRequest(fbody,
                            if (destinationNumbers.isNullOrEmpty()) arrayListOf(teams?.firstOrNull()?.teamPhoneNumber ?: getUserTelephoneNumber()).joinToString { it } else destinationNumbers.joinToString { it },
                            source.ifEmpty { null },
                            messageText,
                            messageId, finishedCallback)
                    }
                }

                override fun onError(e: Throwable) {
                    finishedCallback(null)
                }
            })
    }

    fun onResendMmsMessage(message: SmsMessage, activity: FragmentActivity?) {
        if (connectionStateManager.isInternetConnected) {
            if(!inProgressMmsMessages.contains(message.messageId)){
                message.messageId?.let { inProgressMmsMessages.add(it) }
                val source = getSendingPhoneNumber()
                message.messageId?.let { messageId ->
                    dbManager.updateSentStatus(message.messageId, Enums.SMSMessages.SentStatus.PENDING)
                    val teams = conversationDetails?.getAllTeams()?.sortedBy { it.teamId }
                    val destinationNumbers = conversationDetails?.getDestinationNumbers()
                    val destination = if (destinationNumbers.isNullOrEmpty()) arrayListOf(teams?.firstOrNull()?.teamPhoneNumber ?: getUserTelephoneNumber()).joinToString { it } else destinationNumbers.joinToString { it }
                    message.body?.let { it1 ->
                        if (activity != null) {
                            message.attachments?.get(0)?.fileName?.let { fileName ->
                                sendMmsRequest(getMultiPartBody(activity, fileName), destination, source.ifEmpty { null }, it1, messageId)
                            }
                        }
                    }
                }
            }
        } else {
            showNoInternetLiveData.value = null
        }
    }

    private fun updateMessageIdAndSentStatus(tempMessageId: String, messageId: String?, sentStatus: Int, groupId: String?) {
        dbManager.updateMessageIdAndSentStatus(tempMessageId, messageId, sentStatus, groupId)
        actualMessageIdSent = messageId.orEmpty()
    }

    private fun updateMessageSentStatus(status: Int, messageId: String) {
        dbManager.updateSentStatus(messageId, status)
    }

    fun getOutStateBundle(outState: Bundle): Bundle {
        with(outState) {
            putString(Constants.Chats.PARAMS_CHAT_TYPE, chatType)
            putString(Constants.Chats.PARAMS_SMS_CONVERSATION_DETAILS, GsonUtil.getJSON(conversationDetails))
            putBoolean(Constants.Chats.PARAMS_IS_NEW_CHAT, isNewChat)
            putBoolean(Constants.Chats.PARAMS_IS_CALL_OPTIONS_DISABLED, isCallOptionsDisabled ?: false)
            putSerializable(Constants.Chats.PARAMS_PENDING_MESSAGE_DATA, null)
        }

        return outState
    }

    fun getUiNameToDisplay() {
        uiNameTextChangedLiveData.value = null
    }

    fun getFromItemsForTeamSMSBanner(): ArrayList<BottomSheetMenuListItem> {
        val fromItems: ArrayList<BottomSheetMenuListItem> = ArrayList()
        val totalTeamsInConversation = conversationDetails?.getTotalTeamsInConversation().orZero()
        getUserSmsEnabledNumbers().forEach { numbers ->
            if (!numbers.phoneNumber.isNullOrEmpty() && numbers.enabled) {
                fromItems.add(
                    BottomSheetMenuListItem(
                        PhoneNumberUtils.formatNumber(numbers.phoneNumber, Locale.getDefault().country),
                        numbers.team?.teamName,
                        numbers.isSelected)
                )
            }
        }

        if (totalTeamsInConversation > 1) {
            fromItems.removeAt(0)
        }

        return ArrayList(fromItems)
    }

    fun getToItemsForTeamSMSBanner(participantList: List<SmsParticipant>?): ArrayList<BottomSheetMenuListItem> {
        val toItems: ArrayList<BottomSheetMenuListItem> = ArrayList()
        if (!isNewChat) {
            participantList?.firstOrNull()?.let { participant ->
                if (participantList.size == 1 && (participant.contact?.allPhoneNumbersSorted?.filter { CallUtil.isValidSMSNumber(it.strippedNumber) }?.size ?: 0) > 1) {
                    participant.contact?.allPhoneNumbersSorted?.filter { CallUtil.isValidSMSNumber(it.strippedNumber) }?.forEach { phoneNumber ->
                        toItems.add(BottomSheetMenuListItem(
                            PhoneNumberUtils.formatNumber(phoneNumber.strippedNumber, Locale.getDefault().country),
                            phoneNumber.label,
                            CallUtil.arePhoneNumbersEqual(phoneNumber.strippedNumber, participant.phoneNumber))
                        )
                    }
                }
            }
        }
        return toItems
    }

    private fun existsBusinessContactInThisConversation(exists: (Boolean) -> Unit) {
        val atLeastOneContactIsNull = participantsList.any { it.contact == null }
        val atLeastOneParticipantIsUnsavedContact = participantsList.any { it.uiName == null }
        if (atLeastOneContactIsNull) {
            if (atLeastOneParticipantIsUnsavedContact) {
                exists(true)
            } else {
                val listOfPhoneNumbers = participantsList.map { it.phoneNumber }
                dbManager.getConnectContactsFromPhoneNumbers(listOfPhoneNumbers)
                    .subscribe(object : DisposableSingleObserver<List<DbResponse<NextivaContact>>>() {
                        override fun onSuccess(list: List<DbResponse<NextivaContact>>) {
                            val contacts = list.map { it.value!! }
                            exists(existBusinessOrUnsavedContactInList(contacts))
                        }
                        override fun onError(e: Throwable) {}
                    })
            }
        } else {
            val contacts = participantsList.mapNotNull { it.contact }
            exists(existBusinessOrUnsavedContactInList(contacts))
        }
    }

    private fun existBusinessOrUnsavedContactInList(contacts: List<NextivaContact>): Boolean {
        return contacts.any {
            it.contactType == Enums.Contacts.ContactTypes.CONNECT_PERSONAL ||
                    it.contactType == Enums.Contacts.ContactTypes.CONNECT_SHARED ||
                    it.contactType == Enums.Contacts.ContactTypes.UNKNOWN ||
                    it.contactType == Enums.Contacts.ContactTypes.NONE ||
                    it.contactType.isNull()
        }
    }

    fun shouldDisplayFooterAndHideComposer(shouldDisplay: (Boolean) -> Unit) {
        existsBusinessContactInThisConversation { exists ->
            shouldDisplay(
                (isSMSCampaignFeatureEnabled && (SMSCampaignStatus.isNotStarted(smsCampaignStatus)) && exists)
                        || (isSMSCampaignFeatureEnabled && SMSCampaignStatus.isPending(smsCampaignStatus) && exists)
            )
        }
    }

    fun getFooterType(footerType: (String) -> Unit) {
        existsBusinessContactInThisConversation { exists ->
            val type = when {
                SMSCampaignStatus.isPending(smsCampaignStatus) && exists -> FooterMessageType.ADMIN_AND_USER
                SMSCampaignStatus.isNotStarted(smsCampaignStatus) -> {
                    if (isUserAdmin) FooterMessageType.ADMIN else FooterMessageType.USER
                }
                else -> FooterMessageType.UNKNOWN
            }
            footerType(type)
        }
    }

    fun shouldDisplayRibbonAboveComposer(shouldDisplay: (Boolean) -> Unit) {
        existsBusinessContactInThisConversation { exists ->
            val shouldShowBanner = when {
                !isSMSCampaignFeatureEnabled -> false
                SMSCampaignStatus.isAccepted(smsCampaignStatus) -> false
                SMSCampaignStatus.isPending(smsCampaignStatus) -> exists
                SMSCampaignStatus.isNotStarted(smsCampaignStatus) && !exists -> true
                else -> false
            }
            shouldDisplay(shouldShowBanner)
        }
    }

// --------------------------------------------------------------------------------------------
// Webservice Events
// --------------------------------------------------------------------------------------------

    fun updateReadStatus() {
        conversationDetails?.groupId?.let { groupId ->
            val messageStateList = dbManager.getMessageStateListInThread(groupId)
            makeUpdateApiRequest(messageStateList.filter { messageState -> messageState.readStatus != Enums.SMSMessages.ReadStatus.READ })
        }
    }

    private fun makeUpdateApiRequest(messageStateList: List<DbMessageState>) {
        val messageIds: List<String> = messageStateList.map { it.messageId ?: "" }
        val userMessageState = UserMessageState(true)
        if (messageIds.isNotEmpty()) {
            smsManagementRepository.bulkUpdateMessageReadStatus(BulkUpdateUserMessageState(messageIds, userMessageState))
                .subscribe(object : DisposableSingleObserver<Boolean>() {
                    override fun onSuccess(success: Boolean) {
                        if (success) {
                            conversationDetails?.groupId?.let {
                                dbManager.updateReadStatusForGroupId(it)
                            }
                        } else {
                            dbManager.markMessagesUnread(messageStateList)
                        }
                        sessionManager.updateNotificationsCount(conversationRepository, application)
                    }

                    override fun onError(e: Throwable) {
                        sessionManager.updateNotificationsCount(conversationRepository, application)
                        LogUtil.e("Failed updating user message read status. ${e.localizedMessage}")
                    }
                })
        }
    }

    private fun getAttachmentsList(
        imageUri: Uri,
        contentType: String,
        fileName: String,
        contentData: ByteArray?,
        uploadedDate: String,
        audioDuration: Long
    ): ArrayList<Attachment> {
        val attachmentList: ArrayList<Attachment> = ArrayList()
        attachmentList.add(Attachment(imageUri.toString(), contentType, fileName, contentData, null, uploadedDate, audioDuration))
        return attachmentList
    }

    private fun getUploadedDate(): String {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSZ")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return (dateFormat.format(Date()).toString())
    }

    private fun getMessageStateObject(uuid: String): MessageState {
        return MessageState(false, null, uuid, "NORMAL", Enums.SMSMessages.ReadStatus.READ)
    }

    private fun getRecipientList(): ArrayList<Participant> {
        val recipientList: ArrayList<Participant> = ArrayList()

        conversationDetails?.getParticipantsList()?.forEach { participant ->
            recipientList.add(Participant(null, participant.uiName, participant.phoneNumber, null, null, participant.userUUID))
        }

        return recipientList
    }

    fun getUserTelephoneNumber(): String{
        userNumber?.let { userPhoneNumber->
            return CallUtil.getCountryCode() + userPhoneNumber
        }?: kotlin.run {
            return ""
        }
    }

    fun setSenderPhoneNumber(phoneNumber: String, teamName: String? = null, selectedParticipants: ArrayList<SmsParticipant>? = null, isSwitchingConversation: Boolean = false) {
        sendingPhoneNumber.value = SendingPhoneNumber(phoneNumber, if (!teamName.isNullOrEmpty()) conversationDetails?.userTeams?.firstOrNull { it.teamName == teamName } else null)
        sendingPhoneNumber.value?.let { conversationDetails?.updateSendingPhoneNumber(it, selectedParticipants, isSwitchingConversation) }

        refreshConversationDetails(isSwitchingConversation)
    }

    fun selectTeamNumberAsSender(name: String?) {
        run loop@ {
            getUserSmsEnabledNumbers().forEach { phoneNumber ->
                val num = phoneNumber.phoneNumber
                val teamName = phoneNumber.team?.teamName
                if (num.isNotBlank() && teamName?.isNotBlank().orFalse() && (name == teamName || name?.isBlank().orTrue())) {
                    setSenderPhoneNumber(
                        phoneNumber.phoneNumber,
                        phoneNumber.team?.teamName,
                        isSwitchingConversation = true
                    )
                    return@loop
                }
            }
        }
    }

    private fun refreshConversationDetails(shouldForceCheckDb: Boolean = false) {
        if (shouldForceCheckDb) {
            dbManager.getConversationDetailsFrom(conversationDetails)?.let {
                conversationDetails = it
            }
        }

        updateReadStatus()
        conversationDetails?.isNewChat = isNewChat
        conversationDetailsLiveData.value = conversationDetails
        membersUiNames.clear()
        getUiNameToDisplay()
    }

    fun getSendingPhoneNumber(): String {
        return sendingPhoneNumber.value?.phoneNumber ?: getUserTelephoneNumber()
    }

    fun getUserSmsEnabledNumbers(): ArrayList<SendingPhoneNumber> {
        val numberList: ArrayList<SendingPhoneNumber> = ArrayList()
        val phoneNumberInformation = sessionManager.phoneNumberInformation
        var selectedTeam: SmsTeam? = null

        conversationDetails?.userTeams?.forEach { team ->
            if (isTeamSmsLicenseEnabled && team.smsEnabled == true) {
                team.teamPhoneNumber?.let { teamPhoneNumber ->
                    participantsList.map { it.phoneNumber ?: "" }
                        .firstOrNull { it == teamPhoneNumber }?.let { selectedTeam = team }
                }
            }
        }

        if (selectedTeam == null) {
            getUserTelephoneNumber().let { phoneNumber ->
                numberList.add(SendingPhoneNumber(phoneNumber, null, phoneNumberInformation?.metadata?.smsEnabled ?: false, getSendingPhoneNumber() == phoneNumber))
            }

            conversationDetails?.userTeams?.forEach { team ->
                if (isTeamSmsLicenseEnabled && team.smsEnabled == true) {
                    team.teamPhoneNumber?.let { teamPhoneNumber ->
                        numberList.add(
                            SendingPhoneNumber(
                                teamPhoneNumber,
                                team,
                                true,
                                getSendingPhoneNumber() == teamPhoneNumber
                            )
                        )
                    }
                }
            }

        } else {
            selectedTeam?.teamPhoneNumber?.let { teamPhoneNumber ->
                numberList.add(SendingPhoneNumber(teamPhoneNumber, selectedTeam, selectedTeam?.smsEnabled ?: true, getSendingPhoneNumber() == teamPhoneNumber))
            }
        }

        numberList.removeIf { !it.enabled }
        return numberList
    }

    private fun getSenderObject(): Participant {
        val teamIds = if (sendingPhoneNumber.value?.team?.teamId != null) listOf(sendingPhoneNumber.value?.team?.teamId ?: "") else null
        val participant = Participant(null, null, sendingPhoneNumber.value?.phoneNumber ?: getUserTelephoneNumber(), teamIds, null)
        participant.userUuid = conversationDetails?.ourUuid
        return participant
    }

    fun removeContact(nextivaContact: NextivaContact?) {
        if (nextivaContact != null) {
            if (nextivaContact.phoneNumbers?.isNotEmpty()!!) {
                for (participant in participantsList.map { it.phoneNumber ?: "" }) {
                    if (participant.isNotEmpty() && participant == nextivaContact.phoneNumbers!![0].number) {
                        membersUiNamesWithParticipant.remove(participant)
                        membersUiNames.remove(membersUiNames[participantsList.map { it.phoneNumber ?: "" }.indexOf(participant)])
                        conversationDetails?.removeRecipient(participant)

                        conversationDetails?.groupValue?.let { groupValue ->
                            if (groupValue.isNotEmpty() && groupValue.contains(participant)) {
                                conversationDetails?.groupValue = conversationDetails?.groupValue?.replace(participant, "")
                                conversationDetails?.groupValue = conversationDetails?.groupValue?.replace(",,", "")
                            }
                        }

                        return
                    }
                }
            }
        }
    }

    fun scaleBitmapForMMS(context: Context, bitmap: Bitmap): Bitmap {
        val originalSize = bitmap.allocationByteCount
        return if (originalSize > Constants.MMS.MAX_FILE_SIZE) {
            val scale = sqrt(Constants.MMS.MAX_FILE_SIZE.toDouble() / originalSize)
            val resizedBitmap = resizeBitmapWithGlide(context, bitmap, scale)
            val resizedSize = resizedBitmap.allocationByteCount
            if (resizedSize > Constants.MMS.MAX_FILE_SIZE) {
                val secondScale = sqrt(Constants.MMS.MAX_FILE_SIZE.toDouble() / resizedSize)
                resizeBitmapWithGlide(context, resizedBitmap, secondScale)
            } else {
                resizedBitmap
            }
        } else {
            bitmap
        }
    }

    private fun resizeBitmapWithGlide(context: Context, bitmap: Bitmap, scale: Double): Bitmap {
        val targetWidth = (bitmap.width * scale).toInt()
        val targetHeight = (bitmap.height * scale).toInt()

        return Glide
            .with(context)
            .asBitmap()
            .load(bitmap)
            .override(targetWidth, targetHeight)
            .centerInside()
            .submit()
            .get()
    }

    fun getBitmapData(extension: String, f: File, selectedBitmap: Bitmap): ByteArray {
        lateinit var bitmapData: ByteArray
        val bos = ByteArrayOutputStream()
        val quality = 100
        var bitmapCompression = Bitmap.CompressFormat.PNG
        if (!TextUtils.isEmpty(extension)) {
            if (extension.lowercase(Locale.ROOT).contains(Enums.Attachment.ContentExtensionType.EXT_JPEG) ||
                extension.lowercase(Locale.ROOT).contains(Enums.Attachment.ContentExtensionType.EXT_JPG)
            ) {
                bitmapCompression = Bitmap.CompressFormat.JPEG
            }
            selectedBitmap.compress(bitmapCompression, quality, bos)
            bitmapData = bos.toByteArray()
            val fos = FileOutputStream(f)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
        }
        return bitmapData
    }

    fun getFileBytes(context: Context, f: File, attachmentUri: Uri): ByteArray {
        val chunkSize = 1024
        val imageData = ByteArray(chunkSize)
        var inn: InputStream? = null
        var out: OutputStream? = null
        try {
            inn = context.contentResolver.openInputStream(attachmentUri)
            out = FileOutputStream(f)
            var bytesRead: Int
            if (inn != null) {
                while (inn.read(imageData).also { bytesRead = it } > 0) {
                    out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)))
                }
            }
        } catch (ex: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
        } finally {
            inn?.close()
            out?.close()
        }
        return f.readBytes()
    }

    data class SendingPhoneNumber(val phoneNumber: String, val team: SmsTeam?, val enabled: Boolean = true, val isSelected: Boolean = true) : Serializable

    // --------------------------------------------------------------------------------------------
    // Draft messages functions
    // --------------------------------------------------------------------------------------------

    fun removeDraftMessages() {
        conversationDetails?.groupId?.let {
            dbManager.deleteDraftMessagesFromConversation(it, Enums.SMSMessages.SentStatus.DRAFT)
        }
    }

    fun saveOrUpdateDraftMessage(message: String) {
        removeDraftMessages()
        if (message.isNotEmpty()) {
            val data = Data(conversationDetails?.groupValue, createDraftMessageList(isAttachment = false, message))
            saveDraftMessageInInternalDb(data)
        }
    }

    fun saveOrUpdateDraftAttachment(imageUri: Uri, message: String, fileName: String, contentType: String, contentData: ByteArray?, audioDuration: Long) {
        removeDraftMessages()
        val data = Data(conversationDetails?.groupValue,
            createDraftMessageList(isAttachment = true, message, imageUri, fileName, contentType, contentData, audioDuration)
        )
        saveDraftMessageInInternalDb(data)
    }

    private fun createDraftMessageList(
        isAttachment: Boolean,
        message: String,
        imageUri: Uri? = null,
        fileName: String? = null,
        contentType: String? = null,
        contentData: ByteArray? = null,
        audioDuration: Long = 0L
    ): ArrayList<Message> {

        val uploadedDate = getUploadedDateForDraft(message)
        val uuid: String = UUID.randomUUID().toString()
        val messageList: ArrayList<Message> = ArrayList()
        val attachment = if (isAttachment) {
            getAttachmentsList(imageUri!!, contentType!!, fileName!!, contentData, uploadedDate, audioDuration)
        } else null

        messageList.add(
            Message(
                attachment, message, "SMS", uuid, uuid, null, "NORMAL",
                getRecipientList(), getSenderObject(), sent = uploadedDate, null, null, conversationDetails?.groupValue, getMessageStateObject(uuid), true, Enums.SMSMessages.SentStatus.DRAFT,
                conversationDetails?.getAllTeams()?.map { team -> SmsTeamPayload(team.teamName, team.teamId, team.teamPhoneNumber, team.legacyId) }, conversationDetails?.groupId
            )
        )

        return messageList
    }

    private fun getUploadedDateForDraft(message: String): String {
        return ((if (draftMessage?.body == message) {
            draftMessage?.sent
        } else {
            null
        }) ?: Instant.now()).toString()
    }

    private fun saveDraftMessageInInternalDb(data: Data) {
        dbManager.saveSendMessage(data, getUserTelephoneNumber(), Enums.SMSMessages.SentStatus.DRAFT, currentUser?.userUuid, allSavedTeams, conversationDetails?.groupId)
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    data.messages?.get(0)?.messageId?.let {
                        LogUtil.i("Success saving draft message with ID: $it")
                    }
                }
                override fun onError(e: Throwable) {
                    LogUtil.e("Failed saving draft message: ${e.localizedMessage}")
                }
            })
    }

    fun deleteMessage(message: SmsMessageListItem) {
        userRepoApiSmsStartedLiveData.value = R.string.progress_deleting
        mCompositeDisposable.add(
            conversationRepository.deleteMessage(
                getDeleteSingleCommunicationData(message.data.messageId))
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    smsMessageListItemDeleted.value = message
                    messageDeleteResultLiveData.value = success
                }
        )
    }

    private fun getDeleteSingleCommunicationData(messageId: String? = null): BulkActionsConversationData {
        val channels = Channels()
        messageId?.let {
            channels.SMS = SMS(
                identifierType = BulkActionsConversationData.IDENTIFIER_MESSAGE_ID,
                identifiers = arrayListOf(it)
            )
        }
        return BulkActionsConversationData(
            jobType = BulkActionsConversationData.JOB_TYPE_DELETE,
            modifications = null,
            isSoftDelete = null,
            channels = channels
        )
    }

    fun fetchGroupId(fromText: String, fromLabel: String?, participantList: ArrayList<SmsParticipant>?){
        viewModelScope.launch {
            _loading.emit(true)
            var isTeamMessage = false
            val teamIds = mutableSetOf<String>()
            val phones = mutableSetOf<String>()
            participantsList.forEach { participant ->
                if(participant.representingTeam?.teamId?.isNotBlank().orFalse()) {
                    isTeamMessage = true
                    teamIds.add(participant.representingTeam?.teamId.orEmpty())
                } else {
                    participant.phoneNumber?.let { num ->
                        val number = num.split("x")[0].replace("[^0-9]".toRegex(), "")
                        if (number.isNotBlank().orFalse()) {
                            phones.add(number)
                        }
                    }
                }
            }
            run exit@ { sessionManager.usersTeams?.forEach { team ->
                team.teamPhoneNumber?.nullIfEmpty()?.let { teamPhoneNumber ->
                    team.teamId?.let { teamId ->
                        if (CallUtil.getStrippedNumberWithCountryCode(teamPhoneNumber) == CallUtil.getStrippedNumberWithCountryCode(fromText)) {
                            isTeamMessage = true
                            teamIds.add(teamId)
                            return@exit
                        }
                    }
                }
            } }
            val body = GenerateGroupIdPostBody().apply {
                this.teamIds.addAll(teamIds)
                this.phoneNumbers.addAll(phones)
            }

            if(isTeamMessage) {
                // Group Id calculated using API Endpoint because this chat has a Team
                smsManagementRepository.generateGroupId(body).orEmpty()
            } else {
                sessionManager.userDetails?.telephoneNumber?.let { number ->
                    val selfNumber = CallUtil.getStrippedNumberWithCountryCode(
                        number.split("x")[0].replace(
                            "[^0-9]".toRegex(),
                            ""
                        )
                    )
                    if(selfNumber.isNotBlank()) {
                        body.phoneNumbers.add(selfNumber)
                    }
                }
                // Group Id is just an sorted arrange of phone numbers
                getPlainGroupId(body.phoneNumbers)
            }.nullIfEmpty()?.let {
                conversationDetails?.groupId = it
                setSenderPhoneNumber(
                    CallUtil.getStrippedPhoneNumber(fromText),
                    fromLabel,
                    participantList,
                    true
                )
                _groupId.emit(it)
            }
            _loading.emit(false)
        }
    }

    private fun getPlainGroupId(numbers: List<String>) : String {
        return numbers.toSet().sorted().joinToString("")
    }
}
