package com.nextiva.nextivaapp.android.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.ConnectContactDetailsActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BottomSheetMenuListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.PagedConnectListAdapter
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.Screen
import com.nextiva.nextivaapp.android.constants.Enums.Service.DialingServiceTypes
import com.nextiva.nextivaapp.android.constants.RequestCodes
import com.nextiva.nextivaapp.android.databinding.FragmentConnectContactsListBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.dialogs.ContactActionDialog
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetAllowContactAccess
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetMenu
import com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard.BottomSheetImportLocalContacts
import com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard.BottomSheetImportWizard
import com.nextiva.nextivaapp.android.interfaces.BackFragmentListener
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager.ProcessParticipantInfoCallBack
import com.nextiva.nextivaapp.android.managers.interfaces.LocalContactsManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.MenuUtil
import com.nextiva.nextivaapp.android.util.extensions.orZero
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import com.nextiva.nextivaapp.android.view.ConnectFilterView
import com.nextiva.nextivaapp.android.view.ConnectSearchView
import com.nextiva.nextivaapp.android.viewmodels.ConnectContactsListViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ConnectContactsListFragment(private val searchViewFocusChangeCallback: ((Boolean) -> Unit)?) : GeneralRecyclerViewFragment(), SearchView.OnQueryTextListener,
    ProcessParticipantInfoCallBack, BackFragmentListener {

    constructor() : this(null)

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var localContactsManager: LocalContactsManager

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Inject
    lateinit var callManager: CallManager

    @Inject
    lateinit var logManager: LogManager

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var avatarManager: AvatarManager

    @Inject
    lateinit var nextivaMediaPlayer: NextivaMediaPlayer

    companion object {
        var isLoadingOnFragment = false
    }

    private val viewModel: ConnectContactsListViewModel by viewModels()

    private var pagedAdapter: PagedConnectListAdapter? = null

    private lateinit var searchText: TextView
    private lateinit var searchTextLayout: LinearLayout
    private lateinit var searchFilter: ConnectFilterView
    private lateinit var searchView: ConnectSearchView

    private var searchHasFocus = false

    private var newCallType: Int = RequestCodes.NewCall.NEW_CALL_NONE
    private var contactHeaderListItem: ConnectContactHeaderListItem? = null

    private val blockOrUnblockNumberObserver = Observer<List<String>> {
        pagedAdapter?.refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldAddDivider = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.let { bindViews(it) }

        newCallType = requireActivity().intent.getIntExtra(
            Constants.Calls.PARAMS_NEW_CALL_TYPE,
            RequestCodes.NewCall.NEW_CALL_NONE
        )

        pagedAdapter = PagedConnectListAdapter(requireActivity(), this@ConnectContactsListFragment, calendarManager, dbManager, sessionManager, avatarManager, settingsManager, nextivaMediaPlayer)
        mRecyclerView.adapter = pagedAdapter

        pagedAdapter?.addOnPagesUpdatedListener {
            if (isLoadingOnFragment) {
                refreshAdapter()
            }
        }

        pagedAdapter?.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    if (viewModel.resetScrollPosition) {
                        mSwipeRefreshLayout?.isRefreshing = true
                    }
                }
                is LoadState.NotLoading, is LoadState.Error -> {
                    // logging in for the first time, but data has not been downloaded yet
                    mSwipeRefreshLayout?.isRefreshing = pagedAdapter?.itemCount.orZero() == 4 && totalItemsOnEachCategory() == 0
                }
            }

            if (it.append.endOfPaginationReached || it.prepend.endOfPaginationReached){
                val state = viewModel.getCurrentState()
                if (state == ConnectContactsListViewModel.CurrentPosition.Search) {
                    val count = pagedAdapter?.itemCount.orZero()
                    searchTextLayout.visibility = View.VISIBLE
                    searchText.text = getString(R.string.connect_contacts_search_results_count, count)
                    if (count > 0) {
                        showRecyclerView()
                    } else {
                        showEmptyState()
                    }
                } else {
                    if (mRecyclerView.visibility == View.GONE) {
                        showRecyclerView()
                    }

                    if (state == ConnectContactsListViewModel.CurrentPosition.RecentContacts) {
                        searchText.text = getString(R.string.connect_contacts_recent_contacts)
                        searchTextLayout.visibility = View.VISIBLE
                    } else {
                        searchTextLayout.visibility = View.GONE
                    }
                }

                if (viewModel.resetScrollPosition) {
                    mRecyclerView.scrollToPosition(0)
                    viewModel.resetScrollPosition = false
                }
            }
        }

        permissionManager.requestContactsPermission(requireActivity(),
            Enums.Analytics.ScreenName.BLOCK_MY_CALLER_ID_SCREEN,
            {
                mCompositeDisposable.add(
                    localContactsManager.localContactsWithReturn.subscribe { contacts ->
                        if (!sharedPreferencesManager.getBoolean(SharedPreferencesManager.IMPORT_LOCAL_CONTACTS_DIALOG_SHOWN, false)) {
                            sharedPreferencesManager.setBoolean(SharedPreferencesManager.IMPORT_LOCAL_CONTACTS_DIALOG_SHOWN, true)
                            val bottomSheet = BottomSheetImportLocalContacts()
                            bottomSheet.arguments =
                                Bundle().apply { putInt(BottomSheetImportLocalContacts.NUM_OF_CONTACTS, contacts.size) }
                            bottomSheet.show(requireActivity().supportFragmentManager,null)
                        }
                    })
            },
            { })

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (searchHasFocus && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    searchView.clearFocus()
                }
            }

        })

        viewModel.blockedNumbersLiveData.observe(viewLifecycleOwner, blockOrUnblockNumberObserver)

        return view
    }

    private fun refreshAdapter() {
        isLoadingOnFragment = false
        contactHeaderListItem?.isLoadingLiveData?.value = false
        pagedAdapter?.refresh()
    }

    private fun totalItemsOnEachCategory(): Int {
        var total = 0
        pagedAdapter?.itemCount?.let { itemCount ->
            for (index in 0 until itemCount) {
                val mListItem = pagedAdapter?.peek(index)
                if (mListItem is ConnectContactHeaderListItem) {
                    total += mListItem.currentCount.orZero()
                }
            }
        }
        return total
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.listItems.collectLatest {
                    pagedAdapter?.submitData(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        if (isVisible) {
            menu.clear()
            inflater.inflate(R.menu.menu_connect_contacts_list, menu)
            MenuUtil.setMenuContentDescriptions(menu)

            activity?.let { activity ->
                menu.findItem(R.id.connect_contacts_list_action_local_contacts)?.icon = FontDrawable(activity,
                    R.string.fa_address_book,
                    Enums.FontAwesomeIconType.REGULAR)
                    .withColor(ContextCompat.getColor(activity, R.color.connectGrey09))
                    .withSize(R.dimen.material_text_title)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (activity == null) {
            return super.onOptionsItemSelected(item)
        }

        when (item.itemId) {
            R.id.connect_contacts_list_action_local_contacts -> {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    BottomSheetAllowContactAccess().show(requireActivity().supportFragmentManager, null)
                } else {
                    BottomSheetImportWizard.newInstance(
                        viewModel.hasLocalContacts(),
                        startImporting = false,
                        enableImport = newCallType == RequestCodes.NewCall.NEW_CALL_NONE
                    ).show(requireActivity().supportFragmentManager, null)
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onPause() {
        super.onPause()
        mCompositeDisposable.clear()
        viewModel.compositeDisposableClear()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
        viewModel.compositeDisposableClear()
    }

    private fun bindViews(view: View) {
        val binding = FragmentConnectContactsListBinding.bind(view)

        searchText = binding.connectSearchResultsTextView
        searchTextLayout = binding.connectContactsSearchLayout
        searchFilter = binding.connectContactsSearchFilter
        searchView = binding.connectContactsSearchView
        searchView.setOnQueryTextListener(this)
        searchView.lockSearch()

        searchFilter.setOnClickListener {
            val curSelection = searchFilter.getSelectedOption()
            val bottomSheetListItems = arrayListOf(
                BottomSheetMenuListItem(getString(R.string.connect_contacts_type_search_filter_all),
                    TextUtils.equals(getString(R.string.connect_contacts_type_search_filter_all), curSelection)),
                BottomSheetMenuListItem(getString(R.string.connect_contacts_type_search_filter_business),
                    TextUtils.equals(getString(R.string.connect_contacts_type_search_filter_business), curSelection)),
                BottomSheetMenuListItem(getString(R.string.connect_contacts_type_search_filter_teammate),
                    TextUtils.equals(getString(R.string.connect_contacts_type_search_filter_teammate), curSelection)))

            BottomSheetMenu(bottomSheetListItems) { listItem ->
                searchFilter.setSelectedOption(listItem.text)
                viewModel.updateFilter(getSelectedFilter())
            }.show(requireActivity().supportFragmentManager, null)
        }

        searchView.setOnCloseClickedCallback { viewModel.setAllItems() }
        searchView.setOnFocusChangedCallback { hasFocus ->
            searchHasFocus = hasFocus
            if (hasFocus) {
                viewModel.onFocusChanged(
                    searchView.getCurrentQuery(),
                    getSelectedFilter()
                )
            }
            searchViewFocusChangeCallback?.invoke(hasFocus)
        }
    }

    private fun processCallInfo(
        participantInfo: ParticipantInfo,
        @Screen analyticsScreenName: String
    ) {
        callManager.processParticipantInfo(
            requireActivity(),
            analyticsScreenName,
            participantInfo,
            null,
            mCompositeDisposable,
            this
        )
    }


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
        val callback = PermissionManager.PermissionGrantedCallback {
            val data = Intent()
            data.putExtra(
                Constants.EXTRA_PARTICIPANT_INFO,
                participantInfo
            )
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


    override fun getLayoutId(): Int {
        return R.layout.fragment_connect_contacts_list
    }

    override fun getRecyclerViewId(): Int {
        return R.id.connect_contacts_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.CONNECT_CONTACTS_LIST
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return R.id.connect_contacts_swipe_refresh_layout
    }

    override fun getConnectEmptyStateViewId(): Int {
        return R.id.connect_contacts_empty_state_view
    }

    override fun fetchItemList(forceRefresh: Boolean) {
        mSwipeRefreshLayout?.isRefreshing = true
        viewModel.fetchContacts(forceRefresh) {
            stopRefreshing()
        }
    }

    override fun onConnectContactHeaderListItemClicked(listItem: ConnectContactHeaderListItem) {
        super.onConnectContactHeaderListItemClicked(listItem)
        isLoadingOnFragment = true
        contactHeaderListItem = listItem
        viewModel.headerListItemClicked(listItem)
        pagedAdapter?.refresh()
    }

    override fun onConnectContactFavoriteIconClicked(listItem: ConnectContactListItem) {
        super.onConnectContactFavoriteIconClicked(listItem)
        viewModel.setFavorite(listItem)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchView.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (searchHasFocus) {
            viewModel.queryUpdated(newText.orEmpty(), getSelectedFilter())
        }
        return false
    }

    override fun onConnectContactListItemClicked(listItem: ConnectContactListItem) {
        super.onConnectContactListItemClicked(listItem)
        viewModel.getContactFromListItem(listItem) { contact ->
            if (newCallType == RequestCodes.NewCall.NEW_CALL_NONE) {
                contact?.let {
                    startActivity(ConnectContactDetailsActivity.newIntent(requireActivity(), it))
                }
            } else {
                val phone = contact?.phoneNumbers?.firstOrNull()?.strippedNumber
                    ?: CallUtil.getStrippedPhoneNumber(contact?.phoneNumbers?.firstOrNull()?.number.orEmpty())

                val participantInfo = ParticipantInfo(displayName = contact?.uiName,
                    contactId = contact?.userId,
                    numberToCall = phone,
                    dialingServiceType = DialingServiceTypes.VOIP)

                if (newCallType == RequestCodes.NewCall.TRANSFER_REQUEST_CODE || newCallType == RequestCodes.NewCall.CONFERENCE_REQUEST_CODE) {
                    participantInfo.callType = Enums.Sip.CallTypes.VOICE
                }

                processCallInfo(participantInfo, ScreenName.NEW_CALL_CONTACTS_LIST)
            }
        }
    }

    override fun onConnectContactListItemLongClicked(listItem: ConnectContactListItem) {
        super.onConnectContactListItemLongClicked(listItem)

        listItem.nextivaContact?.let {
            ContactActionDialog.newInstance(
                nextivaContact = it,
                fromLongPress = true
            ).show(requireActivity().supportFragmentManager, null)
        }

        listItem.strippedContact?.let { strippedContact ->
            strippedContact.contactTypeId?.let { contactTypeId ->
                viewModel.getContact(contactTypeId) { contact ->
                    contact?.let { nextivaContact ->
                        ContactActionDialog.newInstance(
                            nextivaContact = nextivaContact,
                            fromLongPress = true
                        ).show(requireActivity().supportFragmentManager, null)
                    }
                }
            }
        }
    }

    private fun getSelectedFilter() : IntArray {
        return when (searchFilter.getSelectedOption()) {
            getString(R.string.connect_contacts_type_search_filter_teammate) ->
                intArrayOf(Enums.Contacts.ContactTypes.CONNECT_USER)
            getString(R.string.connect_contacts_type_search_filter_business) ->
                intArrayOf(
                    Enums.Contacts.ContactTypes.CONNECT_SHARED,
                    Enums.Contacts.ContactTypes.CONNECT_PERSONAL
                )
            else -> intArrayOf(
                Enums.Contacts.ContactTypes.CONNECT_PERSONAL,
                Enums.Contacts.ContactTypes.CONNECT_SHARED,
                Enums.Contacts.ContactTypes.CONNECT_USER,
                Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW,
                Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS,
                Enums.Contacts.ContactTypes.CONNECT_TEAM
            )
        }
    }

    override fun onBackPressed() {
        if (::searchView.isInitialized && searchView.isSearchLocked()) {
            searchView.onBackPressed()
        }
    }
}