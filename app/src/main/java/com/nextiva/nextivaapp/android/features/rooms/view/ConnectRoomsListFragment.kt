package com.nextiva.nextivaapp.android.features.rooms.view

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.Screen
import com.nextiva.nextivaapp.android.constants.RequestCodes
import com.nextiva.nextivaapp.android.databinding.FragmentConnectRoomListBinding
import com.nextiva.nextivaapp.android.features.rooms.RoomsGeneralRecyclerViewFragment
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.ConnectRoomsListViewModel
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.PagedRoomsListAdapter
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager.ProcessParticipantInfoCallBack
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.util.RecyclerViewFastScroller
import com.nextiva.nextivaapp.android.view.ConnectSearchView
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@AndroidEntryPoint
class ConnectRoomsListFragment(private val searchViewFocusChangeCallback: ((Boolean) -> Unit)?) : RoomsGeneralRecyclerViewFragment(), SearchView.OnQueryTextListener,
    ProcessParticipantInfoCallBack {

    constructor() : this(null)

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var roomsDbManager: RoomsDbManager

    private val viewModel: ConnectRoomsListViewModel by viewModels()

    private var pagedAdapter: PagedRoomsListAdapter? = null

    private lateinit var searchView: ConnectSearchView

    private var newCallType: Int = RequestCodes.NewCall.NEW_CALL_NONE

    private val allListItemObserver = Observer<PagingData<BaseListItem>> { listItems ->
        pagedAdapter?.submitData(viewLifecycleOwner.lifecycle, listItems)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldAddDivider = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.let { bindViews(it) }

        val recyclerViewFastScroller = RecyclerViewFastScroller(requireActivity(), settingsManager, true)
        recyclerViewFastScroller.setRecyclerView(mRecyclerView)

        showRecyclerView()

        newCallType = requireActivity().intent.getIntExtra(Constants.Calls.PARAMS_NEW_CALL_TYPE, RequestCodes.NewCall.NEW_CALL_NONE)

        pagedAdapter = PagedRoomsListAdapter(requireActivity(), this@ConnectRoomsListFragment, sessionManager, roomsDbManager)
        mRecyclerView.adapter = pagedAdapter
        viewModel.listItemsLiveData.observe(viewLifecycleOwner, allListItemObserver)

        pagedAdapter?.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    if (viewModel.isCurrentlySearching) startRefreshing()
                    viewModel.hasLoaded = true
                }
                is LoadState.NotLoading, is LoadState.Error -> {
                    if (viewModel.isCurrentlySearching) stopRefreshing()
                    shouldScrollToTopCheck()
                }
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //TODO
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //TODO
        return true
    }

    override fun onConnectRoomFavoriteIconClicked(listItem: ConnectRoomsListItem) {
        super.onConnectRoomFavoriteIconClicked(listItem)

        viewModel.setFavorite(listItem)
    }

    private fun bindViews(view: View) {
        val binding = FragmentConnectRoomListBinding.bind(view)

        searchView = binding.connectRoomsSearchView
        searchView.setOnQueryTextListener(this)
        searchView.setOnFocusChangedCallback(searchViewFocusChangeCallback)
    }

    private fun shouldScrollToTopCheck() {
        if ((viewModel.isCurrentlySearching || viewModel.finishedSearching) && viewModel.shouldScrollToTop && viewModel.hasLoaded) {
            mRecyclerView.scrollToPosition(0)
            viewModel.shouldScrollToTop = false
        }
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
    }

    // --------------------------------------------------------------------------------------------

    override fun getLayoutId(): Int {
        return R.layout.fragment_connect_room_list
    }

    override fun getRecyclerViewId(): Int {
        return R.id.connect_rooms_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.CONNECT_ROOMS_LIST
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return R.id.connect_rooms_swipe_refresh_layout
    }

    override fun getConnectEmptyStateViewId(): Int {
        return R.id.connect_rooms_empty_state_view
    }

    override fun fetchItemList(forceRefresh: Boolean) {
        viewModel.fetchRooms(forceRefresh) {
            stopRefreshing()
        }
    }

    override fun onConnectRoomsHeaderListItemClicked(listItem: ConnectRoomsHeaderListItem) {
        viewModel.headerListItemClicked(listItem)
        pagedAdapter?.refresh()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.onSearchTermUpdated(query)
        pagedAdapter?.refresh()

        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.onSearchTermUpdated(newText)
        pagedAdapter?.refresh()

        return false
    }

    override fun onConnectRoomListItemClicked(listItem: ConnectRoomsListItem) {
        super.onConnectRoomListItemClicked(listItem)
        startActivity(RoomConversationActivity.newIntent(requireActivity(), listItem.room.roomId, viewModel.displayName(listItem.room, isToolbarTitle = true)))
    }

    override fun onConnectRoomListItemLongClicked(listItem: ConnectRoomsListItem) {
        super.onConnectRoomListItemLongClicked(listItem)
        BottomSheetRoomDetailsFragment.newInstance(listItem.room.roomId).show(childFragmentManager, null)
    }
}
