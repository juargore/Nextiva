package com.nextiva.nextivaapp.android.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListAdapter
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.FragmentDesignSystemUtilityBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.rooms.view.DesignSystemRoomsActivity
import com.nextiva.nextivaapp.android.features.rooms.view.components.ChipSelectorView
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.viewmodels.DesignSystemUtilityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DesignSystemUtilityFragment : GeneralRecyclerViewFragment() {

    private lateinit var viewModel: DesignSystemUtilityViewModel

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var settingsManager: SettingsManager

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var componentsLayout: ScrollView
    private lateinit var roomsLinearLayout: LinearLayoutCompat
    private lateinit var addChipButton: Button
    private lateinit var chipSelectorView: ChipSelectorView

    private val listItemsObserver = Observer<ArrayList<BaseListItem>?> { listItems ->
        mAdapter.updateList(listItems)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_design_system_utility, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[DesignSystemUtilityViewModel::class.java]

        viewModel.getListItemLiveData().observe(viewLifecycleOwner, listItemsObserver)

        // Ensure mAdapter is initialized before calling bindViews
        initializeAdapter()

        bindViews(view)

        viewModel.loadListItems()
    }

    private fun initializeAdapter() {
        mAdapter = MasterListAdapter(
            requireContext(),
            ArrayList(),
            null, // Replace with a valid MasterListListener if needed
            calendarManager,
            dbManager,
            sessionManager,
            settingsManager
        )
    }

    private fun bindViews(view: View) {
        val binding = FragmentDesignSystemUtilityBinding.bind(view)

        button1 = binding.button1
        button2 = binding.button2
        button3 = binding.button3
        componentsLayout = binding.componentsLayout
        roomsLinearLayout = binding.roomsLinearLayout
        addChipButton = binding.addChipButton
        chipSelectorView = binding.chipSelectorView

        button1.setOnClickListener {
            viewModel.selectedTab(Enums.DesignSystemResourceType.COLOR)
            componentsLayout.visibility = View.GONE
            mAdapter.notifyDataSetChanged()
        }
        button2.setOnClickListener {
            viewModel.selectedTab(Enums.DesignSystemResourceType.FONT)
            componentsLayout.visibility = View.GONE
            mAdapter.notifyDataSetChanged()
        }
        button3.setOnClickListener {
            viewModel.selectedTab(Enums.DesignSystemResourceType.CUSTOM)
            componentsLayout.visibility = View.VISIBLE
            mAdapter.notifyDataSetChanged()
        }

        roomsLinearLayout.setOnClickListener {
            startActivity(Intent(requireContext(), DesignSystemRoomsActivity::class.java))
        }

        viewModel.selectedTab(Enums.DesignSystemResourceType.COLOR)
        componentsLayout.visibility = View.GONE
        mAdapter.notifyDataSetChanged()

        addChipButton.setOnClickListener {
            val names = arrayListOf("Ashlynn Dias", "Billy Da", "Jacqueline Weatherford", "Josh Calzoni", "Amanda Gomb")
            val currentChipCount = chipSelectorView.currentChipCount()
            val index = currentChipCount % (names.size)
            chipSelectorView.addChip(names[index], names[index])
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_design_system_utility
    }

    override fun getRecyclerViewId(): Int {
        return R.id.design_system_utility_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.DESIGN_SYSTEM_UTILITY
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return 0
    }
}
