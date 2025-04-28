package com.nextiva.nextivaapp.android.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.viewmodels.DatabaseCountUtilityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatabaseCountUtilityFragment : GeneralRecyclerViewFragment() {

    private lateinit var viewModel: DatabaseCountUtilityViewModel

    private val listItemsObserver = Observer<ArrayList<BaseListItem>> { listItems ->
        stopRefreshing()
        mAdapter.updateList(listItems)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DatabaseCountUtilityViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.baseListItemsLiveData.observe(viewLifecycleOwner, listItemsObserver)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_database_count_utility
    }

    override fun getRecyclerViewId(): Int {
        return R.id.database_count_utility_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.DATABASE_COUNT_UTILITY
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return R.id.datebase_count_utility_swipe_refresh_layout
    }

    override fun fetchItemList(forceRefresh: Boolean) {}
}