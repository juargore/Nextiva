package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.os.Build
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
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.view.DialerPadView
import com.nextiva.nextivaapp.android.view.compose.ConnectWarmTransferView
import com.nextiva.nextivaapp.android.view.compose.viewstate.ButtonStateEnum
import com.nextiva.nextivaapp.android.viewmodels.OneActiveCallViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.CallState
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetWarmTransferFragment() : BaseBottomSheetDialogFragment(),
    DialerPadView.DialerPadClickListener {

    private val viewModel: OneActiveCallViewModel by viewModels()
    private var selectedContactParticipantInfo: ParticipantInfo? = null
    private var isRemoteHold = false
    private var hasConnected = false

    private val passiveCallObserver = Observer<SipCall?> { callSession ->
        if (callSession == null && !hasConnected) {
            return@Observer
        }

        hasConnected = true

        if (shouldDismissWarmTransfer(callSession)) {
            viewModel.onWarmTransferDismissed()
            hasConnected = false
            dismiss()
        }
    }

    private val activeCallObserver = Observer<SipCall?> { callSession ->
        handleActiveCallSession(callSession)

        callSession?.let {
            viewModel.updateMuteButtonActiveState(it.isLocalMute)
            handleRemoteHoldState(it.isLocalHold)
        }
    }

    private fun handleActiveCallSession(sipCall: SipCall?) {
        sipCall?.let { session ->
            if (session.participantInfoList.isNotEmpty()) {
                processParticipantInfoList(session.participantInfoList, session.isCallConference)
            }
            updateHoldState(session.isLocalHold, session.state)
        }
    }

    private fun updateHoldState(isHold: Boolean, callState: CallState) {
        viewModel.updateHoldButtonActiveState(isHold)

        if (isRemoteHold) {
            setSipButtonsDisabled()
        } else {
            if (isHold) {
                setActiveCallButtonStateConnectedAndOnHold()
            } else {
                setActiveCallButtonState(callState)
            }
        }
    }

    private fun processParticipantInfoList(participantInfoList: ArrayList<ParticipantInfo>, isCallConference: Boolean) {
        viewModel.updateCallerInfoView(participantInfoList, isCallConference)

        participantInfoList.forEach { callInfo ->
            callInfo.numberToCall?.let { number ->
                if (number.contains("@") && callInfo.contactId == null) {
                    viewModel.loadContactInfo(callInfo)
                }
            }
        }
    }

    private fun setActiveCallButtonStateConnectedAndOnHold() {
        // ComposeView buttons
        viewModel.updateHoldButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateSpeakerButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateMuteButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateKeyPadButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateAddCallButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateMoreButtonState(ButtonStateEnum.DISABLED)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFullHeight = true
        selectedContactParticipantInfo = arguments?.let {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(BottomSheetContactSelectionFragment.SELECTED_CONTACT_PARTICIPANT_INFO) as ParticipantInfo
            } else {
                it.getSerializable(
                    BottomSheetContactSelectionFragment.SELECTED_CONTACT_PARTICIPANT_INFO,
                    ParticipantInfo::class.java
                )
            }
        }
        this.isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.activeCallSessionLiveData.observe(viewLifecycleOwner, activeCallObserver)
        viewModel.dismissBottomSheetLiveData.observe(viewLifecycleOwner, dismissBottomSheetObserver)
        viewModel.onKeyPadButtonClickedLiveData.observe(viewLifecycleOwner, onKeyPadClickedObserver)
        viewModel.passiveCallSessionLiveData.observe(viewLifecycleOwner, passiveCallObserver)
        viewModel.activeCallDurationLiveData.observe(viewLifecycleOwner, callTimerObserver)

        return ComposeView(requireContext()).apply {
            setContent {
                val viewState by viewModel.activeCallViewStateFlow.collectAsStateWithLifecycle()
                ConnectWarmTransferView(viewState = viewState)
            }
        }
    }

    private val onKeyPadClickedObserver = Observer<Unit> {
        onKeypadButtonClicked()
    }

    private fun onKeypadButtonClicked() {
        if (!settingsManager.isOldActiveCallLayoutEnabled) {
            BottomSheetKeypadFragment().show(childFragmentManager, FragmentTags.DIALER_KEYPAD_DIALOG)
        }
    }

    private val callTimerObserver = Observer<String> { timerInSeconds: String? ->
        if (timerInSeconds?.isNotEmpty() == true){
            viewModel.updateCallTimerInfo(timerInSeconds)
        }
    }

    private fun setSipButtonsDisabled() {
        viewModel.updateHoldButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateMuteButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateKeyPadButtonState(ButtonStateEnum.DISABLED)
    }

    private fun handleRemoteHoldState(isRemoteHold: Boolean) {
        if (isRemoteHold) {
            setSipButtonsDisabled()
        } else {
            setActiveCallButtonState(CallState.CONNECTED)
        }
        LogUtil.d("Call Remote hold: $isRemoteHold")
    }

    private fun setActiveCallButtonState(callState: CallState) {
        LogUtil.d("TRANSFER: $callState")
        when (callState) {
            CallState.NONE, CallState.TRYING, CallState.INCOMING, CallState.CLOSED, CallState.FAILED -> {
                setActiveCallButtonStateNotConnected()
                viewModel.setTransferButtonState(false)
            }

            CallState.CONNECTED -> {
                setActiveCallButtonConnected()
                viewModel.setTransferButtonState(true)
            }
        }
    }

    private fun setActiveCallButtonConnected() {
        viewModel.updateHoldButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateMuteButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateKeyPadButtonState(ButtonStateEnum.NORMAL)
    }

    private fun setActiveCallButtonStateNotConnected() {
        viewModel.updateHoldButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateMuteButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateKeyPadButtonState(ButtonStateEnum.DISABLED)
    }

    private val dismissBottomSheetObserver = Observer<Unit> {
        this.dismiss()
    }

    private fun shouldDismissWarmTransfer(sipCall: SipCall?): Boolean {
        return sipCall == null ||
                sipCall.state == CallState.CLOSED ||
                sipCall.participantInfoList.isEmpty()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedContactParticipantInfo?.let {
            viewModel.startCall(it)
            viewModel.onWarmTransferInitiated()
        }
    }

    override fun onKeyPressed(key: String) {
        viewModel.playDialerKeyPress(key)
    }

    override fun onVoiceMailPressed() {

    }
}