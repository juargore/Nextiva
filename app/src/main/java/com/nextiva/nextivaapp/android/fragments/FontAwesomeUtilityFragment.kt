package com.nextiva.nextivaapp.android.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.viewmodels.FontAwesomeUtilityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FontAwesomeUtilityFragment : GeneralRecyclerViewFragment() {

    private lateinit var viewModel: FontAwesomeUtilityViewModel

    private val listItemsObserver = Observer<ArrayList<BaseListItem>?> { listItems ->
        mAdapter.updateList(listItems)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[FontAwesomeUtilityViewModel::class.java]

        viewModel.getListItemLiveData().observe(viewLifecycleOwner, listItemsObserver)

        viewModel.loadListItems()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_font_awesome_utility
    }

    override fun getRecyclerViewId(): Int {
        return R.id.font_awesome_utility_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.FONT_AWESOME_UTILITY
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return 0
    }
}