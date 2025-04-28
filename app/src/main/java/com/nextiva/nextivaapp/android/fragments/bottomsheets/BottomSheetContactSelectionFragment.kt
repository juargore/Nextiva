package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.FragmentTags
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.view.compose.ConnectContactSelectionView
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectContactListItemViewState
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetContactSelectionViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetContactSelectionFragment() : BaseBottomSheetDialogFragment() {
    companion object {
        const val SELECTED_CONTACT_PARTICIPANT_INFO = "selected_contact_participant_info"
        const val CONTACT_SELECTION_ADD_CALL = "contact_selection_add_call"
        const val CONTACT_SELECTION_TRANSFER_CALL = "contact_selection_transfer_call"
        const val CONTACT_SELECTION_TRANSFER_TO_MOBILE = "contact_selection_transfer_to_mobile"
    }

    private var onContactSelectedListener: ((ParticipantInfo) -> Unit)? = null
    private var contactSelectionType: String = ""

    constructor(contactSelectionType: String, onResultListener: (ParticipantInfo) -> Unit) : this() {
        this.onContactSelectedListener = onResultListener
        this.contactSelectionType = contactSelectionType
    }

    private val viewModel: BottomSheetContactSelectionViewModel by viewModels()

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
        viewModel.onContactItemClickedLiveData.observe(viewLifecycleOwner, onContactItemClickedObserver)
        viewModel.onContactSelectionTypeUpdated(contactSelectionType)

        return ComposeView(requireContext()).apply {
            setContent {
                val lazyPagingList: LazyPagingItems<ConnectContactListItemViewState> = viewModel.listItems.collectAsLazyPagingItems()
                val viewState by viewModel.contactSelectionViewStateFlow.collectAsStateWithLifecycle()
                ConnectContactSelectionView(viewState = viewState, lazyPagingList)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        return dialog
    }

    private val onCloseButtonClickedObserver = Observer<Unit> {
        hideKeyboard()
        this.dismiss()
    }

    private val onContactItemClickedObserver = Observer<Pair<NextivaContact, String>> {
       onContactSelected(it)
    }

    private fun onContactSelected(contactPair: Pair<NextivaContact, String>) {
        val participantInfo = createParticipantInfo(contactPair)

        when (contactSelectionType) {
            CONTACT_SELECTION_ADD_CALL,
            CONTACT_SELECTION_TRANSFER_TO_MOBILE -> {
                onContactSelectedListener?.invoke(participantInfo)
                dismiss()
            }
            CONTACT_SELECTION_TRANSFER_CALL -> {
                viewModel.clearCurrentSearch()
                showCallTransferTypeSelection(participantInfo)
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireView()
        view.let {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun createParticipantInfo(contactPair: Pair<NextivaContact, String>): ParticipantInfo {
        return ParticipantInfo(contactId = contactPair.first.userId,
                                displayName = contactPair.first.uiName,
                                numberToCall = contactPair.second.replace("x",";"),
                                callType = Enums.Sip.CallTypes.VOICE,
                                dialingServiceType = Enums.Service.DialingServiceTypes.VOIP)
    }

    private fun showCallTransferTypeSelection(participantInfo: ParticipantInfo) {
            BottomSheetCallTransferTypeSelection(participantInfo, onTransferCancelledListener = {
                this.dismiss()
            }).show(
                childFragmentManager,
                FragmentTags.CALL_TRANSFER_TYPE_SELECTION
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.queryUpdated("")
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