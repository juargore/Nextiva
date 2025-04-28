package com.nextiva.nextivaapp.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.FeatureFlagListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.FragmentFeatureFlagsBinding
import com.nextiva.nextivaapp.android.viewmodels.FeatureFlagsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeatureFlagsFragment : GeneralRecyclerViewFragment() {

    private lateinit var viewModel: FeatureFlagsViewModel

    private lateinit var noFeatureFlagsTextView: TextView

    private val listItemsObserver = Observer<ArrayList<BaseListItem>?> { listItems ->
        if (listItems.isNullOrEmpty()) {
            noFeatureFlagsTextView.visibility = View.VISIBLE

        } else {
            mAdapter.updateList(listItems)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        view?.let { bindViews(it) }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[FeatureFlagsViewModel::class.java]

        viewModel.getListItemLiveData().observe(viewLifecycleOwner, listItemsObserver)

        viewModel.loadListItems()
    }

    fun bindViews(view: View) {
        val binding = FragmentFeatureFlagsBinding.bind(view)

        noFeatureFlagsTextView = binding.featureFlagsEmptyTextView
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_feature_flags
    }

    override fun getRecyclerViewId(): Int {
        return R.id.feature_flags_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.FEATURE_FLAGS
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return 0
    }

    override fun onFeatureFlagListItemChecked(listItem: FeatureFlagListItem) {
        super.onFeatureFlagListItemChecked(listItem)
        viewModel.setFeatureFlagDisabled(listItem)
    }
}