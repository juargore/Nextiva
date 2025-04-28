package com.nextiva.nextivaapp.android.features.messaging.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.liveData
import androidx.paging.map
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.mediators.SmsMessageRemoteMediator
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbMessageState
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.features.messaging.helpers.NotificationEvent
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.ConnectEditModeViewState
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData.Companion.MODIFICATION_STATUS_UNREAD
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.Channels
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.Modifications
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.SMS
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.containsLetter
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(application: Application,
                                               val nextivaApplication: Application,
                                               val smsManagementRepository: SmsManagementRepository,
                                               val dbManager: DbManager,
                                               val sessionManager: SessionManager,
                                               val conversationRepository: ConversationRepository,
                                               val settingsManager: SettingsManager,
                                               var schedulerProvider: SchedulerProvider) : BaseViewModel(application) {

    val searchTermMutableLiveData = MutableLiveData<String?>()
    val filterUpdatedLiveData = MutableLiveData<String>()
    var conversationSelectedItemList: MutableSet<String> = mutableSetOf()
    var prevConversationSwiped: MutableLiveData<MessageListItem?> = MutableLiveData(null)
    var currentConversationSwiped: MutableLiveData<MessageListItem?> = MutableLiveData(null)
    var isConnectEditModeEnabledLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    var isSelectAllCheckedLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    var connectEditModeViewStateLiveData: MutableLiveData<ConnectEditModeViewState> = MutableLiveData(buildEditModeViewState())
    var deleteIconClickedLiveData: MutableLiveData<Unit> = MutableLiveData()
    var updateReadStatusIconClickedLiveData: MutableLiveData<String> = MutableLiveData()
    private val _notificationEvent: MutableLiveData<NotificationEvent<Boolean>> = MutableLiveData()
    val notificationEvent: LiveData<NotificationEvent<Boolean>> = _notificationEvent
    var updateReadStatusResultLiveData: MutableLiveData<Pair<Boolean, String>> = MutableLiveData()
    var userRepoApiSmsStartedLiveData: MutableLiveData<Int> = MutableLiveData()
    var userRepoApiSmsFinishedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var originalList = listOf<MessageListItem>()
    private var editModeItemCount = 0
    private var initialEditModeItemCount = 0
    var ourUuid: String? = null
    var ourNumber: String? = null

    var selectedFilter: String = Enums.Platform.ConnectSmsFilter.ALL
        set(value) {
            if (field != value) {
                field = value

                listItemsLiveData.removeSource(smsConversationsLiveData)
                listItemsLiveData.addSource(smsConversationsLiveData) {
                    listItemsLiveData.value = it
                }
                filterUpdatedLiveData.value = value
            }
        }

    val listItemsLiveData: MediatorLiveData<PagingData<BaseListItem>> = MediatorLiveData()

    val mediator = SmsMessageRemoteMediator(smsManagementRepository, dbManager, sessionManager)

    @OptIn(ExperimentalPagingApi::class)
    private val smsConversationsLiveData: LiveData<PagingData<BaseListItem>> =
        Pager(
            config = PagingConfig(
                initialLoadSize = SmsMessageRemoteMediator.PAGE_SIZE,
                pageSize = SmsMessageRemoteMediator.PAGE_SIZE,
                prefetchDistance = SmsMessageRemoteMediator.PAGE_SIZE
            ),
            pagingSourceFactory = {
                when(selectedFilter) {
                    Enums.Platform.ConnectSmsFilter.ALL -> dbManager.allSmsMessagesPagingSource
                    else -> dbManager.getFilteredSmsMessagePagingSource(selectedFilter)
                }
            },
            remoteMediator = mediator
        ).liveData
            .map {
                it.filter { message ->
                    message.conversationId != null
                }.map { message ->
                    val listItem = MessageListItem(message, null, 0, draftMessage = null, isChecked = null)

                    message.groupId?.let { groupId ->
                        val messageStateList = dbManager.getMessageStateListInThread(groupId)
                        val messagesToUpdate: MutableList<DbMessageState> = ArrayList()
                        for (state in messageStateList) {
                            if (!TextUtils.equals(state.readStatus, Enums.SMSMessages.ReadStatus.READ)) {
                                messagesToUpdate.add(state)
                            }
                        }
                        listItem.unReadCount = messagesToUpdate.size
                        listItem.unreadMessagesIds = messagesToUpdate.map { it.messageId.orEmpty() }
                        listItem.isChecked = getMessageHistoryItemCheckedValue(groupId)
                        listItem.isSwipeActionEnabled = settingsManager.isSwipeActionsEnabled
                        ourNumber?.let { listItem.participants = message.getParticipantsList(it) }

                        val draftStatus = Enums.SMSMessages.SentStatus.DRAFT
                        val draftMessages = dbManager.getDraftMessagesFromConversationInThread(groupId, draftStatus)
                        listItem.draftMessage = draftMessages.lastOrNull()
                    }

                    listItem as BaseListItem
                }
            }
            .cachedIn(viewModelScope)

    init {
        listItemsLiveData.addSource(smsConversationsLiveData) { listItemsLiveData.value = it }
        ourUuid = sessionManager.currentUser?.userUuid
        ourNumber = getUserTelephoneNumber()
    }

    private fun getUserTelephoneNumber(): String{
        sessionManager.userDetails?.telephoneNumber?.let {userPhoneNumber->
            return CallUtil.getCountryCode() + userPhoneNumber
        }?: kotlin.run { return "" }
    }

    fun getNextivaContact(number: String?, contactCallback: (NextivaContact?) -> Unit) {
        val noContactReturnValue = NextivaContact(number, Enums.Contacts.ContactTypes.CONNECT_UNKNOWN)

        number?.let {
            mCompositeDisposable.add(
                dbManager.getConnectContactFromPhoneNumber(number)
                    .onErrorReturn { DbResponse(null) }
                    .subscribe { dbResponse: DbResponse<NextivaContact?>? ->
                        contactCallback(dbResponse?.value ?: noContactReturnValue)
                    })
        } ?: kotlin.run {
            contactCallback(noContactReturnValue)
        }
    }

    fun onSearchTermUpdated(searchTerm: String?) {
        searchTermMutableLiveData.value = searchTerm
    }

    fun doesSearchTermMatchParticipant(participant: SmsParticipant, searchTerm: String): Boolean {
        ourNumber?.let { ourNumber ->
            val searchTermNumberStripped = CallUtil.getStrippedPhoneNumber(searchTerm)
            val ourNumberStripped = CallUtil.getStrippedPhoneNumber(ourNumber)
            participant.phoneNumber?.nullIfEmpty()?.let { participantNumber ->
                val participantNumberStripped = CallUtil.getStrippedPhoneNumber(participantNumber)

                if (!participantNumberStripped.contains(ourNumberStripped)) {
                    if (participant.uiName?.contains(searchTerm, ignoreCase = true) == true) {
                        return true
                    }

                    if (searchTermNumberStripped.isNotEmpty() && !searchTerm.containsLetter()) {
                        if (participantNumberStripped.contains(searchTermNumberStripped, ignoreCase = true)) {
                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    fun doesSearchTermMatchTeam(team: SmsTeam, searchTerm: String): Boolean {
        if (team.uiName?.contains(searchTerm, ignoreCase = true) == true) {
            return true
        }
        team.teamPhoneNumber?.nullIfEmpty()?.let { teamNumber ->
            val searchTermNumberStripped = CallUtil.getStrippedPhoneNumber(searchTerm)
            val teamNumberStripped = CallUtil.getStrippedPhoneNumber(teamNumber)
            if (searchTermNumberStripped.isNotEmpty() && !searchTerm.containsLetter()) {
                if (teamNumberStripped.contains(searchTermNumberStripped)) {
                    return true
                }
            }
        }
        return false
    }

    fun isTeamSmsLicenseEnabled(): Boolean {
        return sessionManager.isTeamSmsLicenseEnabled
    }

    fun isTeamSmsEnabled(): Boolean {
        return sessionManager.isTeamSmsEnabled
    }

    fun getContactFromPhoneNumber(phoneNumber: String?): NextivaContact {
        var contact: NextivaContact? =
            dbManager.getConnectContactFromPhoneNumberInThread(phoneNumber).value

        if (contact == null) {
            contact = NextivaContact(phoneNumber, Enums.Contacts.ContactTypes.CONNECT_UNKNOWN)
        }

        return contact
    }


    // --------------------------------------------------------------------------------------------
    // Bulk functions for conversations
    // --------------------------------------------------------------------------------------------

    private fun getMessageHistoryItemCheckedValue(groupId: String?): Boolean? {
        return if (isConnectEditModeEnabledLiveData.value == true)
            ((isSelectAllCheckedLiveData.value == true && conversationSelectedItemList.contains(
                groupId
            )) || conversationSelectedItemList.contains(groupId)) else null
    }

    fun onEditModeEvent(isEnabled: Boolean) {
        isConnectEditModeEnabledLiveData.value = isEnabled
        mediator.updateEditModeState(isEnabled)
        clearSelectionList()
        if (!isEnabled) {
            isSelectAllCheckedLiveData.value = false
            updateEditModeViewState(
                selectAllCheckState = ToggleableState.Off,
                shouldShowBulkIcons = false
            )
        }
    }

    private fun clearSelectionList() {
        conversationSelectedItemList.clear()
        conversationSelectedItemList = mutableSetOf()
        isSelectAllCheckedLiveData.value = false
    }

    fun updateEditModeViewState(
        selectAllCheckState: ToggleableState? = null,
        shouldShowBulkIcons: Boolean? = null,
        itemCount: Int? = null
    ) {
        connectEditModeViewStateLiveData.value = connectEditModeViewStateLiveData.value?.copy(
            selectAllCheckState = selectAllCheckState
                ?: connectEditModeViewStateLiveData.value?.selectAllCheckState,
            shouldShowBulkUpdateActionIcons = shouldShowBulkIcons == true && sessionManager.isCommunicationsBulkUpdatesEnabled,
            shouldShowBulkDeleteActionIcons = shouldShowBulkIcons == true && sessionManager.isCommunicationsBulkDeletesEnabled,
            itemCountDescription = if (itemCount != null) {
                getCurrentItemCountDescription(itemCount)
            } else {
                connectEditModeViewStateLiveData.value?.itemCountDescription ?: ""
            }
        )
    }

    private fun buildEditModeViewState(): ConnectEditModeViewState {
        return ConnectEditModeViewState(
            itemCountDescription = getCurrentItemCountDescription(0),
            onSelectAllCheckedChanged = { cState -> onSelectAllCheckedChanged(cState) },
            onMarkUnReadIconClicked = { onUpdateReadStatusEvent(BulkActionsConversationData.MODIFICATION_STATUS_UNREAD) },
            onMarkReadIconClicked = { onUpdateReadStatusEvent(BulkActionsConversationData.MODIFICATION_STATUS_READ) },
            onDeleteIconClicked = { onDeleteEvent() },
            onDoneClicked = { onEditModeEvent(false) }
        )
    }

    private fun getCurrentItemCountDescription(itemCount: Int): String {
        return if (getTotalSelectedItemsCount() > 0) {
            nextivaApplication.getString(
                R.string.connect_item_count_description_selected,
                getTotalSelectedItemsCount().toString()
            )
        } else {
            ""
        }
    }

    private fun getTotalSelectedItemsCount() = conversationSelectedItemList.count()

    private fun onSelectAllCheckedChanged(currentState: ToggleableState) {
        val newStateToSet = when (currentState) {
            ToggleableState.On -> ToggleableState.Off
            ToggleableState.Indeterminate -> ToggleableState.Off
            ToggleableState.Off -> ToggleableState.On
        }
        isSelectAllCheckedLiveData.value = newStateToSet == ToggleableState.On
        updateEditModeViewState(
            selectAllCheckState = newStateToSet,
            shouldShowBulkIcons = (newStateToSet == ToggleableState.On),
            itemCount = editModeItemCount
        )
    }

    private fun onDeleteEvent() {
        if (isConnectEditModeEnabledLiveData.value == true && conversationSelectedItemList.size > 0) {
            deleteIconClickedLiveData.value = Unit
        }
    }

    private fun onUpdateReadStatusEvent(readStatus: String) {
        if (isConnectEditModeEnabledLiveData.value == true && conversationSelectedItemList.size > 0){
            updateReadStatusIconClickedLiveData.value = readStatus
        }
    }

    fun updateEditModeItemCount(itemCount: Int) {
        editModeItemCount = itemCount
        initialEditModeItemCount = itemCount
        updateEditModeViewState(
            itemCount = editModeItemCount,
            selectAllCheckState = determineSelectAllCheckState(
                getTotalSelectedItemsCount(),
                itemCount
            )
        )
    }

    private fun determineSelectAllCheckState(selectedItemCount: Int, itemCount: Int): ToggleableState {
        return when {
            (selectedItemCount > 0 && selectedItemCount == itemCount) -> ToggleableState.On
            selectedItemCount > 0 -> ToggleableState.Indeterminate
            else -> ToggleableState.Off
        }
    }

    fun performBulkActionOnCommunications(jobType: String, modificationStatus: String? = null ) {
        when (jobType) {
            BulkActionsConversationData.JOB_TYPE_DELETE -> deleteConversations()
            BulkActionsConversationData.JOB_TYPE_UPDATE -> {
                modificationStatus?.let {
                    bulkUpdateReadStatus(modificationStatus = it)
                }
            }
        }
    }

    private fun bulkUpdateReadStatus(modificationStatus: String) {
        val conversationsSelectedList = ArrayList(conversationSelectedItemList)
        userRepoApiSmsStartedLiveData.value = R.string.progress_updating
        mCompositeDisposable.add(
            conversationRepository.bulkUpdateConversations(
                getBulkActionCommunicationData(conversationsSelectedList, BulkActionsConversationData.JOB_TYPE_UPDATE, modificationStatus))
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    userRepoApiSmsFinishedLiveData.value = success
                    updateReadStatusResultLiveData.value = Pair(success, modificationStatus)
                    if (success) {
                        conversationsSelectedList.forEach { groupId ->
                            if (modificationStatus == BulkActionsConversationData.MODIFICATION_STATUS_READ) {
                                dbManager.updateReadStatusForGroupId(groupId)
                            } else {
                                val mostRecentMessageInConversation = dbManager.getMostRecentMessageFromConversationWithoutDraft(groupId, Enums.SMSMessages.SentStatus.DRAFT)
                                mostRecentMessageInConversation?.messageId?.let {
                                    dbManager.updateUnreadStatusForMessageId(it)
                                }
                            }
                        }
                        clearSelectionList()
                        updateEditModeViewState(
                            selectAllCheckState = ToggleableState.Off,
                            shouldShowBulkIcons = false,
                            itemCount = editModeItemCount
                        )
                    } else {
                        _notificationEvent.value = NotificationEvent(
                            status = success,
                            NotificationEvent.Event.SINGLE_READ_STATUS
                        )
                    }
                }
        )
    }

    fun updateReadStatusForSingleSms(groupId: String, status: String) {
        val conversationsSelectedList = arrayListOf(groupId)
        if (status == BulkActionsConversationData.MODIFICATION_STATUS_READ) {
            dbManager.updateReadStatusForGroupId(groupId)
        } else {
            val mostRecentMessageInConversation = dbManager.getMostRecentMessageFromConversationWithoutDraft(groupId, Enums.SMSMessages.SentStatus.DRAFT)
            mostRecentMessageInConversation?.messageId?.let {
                dbManager.updateUnreadStatusForMessageId(it)
            }
        }
        userRepoApiSmsStartedLiveData.value = R.string.progress_updating
        mCompositeDisposable.add(
            conversationRepository.bulkUpdateConversations(
                getBulkActionCommunicationData(conversationsSelectedList, BulkActionsConversationData.JOB_TYPE_UPDATE, status))
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    userRepoApiSmsFinishedLiveData.value = success
                    if (!success) {
                        if (status == BulkActionsConversationData.MODIFICATION_STATUS_READ) {
                            dbManager.updateUnreadStatusForGroupId(groupId)
                        } else {
                            val mostRecentMessageInConversation = dbManager.getMostRecentMessageFromConversationWithoutDraft(groupId, Enums.SMSMessages.SentStatus.DRAFT)
                            mostRecentMessageInConversation?.messageId?.let {
                                dbManager.updateReadStatusForMessageId(it)
                            }
                        }
                    }
                    _notificationEvent.value = NotificationEvent(
                        status = success,
                        NotificationEvent.Event.SINGLE_READ_STATUS
                    )
                }
        )
    }

    fun deleteConversations() {
        val conversationsSelectedList = ArrayList(conversationSelectedItemList)
        userRepoApiSmsStartedLiveData.value = R.string.progress_deleting
        mCompositeDisposable.add(
            conversationRepository.bulkDeleteConversations(
                getBulkActionCommunicationData(conversationsSelectedList, BulkActionsConversationData.JOB_TYPE_DELETE)) { _, _ -> }
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    userRepoApiSmsFinishedLiveData.value = success
                    _notificationEvent.value = NotificationEvent(
                        status = success,
                        NotificationEvent.Event.BULK_DELETE
                    )
                    if (success) {
                        editModeItemCount -= getTotalSelectedItemsCount()
                        clearSelectionList()
                        updateEditModeViewState(
                            selectAllCheckState = ToggleableState.Off,
                            shouldShowBulkIcons = false,
                            itemCount = editModeItemCount
                        )
                    }
                }
        )
    }

    fun deleteSingleConversation(groupId: String) {
        val conversationSelected = arrayListOf(groupId)
        userRepoApiSmsStartedLiveData.value = R.string.progress_deleting
        mCompositeDisposable.add(
            conversationRepository.bulkDeleteConversations(
                getBulkActionCommunicationData(conversationSelected, BulkActionsConversationData.JOB_TYPE_DELETE)) { _, _ -> }
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    userRepoApiSmsFinishedLiveData.value = success
                    _notificationEvent.value = NotificationEvent(
                        status = success,
                        NotificationEvent.Event.SINGLE_DELETE
                    )
                }
        )
    }

    fun checkForSelectAllStateChange(itemCount: Int) {
        val selectedCount = getTotalSelectedItemsCount()
        updateEditModeViewState(
            selectAllCheckState = determineSelectAllCheckState(selectedCount, itemCount),
            shouldShowBulkIcons = (selectedCount > 0),
            itemCount = if (selectedCount > 0) selectedCount else itemCount
        )
    }

    private fun getBulkActionCommunicationData(
        conversationsSelectedList: ArrayList<String>? = null,
        jobType: String,
        readStatus: String? = null
    ): BulkActionsConversationData {
        val channels = Channels()
        val modifications = readStatus?.takeIf { it.isNotEmpty() }?.let { Modifications(readStatus = it) }
        if (readStatus == MODIFICATION_STATUS_UNREAD) {
            val messagesList = mutableListOf<String>()
            conversationsSelectedList?.forEach {
                val mostRecentMessageInConversation = dbManager.getMostRecentMessageFromConversationWithoutDraft(it, Enums.SMSMessages.SentStatus.DRAFT)
                mostRecentMessageInConversation?.messageId?.let { messagesList.add(it) }
            }
            channels.SMS = SMS(identifierType = BulkActionsConversationData.IDENTIFIER_MESSAGE_ID, identifiers = ArrayList(messagesList) )
        } else {
            conversationsSelectedList?.takeIf { it.isNotEmpty() }?.let {
                channels.SMS = SMS(identifierType = BulkActionsConversationData.IDENTIFIER_GROUP_ID, identifiers = it)
            }
        }
        return BulkActionsConversationData(
            jobType = jobType,
            modifications = modifications,
            isSoftDelete = true,
            channels = channels
        )
    }

    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }
}