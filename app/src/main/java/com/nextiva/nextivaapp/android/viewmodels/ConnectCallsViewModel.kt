package com.nextiva.nextivaapp.android.viewmodels

import android.app.Activity
import android.app.Application
import android.text.TextUtils
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectCallHistoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.mediators.CallsRemoteMediator
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbCallLogEntry
import com.nextiva.nextivaapp.android.db.model.DbSession
import com.nextiva.nextivaapp.android.fragments.ConnectCallsFragment
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.BlockingNumberManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.CallTabItem
import com.nextiva.nextivaapp.android.models.CallsDbReturnModel
import com.nextiva.nextivaapp.android.models.ConnectEditModeViewState
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.Channels
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.Modifications
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VOICE
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailRatingBody
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConnectCallsViewModel @Inject constructor(application: Application,
                                                private val nextivaApplication: Application,
                                                val logManager: LogManager,
                                                val dbManager: DbManager,
                                                val callManager: CallManager,
                                                val blockingManager: BlockingNumberManager,
                                                val userRepository: UserRepository,
                                                val nextivaMediaPlayer: NextivaMediaPlayer,
                                                val sessionManager: SessionManager,
                                                val platformRepository: PlatformRepository,
                                                val callManagementRepository: CallManagementRepository,
                                                val schedulerProvider: SchedulerProvider,
                                                val conversationRepository: ConversationRepository,
                                                private val callsRemoteMediator: CallsRemoteMediator) : BaseViewModel(application) {

    var fetchingVoicemailDetailsStartedLiveData: LiveData<Int> = nextivaMediaPlayer.getFetchingVoicemailDetailsStartedLiveData()
    var fetchingVoicemailDetailsFinishedLiveData: LiveData<Boolean> = nextivaMediaPlayer.getFetchingVoicemailDetailsFinishedLiveData()
    var fetchingVoicemailFailedNoInternetLiveData: LiveData<Void> = nextivaMediaPlayer.getFetchingVoicemailFailedNoInternetLiveData()
    var newVoicemailCountLiveData: LiveData<DbSession?> = sessionManager.newVoicemailMessagesCountLiveData
    var newVoiceCallCountLiveData: LiveData<Int> = sessionManager.newVoiceCallMessagesCountLiveData
    var voiceCallVoicemailCountLiveDataList: LiveData<List<DbSession>> = sessionManager.voiceCallVoicemailCount
    var isConnectEditModeEnabledLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    var isSelectAllCheckedLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    var callHistorySelectedItemList: MutableSet<String> = mutableSetOf()
    var voicemailSelectedItemList: MutableSet<String> = mutableSetOf()
    var connectEditModeViewStateLiveData: MutableLiveData<ConnectEditModeViewState> = MutableLiveData(buildEditModeViewState())
    var deleteIconClickedLiveData: MutableLiveData<Unit> = MutableLiveData()
    var updateReadStatusIconClickedLiveData: MutableLiveData<String> = MutableLiveData()
    var communicationsDeleteResultLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var updateReadStatusResultLiveData: MutableLiveData<Pair<Boolean, String>> = MutableLiveData()
    var prevCallSwiped: MutableLiveData<BaseListItem?> = MutableLiveData(null)
    var currentCallSwiped: MutableLiveData<BaseListItem?> = MutableLiveData(null)
    var isSwipeActionsEnabled:MutableLiveData<Boolean?> = MutableLiveData(null)
    private var initialEditModeItemCount = 0
    var tabIndex: MutableLiveData<Int> = MutableLiveData(0)
    val blockedNumbersLiveData: LiveData<List<String>> = blockingManager.observeBlockedNumbers().asLiveData()
    private var userRepoApiCallStartedLiveData: MutableLiveData<Int> = MutableLiveData()
    private var userRepoApiCallFinishedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var editModeItemCount = 0
    private var isSwipeAction = false
    lateinit var tabsList : List<CallTabItem>
    val query = MutableStateFlow("")

    private val allTabPageSize = 100
    private val allTabPrefetchDistance = 60
    private val allTabInitialLoadSize = 100
    private val otherTabsPageSize = 50
    private val otherTabsPrefetchDistance = 60
    private val otherTabsInitialLoadSize = 100

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    val allListItemsLiveData = query.flatMapLatest { query ->
        Pager(
            config = PagingConfig(
                pageSize = allTabPageSize,
                prefetchDistance = allTabPrefetchDistance,
                initialLoadSize = allTabInitialLoadSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                dbManager.getCallLogAndVoicemailPagingSource(query)
            },
            remoteMediator = callsRemoteMediator
        ).flow.map { pagingData -> itemsMapper.invoke(query, pagingData) }
            .cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    val voiceMailItemsLiveData = query.flatMapLatest { query ->
        Pager(
            config = PagingConfig(
                pageSize = otherTabsPageSize,
                prefetchDistance = otherTabsPrefetchDistance,
                initialLoadSize = otherTabsInitialLoadSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                dbManager.getVoicemailPagingSource(query)
            },
            remoteMediator = callsRemoteMediator
        ).flow.map { pagingData -> itemsMapper.invoke(query, pagingData) }
            .cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    val missedItemsLiveData = query.flatMapLatest { query ->
        Pager(
            config = PagingConfig(
                pageSize = otherTabsPageSize,
                prefetchDistance = otherTabsPrefetchDistance,
                initialLoadSize = otherTabsInitialLoadSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                dbManager.getCallLogPagingSource(
                    listOf(Enums.Calls.CallTypes.MISSED),
                    query
                )
            },
            remoteMediator = callsRemoteMediator
        ).flow.map { pagingData -> itemsMapper.invoke(query, pagingData) }
            .cachedIn(viewModelScope)
    }

    private val itemsMapper = object : ((String, PagingData<CallsDbReturnModel>) -> PagingData<BaseListItem>) {
        override fun invoke(
            query: String,
            pagingData: PagingData<CallsDbReturnModel>
        ): PagingData<BaseListItem> {
            return pagingData.map { model ->
                when {
                    model.callLogId != null -> ConnectCallHistoryListItem(
                        model.callLogEntry!!,
                        query,
                        getCallHistoryItemCheckedValue(model.callLogId),
                        blockingManager.isNumberBlocked(model.callLogEntry?.phoneNumber.orEmpty())
                    )

                    model.messageId != null -> VoicemailListItem(
                        model.voicemail!!,
                        getVoicemailItemCheckedValue(model.messageId),
                        blockingManager.isNumberBlocked(model.voicemail?.formattedPhoneNumber.orEmpty())
                    )

                    else -> ConnectCallHistoryListItem(
                        DbCallLogEntry().toCallLogEntry(null, null),
                        query,
                        getCallHistoryItemCheckedValue(model.callLogId),
                        blockingManager.isNumberBlocked(model.callLogEntry?.phoneNumber.orEmpty())
                    )
                }
            }
        }
    }

    private fun getCallHistoryItemCheckedValue(callLogId: String?): Boolean? {
        return if (isConnectEditModeEnabledLiveData.value == true)
            ((isSelectAllCheckedLiveData.value == true && callHistorySelectedItemList.contains(
                callLogId
            )) || callHistorySelectedItemList.contains(callLogId)) else null
    }

    private fun getVoicemailItemCheckedValue(messageId: String?): Boolean? {
        return if (isConnectEditModeEnabledLiveData.value == true)
            ((isSelectAllCheckedLiveData.value == true && voicemailSelectedItemList.contains(
                messageId
            )) || voicemailSelectedItemList.contains(messageId)) else null
    }

    init {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History ViewModel init. " +
                "All Tab Page Size: [$allTabPageSize], All Tab Prefetch Distance: [$allTabPrefetchDistance], All Tab Initial Load: [$allTabInitialLoadSize], " +
                "Other Tab Page Size: [$otherTabsPageSize], Other Tab Prefetch Distance: [$otherTabsPrefetchDistance], Other Tab Initial Load Size [$otherTabsInitialLoadSize]")
        createTabsList()
    }

    private fun createTabsList() {
        tabsList = listOf(
            CallTabItem(
                CallTabId = ConnectCallsFragment.CallTabs.ALL_TAB_ID.id,
                ConnectCallsFilterType = Enums.Platform.ConnectCallsFilter.ALL,
                CallTabTitle = nextivaApplication.getString(R.string.call_history_list_filter_all),
                CallTabIcon = null,
                CallTabContentDescription = nextivaApplication.getString(R.string.call_history_list_action_tab_item_all),
                CallTabBadgeNumber = null
            ),
            CallTabItem(
                CallTabId = ConnectCallsFragment.CallTabs.MISSED_TAB_ID.id,
                ConnectCallsFilterType = Enums.Platform.ConnectCallsFilter.MISSED,
                CallTabTitle = nextivaApplication.getString(R.string.call_history_list_filter_missed),
                CallTabIcon = R.drawable.ic_call_missed_grey,
                CallTabContentDescription = nextivaApplication.getString(R.string.call_history_list_action_tab_item_missed),
                CallTabBadgeNumber = null
            ),
            CallTabItem(
                CallTabId = ConnectCallsFragment.CallTabs.VOICEMAIL_TAB_ID.id,
                ConnectCallsFilterType = Enums.Platform.ConnectCallsFilter.VOICEMAIL,
                CallTabTitle = nextivaApplication.getString(R.string.connect_calls_voicemail_filter),
                CallTabIcon = R.drawable.ic_voicemail_tab_icon,
                CallTabContentDescription = nextivaApplication.getString(R.string.call_history_list_action_tab_item_voicemail),
                CallTabBadgeNumber = null
            )
        )
    }

    fun onSearchTermUpdated(searchTerm: String?) {
        query.update { searchTerm.orEmpty() }
    }

    fun resetLiveData() {
        userRepoApiCallStartedLiveData = MutableLiveData()
        userRepoApiCallFinishedLiveData = MutableLiveData()
        deleteIconClickedLiveData = MutableLiveData()
        updateReadStatusIconClickedLiveData = MutableLiveData()
        communicationsDeleteResultLiveData = MutableLiveData()
        updateReadStatusResultLiveData = MutableLiveData()
    }

    fun updateVoicemailRating(voiceMailId: String, voicemailRatingBody: VoicemailRatingBody) {
        mCompositeDisposable.add(
            platformRepository.updateVoicemailsRating(voiceMailId, voicemailRatingBody)
                .subscribe({
                }, {
                    dbManager.updateVoicemailRating("null", voiceMailId)
                })
        )
    }

    fun pauseCurrentPlayingVoicemail() {
        nextivaMediaPlayer.pausePlaying()
    }

    fun placeCall(
        activity: Activity,
        @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
        nextivaContact: NextivaContact) {

        val participantInfo = nextivaContact.getParticipantInfo(null)
        participantInfo.dialingServiceType = Enums.Service.DialingServiceTypes.NONE

        callManager.makeCall(activity, analyticsScreenName, participantInfo, mCompositeDisposable)

    }

    fun placeCall(
        activity: Activity,
        @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
        number: String?) {

        number?.let {
            mCompositeDisposable.add(
                dbManager.getContactFromPhoneNumber(number)
                    .onErrorReturn { DbResponse(NextivaContact("")) }
                    .subscribe { dbResponse: DbResponse<NextivaContact?>? ->
                        val participantInfo = ParticipantInfo(numberToCall = number,
                            dialingServiceType = Enums.Service.DialingServiceTypes.NONE)

                        if (dbResponse != null && dbResponse.value != null) {
                            participantInfo.contactId = dbResponse.value?.userId
                            participantInfo.displayName = dbResponse.value?.uiName
                        }

                        callManager.makeCall(
                            activity,
                            analyticsScreenName,
                            participantInfo,
                            mCompositeDisposable
                        )
                    })
        }
    }

    fun markVoicemailUnread(messageId: String) {
        if( !isSwipeAction) {
            userRepoApiCallStartedLiveData.value = R.string.progress_processing
        }

        mCompositeDisposable.add(
            conversationRepository.markVoicemailUnread(messageId.split("/").last())
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    userRepoApiCallFinishedLiveData.value = success
                    if(isSwipeAction){
                        updateReadStatusResultLiveData.value = Pair(success, BulkActionsConversationData.MODIFICATION_STATUS_UNREAD)
                    }
                })

        updateNotificationsCount()
    }

    fun markVoicemailRead(messageId: String) {
        if (!isSwipeAction) {
            userRepoApiCallStartedLiveData.value = R.string.progress_processing
        }

        mCompositeDisposable.add(
            conversationRepository.markVoicemailRead(messageId.split("/").last())
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    userRepoApiCallFinishedLiveData.value = success
                    if(isSwipeAction){
                        updateReadStatusResultLiveData.value = Pair(success, BulkActionsConversationData.MODIFICATION_STATUS_READ)
                    }
                })

        updateNotificationsCount()
    }

    fun markCallRead(callMessageId: String) {
        conversationRepository.markCallRead(callMessageId)
            .subscribe(object : DisposableSingleObserver<Boolean>() {
                override fun onSuccess(success: Boolean) {
                    if (success) {
                        dbManager.markCallLogEntryRead(callMessageId).subscribe()
                    }
                }

                override fun onError(e: Throwable) {
                    LogUtil.e("Error marking call read ${e.localizedMessage}")
                }
            })

    }

    fun deleteVoicemail(messageId: String) {
        userRepoApiCallStartedLiveData.value = R.string.progress_deleting

        mCompositeDisposable.add(
            conversationRepository.deleteVoicemail(messageId.split("/").last())
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    userRepoApiCallFinishedLiveData.value = success
                    communicationsDeleteResultLiveData.value = success
                    if (success) {
                        nextivaMediaPlayer.deleteAudioFile(nextivaApplication, messageId)
                    }
                })

        updateNotificationsCount()
    }

    fun getNextivaContact(nextivaContact: NextivaContact?, number: String?, contactCallback: (NextivaContact?) -> Unit) {
        if (nextivaContact != null) {
            contactCallback(nextivaContact)

        } else number?.let {
            mCompositeDisposable.add(
                dbManager.getConnectContactFromPhoneNumber(number)
                    .onErrorReturn { DbResponse(NextivaContact("")) }
                    .subscribe { dbResponse: DbResponse<NextivaContact?>? ->
                        contactCallback(dbResponse?.value)
                    })
        }
    }

    fun getUserRepoApiCallStartedLiveData(): LiveData<Int> {
        return userRepoApiCallStartedLiveData
    }

    fun getUserRepoApiCallFinishedLiveData(): LiveData<Boolean> {
        return userRepoApiCallFinishedLiveData
    }

    fun getSortedGroupValue(groupValue: String): String {
        val sortingGroupValueList = ArrayList(listOf(*groupValue.trim { it <= ' ' }.replace("\\s".toRegex(), "").split(",".toRegex()).toTypedArray()))
        sortingGroupValueList.sortWith { s, t1 -> s.trim { it <= ' ' }.toLong().compareTo(t1.trim { it <= ' ' }.toLong()) }
        return TextUtils.join(",", sortingGroupValueList).trim { it <= ' ' }
    }

    private fun updateNotificationsCount(){
        sessionManager.updateNotificationsCount(conversationRepository, nextivaApplication)
    }

    fun onEditModeEvent(isEnabled: Boolean) {
        isConnectEditModeEnabledLiveData.value = isEnabled
        clearSelectionList()
        callsRemoteMediator.updateEditModeState(isEnabled)
        if (!isEnabled) {
            isSelectAllCheckedLiveData.value = false
            updateEditModeViewState(
                selectAllCheckState = ToggleableState.Off,
                shouldShowBulkIcons = false
            )
        }
    }

    private fun onDeleteEvent() {
        if (voicemailSelectedItemList.size > 0 || callHistorySelectedItemList.size > 0) {
            deleteIconClickedLiveData.value = Unit
        }
    }

    private fun onUpdateReadStatusEvent(readStatus: String) {
        if (voicemailSelectedItemList.size > 0 || callHistorySelectedItemList.size > 0){
            updateReadStatusIconClickedLiveData.value = readStatus
        }
    }

    private fun getCurrentItemCountDescription() =
        if (getTotalSelectedItemsCount() > 0) {
            nextivaApplication.getString(
                R.string.connect_item_count_description_selected,
                getTotalSelectedItemsCount().toString()
            )
        } else {
            ""
        }

    private fun updateEditModeViewState(
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
                getCurrentItemCountDescription()
            } else {
                connectEditModeViewStateLiveData.value?.itemCountDescription ?: ""
            }
        )
    }

    private fun buildEditModeViewState() =
        ConnectEditModeViewState(
            itemCountDescription = getCurrentItemCountDescription(),
            onSelectAllCheckedChanged = { currentState ->
                onSelectAllCheckedChanged(currentState)
            },
            onMarkUnReadIconClicked = {onUpdateReadStatusEvent(BulkActionsConversationData.MODIFICATION_STATUS_UNREAD)},
            onMarkReadIconClicked = {onUpdateReadStatusEvent(BulkActionsConversationData.MODIFICATION_STATUS_READ) },
            onDeleteIconClicked = { onDeleteEvent() },
            onDoneClicked = { onEditModeEvent(false) }
        )

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

    fun checkForSelectAllStateChange(itemCount: Int) {
        val selectedCount = when (tabIndex.value) {
            Enums.Platform.ConnectCallsFilter.ALL -> getTotalSelectedItemsCount()
            Enums.Platform.ConnectCallsFilter.MISSED -> callHistorySelectedItemList.count()
            Enums.Platform.ConnectCallsFilter.VOICEMAIL -> voicemailSelectedItemList.count()
            else -> 0
        }
        updateEditModeViewState(
            selectAllCheckState = determineSelectAllCheckState(selectedCount, itemCount),
            shouldShowBulkIcons = (selectedCount > 0),
            itemCount = if (selectedCount > 0) selectedCount else itemCount
        )
    }

    private fun clearSelectionList(){
        callHistorySelectedItemList = mutableSetOf()
        voicemailSelectedItemList = mutableSetOf()
    }

    fun performBulkActionOnCommunications(jobType: String, modificationStatus: String? = null ){
        val callLogList = if (tabIndex.value == Enums.Platform.ConnectCallsFilter.ALL || tabIndex.value == Enums.Platform.ConnectCallsFilter.MISSED)
            ArrayList(callHistorySelectedItemList) else null

        val voicemailList = if (tabIndex.value == Enums.Platform.ConnectCallsFilter.ALL || tabIndex.value == Enums.Platform.ConnectCallsFilter.VOICEMAIL)
            ArrayList(voicemailSelectedItemList) else null

        when (jobType) {
            BulkActionsConversationData.JOB_TYPE_DELETE -> {
                deleteItems(callLogSelectedList = callLogList, voicemailSelectedList = voicemailList)
            }
            BulkActionsConversationData.JOB_TYPE_UPDATE -> {
                modificationStatus?.let {
                    bulkUpdateReadStatus(callLogSelectedList = callLogList, voicemailSelectedList = voicemailList, modificationStatus = it)
                }
            }
        }
    }

    private fun prepareDeleteOperation() {
        userRepoApiCallStartedLiveData.value = R.string.progress_deleting
    }

    private fun deleteItems(
        callLogSelectedList: ArrayList<String>? = null,
        voicemailSelectedList: ArrayList<String>? = null
    ) {
        prepareDeleteOperation()
        mCompositeDisposable.add(
            conversationRepository.bulkDeleteConversations(
                getBulkDeleteCommunicationData(callLogSelectedList, voicemailSelectedList, BulkActionsConversationData.JOB_TYPE_DELETE)
            ){context, messageIds -> nextivaMediaPlayer.deleteAudioFiles(context,messageIds) }
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    userRepoApiCallFinishedLiveData.value = success
                    communicationsDeleteResultLiveData.value = success
                    if (success) {
                        updateNotificationsCount()
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

    private fun bulkUpdateReadStatus(
        callLogSelectedList: ArrayList<String>? = null,
        voicemailSelectedList: ArrayList<String>? = null,
        modificationStatus: String
    ){
        userRepoApiCallStartedLiveData.value = R.string.progress_updating
        mCompositeDisposable.add(
            conversationRepository.bulkUpdateConversations(
                getBulkDeleteCommunicationData(callLogSelectedList, voicemailSelectedList, BulkActionsConversationData.JOB_TYPE_UPDATE, modificationStatus))
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    userRepoApiCallFinishedLiveData.value = success
                    updateReadStatusResultLiveData.value = Pair(success, modificationStatus)
                    if (success) {
                        val readStatus = if (modificationStatus === BulkActionsConversationData.MODIFICATION_STATUS_READ) 1 else 0
                        if(callLogSelectedList?.isNotEmpty() == true ){
                            dbManager.bulkUpdateCallLogsReadStatus(readStatus, callLogSelectedList).subscribe()
                        }
                        if (voicemailSelectedList?.isNotEmpty() == true ){
                            dbManager.bulkUpdateVoicemailReadStatus(readStatus, voicemailSelectedList).subscribe()
                        }
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

    private fun getTotalSelectedItemsCount() =
        callHistorySelectedItemList.count().plus(voicemailSelectedItemList.count())

    private fun getBulkDeleteCommunicationData(
        callLogSelectedList: ArrayList<String>? = null,
        voicemailSelectedList: ArrayList<String>? = null,
        jobType: String,
        readStatus: String? = null
    ): BulkActionsConversationData {
        val channels = Channels()

        callLogSelectedList?.takeIf { it.isNotEmpty() }?.let {
            channels.VOICE = VOICE(identifierType = BulkActionsConversationData.IDENTIFIER_MESSAGE_ID, identifiers = it)
        }

        voicemailSelectedList?.takeIf { it.isNotEmpty() }?.let {
            channels.VOICEMAIL = VOICE(identifierType = BulkActionsConversationData.IDENTIFIER_MESSAGE_ID, identifiers = it)
        }

        val modifications = readStatus?.takeIf { it.isNotEmpty() }?.let { Modifications(readStatus = it) }

        return BulkActionsConversationData(
            jobType = jobType,
            modifications = modifications,
            isSoftDelete = true,
            channels = channels
        )
    }

    fun updateEditModeItemCount(itemCount: Int) {
        editModeItemCount = itemCount
        updateEditModeViewState(
            selectAllCheckState = determineSelectAllCheckState(
                getTotalSelectedItemsCount(),
                itemCount
            ), itemCount = editModeItemCount
        )
        initialEditModeItemCount = itemCount
    }

    fun onAdapterItemCountChanged(adapterItemCount: Int) {
        if (adapterItemCount > initialEditModeItemCount || editModeItemCount == 0){
            updateEditModeItemCount(adapterItemCount)
        }
    }

    private fun determineSelectAllCheckState(
        selectedItemCount: Int,
        itemCount: Int
    ): ToggleableState =
        when {
            (selectedItemCount > 0 && selectedItemCount == itemCount) -> ToggleableState.On
            selectedItemCount > 0 -> ToggleableState.Indeterminate
            else -> ToggleableState.Off
        }

    fun getGroupId(conversationDetails: SmsConversationDetails): String? {
        return dbManager.getGroupIdFrom(conversationDetails.getConversationId())
    }
    // --------------------------------------------------------------------------------------------
    // Swipe Functions
    // --------------------------------------------------------------------------------------------

    fun callSwipedActions(callLogId: String, jobType: String, modificationStatus: String? = null) {
        if (tabIndex.value == Enums.Platform.ConnectCallsFilter.ALL || tabIndex.value == Enums.Platform.ConnectCallsFilter.MISSED){
            isSwipeAction = true
            when (jobType) {
                BulkActionsConversationData.JOB_TYPE_DELETE -> {
                    val callLogList = ArrayList<String>()
                    callLogList.add(callLogId)
                    deleteSingleItem(callLogSelectedList = callLogList, voicemailSelectedList = null)
                }
                BulkActionsConversationData.JOB_TYPE_UPDATE -> {
                    modificationStatus?.let {
                        updateCallLogReadStatus(callLogId, it)
                    }
                }
            }
        }
    }

    fun deleteSingleVoiceMail(messageId: String){
        if (tabIndex.value == Enums.Platform.ConnectCallsFilter.ALL || tabIndex.value == Enums.Platform.ConnectCallsFilter.VOICEMAIL){
            val voicemailList = ArrayList<String>()
            voicemailList.add(messageId)
            isSwipeAction = true
            deleteSingleItem(callLogSelectedList = null, voicemailSelectedList = voicemailList)
        }
    }

    fun voiceMailSwipedMarkAsReadUnread(messageId: String, isRead: Boolean) {
        this.isSwipeAction = true
        if (isRead) {
            markVoicemailUnread(messageId)
        } else {
            markVoicemailRead(messageId)
            markCallRead(messageId)
        }
    }

    fun isSwipeAction(): Boolean{
        return isSwipeAction
    }

    fun setIsSwipeAction(isSwipeAction: Boolean){
        this.isSwipeAction = isSwipeAction
    }

    private fun deleteSingleItem(
        callLogSelectedList: ArrayList<String>? = null,
        voicemailSelectedList: ArrayList<String>? = null
    ) {
        prepareDeleteOperation()
        mCompositeDisposable.add(
            conversationRepository.bulkDeleteConversations(
                getDeleteSingleCommunicationData(callLogSelectedList = callLogSelectedList, voicemailSelectedList = voicemailSelectedList)
            ){context, messageIds -> nextivaMediaPlayer.deleteAudioFiles(context,messageIds) }
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    userRepoApiCallFinishedLiveData.value = success
                    communicationsDeleteResultLiveData.value = success
                    if (success) {
                        updateNotificationsCount()
                    }
                }
        )
    }

    private fun getDeleteSingleCommunicationData(
        callLogSelectedList: ArrayList<String>? = null,
        voicemailSelectedList: ArrayList<String>? = null,
    ): BulkActionsConversationData {
        val channels = Channels()

        callLogSelectedList?.takeIf { it.isNotEmpty() }?.let {
            channels.VOICE = VOICE(identifierType = BulkActionsConversationData.IDENTIFIER_MESSAGE_ID, identifiers = it)
        }

        voicemailSelectedList?.takeIf { it.isNotEmpty() }?.let {
            channels.VOICEMAIL = VOICE(identifierType = BulkActionsConversationData.IDENTIFIER_MESSAGE_ID, identifiers = it)
        }

        return BulkActionsConversationData(
            jobType = BulkActionsConversationData.JOB_TYPE_DELETE,
            modifications = null,
            isSoftDelete = true,
            channels = channels
        )
    }

    private fun updateCallLogReadStatus(callLogId: String, readStatus: String) {
        if (readStatus === BulkActionsConversationData.MODIFICATION_STATUS_READ) {
            mCompositeDisposable.add(
                conversationRepository.markCallRead(callLogId)
                    .observeOn(schedulerProvider.ui())
                    .subscribe { success ->
                        if (success) {
                            dbManager.markCallLogEntryRead(callLogId).subscribe()
                        } else {
                            userRepoApiCallFinishedLiveData.value = success
                            updateReadStatusResultLiveData.value = Pair(false, BulkActionsConversationData.MODIFICATION_STATUS_READ)
                        }
                    }
            )
        } else {
            mCompositeDisposable.add(
                conversationRepository.markCallUnread(callLogId)
                    .observeOn(schedulerProvider.ui())
                    .subscribe { success ->
                        if (success) {
                            dbManager.markCallLogEntryUnread(callLogId).subscribe()
                        } else {
                            userRepoApiCallFinishedLiveData.value = success
                            updateReadStatusResultLiveData.value = Pair(false, BulkActionsConversationData.MODIFICATION_STATUS_UNREAD)
                        }
                    }
            )
        }
    }
}