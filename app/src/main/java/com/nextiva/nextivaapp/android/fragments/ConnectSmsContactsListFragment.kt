package com.nextiva.nextivaapp.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.PagedConnectListAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.BottomSheetNewMessageViewModel
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.util.extensions.orZero
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ConnectSmsContactsListFragment : GeneralRecyclerViewFragment() {
    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var nextivaMediaPlayer: NextivaMediaPlayer

    @Inject
    lateinit var avatarManager: AvatarManager

    private lateinit var viewModel: BottomSheetNewMessageViewModel

    private var updateRecentSearch: ((Int) -> Unit)? = null
    private var pagedAdapter: PagedConnectListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireParentFragment())[BottomSheetNewMessageViewModel::class.java]
        shouldAddDivider = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        pagedAdapter = PagedConnectListAdapter(requireContext(),this@ConnectSmsContactsListFragment, calendarManager, dbManager, sessionManager, avatarManager, settingsManager, nextivaMediaPlayer)
        pagedAdapter?.addLoadStateListener {
            if (viewModel.resetScrollPosition && it.source.refresh == LoadState.Loading) {
                mSwipeRefreshLayout?.isRefreshing = true
            }

            if (it.source.append.endOfPaginationReached || it.source.prepend.endOfPaginationReached) {
                mSwipeRefreshLayout?.isRefreshing = false
                if (viewModel.tempState == BottomSheetNewMessageViewModel.CurrentState.Search) {
                    val count = pagedAdapter?.itemCount.orZero()
                    updateRecentSearch?.invoke(count)

                    if (count > 0) {
                        showRecyclerView()
                    } else {
                        showEmptyState()
                    }
                }

                if (viewModel.resetScrollPosition) {
                    mRecyclerView.scrollToPosition(0)
                    viewModel.resetScrollPosition = false
                }
            }
        }

        mSwipeRefreshLayout?.isEnabled = false
        mRecyclerView.adapter = pagedAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.items.collectLatest { listItems ->
                    pagedAdapter?.submitData(listItems)
                }
            }
        }
    }


    fun listenForSearchResultsOnAdapter(listener: (Int) -> Unit) {
        updateRecentSearch = listener
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

    override fun onDestroy() {
        super.onDestroy()
        pagedAdapter?.clean()
        pagedAdapter = null
    }
}