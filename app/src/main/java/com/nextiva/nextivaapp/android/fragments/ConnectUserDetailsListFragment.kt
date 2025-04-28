package com.nextiva.nextivaapp.android.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
import com.nextiva.nextivaapp.android.viewmodels.ConnectUserDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConnectUserDetailsListFragment(): GeneralRecyclerViewFragment() {
    @Inject
    lateinit var intentManager: IntentManager

    private lateinit var viewModel: ConnectUserDetailsViewModel

    private val baseListItemObserver = Observer<ArrayList<BaseListItem>> { listItems ->
        mAdapter.updateList(listItems)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldAddDivider = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ConnectUserDetailsViewModel::class.java]
        viewModel.getBaseListItemsLiveData().observe(viewLifecycleOwner, baseListItemObserver)
        viewModel.getUserDetailListItems()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_connect_user_details
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return 0
    }

    override fun getRecyclerViewId(): Int {
        return R.id.connect_user_details_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.CONNECT_USER_DETAILS
    }

    override fun fetchItemList(forceRefresh: Boolean) {
        if (mIsRefreshing) {
            return
        }

        startRefreshing()
        viewModel.getUserDetailListItems()
    }
}