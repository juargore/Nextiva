package com.nextiva.nextivaapp.android.features.rooms.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.PagedConnectListAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.rooms.RoomsGeneralRecyclerViewFragment
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.BottomSheetRoomParticipantsViewModel
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.util.RecyclerViewFastScroller
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactListFragment : RoomsGeneralRecyclerViewFragment() {
    @Inject
    lateinit var settingsManager: SettingsManager

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

    private lateinit var viewModel: BottomSheetRoomParticipantsViewModel

    private var pagedAdapter: PagedConnectListAdapter? = null

    private val allListItemObserver = Observer<PagingData<BaseListItem>> { listItems ->
        pagedAdapter?.submitData(viewLifecycleOwner.lifecycle, listItems)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldAddDivider = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val recyclerViewFastScroller = RecyclerViewFastScroller(requireActivity(), settingsManager, true)
        recyclerViewFastScroller.setRecyclerView(mRecyclerView)

        pagedAdapter = PagedConnectListAdapter(requireActivity(), this@ContactListFragment, calendarManager, dbManager, sessionManager, avatarManager, settingsManager, nextivaMediaPlayer)
        mRecyclerView.adapter = pagedAdapter

        viewModel.allListItemsLiveData.observe(viewLifecycleOwner, allListItemObserver)

        showRecyclerView()


        return view
    }


    fun setViewModel(viewModel: BottomSheetRoomParticipantsViewModel) {
        this.viewModel = viewModel
    }

    fun onSearchTermUpdated(searchTerm: String) {
        viewModel.onSearchTermUpdated(searchTerm)
        pagedAdapter?.refresh()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_connect_sms_contacts_list
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
        if (mIsRefreshing) {
            return
        }
    }

    override fun onConnectContactListItemClicked(listItem: ConnectContactListItem) {
        super.onConnectContactListItemClicked(listItem)

        val nextivaContact = listItem.nextivaContact

        if (nextivaContact == null) {
            listItem.strippedContact?.contactTypeId?.let { userId ->
                viewModel.getContactFromUserId(userId) {
                    viewModel.contactSelected(it)
                }
            }
        } else {
            viewModel.contactSelected(nextivaContact)
        }
    }
}