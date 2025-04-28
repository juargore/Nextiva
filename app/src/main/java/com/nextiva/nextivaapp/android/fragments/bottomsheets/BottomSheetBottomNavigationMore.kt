/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.BottomSheetBottomNavigationMoreAdapter
import com.nextiva.nextivaapp.android.adapters.ConnectMainViewPagerAdapter
import com.nextiva.nextivaapp.android.databinding.BottomSheetBottomNavigationMoreBinding
import com.nextiva.nextivaapp.android.models.BottomNavigationItem
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetBottomNavigationMoreViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Thaddeus Dannar on 9/20/23.
 */
@AndroidEntryPoint
class BottomSheetBottomNavigationMore : BaseBottomSheetDialogFragment(tintedAppBar = true), BottomSheetBottomNavigationMoreAdapter.UnreadCountLiveData {

    private lateinit var viewModel: BottomSheetBottomNavigationMoreViewModel
    private lateinit var moreFeaturesRecyclerView: RecyclerView
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: BottomSheetBottomNavigationMoreAdapter
    private lateinit var listItems: ArrayList<BottomNavigationItem>
    private var isLoading = false

    val BOTTOM_NAVIGATION_ITEM_LIST = "bottomNavigationItemList"

    private val progressObserver = Observer<Boolean> { progress ->
        if (progress) {
            showProgressIndicator()
        } else {
            this.showEmptyState()
        }
    }

    private val isLoadingObserver = Observer<Boolean> {
        isLoading = it
    }

    private val bottomNavigationItemListObserver = Observer<List<BottomNavigationItem>> { bottomNavigationItemList ->
        if (bottomNavigationItemList.isNotEmpty()) {
            listItems.clear()
            listItems.addAll(bottomNavigationItemList)
            adapter.notifyDataSetChanged()
            showRecyclerView()
        } else {
            showEmptyState()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFullHeight = true
        viewModel = ViewModelProvider(
            requireActivity()
        )[BottomSheetBottomNavigationMoreViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_bottom_navigation_more, container, false)
        view?.let { bindViews(view) }

        viewModel.moreNavigationItemsMutableLiveData.observe(viewLifecycleOwner, bottomNavigationItemListObserver)
        viewModel.progressIndicatorMutableLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.isLoadingMutableLiveData.observe(viewLifecycleOwner, isLoadingObserver)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moreFeaturesRecyclerView.layoutManager = LinearLayoutManager(activity)
        listItems = ArrayList()
        setRecyclerView()
        var bottomNavigationItemList = ArrayList<BottomNavigationItem>()
        if (arguments != null) {
            bottomNavigationItemList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable(BOTTOM_NAVIGATION_ITEM_LIST, ArrayList::class.java) as ArrayList<BottomNavigationItem>
            } else {
                arguments?.getSerializable(BOTTOM_NAVIGATION_ITEM_LIST) as ArrayList<BottomNavigationItem>
            }
        }

        viewModel.moreNavigationItemsMutableLiveData.value = bottomNavigationItemList

    }

    private fun bindViews(view: View) {
        val binding = BottomSheetBottomNavigationMoreBinding.bind(view)
        moreFeaturesRecyclerView = binding.bottomNavigationMoreRecyclerView
        cancelIcon = binding.cancelIconInclude.closeIconView
        progressBar = binding.indeterminateBar
        cancelIcon.setOnClickListener {
            dismiss()
        }
    }

    private fun showRecyclerView() {
        val bottomSheetDialog = super.getDialog() as BottomSheetDialog
        adjustHeight(bottomSheetDialog)
        progressBar.visibility = View.GONE
        moreFeaturesRecyclerView.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        val bottomSheetDialog = super.getDialog() as BottomSheetDialog
        setupFullHeight(bottomSheetDialog)
        progressBar.visibility = View.GONE
        moreFeaturesRecyclerView.visibility = View.GONE
    }

    private fun setRecyclerView() {
        adapter = BottomSheetBottomNavigationMoreAdapter(listItems,this)
        moreFeaturesRecyclerView.adapter = adapter
        moreFeaturesRecyclerView.scrollState

        moreFeaturesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            }
        })
    }

    private fun showProgressIndicator() {
        moreFeaturesRecyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun observeUnreadCountLiveData(featureType: ConnectMainViewPagerAdapter.FeatureType, updateView: (Int) -> Unit) {
        viewModel.getUnreadLiveData(featureType)?.let {
            it.observe(viewLifecycleOwner) { count ->
                if(count != null)
                    updateView(count)
            }
        }
    }


}