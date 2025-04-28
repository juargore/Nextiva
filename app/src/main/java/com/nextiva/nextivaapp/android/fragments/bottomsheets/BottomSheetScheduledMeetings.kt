package com.nextiva.nextivaapp.android.fragments.bottomsheets

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
import com.nextiva.nextivaapp.android.adapters.ScheduledMeetingAdapter
import com.nextiva.nextivaapp.android.databinding.BottomSheetScheduledMeetingsBinding
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventDetail
import com.nextiva.nextivaapp.android.view.ConnectEmptyStateView
import com.nextiva.nextivaapp.android.viewmodels.ScheduledMeetingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetScheduledMeetings : BaseBottomSheetDialogFragment(tintedAppBar = true) {

    private lateinit var viewModel: ScheduledMeetingsViewModel
    private lateinit var scheduledMeetingsRecyclerView: RecyclerView
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var emptyStateView: ConnectEmptyStateView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ScheduledMeetingAdapter
    private lateinit var listItems: ArrayList<CalendarApiEventDetail>
    private var isLoading = false

    private val allListItemObserver = Observer<List<CalendarApiEventDetail>> { newItems ->
        if (newItems.isNotEmpty()) {
            this.showRecyclerView()
            listItems.addAll(newItems as ArrayList<CalendarApiEventDetail>)
            adapter.notifyDataSetChanged()
            isLoading = false
        } else {
            this.showEmptyState()
        }
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFullHeight = true
        viewModel = ViewModelProvider(
            requireActivity()
        )[ScheduledMeetingsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_scheduled_meetings, container, false)
        view?.let { bindViews(view) }
        viewModel.meetingsMutableLiveData.observe(viewLifecycleOwner, allListItemObserver)
        viewModel.progressIndicatorMutableLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.isLoadingMutableLiveData.observe(viewLifecycleOwner, isLoadingObserver)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scheduledMeetingsRecyclerView.layoutManager = LinearLayoutManager(activity)
        listItems = ArrayList()
        setRecyclerView()
        viewModel.getMeetings()
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetScheduledMeetingsBinding.bind(view)
        scheduledMeetingsRecyclerView = binding.rvScheduledMeetingsList
        cancelIcon = binding.cancelIconInclude.closeIconView
        emptyStateView = binding.scheduledMeetingsEmptyStateView
        progressBar = binding.indeterminateBar
        cancelIcon.setOnClickListener {
            dismiss()
        }
    }

    private fun showRecyclerView() {
        val bottomSheetDialog = super.getDialog() as BottomSheetDialog
        adjustHeight(bottomSheetDialog)
        progressBar.visibility = View.GONE
        emptyStateView.visibility = View.GONE
        scheduledMeetingsRecyclerView.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        val bottomSheetDialog = super.getDialog() as BottomSheetDialog
        setupFullHeight(bottomSheetDialog)
        progressBar.visibility = View.GONE
        scheduledMeetingsRecyclerView.visibility = View.GONE
        emptyStateView.visibility = View.VISIBLE
    }

    private fun setRecyclerView() {
        adapter = ScheduledMeetingAdapter(listItems)
        scheduledMeetingsRecyclerView.adapter = adapter
        scheduledMeetingsRecyclerView.scrollState

        scheduledMeetingsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (scheduledMeetingsRecyclerView.visibility == View.VISIBLE && progressBar.visibility == View.GONE) {
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    val totalItem = linearLayoutManager!!.itemCount
                    val lastVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                    if (!isLoading && totalItem <= (lastVisible + 10)) {
                        isLoading = true
                        viewModel.getMoreMeetings()
                    }
                }
            }
        })
    }

    private fun showProgressIndicator() {
        scheduledMeetingsRecyclerView.visibility = View.GONE
        emptyStateView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }
}