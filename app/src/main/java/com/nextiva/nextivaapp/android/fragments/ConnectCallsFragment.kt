package com.nextiva.nextivaapp.android.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.ContentViewCallback
import com.nextiva.nextivaapp.android.ConnectContactDetailsActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectCallHistoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.PagedConnectListAdapter
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.Screen
import com.nextiva.nextivaapp.android.constants.RequestCodes
import com.nextiva.nextivaapp.android.core.common.ui.TabListComposeView
import com.nextiva.nextivaapp.android.databinding.FragmentConnectCallsBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbSession
import com.nextiva.nextivaapp.android.features.calls.CallsGeneralRecyclerViewFragment
import com.nextiva.nextivaapp.android.features.messaging.view.ConversationActivity
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetContactDetailsFragment
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetDeleteConfirmation
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetDialerFragment
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager.PermissionGrantedCallback
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailRatingBody
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.RecyclerViewFastScroller
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.util.extensions.orTrue
import com.nextiva.nextivaapp.android.util.extensions.orZero
import com.nextiva.nextivaapp.android.view.ConnectSearchView
import com.nextiva.nextivaapp.android.view.CustomSnackbar
import com.nextiva.nextivaapp.android.view.SnackStyle
import com.nextiva.nextivaapp.android.view.compose.ConnectEditModeView
import com.nextiva.nextivaapp.android.viewmodels.ConnectCallsViewModel
import com.nextiva.nextivaapp.android.viewmodels.ConnectMainViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
@SuppressLint("NotifyDataSetChanged")
class ConnectCallsFragment(private val searchViewFocusChangeCallback: ((Boolean) -> Unit)?) :
    CallsGeneralRecyclerViewFragment(), SearchView.OnQueryTextListener, ContentViewCallback,
    CallManager.ProcessParticipantInfoCallBack {

    constructor() : this(null)
    constructor(
        searchViewFocusChangeCallback: ((Boolean) -> Unit)?, startTabId: CallTabs
    ) : this(searchViewFocusChangeCallback) {
        this@ConnectCallsFragment.currentTabId = startTabId
    }

    companion object {
        const val MAX_BADGE_CHAR_LIMIT: Int = 3
    }

    enum class CallTabs(val id: Int) {
        ALL_TAB_ID(0),
        MISSED_TAB_ID(1),
        VOICEMAIL_TAB_ID(2)
    }

    // --------------------------------------------------------------------------------------------
    // Variables region
    // --------------------------------------------------------------------------------------------

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var connectionStateManager: ConnectionStateManager

    @Inject
    lateinit var callManager: CallManager

    @Inject
    lateinit var logManager: LogManager

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var avatarManager: AvatarManager

    @Inject
    lateinit var nextivaMediaPlayer: NextivaMediaPlayer

    private var currentTabId: CallTabs = CallTabs.ALL_TAB_ID
    private lateinit var recentText: TextView
    private lateinit var connectCallsActionView: LinearLayout
    private lateinit var searchView: ConnectSearchView
    private lateinit var callsFragmentHeaderView: ComposeView
    private lateinit var editIcon: AppCompatTextView
    private lateinit var linearFragmentCalls: LinearLayout
    private var searchTextChangeCountDownTimer: CountDownTimer? = null
    private var isDisableScroll = false
    private var searchViewHasFocus = false
    private var dataPreviouslyLoaded = false
    private var newCallType: Int = RequestCodes.NewCall.NEW_CALL_NONE
    private var itemCountUpdateDelay: Long = 100L
    private var pagedAdapterAll: PagedConnectListAdapter? = null
    private var pagedAdapterVoice: PagedConnectListAdapter? = null
    private var pagedAdapterMissed: PagedConnectListAdapter? = null

    private val viewModel: ConnectCallsViewModel by lazy {
        val owner = activity ?: this
        ViewModelProvider(owner)[ConnectCallsViewModel::class.java]
    }

    private val mainActivityViewModel: ConnectMainViewModel by lazy {
        ViewModelProvider(requireActivity())[ConnectMainViewModel::class.java]
    }

    private val userRepoApiCallStartedObserver = Observer<Int> { stringId ->
        mDialogManager.showProgressDialog(requireActivity(), analyticScreenName, stringId)
    }

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Observers region
    // --------------------------------------------------------------------------------------------

    private val userRepoApiCallFinishedObserver = Observer<Boolean> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History API call finished observed.")

        mDialogManager.dismissProgressDialog()
        stopRefreshing()
    }

    private val fetchingVoicemailFailedNoInternetObserver = Observer<Void> {
        if (!connectionStateManager.isInternetConnected) {
            logManager.logToFile(Enums.Logging.STATE_FAILURE, "Call History API call failed because of no internet.")
            mDialogManager.dismissProgressDialog()
            stopRefreshing()

            mDialogManager.showDialog(
                requireActivity(),
                R.string.error_no_internet_title,
                R.string.error_no_internet_voicemail,
                R.string.general_ok
            ) { _, _ -> }
        }
    }

    private val newVoicemailCountObserver = Observer<DbSession?> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Updating unread voicemail count UI badge to [${it?.value?.toInt() ?: 0}]")
        setTabBadgeCount(CallTabs.VOICEMAIL_TAB_ID, it?.value?.toInt() ?: 0)
    }

    private val newVoiceCallCountObserver = Observer<Int> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Updating missed call count UI badge to [${it}]")
        setTabBadgeCount(CallTabs.MISSED_TAB_ID, it)
    }

    private val totalVoiceCallVoicemailCountObserver = Observer<List<DbSession>?> {
        var totalCount = 0

        if (it != null) {
            for (session in it) {
                if (session.value != null) totalCount += session.value?.toInt() ?: 0
            }
        }

        logManager.logToFile(Enums.Logging.STATE_INFO, "Updating all count count UI badge to [${totalCount}]")
        setTabBadgeCount(CallTabs.ALL_TAB_ID, totalCount)
    }

    private val editModeObserver = Observer<Boolean> { isEditModeEnabled ->
        logManager.logToFile(Enums.Logging.STATE_INFO, "Setting call history edit mode to [${isEditModeEnabled}]")
        updateEditModeUi(isEditModeEnabled)
        viewModel.updateEditModeItemCount(if (isEditModeEnabled)getCurrentAdapter().itemCount else 0)
        refreshAdapter(isEditModeEnabled)
    }

    private val deleteIconClickObserver = Observer<Unit> {
        activity?.supportFragmentManager?.let {
            logManager.logToFile(Enums.Logging.STATE_INFO, "Will show Call History bulk delete dialog.")
            BottomSheetDeleteConfirmation.newInstance(title = resources.getString(R.string.connect_calls_delete_communications_title),
                subtitle = resources.getString(R.string.connect_calls_delete_message_sub_title),
                deleteAction = {
                    viewModel.performBulkActionOnCommunications(
                        BulkActionsConversationData.JOB_TYPE_DELETE
                    )
                },
                cancelAction = {
                    logManager.logToFile(Enums.Logging.STATE_INFO, "Call History bulk delete dialog dismissed.")
                }).show(it, null)
        }
    }

    private val updateReadStatusClickObserver = Observer<String> { readStatus ->
        activity?.supportFragmentManager?.let {
            logManager.logToFile(Enums.Logging.STATE_INFO, "Call History bulk read status update set to [$readStatus]")
            viewModel.performBulkActionOnCommunications(BulkActionsConversationData.JOB_TYPE_UPDATE, readStatus)
        }
    }

    private val onTabChangeObserver = Observer<Int> { tabIndex ->
        currentTabId = tabIndex.let { CallTabs.entries[it] }
        when (tabIndex) {
            Enums.Platform.ConnectCallsFilter.ALL -> {
                logManager.logToFile(Enums.Logging.STATE_INFO, "Call History All tab selected.")
                recentText.text = getString(R.string.connect_calls_recent_calls)
                mRecyclerView.adapter = pagedAdapterAll
            }
            Enums.Platform.ConnectCallsFilter.MISSED -> {
                logManager.logToFile(Enums.Logging.STATE_INFO, "Call History Missed tab selected.")
                recentText.text = getString(R.string.connect_calls_recent_missed)
                mRecyclerView.adapter = pagedAdapterMissed
            }
            Enums.Platform.ConnectCallsFilter.VOICEMAIL -> {
                logManager.logToFile(Enums.Logging.STATE_INFO, "Call History Voicemail tab selected.")
                recentText.text = getString(R.string.connect_calls_recent_voicemail)
                mRecyclerView.adapter = pagedAdapterVoice
            }
        }
    }

    private val onCommunicationsDeletedObserver = Observer<Boolean> { isDeleteSuccessful ->
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History deletion result observed [$isDeleteSuccessful].")
        showSnackBar(
            isJobSuccessFul = isDeleteSuccessful,
            jobType = BulkActionsConversationData.JOB_TYPE_DELETE,
            isSwipeAction = viewModel.isSwipeAction()
        )
        viewModel.setIsSwipeAction(false)
    }

    private val onReadStatusUpdatedObserver = Observer<Pair<Boolean, String>> { newValue ->
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History Read status update observed [${newValue.first}].")

        if (!newValue.first) {
            showSnackBar(
                isJobSuccessFul = false,
                jobType = BulkActionsConversationData.JOB_TYPE_UPDATE,
                isSwipeAction = viewModel.isSwipeAction())
        }
        viewModel.setIsSwipeAction(false)

        getCurrentAdapter().notifyDataSetChanged()
    }

    private val onSelectAllCheckedObserver = Observer<Boolean> { isSelectAllChecked ->
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History Select All checked [$isSelectAllChecked].")
        updateCheckBoxSelectedState(isSelectAllChecked)
    }

    private val blockOrUnblockNumberObserver = Observer<List<String>> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History Block or Unblock finished observed.")
        refreshDataAfterBlockingAction()
    }

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Lifecycle methods region
    // --------------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.tabIndex.value = currentTabId.id
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.let { bindViews(it) }

        setComposeView()
        showOrHideEditIcon()

        viewModel.newVoicemailCountLiveData.observe(viewLifecycleOwner, newVoicemailCountObserver)
        viewModel.newVoiceCallCountLiveData.observe(viewLifecycleOwner, newVoiceCallCountObserver)
        viewModel.voiceCallVoicemailCountLiveDataList.observe(
            viewLifecycleOwner,
            totalVoiceCallVoicemailCountObserver
        )
        viewModel.isConnectEditModeEnabledLiveData.observe(viewLifecycleOwner, editModeObserver)
        viewModel.tabIndex.observe(viewLifecycleOwner, onTabChangeObserver)
        viewModel.deleteIconClickedLiveData.observe(viewLifecycleOwner, deleteIconClickObserver)
        viewModel.updateReadStatusIconClickedLiveData.observe(viewLifecycleOwner, updateReadStatusClickObserver)
        viewModel.isSelectAllCheckedLiveData.observe(viewLifecycleOwner, onSelectAllCheckedObserver)
        viewModel.communicationsDeleteResultLiveData.observe(viewLifecycleOwner, onCommunicationsDeletedObserver)
        viewModel.updateReadStatusResultLiveData.observe(viewLifecycleOwner, onReadStatusUpdatedObserver)
        viewModel.blockedNumbersLiveData.observe(viewLifecycleOwner, blockOrUnblockNumberObserver)
        exitEditModeIfActive()

        val recyclerViewFastScroller =
            RecyclerViewFastScroller(requireActivity(), settingsManager, true)
        recyclerViewFastScroller.setRecyclerView(mRecyclerView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchingVoicemailDetailsStartedLiveData.observe(
            viewLifecycleOwner,
            userRepoApiCallStartedObserver
        )
        viewModel.fetchingVoicemailDetailsFinishedLiveData.observe(
            viewLifecycleOwner,
            userRepoApiCallFinishedObserver
        )
        viewModel.fetchingVoicemailFailedNoInternetLiveData.observe(
            viewLifecycleOwner,
            fetchingVoicemailFailedNoInternetObserver
        )
        viewModel.getUserRepoApiCallStartedLiveData()
            .observe(viewLifecycleOwner, userRepoApiCallStartedObserver)
        viewModel.getUserRepoApiCallFinishedLiveData()
            .observe(viewLifecycleOwner, userRepoApiCallFinishedObserver)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.allListItemsLiveData.collectLatest {
                        pagedAdapterAll?.submitData(it)
                    }
                }
                launch {
                    viewModel.missedItemsLiveData.collectLatest {
                        pagedAdapterMissed?.submitData(it)
                    }
                }
                launch {
                    viewModel.voiceMailItemsLiveData.collectLatest {
                        pagedAdapterVoice?.submitData(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    pagedAdapterAll?.loadStateFlow?.collectLatest {
                        onAdapterDataChanged(it, pagedAdapterAll)
                    }
                }

                launch {
                    pagedAdapterMissed?.loadStateFlow?.collectLatest {
                        onAdapterDataChanged(it, pagedAdapterMissed)
                    }
                }

                launch {
                    pagedAdapterVoice?.loadStateFlow?.collectLatest {
                        onAdapterDataChanged(it, pagedAdapterVoice)
                    }
                }
            }
        }

        if (requireActivity().intent != null) {
            newCallType = requireActivity().intent.getIntExtra(
                Constants.Calls.PARAMS_NEW_CALL_TYPE,
                RequestCodes.NewCall.NEW_CALL_NONE
            )
        }

        setButtonClickListener {
            BottomSheetDialerFragment().show(requireActivity().supportFragmentManager, null)
        }

        pagedAdapterAll = PagedConnectListAdapter(requireActivity(), this, calendarManager, dbManager, sessionManager, avatarManager, settingsManager, nextivaMediaPlayer)
        pagedAdapterMissed = PagedConnectListAdapter(requireActivity(), this, calendarManager, dbManager, sessionManager, avatarManager, settingsManager, nextivaMediaPlayer)
        pagedAdapterVoice = PagedConnectListAdapter(requireActivity(), this, calendarManager, dbManager, sessionManager, avatarManager, settingsManager, nextivaMediaPlayer)
        mRecyclerView.adapter = pagedAdapterAll
        mRecyclerView.itemAnimator = null

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager: LinearLayoutManager? =
                    LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val firstVisible: Int = layoutManager?.findFirstVisibleItemPosition() ?: 0
                isDisableScroll = firstVisible > 4
            }
        })

        viewModel.isSwipeActionsEnabled.value = settingsManager.isSwipeActionsEnabled
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseCurrentPlayingVoicemail()
        getCurrentAdapter().notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetLiveData()
    }

    override fun onResume() {
        super.onResume()
        if (this::editIcon.isInitialized) {
            showOrHideEditIcon()
        }
        enableOrDisableSwipeActions()
        mSwipeRefreshLayout?.isRefreshing = true
        dataPreviouslyLoaded = false
    }

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Private functions region
    // --------------------------------------------------------------------------------------------

    private fun updateEditModeUi(isEditModeEnabled: Boolean) {
        setComposeView()
        val visibility = if (isEditModeEnabled) View.GONE else View.VISIBLE
        searchView.visibility = visibility
        connectCallsActionView.visibility = visibility
        modifyCheckBoxState(isEditModeEnabled)
        mSwipeRefreshLayout?.isEnabled = !isEditModeEnabled
        mainActivityViewModel.onEditModeClicked(isEditModeEnabled)
    }

    private fun refreshDataAfterBlockingAction() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch { pagedAdapterAll?.submitData(viewModel.allListItemsLiveData.first()) }
            launch { pagedAdapterVoice?.submitData(viewModel.voiceMailItemsLiveData.first()) }
            launch { pagedAdapterMissed?.submitData(viewModel.missedItemsLiveData.first()) }
        }
    }

    private fun refreshAdapter(isEditModeEnabled: Boolean) {
        if (!isEditModeEnabled) {
            pagedAdapterAll?.refresh()
            pagedAdapterMissed?.refresh()
            pagedAdapterVoice?.refresh()
        }
    }

    private fun showSnackBar(
        isJobSuccessFul: Boolean,
        jobType: String,
        isSwipeAction: Boolean = false
    ) {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History will show snackbar.")

        val customSnackbar = CustomSnackbar.make(
            requireView(),
            BaseTransientBottomBar.LENGTH_LONG, this,
            snackStyle = SnackStyle.fromBoolean(isJobSuccessFul)
        )
        when (jobType) {
            BulkActionsConversationData.JOB_TYPE_DELETE ->
                if (isJobSuccessFul) {
                    customSnackbar.setText(getString(
                        if (isSwipeAction)
                            R.string.connect_calls_single_delete_done_message
                        else
                            R.string.connect_calls_delete_done_message)
                    )
                    customSnackbar.setFontAwesomeIcon(getString(R.string.fa_trash_alt))
                } else {
                    customSnackbar.setFontAwesomeIcon(
                        getString(R.string.fa_times_circle)
                    )
                    customSnackbar.setText(
                        getString(R.string.connect_sms_message_deleted_failed),
                        Gravity.START,
                    )
                }
            BulkActionsConversationData.JOB_TYPE_UPDATE -> {
                if (!isJobSuccessFul) {
                    customSnackbar.setFontAwesomeIcon(
                        getString(R.string.fa_times_circle)
                    )
                    customSnackbar.setText(
                        getString(R.string.connect_sms_message_updated_failed),
                        Gravity.START
                    )
                    customSnackbar.disableCloseButton()
                }
            }
        }
        customSnackbar.setCloseAction {
            customSnackbar.dismiss()
        }

        customSnackbar.show()
    }

    private fun modifyCheckBoxState(isEditModeEnabled: Boolean) {
        getCurrentAdapter().apply {
            snapshot().items.forEach { item ->
                when (item) {
                    is ConnectCallHistoryListItem -> {
                        item.isChecked = if (isEditModeEnabled) false else null
                    }
                    is VoicemailListItem -> {
                        item.isChecked = if (isEditModeEnabled) false else null
                    }
                }
            }
            notifyDataSetChanged()
        }
    }

    private fun getCurrentAdapter() = mRecyclerView?.adapter as PagedConnectListAdapter

    private fun updateCheckBoxSelectedState(isSelectAllChecked: Boolean) {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History update check box selected state [$isSelectAllChecked].")

        getCurrentAdapter().apply {
            snapshot().items.forEach {item->
                when(item){
                    is ConnectCallHistoryListItem -> {
                        item.callEntry.callLogId?.let { id ->
                            if (isSelectAllChecked) {
                                viewModel.callHistorySelectedItemList.add(id)
                            } else {
                                viewModel.callHistorySelectedItemList.remove(id)
                            }
                        }
                        item.isChecked = isSelectAllChecked
                    }
                    is VoicemailListItem -> {
                        item.voicemail.messageId?.let { id ->
                            if (isSelectAllChecked) {
                                viewModel.voicemailSelectedItemList.add(id)
                            } else {
                                viewModel.voicemailSelectedItemList.remove(id)
                            }
                        }
                        item.isChecked = isSelectAllChecked
                    }
                }
            }
            notifyDataSetChanged()
        }
    }

    private fun setTabBadgeCount(callTab: CallTabs, count: Int) {
        for (tab in viewModel.tabsList) {
            if (tab.CallTabId == callTab.id) {
                if (tab.CallTabBadgeNumber != count) {
                    tab.CallTabBadgeNumber = count
                    setComposeView()
                }
                break
            }
        }
    }

    private fun exitEditModeIfActive() {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History exiting edit mode.")

        if (viewModel.isConnectEditModeEnabledLiveData.value == true){
            viewModel.onEditModeEvent(false)
        }
    }

    private suspend fun onAdapterDataChanged(
        loadState: CombinedLoadStates,
        pagedAdapter: PagedConnectListAdapter?
    ) {
        if (loadState.refresh is LoadState.NotLoading) {
            if (viewModel.isConnectEditModeEnabledLiveData.value == true) {
                delay(itemCountUpdateDelay)
                viewModel.onAdapterItemCountChanged(
                    mRecyclerView?.adapter?.itemCount ?: 0
                )
            }
        }

        if (pagedAdapter?.itemCount.orZero() > 0) {
            showRecyclerViewIfApplies()
        } else if (viewModel.query.value.isNotEmpty().orFalse()) {
            showEmptySearchResultsState()
        } else if (viewModel.query.lastOrNull()?.isEmpty().orTrue()){
            showEmptyState()
        }

        val isDataFullyLoaded = loadState.append is LoadState.NotLoading

        if (isDataFullyLoaded) {
            mSwipeRefreshLayout?.isRefreshing = false
            showRecyclerViewIfApplies()
        } else {
            if (!dataPreviouslyLoaded) {
                mSwipeRefreshLayout?.isRefreshing = true
                dataPreviouslyLoaded = true
            }
        }
    }

    private fun showRecyclerViewIfApplies() {
        if (mRecyclerView.visibility == View.GONE) {
            showRecyclerView()
        }
    }

    private fun bindViews(view: View) {
        val binding = FragmentConnectCallsBinding.bind(view)

        callsFragmentHeaderView = binding.composeView
        recentText = binding.connectCallsRecentText
        searchView = binding.connectCallsSearchView
        editIcon = binding.connectCallsEditIcon
        connectCallsActionView = binding.connectCallsActionLayout
        searchView.setOnQueryTextListener(this)
        linearFragmentCalls = binding.fragmentCallsLinear

        editIcon.setOnClickListener {
            viewModel.onEditModeEvent(isEnabled = true)
        }
        searchView.setOnFocusChangedCallback { hasFocus ->
            searchViewFocusChangeCallback?.invoke(hasFocus)
            if (sessionManager.isCommunicationsBulkDeletesEnabled || sessionManager.isCommunicationsBulkUpdatesEnabled) {
                searchViewHasFocus = hasFocus
                editIcon.visibility = if (hasFocus) View.GONE else View.VISIBLE
            }
        }
    }

    private fun processCallInfo(
        participantInfo: ParticipantInfo
    ) {
        callManager.processParticipantInfo(
            requireActivity(),
            ScreenName.NEW_CALL_CONTACTS_LIST,
            participantInfo,
            null,
            mCompositeDisposable,
            this
        )
    }

    private fun setComposeView() {
        callsFragmentHeaderView.setContent {
            val isEditModeEnabled by viewModel.isConnectEditModeEnabledLiveData.observeAsState(false)
            if (isEditModeEnabled) {
                ConnectEditModeView(connectEditModeViewStateLiveDate = viewModel.connectEditModeViewStateLiveData)
            } else {
                TabListComposeView(viewModel.tabsList, currentTabId) {
                    viewModel.tabIndex.value = it
                }
            }
        }
    }

    private fun showOrHideEditIcon() {
        editIcon.visibility = if (sessionManager.isCommunicationsBulkDeletesEnabled || sessionManager.isCommunicationsBulkUpdatesEnabled) {
            logManager.logToFile(Enums.Logging.STATE_INFO, "Call History showing edit icon.")
            if (searchViewHasFocus) View.GONE else View.VISIBLE
        } else {
            logManager.logToFile(Enums.Logging.STATE_INFO, "Call History hiding edit icon.")
            View.GONE
        }
    }

    private fun voiceMailReadAction(listItem: VoicemailListItem) {
        listItem.voicemail.actualVoiceMailId?.let { messageId ->
            if (listItem.voicemail.isRead == true) {
                logManager.logToFile(Enums.Logging.STATE_INFO, "Call History will mark voicemail unread [$messageId].")
                viewModel.markVoicemailUnread(messageId)

            } else {
                logManager.logToFile(Enums.Logging.STATE_INFO, "Will mark call/voicemail read [$messageId].")
                viewModel.markVoicemailRead(messageId)
                viewModel.markCallRead(messageId)
            }
        }
    }

    private fun updateSwipedConversations(listItem: BaseListItem) {
        viewModel.prevCallSwiped.value = viewModel.currentCallSwiped.value
        viewModel.prevCallSwiped.value?.forceChangeState = true
        viewModel.currentCallSwiped.value = listItem
        viewModel.currentCallSwiped.value?.forceChangeState = false
        notifyItemChanged(viewModel.prevCallSwiped.value)
        notifyItemChanged(viewModel.currentCallSwiped.value)
    }


    private fun notifyItemChanged(listItem: BaseListItem?) {
        val adapter = getCurrentAdapter()
        getIndexForListItem(listItem)?.let { index ->
            when(val item = adapter.peek(index)) {
                is VoicemailListItem, is ConnectCallHistoryListItem -> {
                    item.apply {
                        forceChangeState = listItem?.forceChangeState ?: forceChangeState?.not() ?: true
                    }
                }
            }
            adapter.notifyItemChanged(index)
        }
    }

    private fun getIndexForListItem(listItem: BaseListItem?): Int? {
        listItem ?: return null

        getCurrentAdapter().itemCount.let { itemCount ->
            var listItemId: String? = null
            when(listItem){
                is ConnectCallHistoryListItem -> {
                    listItemId = listItem.callEntry.callLogId
                }

                is VoicemailListItem -> {
                    listItemId = listItem.voicemail.messageId
                }
            }
            var parsedIndexItemId: String? = null
            var position : Int? = null
            for (index in 0 until itemCount) {
                when(getCurrentAdapter().peek(index)){
                    is VoicemailListItem -> {
                        parsedIndexItemId = (getCurrentAdapter().peek(index) as VoicemailListItem).voicemail.messageId
                        position = index
                    }

                    is ConnectCallHistoryListItem -> {
                        parsedIndexItemId = (getCurrentAdapter().peek(index) as ConnectCallHistoryListItem).callEntry.callLogId
                        position = index
                    }
                }
                if (listItemId == parsedIndexItemId) {
                    return position
                }
            }
        }
        return null
    }

    private fun enableOrDisableSwipeActions() {
        viewModel.isSwipeActionsEnabled.value?.let {
            if(viewModel.isSwipeActionsEnabled.value != settingsManager.isSwipeActionsEnabled){
                lifecycleScope.launch {
                    viewModel.isSwipeActionsEnabled.value = settingsManager.isSwipeActionsEnabled
                    delay(500L)
                    getCurrentAdapter().notifyDataSetChanged()
                }
            }
        }
    }

    private fun resetValuesForSwiping() {
        viewModel.prevCallSwiped.value = null
        viewModel.currentCallSwiped.value = null
    }

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallManager.ProcessCallInfoCallBack Methods
    // --------------------------------------------------------------------------------------------
    override fun onParticipantInfoProcessed(
        activity: Activity,
        @Screen analyticsScreenName: String,
        participantInfo: ParticipantInfo,
        retrievalNumber: String?,
        compositeDisposable: CompositeDisposable
    ) {
        logManager.logToFile(
            Enums.Logging.STATE_INFO,
            R.string.log_message_success_with_message,
            activity.getString(R.string.log_message_processing_call, participantInfo.toString())
        )
        val callback = PermissionGrantedCallback {
            val data = Intent()
            data.putExtra(Constants.EXTRA_PARTICIPANT_INFO, participantInfo)
            data.putExtra(
                Constants.EXTRA_RETRIEVAL_NUMBER,
                retrievalNumber
            )
            activity.setResult(Activity.RESULT_OK, data)
            activity.finish()
        }
        if (participantInfo.callType == Enums.Sip.CallTypes.VIDEO) {
            permissionManager.requestVideoCallPermission(
                activity,
                analyticsScreenName,
                callback,
                null
            )
        } else if (participantInfo.callType == Enums.Sip.CallTypes.VOICE) {
            permissionManager.requestVoiceCallPermission(
                activity,
                analyticsScreenName,
                callback
            )
        }
    }

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Override functions region
    // --------------------------------------------------------------------------------------------

    override fun getLayoutId(): Int {
        return R.layout.fragment_connect_calls
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return R.id.connect_calls_swipe_refresh_layout
    }

    override fun getRecyclerViewId(): Int {
        return R.id.connect_calls_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return ScreenName.CONNECT_CALLS_LIST
    }

    override fun getConnectEmptyStateViewId(): Int {
        return R.id.connect_calls_empty_state_view
    }

    override fun getConnectEmptySearchResultsViewId(): Int {
        return R.id.connect_calls_no_results_empty_state_view
    }

    override fun fetchItemList(forceRefresh: Boolean) {
        if (mIsRefreshing) {
            return
        }

        (mRecyclerView.adapter as? PagedConnectListAdapter)?.refresh()
    }

    override fun onVoicemailCallButtonClicked(listItem: VoicemailListItem) {
        super.onVoicemailCallButtonClicked(listItem)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onVoicemailCallButtonClicked.")

        activity?.let { activity ->
            if (!listItem.strippedNumber.isNullOrEmpty()) {
                viewModel.placeCall(activity, analyticScreenName, listItem.strippedNumber)

            } else listItem.nextivaContact?.let { contact ->
                if (listItem.nextivaContact?.phoneNumbers?.isNotEmpty() == true) {
                    viewModel.placeCall(activity, analyticScreenName, contact)
                }
            }
        }
    }

    override fun onPositiveRatingItemClicked(voicemailListItem: VoicemailListItem) {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onPositiveRatingItemClicked.")
        val voicemailRatingBody =
            VoicemailRatingBody("add", "/voicemail/transcriptRating", "POSITIVE")
        voicemailListItem.voicemail.actualVoiceMailId?.let {
            viewModel.updateVoicemailRating(it, voicemailRatingBody)
        }
    }

    override fun onNegativeRatingItemClicked(voicemailListItem: VoicemailListItem) {
        val voicemailRatingBody =
            VoicemailRatingBody("add", "/voicemail/transcriptRating", "NEGATIVE")
        voicemailListItem.voicemail.actualVoiceMailId?.let {
            viewModel.updateVoicemailRating(it, voicemailRatingBody)
        }
    }

    override fun onVoicemailReadButtonClicked(listItem: VoicemailListItem) {
        super.onVoicemailReadButtonClicked(listItem)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onVoicemailReadButtonClicked.")
        voiceMailReadAction(listItem)
    }

    override fun onVoicemailDeleteButtonClicked(listItem: VoicemailListItem) {
        super.onVoicemailDeleteButtonClicked(listItem)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onVoicemailDeleteButtonClicked.")

        listItem.voicemail.actualVoiceMailId?.let { messageId ->
            mDialogManager.showDialog(
                requireActivity(),
                0,
                R.string.voicemail_list_delete_dialog_description,
                R.string.general_delete,
                { _, _ -> viewModel.deleteVoicemail(messageId) },
                R.string.general_cancel,
                { _, _ -> })
        }
    }

    override fun onVoicemailContactButtonClicked(listItem: VoicemailListItem) {
        super.onVoicemailContactButtonClicked(listItem)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onVoicemailContactButtonClicked.")

        listItem.nextivaContact?.let {
            requireActivity().startActivity(
                ConnectContactDetailsActivity.newIntent(
                    requireActivity(),
                    it
                )
            )

        }
    }

    override fun onVoicemailSmsButtonClicked(listItem: VoicemailListItem) {
        super.onVoicemailSmsButtonClicked(listItem)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onVoicemailSmsButtonClicked.")

        listItem.strippedNumber?.let { number ->
            var ourNumber = ""
            var groupValue: String? = CallUtil.getFormattedNumber(number)

            viewModel.sessionManager.userDetails?.let { userDetails ->
                userDetails.telephoneNumber?.let { telephoneNumber ->
                    ourNumber =
                        CallUtil.getCountryCode() + CallUtil.getStrippedPhoneNumber(telephoneNumber)
                    groupValue = "$groupValue,$ourNumber"
                }
            }

            val smsConversationDetails = SmsConversationDetails(
                viewModel.getSortedGroupValue(groupValue ?: ""),
                listOf(
                    SmsParticipant(
                        CallUtil.getFormattedNumber(number).trim(),
                        listItem.nextivaContact?.userId
                    )
                ),
                ourNumber,
                sessionManager.currentUser?.userUuid ?: ""
            )

            if(smsConversationDetails.groupId.isNullOrEmpty()) {
                smsConversationDetails.groupId = viewModel.getGroupId(smsConversationDetails)
            }

            requireActivity().startActivity(
                ConversationActivity.newIntent(
                    requireActivity(),
                    smsConversationDetails,
                    false,
                    Enums.Chats.ConversationTypes.SMS,
                    Enums.Chats.ChatScreens.CONVERSATION
                )
            )
        }
    }

    override fun onVoicemailListItemClicked(listItem: VoicemailListItem, position: Int) {
        super.onVoicemailListItemClicked(listItem, position)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onVoicemailListItemClicked")

        if (viewModel.isConnectEditModeEnabledLiveData.value == true) {

            val clickedItem = getCurrentAdapter().snapshot().items.firstOrNull { ((it as? VoicemailListItem)?.voicemail?.messageId) == listItem.voicemail.messageId }
            (clickedItem as? VoicemailListItem)?.apply {
                isChecked = isChecked?.not()
                listItem.voicemail.messageId?.let { callLogId->
                    if (viewModel.voicemailSelectedItemList.contains(callLogId)) {
                        viewModel.voicemailSelectedItemList.remove(callLogId)
                    } else {
                        viewModel.voicemailSelectedItemList.add(callLogId)
                    }
                }

            }
            viewModel.checkForSelectAllStateChange(getCurrentAdapter().itemCount)
            getCurrentAdapter().notifyItemChanged(position)
        }
    }

    override fun onConnectCallHistoryListItemClicked(
        listItem: ConnectCallHistoryListItem,
        position: Int
    ) {
        super.onConnectCallHistoryListItemClicked(listItem, position)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onConnectCallHistoryListItemClicked.")

        if (viewModel.isConnectEditModeEnabledLiveData.value == true) {
            val clickedItem = getCurrentAdapter().snapshot().items.firstOrNull { (it as? ConnectCallHistoryListItem)?.callEntry?.callLogId == listItem.callEntry.callLogId }
            (clickedItem as? ConnectCallHistoryListItem)?.apply {
                isChecked = isChecked?.not()
                listItem.callEntry.callLogId?.let { callLogId->
                    if (viewModel.callHistorySelectedItemList.contains(callLogId)){
                        viewModel.callHistorySelectedItemList.remove(callLogId)
                    }else{
                        viewModel.callHistorySelectedItemList.add(callLogId)
                    }
                }

            }
            viewModel.checkForSelectAllStateChange(getCurrentAdapter().itemCount)
            getCurrentAdapter().notifyItemChanged(position)

        }else {
            if (newCallType == RequestCodes.NewCall.NEW_CALL_NONE) {
                viewModel.getNextivaContact(null, listItem.callEntry.phoneNumber) {
                    BottomSheetContactDetailsFragment(listItem.callEntry, it).show(
                        requireActivity().supportFragmentManager,
                        null
                    )
                }
            } else {
                val phoneNumber = listItem.callEntry.phoneNumber ?: ""

                val participantInfo = ParticipantInfo(numberToCall = phoneNumber,
                    dialingServiceType = Enums.Service.DialingServiceTypes.VOIP)

                if (newCallType == RequestCodes.NewCall.TRANSFER_REQUEST_CODE || newCallType == RequestCodes.NewCall.CONFERENCE_REQUEST_CODE) {
                    participantInfo.callType = Enums.Sip.CallTypes.VOICE
                }

                processCallInfo(participantInfo)
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        startRefreshing()
        viewModel.onSearchTermUpdated(query)
        searchView.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchTextChangeCountDownTimer?.cancel()
        searchTextChangeCountDownTimer = object : CountDownTimer(750, 750) {
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                viewModel.onSearchTermUpdated(newText)
            }

        }.start()
        return false
    }

    override fun animateContentIn(delay: Int, duration: Int) {
    }

    override fun animateContentOut(delay: Int, duration: Int) {
    }

    override fun onShortSwipe(listItem: BaseListItem) {
        super.onShortSwipe(listItem)
        var itemId: String? = null
        var prevItemId: String? = null
        when(listItem){
            is ConnectCallHistoryListItem -> {
                listItem.callEntry.callLogId?.let { callLogId ->
                    itemId = callLogId
                    prevItemId = (viewModel.currentCallSwiped.value as? ConnectCallHistoryListItem)?.callEntry?.callLogId
                }
            }
            is VoicemailListItem -> {
                listItem.voicemail.messageId?.let { messageId ->
                    itemId = messageId
                    prevItemId = (viewModel.currentCallSwiped.value as? VoicemailListItem)?.voicemail?.messageId
                }
            }
        }
        itemId?.let {
            if(itemId != prevItemId){
                updateSwipedConversations(listItem)
            }
        }
    }

    override fun onCallHistorySwipedItemDelete(listItem: ConnectCallHistoryListItem) {
        super.onCallHistorySwipedItemDelete(listItem)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onCallHistorySwipedItemDelete")

        listItem.callEntry.callLogId?.let { callLogId ->
            if (settingsManager.isShowDialogToDeleteSmsEnabled) {
                activity?.supportFragmentManager?.let { fm ->
                    BottomSheetDeleteConfirmation.newInstance(
                        title = resources.getString(R.string.connect_calls_delete_communication_title),
                        subtitle = resources.getString(R.string.connect_sms_delete_communication_subtitle),
                        showShowAgainCheckbox = true,
                        deleteAction = {
                            viewModel.callSwipedActions(callLogId,BulkActionsConversationData.JOB_TYPE_DELETE)
                        },
                        onShowAgainDialogChanged = { settingsManager.isShowDialogToDeleteSmsEnabled = !it },
                        cancelAction = {
                            getCurrentAdapter().notifyDataSetChanged()
                        }
                    ).show(fm, null)
                }
            } else {
                viewModel.callSwipedActions(callLogId,BulkActionsConversationData.JOB_TYPE_DELETE)
            }
        }
    }

    override fun onCallHistorySwipedItemMarkAsReadOrUnread(listItem: ConnectCallHistoryListItem) {
        super.onCallHistorySwipedItemMarkAsReadOrUnread(listItem)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onCallHistorySwipedItemMarkAsReadOrUnread.")

        listItem.callEntry.callLogId?.let { callLogId ->
            val status = if(listItem.callEntry.isRead) BulkActionsConversationData.MODIFICATION_STATUS_UNREAD else BulkActionsConversationData.MODIFICATION_STATUS_READ
            viewModel.callSwipedActions(callLogId,BulkActionsConversationData.JOB_TYPE_UPDATE, status)
            getCurrentAdapter().notifyDataSetChanged()
            resetValuesForSwiping()
        }
    }

    override fun onVoicemailSwipedDeleteItem(listItem: VoicemailListItem) {
        super.onVoicemailSwipedDeleteItem(listItem)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onVoicemailSwipedDeleteItem.")

        listItem.voicemail.actualVoiceMailId?.let { messageId ->
            if(settingsManager.isShowDialogToDeleteSmsEnabled) {
                activity?.supportFragmentManager?.let { fm ->
                    BottomSheetDeleteConfirmation.newInstance(
                        title = resources.getString(R.string.connect_calls_delete_communication_title),
                        subtitle = resources.getString(R.string.connect_sms_delete_communication_subtitle),
                        showShowAgainCheckbox = true,
                        deleteAction = {
                            viewModel.deleteSingleVoiceMail(messageId)
                        },
                        onShowAgainDialogChanged = { settingsManager.isShowDialogToDeleteSmsEnabled = !it },
                        cancelAction = {
                            getCurrentAdapter().notifyDataSetChanged()
                        }
                    ).show(fm, null)
                }
            } else {
                viewModel.deleteSingleVoiceMail(messageId)
            }
        }
    }

    override fun onVoicemailSwipedItemMarkAsReadOrUnread(listItem: VoicemailListItem) {
        super.onVoicemailSwipedItemMarkAsReadOrUnread(listItem)
        logManager.logToFile(Enums.Logging.STATE_INFO, "Call History onVoicemailSwipeditemMarkAsReadOrUnread.")

        listItem.voicemail.actualVoiceMailId?.let { messageId ->
            listItem.voicemail.isRead?.let {
                viewModel.voiceMailSwipedMarkAsReadUnread(messageId, it)
                getCurrentAdapter().notifyDataSetChanged()
                resetValuesForSwiping()
            }
        }
    }
    // --------------------------------------------------------------------------------------------
}