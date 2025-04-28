package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nextiva.nextivaapp.android.view.compose.ConnectParticipantsList
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetParticipantsViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetParticipantsFragment() : BaseBottomSheetDialogFragment() {

    companion object {
        const val PARTICIPANTS = "participants"
        fun newInstance(participantList: List<ParticipantInfo>) : BottomSheetParticipantsFragment {
            return BottomSheetParticipantsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PARTICIPANTS, ArrayList(participantList))
                }
            }
        }
    }

    private val viewModel: BottomSheetParticipantsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFullHeight = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (arguments?.getSerializable(PARTICIPANTS) as? ArrayList<ParticipantInfo>)?.let {
            viewModel.createContactList(it.toList())
        }

        viewModel.onCloseButtonClicked.observe(viewLifecycleOwner, onCloseButtonClickedObserver)

        return ComposeView(requireContext()).apply {
            setContent {
                val participantsListViewState by viewModel.participantsListViewStateFlow.collectAsStateWithLifecycle()
                participantsListViewState?.let { ConnectParticipantsList(viewState = it) }
            }
        }
    }

    private val onCloseButtonClickedObserver = Observer<Unit> {
        this.dismiss()
    }
}