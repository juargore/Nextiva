package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.AssignCoHostAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.BottomSheetAssignCoHostBinding
import com.nextiva.nextivaapp.android.meetings.data.CoHostItem
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import com.nextiva.nextivaapp.android.viewmodels.MeetingSessionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetAssignCoHost : BaseBottomSheetDialogFragment(tintedAppBar = true), AssignCoHostAdapter.ItemClickListener {


    private lateinit var cancelIcon: FontTextView
    private lateinit var cancelButton: AppCompatButton
    private lateinit var assignButton: AppCompatButton
    private lateinit var attendeesRecyclerView: RecyclerView
    private lateinit var errorView: LinearLayout
    private lateinit var descriptionView: AppCompatTextView
    private lateinit var adapter: AssignCoHostAdapter
    private lateinit var viewModel: MeetingSessionViewModel
    private lateinit var attendeesList: ArrayList<CoHostItem>
    private lateinit var progressBar: ProgressBar
    private var isPullDown = true


    private val apiSuccessEventObserver = Observer<Boolean> { success ->
        hideProgressBar()
        if(viewModel.getCoHostsAssigned().isNotEmpty() && success){
            errorView.visibility = View.GONE
            closeBottomSheet()
        } else if(wasModified() && !success){
            errorView.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity()
        )[MeetingSessionViewModel::class.java]

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_assign_co_host, container, false)
        view?.let { bindViews(view) }

        viewModel.assignCoHostLiveData.observe(viewLifecycleOwner, apiSuccessEventObserver)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attendeesList = viewModel.getPossibleCoHost()
        val moderator = attendeesList.firstOrNull {
            it.type.equals(Enums.MediaCall.AttendeeTypes.MODERATOR) || it.type.equals(
                Enums.MediaCall.AttendeeTypes.REGULAR
            )
        }
        if(attendeesList.isEmpty() || moderator == null)
            descriptionView.text = getText(R.string.bottom_sheet_assign_cohost_no_cohost)

        enableBtn()
        adapter = AssignCoHostAdapter(attendeesList,this)
        attendeesRecyclerView.layoutManager = LinearLayoutManager(activity)
        attendeesRecyclerView.adapter = adapter
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetAssignCoHostBinding.bind(view)
        cancelIcon = binding.ftvAssignCohostIcon
        cancelButton = binding.btnCancel
        attendeesRecyclerView = binding.rvAssignCohostList
        assignButton = binding.btnAssign
        errorView = binding.llAssignCohostErrorContainer
        descriptionView = binding.tvAssignCohostDescription
        progressBar = binding.indeterminateBar

        cancelIcon.setOnClickListener { closeBottomSheet() }
        cancelButton.setOnClickListener { closeBottomSheet() }
        assignButton.setOnClickListener {
            showProgressBar()
            viewModel.assignCoHost(attendeesList)
        }
    }

    private fun closeBottomSheet(){
        isPullDown = false
        dismiss()
        BottomSheetPhoneForAudio().show(requireActivity().supportFragmentManager, null)
    }

    private fun enableBtn(){
        val enable = wasModified()
        assignButton.isEnabled = enable
        if(enable){
            assignButton.background =  ContextCompat.getDrawable(requireContext(),R.drawable.rounded_primary_button )
            assignButton.setTextColor(ContextCompat.getColor(
                requireContext(),
                R.color.connectWhite
            ))
        }
        else{
            assignButton.background =  ContextCompat.getDrawable(requireContext(),R.drawable.rounded_disable_button )
            assignButton.setTextColor(ContextCompat.getColor(
                requireContext(),
                R.color.connectSecondaryDarkBlue
            ))
        }
    }

    override fun onItemClick() {
        enableBtn()
    }

    override fun progressBarIsVisible(): Boolean {
        return progressBar.isVisible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(isPullDown)
            try{
                BottomSheetPhoneForAudio().show(requireActivity().supportFragmentManager, null)
            } catch (e:RuntimeException){
                e.printStackTrace()
            }
    }

    private fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
        assignButton.visibility = View.GONE
        if(errorView.isVisible)
            errorView.visibility = View.GONE
    }

    private fun hideProgressBar(){
        progressBar.visibility = View.GONE
        assignButton.visibility = View.VISIBLE
    }

    private fun wasModified(): Boolean{
        return attendeesList.isNotEmpty() && attendeesList.any { it.wasModified }
    }
}