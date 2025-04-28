package com.nextiva.nextivaapp.android.core.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.PagedConnectListAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.fragments.GeneralRecyclerViewFragment
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.util.RecyclerViewFastScroller
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ContactsListFragment : GeneralRecyclerViewFragment() {

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


    private lateinit var selectionInterface: BottomSheetSelectContactsInterface

    private var pagedAdapter: PagedConnectListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldAddDivider = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val recyclerViewFastScroller = RecyclerViewFastScroller(requireActivity(), settingsManager, true)
        recyclerViewFastScroller.setRecyclerView(mRecyclerView)

        pagedAdapter = PagedConnectListAdapter(requireContext(),this@ContactsListFragment, calendarManager, dbManager, sessionManager, avatarManager, settingsManager, nextivaMediaPlayer)
        mRecyclerView.adapter = pagedAdapter

        showRecyclerView()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                selectionInterface.allListItemsLiveData.collect { listItems ->
                    lifecycleScope.launch {
                        pagedAdapter?.submitData(listItems)
                    }
                }
            }
        }
    }


    fun setViewModel(selectionInterface: BottomSheetSelectContactsInterface) {
        this.selectionInterface = selectionInterface
    }

    fun onSearchTermUpdated(searchTerm: String) {
        selectionInterface.onSearchTermUpdated(searchTerm)
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
                selectionInterface.getContactFromUserId(userId) {
                    selectionInterface.contactSelected(it)
                }
            }
        } else {
            selectionInterface.contactSelected(nextivaContact)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pagedAdapter?.clean()
        pagedAdapter = null
    }
}