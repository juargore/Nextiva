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
import com.nextiva.nextivaapp.android.constants.FragmentTags
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetContactSelectionFragment.Companion.SELECTED_CONTACT_PARTICIPANT_INFO
import com.nextiva.nextivaapp.android.view.compose.ConnectCallTransferTypeSelectionView
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetTransferSelectionViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetCallTransferTypeSelection() : BaseBottomSheetDialogFragment() {
    private val viewModel: BottomSheetTransferSelectionViewModel by viewModels()
    private var selectedContactParticipantInfo: ParticipantInfo? = null
    private var onTransferCancelledListener: (() -> Unit)? = null

    constructor(selectedContactInfo: ParticipantInfo, onTransferCancelledListener: (() -> Unit)): this(){
        this.onTransferCancelledListener = onTransferCancelledListener
        this.selectedContactParticipantInfo = selectedContactInfo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFullHeight = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.onCloseButtonClicked.observe(viewLifecycleOwner, onCloseButtonClickedObserver)
        viewModel.onWarmTransferOptionSelectedLiveData.observe(viewLifecycleOwner, onWarmOptionSelectedObserver)
        viewModel.onChangeTransferClickedLiveData.observe(viewLifecycleOwner, onChangeTransferClickedObserver)

        return ComposeView(requireContext()).apply {
            setContent {
                val viewState by viewModel.callTransferSelectionViewStateFlow.collectAsStateWithLifecycle()
                ConnectCallTransferTypeSelectionView(viewState = viewState)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated(selectedContactParticipantInfo)
    }

    private val onCloseButtonClickedObserver = Observer<Unit> {
        onTransferCancelledListener?.invoke()
        this.dismiss()
    }

    private val onWarmOptionSelectedObserver = Observer<Unit> {
        showBottomSheetWarmTransferFragment()
        dismiss()
    }

    private val onChangeTransferClickedObserver = Observer<Unit>{
        this.dismiss()
    }

    private fun showBottomSheetWarmTransferFragment() {
        val bottomSheetWarmTransferFragment = BottomSheetWarmTransferFragment().apply {
            arguments = Bundle().apply {
                putSerializable(SELECTED_CONTACT_PARTICIPANT_INFO, selectedContactParticipantInfo)
            }
        }
        bottomSheetWarmTransferFragment.show(parentFragmentManager, FragmentTags.CALL_WARM_TRANSFER_CONFIRMATION)
    }


    override fun onPause() {
        super.onPause()
        viewModel.compositeDisposableClear()
    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.compositeDisposableClear()
    }
}