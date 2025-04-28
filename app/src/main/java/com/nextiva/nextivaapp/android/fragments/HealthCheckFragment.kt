package com.nextiva.nextivaapp.android.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.viewmodels.HealthCheckViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HealthCheckFragment : GeneralRecyclerViewFragment() {

    private lateinit var viewModel: HealthCheckViewModel

    private val listItemsObserver = Observer<ArrayList<BaseListItem>> { listItems ->
        mAdapter.updateList(listItems)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldAddDivider = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[HealthCheckViewModel::class.java]

        viewModel.listItemsLiveData.observe(viewLifecycleOwner, listItemsObserver)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_health_check
    }

    override fun getRecyclerViewId(): Int {
        return R.id.health_check_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.HEALTH_CHECK
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return 0
    }
}