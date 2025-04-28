package com.nextiva.nextivaapp.android.features.messaging.view

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.ContentViewCallback
import com.nextiva.nextivaapp.android.ConnectContactDetailsActivity
import com.nextiva.nextivaapp.android.ConnectNewTextActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.FragmentConnectSmsListBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.messaging.MessagingGeneralRecyclerViewFragment
import com.nextiva.nextivaapp.android.features.messaging.helpers.NotificationEvent
import com.nextiva.nextivaapp.android.features.messaging.helpers.SmsTitleHelper
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.MessageListViewModel
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.PagedMessagingListAdapter
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetDeleteConfirmation
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetSmsDetails
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetSmsFilterFragment
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData.Companion.MODIFICATION_STATUS_READ
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData.Companion.MODIFICATION_STATUS_UNREAD
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.RecyclerViewFastScroller
import com.nextiva.nextivaapp.android.util.extensions.orZero
import com.nextiva.nextivaapp.android.util.extensions.smoothSnapToPosition
import com.nextiva.nextivaapp.android.view.ConnectFilterView
import com.nextiva.nextivaapp.android.view.ConnectSearchView
import com.nextiva.nextivaapp.android.view.CustomSnackbar
import com.nextiva.nextivaapp.android.view.SnackStyle
import com.nextiva.nextivaapp.android.view.compose.ConnectEditModeView
import com.nextiva.nextivaapp.android.viewmodels.ConnectMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class MessageListFragment(private val searchViewFocusChangeCallback: ((Boolean) -> Unit)?) :
    MessagingGeneralRecyclerViewFragment(), SearchView.OnQueryTextListener, ContentViewCallback {

    constructor() : this(null)

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var mConnectionStateManager: ConnectionStateManager

    @Inject
    lateinit var nextivaMediaPlayer: NextivaMediaPlayer

    @Inject
    lateinit var smsTitleHelper: SmsTitleHelper

    @Inject
    lateinit var mAvatarManager: AvatarManager

    @Inject
    lateinit var mSettingsManager: SettingsManager

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var mSessionManager: SessionManager


    private lateinit var connectSmsActionView: LinearLayout
    private lateinit var searchView: ConnectSearchView
    private lateinit var messageFragmentHeaderView: ComposeView
    private lateinit var recentTextView: TextView
    private lateinit var filter: ConnectFilterView
    private lateinit var editIcon: AppCompatTextView

    private val viewModel: MessageListViewModel by lazy {
        val owner = activity ?: this
        ViewModelProvider(owner)[MessageListViewModel::class.java]
    }

    private val mainActivityViewModel: ConnectMainViewModel by lazy {
        val owner = activity ?: this
        ViewModelProvider(owner)[ConnectMainViewModel::class.java]
    }

    private var adapterDataObserver: RecyclerView.AdapterDataObserver? = null
    private var pagedAdapter: PagedMessagingListAdapter? = null
    private val delayToRefreshCounter = 500L
    private var conversationIdOpened: String? = null
    private var searchViewHasFocus = false
    private var totalUnreadMessages = 0

    private val searchTermUpdatedObserver = Observer<String?> {
        refreshAdapter()
    }

    private val allListItemObserver = Observer<PagingData<BaseListItem>> { listItems ->
        if (viewModel.searchTermMutableLiveData.value.isNullOrEmpty()) {
            pagedAdapter?.submitData(viewLifecycleOwner.lifecycle, listItems)
            registerAdapterDataObserver()
        }
    }

    private fun registerAdapterDataObserver() {
        adapterDataObserver?.let { pagedAdapter?.unregisterAdapterDataObserver(it) }
        adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (positionStart == 0) {
                    mRecyclerView.smoothSnapToPosition(0)
                }
            }
        }
        adapterDataObserver?.let { pagedAdapter?.registerAdapterDataObserver(it) }
    }

    private val unreadMessagesObserver = Observer<Int> {
        if (it > totalUnreadMessages) {
            mRecyclerView.postDelayed({
                mRecyclerView.smoothSnapToPosition(0) }, delayToRefreshCounter)
        }
        totalUnreadMessages = it
    }

    private val deleteIconClickObserver = Observer<Unit> {
        activity?.supportFragmentManager?.let {
            BottomSheetDeleteConfirmation.newInstance(
                    title = resources.getString(R.string.connect_calls_delete_communications_title),
                    subtitle = resources.getString(R.string.connect_calls_delete_message_sub_title),
                    deleteAction = { viewModel.performBulkActionOnCommunications(BulkActionsConversationData.JOB_TYPE_DELETE) },
                    cancelAction = { }
            ).show(it, null)
        }
    }

    private val updateReadStatusClickObserver = Observer<String> { readStatus ->
        if (mConnectionStateManager.isInternetConnected) {
            activity?.supportFragmentManager?.let {
                viewModel.performBulkActionOnCommunications(BulkActionsConversationData.JOB_TYPE_UPDATE, readStatus)
            }
        }
    }

    private val onSelectAllCheckedObserver = Observer<Boolean> { isSelectAllChecked ->
        updateCheckBoxSelectedAllState(isSelectAllChecked)
    }

    private val notificationEventObserver = Observer<NotificationEvent<Boolean>> { notification ->
        when (notification.event) {
            NotificationEvent.Event.SINGLE_DELETE,
            NotificationEvent.Event.BULK_DELETE -> {
                when (notification.status) {
                    true -> showSnackBar(
                        R.string.fa_trash_alt,
                        R.string.connect_calls_delete_done_message
                    ) { }
                    false -> showSnackBar(
                        R.string.fa_times_circle,
                        R.string.connect_calls_single_delete_unable_to_delete,
                        SnackStyle.Error
                    )
                }
                if (notification.event == NotificationEvent.Event.SINGLE_DELETE) {
                    resetValuesForSwiping()
                    mDialogManager.dismissProgressDialog()
                }
            }

            NotificationEvent.Event.SINGLE_READ_STATUS,
            NotificationEvent.Event.BULK_READ_STATUS -> {
                when (notification.status) {
                    true -> {}
                    false -> showSnackBar(
                        R.string.fa_times_circle,
                        R.string.connect_sms_message_updated_failed,
                        SnackStyle.Error
                    )
                }
                if (notification.event == NotificationEvent.Event.SINGLE_READ_STATUS) {
                    resetValuesForSwiping()
                    mDialogManager.dismissProgressDialog()
                }
            }
        }
    }

    private fun showSnackBar(
        icon: Int,
        message: Int,
        snackStyle: SnackStyle = SnackStyle.Standard,
        closeAction: (() -> Unit)? = null
    ) {
        CustomSnackbar.make(
            requireView(),
            BaseTransientBottomBar.LENGTH_LONG,
            this,
            snackStyle = snackStyle
        ).apply {
            setText(getString(message))
            setFontAwesomeIcon(getString(icon))
            closeAction?.let { setCloseAction { it()} }
            show()
        }
    }

    private fun resetValuesForSwiping(updateUI: Boolean = true) {
        if (updateUI) {
            notifyItemChanged(viewModel.prevConversationSwiped.value)
            notifyItemChanged(viewModel.currentConversationSwiped.value)
        }

        viewModel.prevConversationSwiped.value = null
        viewModel.currentConversationSwiped.value = null
    }

    private val editModeObserver = Observer<Boolean> { isEditModeEnabled ->
        updateEditModeUi(isEditModeEnabled)
        viewModel.updateEditModeItemCount(if (isEditModeEnabled) getCurrentAdapter().itemCount else 0)
        refreshAdapter(isEditModeEnabled)
    }

    private fun getCurrentAdapter() = mRecyclerView?.adapter as PagedMessagingListAdapter

    private fun updateCheckBoxSelectedAllState(selectAllChecked: Boolean) {
        getCurrentAdapter().apply {
            snapshot().items.forEach { item ->
                (item as MessageListItem).smsMessage.groupId?.let { groupId ->
                    if (selectAllChecked) {
                        viewModel.conversationSelectedItemList.add(groupId)
                    } else {
                        viewModel.conversationSelectedItemList.remove(groupId)
                    }
                    item.isChecked = selectAllChecked
                }
            }
            notifyDataSetChanged()
        }
    }

    private fun updateEditModeUi(isEditModeEnabled: Boolean) {
        setHeaderAccordingActions()
        val visibility = if (isEditModeEnabled) View.GONE else View.VISIBLE
        searchView.visibility = visibility
        connectSmsActionView.visibility = visibility
        mSwipeRefreshLayout?.isEnabled = !isEditModeEnabled
        mainActivityViewModel.onEditModeClicked(isEditModeEnabled)
        modifyCheckBoxState(isEditModeEnabled)
    }

    private fun refreshAdapter(isEditModeEnabled: Boolean? = null) {
        if (isEditModeEnabled == false || isEditModeEnabled == null) {
            pagedAdapter?.refresh()
        }
    }

    private fun modifyCheckBoxState(isEditModeEnabled: Boolean) {
        getCurrentAdapter().apply {
            snapshot().items.forEach { item ->
                (item as MessageListItem).isChecked = if (isEditModeEnabled) false else null
            }
            notifyDataSetChanged()
        }
    }

    private fun setHeaderAccordingActions() {
        messageFragmentHeaderView.setContent {
            val isEditModeEnabled by viewModel.isConnectEditModeEnabledLiveData.observeAsState(false)
            if (isEditModeEnabled) {
                ConnectEditModeView(connectEditModeViewStateLiveDate = viewModel.connectEditModeViewStateLiveData)
            } else {
                modifyCheckBoxState(isEditModeEnabled)
            }
        }
    }

    private val filterUpdatedObserver = Observer<String> {filterId->
        when (filterId) {
            Enums.Platform.ConnectSmsFilter.ALL -> {
                filter.setSelectedOption(getString(R.string.connect_sms_filter_all))
            }
            else -> {
                getTeamName(filterId)?.let { teamName -> filter.setSelectedOption(teamName) }
            }
        }

        refreshAdapter()
    }

    private var conversationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (searchView.getCurrentQuery().isEmpty()) {
                searchView.resetSearchView()
            }
        }
    }

    private fun getTeamName(teamId: String?): String? {
        val userTeams = sessionManager.usersTeams
        val team = userTeams.find { it.teamId == teamId }
        return team?.teamName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldAddDivider = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.let { bindViews(it) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerViewFastScroller = RecyclerViewFastScroller(requireActivity(), settingsManager, true)
        recyclerViewFastScroller.setRecyclerView(mRecyclerView)
        mRecyclerView.setItemViewCacheSize(100)
        mRecyclerView.animation = null
        mRecyclerView.itemAnimator = null
        showRecyclerView()

        pagedAdapter = PagedMessagingListAdapter(requireActivity(), this, mSessionManager, mAvatarManager, dbManager)
        pagedAdapter?.addLoadStateListener {
            pagedAdapter?.let { adapter ->
                if (it.append.endOfPaginationReached || it.prepend.endOfPaginationReached) {
                    stopRefreshing()
                }

                val hasSeveralUsersTeams = !sessionManager.usersTeams.isNullOrEmpty()

                when {
                    viewModel.isConnectEditModeEnabledLiveData.value == true -> {
                        searchView.visibility = View.GONE
                        filter.visibility = View.GONE
                        showRecyclerView()
                    }
                    !viewModel.isTeamSmsEnabled() || !viewModel.isTeamSmsLicenseEnabled() -> {
                        searchView.visibility = View.VISIBLE
                        filter.visibility = View.GONE
                        showRecyclerView()
                    }
                    adapter.itemCount > 0 -> {
                        searchView.visibility = View.VISIBLE
                        filter.visibility = if (hasSeveralUsersTeams) View.VISIBLE else View.GONE
                        showRecyclerView()
                    }
                    adapter.itemCount == 0 && viewModel.searchTermMutableLiveData.value.isNullOrEmpty() -> {
                        searchView.visibility = View.VISIBLE
                        filter.visibility = if (hasSeveralUsersTeams) View.VISIBLE else View.GONE
                        showEmptySearchResultsState()
                    }
                    viewModel.searchTermMutableLiveData.value.isNullOrEmpty() -> {
                        searchView.visibility = View.VISIBLE
                        filter.visibility = View.GONE
                        showEmptyState()
                    }
                    else -> {
                        searchView.visibility = View.VISIBLE
                        filter.visibility = if (hasSeveralUsersTeams) View.VISIBLE else View.GONE
                        showEmptySearchResultsState()
                    }
                }
            }
        }

        pagedAdapter?.addOnPagesUpdatedListener {
            recentTextView.text = when {
                TextUtils.isEmpty(viewModel.searchTermMutableLiveData.value) -> requireActivity().getString(R.string.connect_sms_recent_texts)
                pagedAdapter?.itemCount == 1 -> requireActivity().getString(R.string.connect_sms_result_count_one)
                else -> requireActivity().getString(R.string.connect_sms_results_count, pagedAdapter?.itemCount)
            }
        }

        mRecyclerView.adapter = pagedAdapter
        setButtonClickListener {
            if (sessionManager.isSmsLicenseEnabled && (sessionManager.isSmsProvisioningEnabled || sessionManager.isTeamSmsEnabled)) {
                startActivity(ConnectNewTextActivity.newIntent(requireActivity()))
            } else {
                val dialogTitle = if (!sessionManager.isSmsLicenseEnabled) getString(R.string.invalid_license_dialog_title) else getString(R.string.invalid_provisioning_dialog_title)
                val dialogBody = if (!sessionManager.isSmsLicenseEnabled) getString(R.string.invalid_license_dialog_body) else getString(R.string.invalid_provisioning_dialog_body)

                dialogManager.showDialog(this.requireContext(),
                        dialogTitle,
                        dialogBody,
                        getString(R.string.general_ok)
                ) { _, _ -> }
            }
        }

        viewModel.listItemsLiveData.observe(viewLifecycleOwner, allListItemObserver)
        viewModel.filterUpdatedLiveData.observe(viewLifecycleOwner, filterUpdatedObserver)
        viewModel.searchTermMutableLiveData.observe(viewLifecycleOwner, searchTermUpdatedObserver)
        viewModel.isConnectEditModeEnabledLiveData.observe(viewLifecycleOwner, editModeObserver)
        viewModel.deleteIconClickedLiveData.observe(viewLifecycleOwner, deleteIconClickObserver)
        viewModel.updateReadStatusIconClickedLiveData.observe(viewLifecycleOwner, updateReadStatusClickObserver)
        viewModel.isSelectAllCheckedLiveData.observe(viewLifecycleOwner, onSelectAllCheckedObserver)
        viewModel.notificationEvent.observe(viewLifecycleOwner, notificationEventObserver)
        mainActivityViewModel.unreadChatSmsMediatorLiveData.observe(viewLifecycleOwner, unreadMessagesObserver)

        exitEditModeIfActive()
    }

    private fun exitEditModeIfActive() {
        if (viewModel.isConnectEditModeEnabledLiveData.value == true){
            viewModel.onEditModeEvent(false)
        }
    }

    private fun bindViews(view: View) {
        val binding = FragmentConnectSmsListBinding.bind(view)

        searchView = binding.connectSmsListSearchView
        searchView.setOnQueryTextListener(this)
        recentTextView = binding.recentTextView
        messageFragmentHeaderView = binding.composeView
        editIcon = binding.connectSmsEditIcon
        connectSmsActionView = binding.connectSmsActionLayout
        filter = binding.connectSmsFilter

        filter.setOnClickListener {
            BottomSheetSmsFilterFragment().show(childFragmentManager, null)
        }
        editIcon.setOnClickListener {
            viewModel.onEditModeEvent(isEnabled = true)
        }
        searchView.setOnFocusChangedCallback { hasFocus ->
            searchViewFocusChangeCallback?.invoke(hasFocus)
            if (sessionManager.isCommunicationsBulkDeletesEnabled || sessionManager.isCommunicationsBulkUpdatesEnabled) {
                searchViewHasFocus = hasFocus
                editIcon.visibility = if (hasFocus) View.GONE else View.VISIBLE
                if (searchViewHasFocus && searchView.getCurrentQuery().isEmpty()) {
                    viewModel.originalList = getCurrentAdapter().snapshot().items.map { it as MessageListItem }.toMutableList()
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_connect_sms_list
    }

    override fun getRecyclerViewId(): Int {
        return R.id.connect_sms_list_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.CONNECT_CONTACTS_LIST
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return R.id.connect_sms_list_swipe_refresh_layout
    }

    override fun getConnectEmptyStateViewId(): Int {
        return R.id.connect_sms_list_empty_state_view
    }

    override fun getConnectEmptySearchResultsViewId(): Int {
        return R.id.connect_sms_list_no_results_empty_state_view
    }

    override fun fetchItemList(forceRefresh: Boolean) {
        if (mIsRefreshing) {
            return
        }

        viewModel.mediator.reset()
        refreshAdapter()
    }
    
    override fun onResume() {
        super.onResume()
        fetchItemList(true)
        showOrHideEditIcon()
        enableOrDisableSwipeActions()
    }

    override fun onPause() {
        super.onPause()
        resetValuesForSwiping()
    }

    private fun enableOrDisableSwipeActions() {
        lifecycleScope.launch {
            if (pagedAdapter?.itemCount.orZero() > 0) {
                val itemSwipeActionEnabled = (pagedAdapter?.peek(0) as? MessageListItem)?.isSwipeActionEnabled ?: false
                val settingsSwipeActionEnabled = settingsManager.isSwipeActionsEnabled
                if (itemSwipeActionEnabled != settingsSwipeActionEnabled) {
                    delay(delayToRefreshCounter)
                    pagedAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun showOrHideEditIcon() {
        editIcon.visibility = if (sessionManager.isCommunicationsBulkDeletesEnabled || sessionManager.isCommunicationsBulkUpdatesEnabled) {
            if (searchViewHasFocus) View.GONE else View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onSmsConversationItemClicked(listItem: MessageListItem) {
        super.onSmsConversationItemClicked(listItem)
        if (viewModel.isConnectEditModeEnabledLiveData.value == true) {
            val clickedItemPosition = pagedAdapter?.snapshot()?.indexOfFirst {
                it is MessageListItem && it.smsMessage.messageId == listItem.smsMessage.messageId
            }
            clickedItemPosition?.let { position ->
                val clickedItem = pagedAdapter?.snapshot()?.get(position) as? MessageListItem
                clickedItem?.isChecked = clickedItem?.isChecked?.not()
                listItem.smsMessage.groupId?.let { groupId ->
                    if (viewModel.conversationSelectedItemList.contains(groupId)) {
                        viewModel.conversationSelectedItemList.remove(groupId)
                    } else {
                        viewModel.conversationSelectedItemList.add(groupId)
                    }
                }
                viewModel.checkForSelectAllStateChange(pagedAdapter?.itemCount ?: 0)
                getCurrentAdapter().notifyItemChanged(clickedItemPosition, clickedItem)
            }
        } else {
            Log.d("MessageListFragment", "Conversation groupId: ${listItem.smsMessage.groupId}")
            conversationIdOpened = listItem.smsMessage.conversationId
            conversationLauncher.launch(ConversationActivity.newIntent(
                    activity,
                    SmsConversationDetails(listItem.smsMessage, viewModel.ourNumber ?: "", viewModel.ourUuid ?: ""),
                    false,
                    Enums.Chats.ConversationTypes.SMS,
                    Enums.Chats.ChatScreens.CONVERSATION)
            )
        }
    }

    override fun onSmsConversationLongItemClicked(listItem: MessageListItem) {
        super.onSmsConversationLongItemClicked(listItem)
        if (viewModel.isConnectEditModeEnabledLiveData.value == false) {
            val phoneNumberUnformatted = sessionManager.userDetails?.telephoneNumber ?: ""
            var participantNumbers = listItem.smsMessage.getParticipantsList(phoneNumberUnformatted).mapNotNull { it.phoneNumber }
            val phoneNumber = CallUtil.getCountryCode() + sessionManager.userDetails?.telephoneNumber

            participantNumbers = participantNumbers.filter { it != phoneNumber }

            val participantCount = participantNumbers.size
            val teamsCount = listItem.smsMessage.teams?.size ?: 0

            if (participantCount + teamsCount > 1 || teamsCount > 0) {
                val conversationDetails = SmsConversationDetails(
                    smsMessage = listItem.smsMessage,
                    ourPhoneNumber = viewModel.ourNumber ?: "",
                    ourUuid = viewModel.ourUuid ?: ""
                )
                BottomSheetSmsDetails.newInstance(
                    conversationDetails = conversationDetails,
                    participantsList = participantNumbers as? ArrayList<String>,
                    isFromConversation = false
                ).show(childFragmentManager, null)

            } else if (participantCount > 0) {
                val contact = viewModel.getContactFromPhoneNumber(participantNumbers.firstOrNull())
                requireActivity().startActivity(ConnectContactDetailsActivity.newIntent(requireActivity(), contact))

            } else {
                // Self conversation
                val contact = viewModel.getContactFromPhoneNumber(mSessionManager.phoneNumberInformation.phoneNumber)
                if (contact != null) {
                    startActivity(ConnectContactDetailsActivity.newIntent(requireActivity(), contact))
                }
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onShortSwipe(listItem: MessageListItem) {
        super.onShortSwipe(listItem)
        listItem.smsMessage.groupId?.let { currentGroupId ->
            val prevGroupId = viewModel.currentConversationSwiped.value?.smsMessage?.groupId
            if (prevGroupId != currentGroupId) {
                updateSwipedConversations(listItem)
            }
        }
    }

    private fun updateSwipedConversations(listItem: MessageListItem) {
        viewModel.prevConversationSwiped.value = viewModel.currentConversationSwiped.value
        viewModel.currentConversationSwiped.value = listItem
        notifyItemChanged(viewModel.prevConversationSwiped.value)
    }

    override fun onSwipedSmsConversationItemDelete(listItem: MessageListItem) {
        super.onSwipedSmsConversationItemDelete(listItem)
        getCurrentAdapter().notifyDataSetChanged()
        resetValuesForSwiping(updateUI = false)

        if (settingsManager.isShowDialogToDeleteSmsEnabled) {
            activity?.supportFragmentManager?.let { fm ->
                BottomSheetDeleteConfirmation.newInstance(
                        title = resources.getString(R.string.connect_calls_delete_communication_title),
                        subtitle = resources.getString(R.string.connect_sms_delete_communication_subtitle),
                        showShowAgainCheckbox = true,
                        deleteAction = { onDeleteAction(listItem) },
                        onShowAgainDialogChanged = { settingsManager.isShowDialogToDeleteSmsEnabled = !it },
                        cancelAction = { }
                ).show(fm, null)
            }
        } else {
            onDeleteAction(listItem)
        }
    }

    private fun onDeleteAction(listItem: MessageListItem) {
        listItem.smsMessage.groupId?.let { groupId ->
            dialogManager.showProgressDialog(requireContext(), analyticScreenName, R.string.progress_deleting)
            viewModel.deleteSingleConversation(groupId)
        }
    }

    override fun onSwipedSmsConversationItemMarkAsReadOrUnread(listItem: MessageListItem) {
        super.onSwipedSmsConversationItemMarkAsReadOrUnread(listItem)
        notifyItemChanged(listItem)
        listItem.smsMessage.groupId?.let { groupId ->
            val readStatus = if (listItem.smsMessage.messageState?.isRead() == true) {
                MODIFICATION_STATUS_UNREAD
            } else {
                MODIFICATION_STATUS_READ
            }
            if (mConnectionStateManager.isInternetConnected) {
                viewModel.updateReadStatusForSingleSms(groupId, readStatus)
            } else {
                notifyItemChanged(listItem)
                resetValuesForSwiping()
            }
        }
    }

    private fun notifyItemChanged(listItem: MessageListItem?) {
        getIndexForListItem(listItem)?.let { index ->
            pagedAdapter?.notifyItemChanged(index)
        }
    }

    private fun getIndexForListItem(listItem: MessageListItem?): Int? {
        listItem ?: return null

        pagedAdapter?.itemCount?.let { itemCount ->
            for (index in 0 until itemCount) {
                val mListItem = pagedAdapter?.peek(index) as? MessageListItem
                if (mListItem?.smsMessage?.groupId == listItem.smsMessage.groupId) {
                    return index
                }
            }
        }
        return null
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        mSwipeRefreshLayout?.isRefreshing = true
        viewModel.searchTermMutableLiveData.value = newText

        if (!newText.isNullOrEmpty()) {
            val resultsList = mutableSetOf<MessageListItem>()
            viewModel.originalList.forEach { item ->
                item.participants?.forEach { participant ->
                    if (viewModel.doesSearchTermMatchParticipant(participant, newText)) {
                        resultsList.add(item)
                    }
                    item.smsMessage.teams?.forEach { team ->
                        if (viewModel.doesSearchTermMatchTeam(team, newText)) {
                            resultsList.add(item)
                        }
                    }
                }
            }

            pagedAdapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.from(resultsList.map { it }))
            mSwipeRefreshLayout?.isRefreshing = false

            if (resultsList.isEmpty()) {
                showEmptySearchResultsState()
            } else {
                showRecyclerView()
            }
        } else {
            pagedAdapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.from(viewModel.originalList))
            mSwipeRefreshLayout?.isRefreshing = false
            showRecyclerView()
        }

        return false
    }

    override fun animateContentIn(delay: Int, duration: Int) { }

    override fun animateContentOut(delay: Int, duration: Int) { }

    override fun onDestroyView() {
        super.onDestroyView()
        adapterDataObserver?.let { pagedAdapter?.unregisterAdapterDataObserver(it) }
        adapterDataObserver = null
    }
}
