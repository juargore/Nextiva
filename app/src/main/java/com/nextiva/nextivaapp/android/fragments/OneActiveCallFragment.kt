package com.nextiva.nextivaapp.android.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.BuildConfig
import com.nextiva.nextivaapp.android.ConnectNewCallActivity.Companion.newIntent
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.ConferenceCalleeAdapter
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.constants.Enums.AudioDevices.AudioDevice
import com.nextiva.nextivaapp.android.constants.FragmentTags
import com.nextiva.nextivaapp.android.constants.RequestCodes
import com.nextiva.nextivaapp.android.databinding.FragmentOneActiveCallBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetContactSelectionFragment
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetContactSelectionFragment.Companion.CONTACT_SELECTION_ADD_CALL
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetContactSelectionFragment.Companion.CONTACT_SELECTION_TRANSFER_CALL
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetContactSelectionFragment.Companion.CONTACT_SELECTION_TRANSFER_TO_MOBILE
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetKeypadFragment
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetParticipantsFragment
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager.PermissionGrantedCallback
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.SingleEvent
import com.nextiva.nextivaapp.android.sip.CallSession
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.MenuUtil
import com.nextiva.nextivaapp.android.util.extensions.orZero
import com.nextiva.nextivaapp.android.view.DialerPadView.DialerPadClickListener
import com.nextiva.nextivaapp.android.view.OneActiveCallButtonView
import com.nextiva.nextivaapp.android.view.OneCallerInformationView
import com.nextiva.nextivaapp.android.view.compose.ConnectActiveCallView
import com.nextiva.nextivaapp.android.view.compose.viewstate.ButtonStateEnum
import com.nextiva.nextivaapp.android.viewmodels.OneActiveCallViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.CallState
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import com.nextiva.pjsip.pjsip_lib.sipservice.SipConnectionStatus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OneActiveCallFragment : BaseFragment(), DialerPadClickListener, PopupMenu.OnMenuItemClickListener {

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var logManager: LogManager
    @Inject
    lateinit var permissionManager: PermissionManager
    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var settingsManager: SettingsManager
    @Inject
    lateinit var sessionManager: SessionManager

    private var fragmentListener: ActiveCallFragmentListener? = null

    private val viewModel: OneActiveCallViewModel by viewModels()

    private val TAG = this::class.java.simpleName

    private lateinit var muteButton: OneActiveCallButtonView
    private lateinit var holdButton: OneActiveCallButtonView
    private lateinit var speakerButton: OneActiveCallButtonView
    private lateinit var keypadButton: OneActiveCallButtonView
    private lateinit var addVideoAddParticipantButton: OneActiveCallButtonView
    private lateinit var newCallSwapButton: OneActiveCallButtonView
    private lateinit var endCallButton: OneActiveCallButtonView
    private lateinit var moreButton: OneActiveCallButtonView
    private lateinit var callTransferCompleteButton: OneActiveCallButtonView
    private lateinit var callTransferEndButton: OneActiveCallButtonView
    private lateinit var activeCallerInformationView: OneCallerInformationView
    private lateinit var holdCallerInformationView: OneCallerInformationView
    private lateinit var transitionCallCallerInformationView: OneCallerInformationView
    private lateinit var videoRemoteContainerLayout: ConstraintLayout
    private lateinit var videoLocalContainerLayout: ConstraintLayout
    private lateinit var switchCameraButton: ImageButton
    private lateinit var poorConnectionConstraintLayout: ConstraintLayout
    private lateinit var poorConnectionTransferToMobileTextView: AppCompatTextView
    private lateinit var poorConnectionCloseAppCompatImageButton: AppCompatImageButton
    private lateinit var composeView: ComposeView
    private lateinit var callButtonsConstraintView: ConstraintLayout
    private lateinit var activeCallParentConstraintView: ConstraintLayout

    private var audioDeviceMenu: PopupMenu? = null

    private var conferenceResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        (result.data?.getSerializableExtra(Constants.EXTRA_PARTICIPANT_INFO) as? ParticipantInfo)?.let { participantInfo ->
            viewModel.loadContactInfo(participantInfo)
            viewModel.startConferenceCall(participantInfo)
        }
    }

    private var newCallLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            (result.data?.getSerializableExtra(Constants.EXTRA_PARTICIPANT_INFO) as? ParticipantInfo)?.let { participantInfo ->
                val retrievalNumber: String? = result.data?.getStringExtra(Constants.EXTRA_RETRIEVAL_NUMBER)

                viewModel.startCall(participantInfo, retrievalNumber)
            }
        }
    }

    private var transferLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        (result.data?.getSerializableExtra(Constants.EXTRA_PARTICIPANT_INFO) as? ParticipantInfo)?.let { participantInfo ->
            getTransferOptionsListDialog(participantInfo)
        }
    }

    private val callTimerObserver = Observer<String> { timerInSeconds: String? ->
        activeCallerInformationView.setCallStatus(timerInSeconds)
        if (timerInSeconds?.isNotEmpty() == true){
            viewModel.updateCallTimerInfo(timerInSeconds)
        }
    }

    private val startNewCallObserver = Observer<SingleEvent<Boolean>> { singleEvent ->
        if (singleEvent.contentIfNotHandled != null && singleEvent.peekContent()) {
            getNewCallActivityForResult()
        }
    }

    private val passiveCallObserver = Observer<SipCall?> { sipCall ->
        if (sipCall != null && sipCall.isActive) {
            sipCall.participantInfoList.forEach { participantInfo ->
                participantInfo.numberToCall?.let { number ->
                    if (number.contains("@") && participantInfo.contactId == null) {
                        viewModel.loadContactInfo(participantInfo)
                    }
                }
            }
        }

        if (sipCall != null && sipCall.isActive) {

            if (!settingsManager.isOldActiveCallLayoutEnabled) {
                viewModel.updatePassiveCallBannerInfoView(
                    sipCall.participantInfoList,
                    sipCall.isCallConference
                )
                viewModel.switchAddCallButton(
                    drawableIcon = R.drawable.ic_merge,
                    title = getString(R.string.active_call_merge)
                )

            } else {
                holdCallerInformationView.populateCallerInfo(
                    sessionManager,
                    sipCall.participantInfoList,
                    dbManager,
                    sipCall.isCallConference
                )
                holdCallerInformationView.setCallStatus(R.string.in_call_on_hold)
                holdCallerInformationView.visibility = View.VISIBLE
                newCallSwapButton.switchIconText(
                    R.string.fa_exchange,
                    getString(R.string.active_call_swap)
                )
            }
        } else {
            if (!settingsManager.isOldActiveCallLayoutEnabled) {
                val callInfoArrayList: ArrayList<ParticipantInfo> = ArrayList()
                viewModel.updatePassiveCallBannerInfoView(callInfoArrayList, false)
                viewModel.switchAddCallButton(fontAwesomeIcon = R.string.fa_plus, title = getString(R.string.active_call_add_call_button))

            } else {
                newCallSwapButton.switchIconText(R.string.fa_phone_plus, getString(R.string.active_call_new_call))
                holdCallerInformationView.visibility = View.GONE
                showTransferBottomButtons(false)
            }
        }
    }

    private val activeCallObserver = Observer<SipCall?> { sipCall ->
        sipCall?.let { sipCall ->
            if (sipCall.state == CallState.CLOSED) {
                fragmentListener?.onLastCallEnded()
                return@Observer

            } else if (sipCall.participantInfoList.isNotEmpty()) {
                activeCallerInformationView.populateCallerInfo(sessionManager, sipCall.participantInfoList, dbManager, sipCall.isCallConference)
                viewModel.updateCallerInfoView(sipCall.participantInfoList, sipCall.isCallConference)
            }

            holdButton.updateSelection(sipCall.isLocalHold)
            viewModel.updateHoldButtonActiveState(sipCall.isLocalHold)

            if (sipCall.isLocalHold) {
                setActiveCallButtonStateConnectedAndOnHold()
            } else {
                setActiveCallButtonState(sipCall.state)
            }

            muteButton.updateSelection(sipCall.isLocalMute)
            muteButton.setContentDescription(requireActivity())
            viewModel.updateMuteButtonActiveState(sipCall.isLocalMute)

        } ?: kotlin.run {
            if (viewModel.getIncomingCall() == null && !viewModel.isCallQueued()) {
                fragmentListener?.onLastCallEnded()
            }
        }

        addVideoAddParticipantButton.setContentDescription(requireActivity())
        holdButton.setContentDescription(requireActivity())
        moreButton.setContentDescription(requireActivity())
    }

    private val onActiveCallerInfoClickedObserver = Observer<List<ParticipantInfo>> {
        BottomSheetParticipantsFragment.newInstance(it).show(
            childFragmentManager,
            FragmentTags.PARTICIPANTS_LIST
        )
    }

    private val onKeyPadClickedObserver = Observer<Unit> {
        onKeypadButtonClicked()
    }

    private val onAddCallOrMergeButtonClickedObserver = Observer<Unit> {
        if (!viewModel.showMergeCallOption()) {
            showContactSelectionBottomSheet(CONTACT_SELECTION_ADD_CALL)
        } else {
            viewModel.mergeCall()
        }
    }

    private val onAddCallButtonClickedObserver = Observer<Unit> {
        showContactSelectionBottomSheet(CONTACT_SELECTION_ADD_CALL)
    }

    private val onTransferToMobileClickedObserver = Observer<Unit>{
        showContactSelectionBottomSheet(CONTACT_SELECTION_TRANSFER_TO_MOBILE)
    }

    private val onCallTransferOptionClickedObserver = Observer<Unit> {
        showContactSelectionBottomSheet(CONTACT_SELECTION_TRANSFER_CALL)
    }

    private val onSipConnectionStatusObserver = Observer<SipConnectionStatus> {
        viewModel.updateSipConnectionStatus(it)
    }

    private fun showContactSelectionBottomSheet(contactSelectionType: String) {
        BottomSheetContactSelectionFragment(
            contactSelectionType = contactSelectionType,
            onResultListener = {
                onContactSelectionResult(it, contactSelectionType)
            }).show(childFragmentManager, FragmentTags.CONTACT_SELECTION)
    }

    private fun onContactSelectionResult(participantInfo: ParticipantInfo, contactSelectionType: String) {
        if (contactSelectionType == CONTACT_SELECTION_ADD_CALL){
            participantInfo.numberToCall?.let {
                viewModel.startCall(participantInfo, participantInfo.numberToCall)
            }

        } else if (contactSelectionType == CONTACT_SELECTION_TRANSFER_TO_MOBILE){
            viewModel.transferCall(participantInfo.numberToCall)
        }
    }

    private val onSpeakerButtonClickedObserver = Observer<Unit> {
        onSpeakerButtonClicked()
    }

    private val onPassiveCallResumeButtonClickedObserver = Observer<Unit> {
        onResumeButtonClicked()
    }

    private fun onResumeButtonClicked() {
        analyticsManager.logEvent(
            ScreenName.ACTIVE_CALL,
            if (viewModel.isCallSwappable()) Enums.Analytics.EventName.SWAP_CALL_BUTTON_PRESSED else Enums.Analytics.EventName.NEW_CALL_BUTTON_PRESSED
        )
        viewModel.toggleNewCallSwap()
    }

    private val isStackStartedObserver = Observer<Boolean?> {
        if (it == false) {
            fragmentListener?.onLastCallEnded()
        }
    }

    private val audioDevicesObserver = Observer<Pair<AudioDevice, List<com.nextiva.nextivaapp.android.models.AudioDeviceInfo>>> { pair ->
        Log.d("OneActiveCallFragment", "AudioDevice : Current ${pair.first} : ${pair.second.map { it.deviceName }}")
        if(!settingsManager.isOldActiveCallLayoutEnabled) {
            viewModel.createMenuItems(pair) { viewModel.setAudioDevice(it) }
        }else {
            val devices = pair.second
            if (devices.size.orZero() >= 3) {
                audioDeviceMenu?.menu?.clear()
                audioDeviceMenu = PopupMenu(requireContext(), speakerButton).apply {
                    setOnMenuItemClickListener { menuItem ->
                        speakerButton.setOnClickListener(null)
                        viewModel.setAudioDevice(devices[menuItem.itemId].audioDevice)
                        true
                    }
                }
                devices.forEachIndexed { index, audioDeviceInfo ->
                    val drawable = if (audioDeviceInfo.deviceName == pair.first.name) {
                        audioDeviceInfo.icon
                            ?.apply {
                                DrawableCompat.setTint(
                                    this,
                                    ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                                )
                            }
                    } else {
                        audioDeviceInfo.icon
                    }
                    audioDeviceMenu?.menu?.add(
                        Menu.NONE,
                        index,
                        index,
                        audioDeviceInfo.description
                    )
                        ?.icon = drawable
                }

                try {
                    val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                    fieldMPopup.isAccessible = true
                    val mPopup = fieldMPopup.get(audioDeviceMenu)
                    mPopup.javaClass
                        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(mPopup, true)
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) Log.e(
                        TAG,
                        "Error showing menu icons in Audio Options",
                        e
                    )
                }
                setSpeakerIcon(pair.first)
            } else {
                audioDeviceMenu?.let {
                    speakerButton.switchIconText(
                        R.string.fa_volume,
                        getString(R.string.active_call_speaker)
                    )
                }
                audioDeviceMenu?.menu?.clear()
                audioDeviceMenu = null
            }

            if (pair.first == AudioDevice.SPEAKER_PHONE) {
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                speakerButton.setContentDescription(requireActivity())
            }
            speakerButton.isSelected = pair.first == AudioDevice.SPEAKER_PHONE
            speakerButton.setOnClickListener { onSpeakerButtonClicked() }
        }
    }

    private fun showDialogIfCallIssue(isCallIssue: Boolean) {
        if (isCallIssue) {
            poorConnectionConstraintLayout.visibility = View.VISIBLE
            analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.POOR_CONNECTION_DIALOG_SHOWN)
            logManager.logToFile(getString(R.string.log_message_call_issue_dialog_shown))

        } else {
            poorConnectionConstraintLayout.visibility = View.GONE
            analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.POOR_CONNECTION_DIALOG_REMOVED)
            logManager.logToFile(getString(R.string.log_message_call_issue_dialog_removed))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view: View = bindViews(inflater, container)

        if (!settingsManager.isOldActiveCallLayoutEnabled) {
           loadNewActiveCallLayout()
        } else {
            loadOldActiveCallLayout()
        }

        if (activity == null) {
            return view
        }

        val participantInfo = arguments?.getSerializable(Constants.Calls.PARAMS_PARTICIPANT_INFO) as ParticipantInfo?
        val retrievalNumber = arguments?.getString(Constants.Calls.PARAMS_RETRIEVAL_NUMBER)
        val isFromNotification: Boolean = arguments?.getSerializable(Constants.EXTRA_OPENED_FROM_NOTIFICATION) as? Boolean == true

        when {
            participantInfo == null -> fragmentListener?.onLastCallEnded()
            TextUtils.isEmpty(participantInfo.numberToCall) -> viewModel.endCall()
            else -> {
                //If permission is granted the permission listener will fire.
                if(!isFromNotification) {
                    permissionManager.requestVoiceCallPermission(
                        requireActivity(),
                        ScreenName.ACTIVE_CALL,
                        getPermissionGrantedStartCallCallback(participantInfo, retrievalNumber),
                        requireActivity().currentFocus
                    )
                }
            }
        }

        participantInfo?.let {
            if (!isFromNotification) {
                val participantInfoList = ArrayList<ParticipantInfo>()
                participantInfoList.add(participantInfo)
                activeCallerInformationView.populateCallerInfo(sessionManager, participantInfoList, dbManager, false)
                viewModel.updateCallerInfoView(participantInfoList, false)
            }
        }

        viewModel.audioDevices.observe(viewLifecycleOwner) { audioDevices ->
            val current = audioDevices.first
            val devices = audioDevices.second
            viewModel.configureDeviceMenu(current, devices)
        }

        viewModel.startNewCallMutableLiveData.observe(viewLifecycleOwner, startNewCallObserver)
        viewModel.activeCallDurationLiveData.observe(viewLifecycleOwner, callTimerObserver)
        viewModel.activeCallSessionLiveData.observe(viewLifecycleOwner, activeCallObserver)
        viewModel.passiveCallSessionLiveData.observe(viewLifecycleOwner, passiveCallObserver)
        viewModel.audioDevicesMenuItems.observe(viewLifecycleOwner, audioDevicesObserver)
        viewModel.onKeyPadButtonClickedLiveData.observe(viewLifecycleOwner, onKeyPadClickedObserver)
        viewModel.onSpeakerButtonClickedLiveData.observe(viewLifecycleOwner, onSpeakerButtonClickedObserver)
        viewModel.onActiveCallerInfoCLickedLiveData.observe(viewLifecycleOwner, onActiveCallerInfoClickedObserver)
        viewModel.onAddOrMergeButtonClickedLiveData.observe(viewLifecycleOwner, onAddCallOrMergeButtonClickedObserver)
        viewModel.onAddCallPopupOptionClickedLiveData.observe(viewLifecycleOwner, onAddCallButtonClickedObserver)
        viewModel.onTransferToMobileOptionClickedLiveData.observe(viewLifecycleOwner, onTransferToMobileClickedObserver)
        viewModel.onCallTransferOptionClickedLiveData.observe(viewLifecycleOwner, onCallTransferOptionClickedObserver)
        viewModel.onPassiveCallResumeButtonClickedLiveData.observe(viewLifecycleOwner, onPassiveCallResumeButtonClickedObserver)
        viewModel.isStackStartedLiveData.observe(viewLifecycleOwner, isStackStartedObserver)
        viewModel.sipConnectionStatusLiveData.observe(viewLifecycleOwner, onSipConnectionStatusObserver)
        return view
    }

    private fun loadOldActiveCallLayout() {
        context?.getColor(R.color.activeCallBackground)
            ?.let { activeCallParentConstraintView.setBackgroundColor(it) }
        callButtonsConstraintView.visibility = View.VISIBLE
        activeCallerInformationView.visibility = View.VISIBLE
        composeView.visibility = View.GONE
    }

    private fun loadNewActiveCallLayout() {
        context?.getColor(R.color.connectWhite)
            ?.let { activeCallParentConstraintView.setBackgroundColor(it) }
        callButtonsConstraintView.visibility = View.GONE
        activeCallerInformationView.visibility = View.GONE
        composeView.visibility = View.VISIBLE
        composeView.setContent {
            val viewState by viewModel.activeCallViewStateFlow.collectAsStateWithLifecycle()
            ConnectActiveCallView(connectActiveCallViewState = viewState) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    /**
     * Sets the correct image in the speaker button view depending which audio device is currently
     * selected for the call: SPEAKER, EARPIECE, WIRED_HEADSET or BLUETOOTH
     *
     * @param currentDevice [AudioDevice], the audio device
     */
    private fun setSpeakerIcon(currentDevice: AudioDevice) {
        if ((viewModel.audioDevices.value?.second?.size ?: 0) > 2) {
            val audioDeviceInfo = viewModel.getAudioDeviceImageAndText(device = currentDevice)
            val description = getString(R.string.call_audio_options, audioDeviceInfo.description)
            speakerButton.switchIconText(audioDeviceInfo.textResId, description)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        fragmentListener = null
        mCompositeDisposable.clear();
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            setFragmentListener(context as ActiveCallFragmentListener)
        } catch (e: ClassCastException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            throw UnsupportedOperationException(context.javaClass.simpleName + " must implement ActiveCallFragmentListener.")
        }
    }

    private fun bindViews(inflater: LayoutInflater, container: ViewGroup?): View {
        val binding = FragmentOneActiveCallBinding.inflate(inflater, container, false)

        // Call Action Views
        muteButton = binding.activeCallMuteButton
        holdButton = binding.activeCallHoldButton
        speakerButton = binding.activeCallSpeakerButton
        keypadButton = binding.activeCallKeypadButton
        addVideoAddParticipantButton = binding.activeCallVideoAddParticipantButton
        newCallSwapButton = binding.activeCallNewCallSwapButton
        endCallButton = binding.activeCallEndCallButton
        moreButton = binding.activeCallMoreButton
        callTransferCompleteButton = binding.activeCallTransferCompleteButton
        callTransferEndButton = binding.activeCallTransferEndButton
        callButtonsConstraintView = binding.baseConstraintLayout
        activeCallParentConstraintView = binding.activeCallParentConstraint

        //Caller Info Views
        activeCallerInformationView = binding.activeCallActiveCallerInformationView
        holdCallerInformationView = binding.activeCallHoldCallerInformationView
        transitionCallCallerInformationView = binding.activeCallTransitionCallCallerInformationView

        // Poor Connect Views
        poorConnectionConstraintLayout = binding.poorConnectionConstraintLayout
        poorConnectionCloseAppCompatImageButton = binding.poorConnectionCloseAppCompatImageButton
        poorConnectionTransferToMobileTextView = binding.poorConnectionTransferToMobileButtonTextView

        //Video Views
        videoRemoteContainerLayout = binding.activeCallRemoteVideoContainer
        videoLocalContainerLayout = binding.activeCallLocalVideoContainer
        switchCameraButton = binding.activeCallSwitchCameraButton
        composeView = binding.activeCallComposeView

        // Click Listeners
        muteButton.setOnClickListener { onMuteCallButtonClicked() }
        holdButton.setOnClickListener { onHoldButtonClicked() }
        keypadButton.setOnClickListener { onKeypadButtonClicked() }
        endCallButton.setOnClickListener { onEndCallButtonClicked() }
        callTransferEndButton.setOnClickListener { onEndCallTransferButtonClicked() }
        callTransferCompleteButton.setOnClickListener { onEndCallTransferCompleteButtonClicked() }
        newCallSwapButton.setOnClickListener { onNewCallSwapButtonClicked() }
        holdCallerInformationView.setOnClickListener { onHoldCallerInformationViewClicked() }
        holdCallerInformationView.setOnClickListener { onActiveCallHoldCallerInformationClicked() }
        moreButton.setOnClickListener { button: View -> onMoreButtonClicked(button) }
        speakerButton.setOnClickListener { onSpeakerButtonClicked() }
        activeCallerInformationView.setOnClickListener { onActiveCallActiveCallerInformationClicked() }
        poorConnectionCloseAppCompatImageButton.setOnClickListener { onPoorConnectionCloseButtonClicked() }
        poorConnectionTransferToMobileTextView.setOnClickListener { onPoorConnectionTransferToMobileButtonClicked() }

        return binding.root
    }

    fun setFragmentListener(listener: ActiveCallFragmentListener) {
        fragmentListener = listener
    }

    // --------------------------------------------------------------------------------------------
    //region On Click Events
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region On Click Events
    // --------------------------------------------------------------------------------------------
    private fun onMuteCallButtonClicked() {
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, if (muteButton.isSelected) Enums.Analytics.EventName.MUTE_BUTTON_DESELECTED else Enums.Analytics.EventName.MUTE_BUTTON_SELECTED)
        viewModel.toggleMute()
    }

    private fun onHoldButtonClicked() {
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, if (holdButton.isSelected) Enums.Analytics.EventName.HOLD_BUTTON_DESELECTED else Enums.Analytics.EventName.HOLD_BUTTON_SELECTED)
        viewModel.toggleHold()
    }

    private fun onSpeakerButtonClicked() {
        if (!settingsManager.isOldActiveCallLayoutEnabled) {
            if (!viewModel.shouldShowSpeakerPopup()) {
                viewModel.disableAudioDeviceButton()
                viewModel.toggleSpeaker()
            }
        } else {
            audioDeviceMenu?.let { popupMenu ->
                popupMenu.show()
            } ?: run {
                analyticsManager.logEvent(
                    ScreenName.ACTIVE_CALL,
                    if (speakerButton.isSelected)
                        Enums.Analytics.EventName.SPEAKER_BUTTON_DESELECTED
                    else
                        Enums.Analytics.EventName.SPEAKER_BUTTON_SELECTED
                )
                speakerButton.setOnClickListener(null)
                viewModel.toggleSpeaker()
            }
        }
    }

    private fun onKeypadButtonClicked() {
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.KEYPAD_BUTTON_PRESSED)
        if (!settingsManager.isOldActiveCallLayoutEnabled) {
            BottomSheetKeypadFragment().show(childFragmentManager, FragmentTags.DIALER_KEYPAD_DIALOG)
        } else {
            val fragment = DialerKeypadDialogFragment.newInstance()
            fragment.show(childFragmentManager, FragmentTags.DIALER_KEYPAD_DIALOG)
        }
    }

    private fun onEndCallButtonClicked() {
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.END_CALL_BUTTON_PRESSED)
        viewModel.endCall()
    }

    private fun onEndCallTransferButtonClicked() {
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.END_CALL_TRANSFER_BUTTON_PRESSED)
        showTransferBottomButtons(false)
        viewModel.endCall()
    }

    private fun onEndCallTransferCompleteButtonClicked() {
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.COMPLETE_CALL_TRANSFER_BUTTON_PRESSED)
        if (callTransferCompleteButton.isEnabled) {
            showTransferBottomButtons(false)
            viewModel.warmTransferCall()
        }
    }

    private fun onNewCallSwapButtonClicked() {
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, if (viewModel.isCallSwappable()) Enums.Analytics.EventName.SWAP_CALL_BUTTON_PRESSED else Enums.Analytics.EventName.NEW_CALL_BUTTON_PRESSED)
        viewModel.toggleNewCallSwap()
    }

    private fun onHoldCallerInformationViewClicked() {
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.ON_HOLD_CALL_HEADER_PRESSED)

        //If a call is swapped we need to block another swap until the new active call returns
        newCallSwapButton.isEnabled = false
        viewModel.swapCalls()
    }

    private fun onMoreButtonClicked(view: View) {
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.MORE_BUTTON_PRESSED)

        if (activity == null) {
            return
        }

        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.menuInflater.inflate(R.menu.menu_active_call_more, popupMenu.menu)

        val transferToMobile = popupMenu.menu.findItem(R.id.transferToMobile)
        val transfer = popupMenu.menu.findItem(R.id.transfer)
        val conference = popupMenu.menu.findItem(R.id.conference)
        val merge = popupMenu.menu.findItem(R.id.merge)

        when {
            viewModel.isActiveCallConferenceCall() -> {
                transfer.isVisible = false
                transferToMobile.isVisible = false
                conference.isVisible = false
                merge.isVisible = false
            }
            viewModel.showMergeCallOption() -> {
                transfer.isVisible = true
                transferToMobile.isVisible = true
                merge.isVisible = true
                conference.isVisible = false
            }
            else -> {
                transfer.isVisible = true
                transferToMobile.isVisible = true
                conference.isVisible = true
                conference.setTitle(R.string.active_call_conference)
            }
        }

        MenuUtil.setMenuContentDescriptions(popupMenu.menu)
        popupMenu.show()
    }

    private fun onActiveCallActiveCallerInformationClicked() {
//        viewModel.activeCallSessionLiveData.value?.let { session ->
//            if (session.callInfoArrayList.isNotEmpty() && session.isCallConference) {
//                showConferenceCalleeDialog(session)
//            }
//        }
    }

    private fun onActiveCallHoldCallerInformationClicked() {
//        viewModel.passiveCallSessionLiveData.value?.let { session ->
//            if (session.callInfoArrayList.isNotEmpty() && session.isCallConference) {
//                showConferenceCalleeDialog(session)
//            }
//        }
    }

    private fun onPoorConnectionCloseButtonClicked() {
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.POOR_CONNECTION_DIALOG_CLOSE_BUTTON_PRESSED)
        logManager.logToFile(getString(R.string.log_message_call_issue_dialog_closed))
        viewModel.setIsDisplayCallIssueWarningEnabled(false)
        poorConnectionConstraintLayout.visibility = View.GONE
    }

    private fun onPoorConnectionTransferToMobileButtonClicked() {
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.POOR_CONNECTION_DIALOG_TRANSFER_TO_MOBILE_BUTTON_PRESSED)
        logManager.logToFile(getString(R.string.log_message_call_issue_dialog_transferred_to_mobile))
        poorConnectionConstraintLayout.visibility = View.GONE
        showTransferToMobileDialog()
    }

    private fun getNewCallActivityForResult() {
        newCallLauncher.launch(newIntent(activity, RequestCodes.NewCall.NEW_CALL_REQUEST_CODE))
    }

    // --------------------------------------------------------------------------------------------
    // endregion DialerPadView.DialerPadClickListener Methods
    // --------------------------------------------------------------------------------------------
    private fun showTransferBottomButtons(isShow: Boolean) {
        if (isShow) {
            endCallButton.visibility = View.GONE
            moreButton.visibility = View.GONE
            callTransferCompleteButton.visibility = View.VISIBLE
            callTransferEndButton.visibility = View.VISIBLE

        } else {
            endCallButton.visibility = View.VISIBLE

            if (!viewModel.isActiveCallConferenceCall()) {
                moreButton.visibility = View.VISIBLE
            } else {
                moreButton.visibility = View.GONE
            }

            callTransferCompleteButton.visibility = View.GONE
            callTransferEndButton.visibility = View.GONE
        }
    }

    private fun setActiveCallButtonState(callState: CallState) {
        LogUtil.d("TRANSFER: $callState")
        when (callState) {
            CallState.NONE, CallState.TRYING, CallState.INCOMING, CallState.CLOSED, CallState.FAILED -> {
                setCallTransferCompleteButtonState(false)
                setActiveCallButtonStateNotConnected()
            }
            CallState.CONNECTED -> {
                setActiveCallButtonConnected()
                setCallTransferCompleteButtonState(true)
            }
        }
    }

    private fun setSipButtonsDisabled() {
        //Top Buttons
        setActiveCallButtonEnabled(muteButton, false)
        setActiveCallButtonEnabled(holdButton, false)
        setActiveCallButtonEnabled(speakerButton, false)

        //Bottom buttons
        setActiveCallButtonEnabled(keypadButton, false)
        setActiveCallButtonEnabled(addVideoAddParticipantButton, false)
        setActiveCallButtonEnabled(newCallSwapButton, true)

        // ComposeView buttons
        viewModel.updateHoldButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateSpeakerButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateMuteButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateKeyPadButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateAddCallButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateMoreButtonState(ButtonStateEnum.DISABLED)

    }

    private fun setActiveCallButtonConnected() {
        //Top Buttons
        setActiveCallButtonEnabled(muteButton, true)
        setActiveCallButtonEnabled(holdButton, true)
        setActiveCallButtonEnabled(speakerButton, true)

//        viewModel.getCurrentAudioDevice()?.let { currentAudioDevice ->
//            speakerButton.isSelected = viewModel.getCurrentAudioDevice() == AudioDevice.SPEAKER_PHONE
//            setSpeakerIcon(currentAudioDevice)
//        }

        //Bottom buttons
        setActiveCallButtonEnabled(keypadButton, true)
        setActiveCallButtonEnabled(addVideoAddParticipantButton, true)
        setActiveCallButtonEnabled(newCallSwapButton, true)

        // ComposeView buttons
        viewModel.updateHoldButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateSpeakerButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateMuteButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateKeyPadButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateAddCallButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateMoreButtonState(ButtonStateEnum.NORMAL)
    }

    private fun setActiveCallButtonStateNotConnected() {
        //Top Buttons
        setActiveCallButtonEnabled(muteButton, true)
        setActiveCallButtonEnabled(holdButton, false)
        setActiveCallButtonEnabled(speakerButton, true)

        //Bottom buttons
        setActiveCallButtonEnabled(keypadButton, false)
        setActiveCallButtonEnabled(addVideoAddParticipantButton, false)
        setActiveCallButtonEnabled(newCallSwapButton, false)

        // ComposeView buttons
        viewModel.updateHoldButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateSpeakerButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateMuteButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateKeyPadButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateAddCallButtonState(ButtonStateEnum.DISABLED)
        viewModel.updateMoreButtonState(ButtonStateEnum.DISABLED)
    }

    private fun setActiveCallButtonStateConnectedAndOnHold() {
        //Top Buttons
        setActiveCallButtonEnabled(muteButton, true)
        setActiveCallButtonEnabled(holdButton, true)
        setActiveCallButtonEnabled(speakerButton, true)

        //Bottom buttons
        setActiveCallButtonEnabled(keypadButton, false)
        setActiveCallButtonEnabled(addVideoAddParticipantButton, false)
        setActiveCallButtonEnabled(newCallSwapButton, true)

        // ComposeView buttons
        viewModel.updateHoldButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateSpeakerButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateMuteButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateKeyPadButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateAddCallButtonState(ButtonStateEnum.NORMAL)
        viewModel.updateMoreButtonState(ButtonStateEnum.NORMAL)

    }

    private fun setCallTransferCompleteButtonState(enableButton: Boolean) {
        callTransferCompleteButton.isEnabled = enableButton

        if (!enableButton) {
            callTransferCompleteButton.setIconBackgroundResource(R.drawable.selector_active_call_gray_button)
        } else {
            callTransferCompleteButton.setIconBackgroundResource(R.drawable.selector_active_call_green_button)
        }
    }

    private fun setActiveCallButtonEnabled(button: OneActiveCallButtonView, state: Boolean) {
        if (!state) {
            button.updateSelection(false)
            button.setIconTint(R.color.activeCallDisabledTextGrey)
        } else {
            button.setIconTint(R.color.activeCallIcon)
        }

        button.isEnabled = state
        button.setContentDescription(requireContext())
    }

    private fun getTransferOptionsListDialog(participantInfo: ParticipantInfo) {
        mDialogManager.showSimpleListDialog(
            requireContext(),
            getString(R.string.active_call_transfer),
            null,
            viewModel.getTransferOptionsArrayList(participantInfo.numberToCall),
            { position: Int ->
                when (position) {
                    viewModel.ACTIVE_CALL_TRANSFER_OPTION_CALL_NUMBER_FIRST_INDEX -> {
                        viewModel.startCall(participantInfo)

                        showTransferBottomButtons(true)
                        setCallTransferCompleteButtonState(false)
                    }
                    viewModel.ACTIVE_CALL_TRANSFER_OPTION_TRANSFER_TO_NUMBER_INDEX -> viewModel.transferCall(participantInfo.numberToCall)
                    else -> LogUtil.d("Transfer option" + position + "not found.")
                }
            },
            { _: MaterialDialog?, _: DialogAction? ->
                analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.ACTIVE_CALL_TRANSFER_OPTIONS_LIST_SHOWN)

            }, R.color.connectSecondaryDarkBlue)
    }

    private fun showConferenceCalleeDialog(callSession: CallSession) {
        mDialogManager.showTwoLineListDialog(
            requireContext(),
            getString(R.string.active_call_participants),
            null,
            { },
            { _: MaterialDialog?, _: DialogAction? -> },
            R.color.connectSecondaryDarkBlue,
            ConferenceCalleeAdapter(viewModel.getConferenceCalleesArrayList(callSession))
        )
    }

    // --------------------------------------------------------------------------------------------
    //endregion On Click Events
    // --------------------------------------------------------------------------------------------

    private fun showTransferToMobileDialog() {
        if (context != null) {
            mDialogManager.showEditTextDialog(
                requireContext(),
                R.string.active_call_transfer_to_mobile,
                InputType.TYPE_CLASS_PHONE,
                20,
                getString(R.string.your_call_will_be_transferred_to),
                sharedPreferencesManager.getString(SharedPreferencesManager.THIS_PHONE_NUMBER, ""),
                null,
                getString(R.string.active_call_transfer),
                positiveTransferToMobile(),
                getString(R.string.general_cancel),
                negativeTransferToMobile(),
                R.color.connectSecondaryDarkBlue)
        }
    }

    private fun positiveTransferToMobile(): SingleButtonCallback {
        return SingleButtonCallback { dialog: MaterialDialog, _: DialogAction? ->

            val transferEditText = dialog.findViewById(R.id.text_input_dialog_edit_text) as EditText
            if (!TextUtils.isEmpty(transferEditText.text.toString())) {
                viewModel.transferCall(transferEditText.text.toString())
            }

            analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.TRANSFER_TO_MOBILE_CALL_DIALOG_ACCEPT_BUTTON_PRESSED)
        }
    }

    private fun negativeTransferToMobile(): SingleButtonCallback {
        return SingleButtonCallback { _: MaterialDialog?, _: DialogAction? ->
            analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.TRANSFER_TO_MOBILE_CALL_DIALOG_CANCEL_BUTTON_PRESSED)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.transfer -> {
                transferLauncher.launch(newIntent(activity, RequestCodes.NewCall.TRANSFER_REQUEST_CODE))
                true
            }
            R.id.conference -> {
                conferenceResultLauncher.launch(newIntent(activity, RequestCodes.NewCall.CONFERENCE_REQUEST_CODE))
                true
            }
            R.id.merge -> {
                viewModel.mergeCall()
                true
            }
            R.id.transferToMobile -> {
                showTransferToMobileDialog()
                true
            }
            else -> false
        }
    }

    // --------------------------------------------------------------------------------------------
    // region DialerPadView.DialerPadClickListener Methods
    // --------------------------------------------------------------------------------------------
    override fun onKeyPressed(key: String) {
        viewModel.playDialerKeyPress(key)
    }

    override fun onVoiceMailPressed() {}

    // --------------------------------------------------------------------------------------------
    // endregion DialerPadView.DialerPadClickListener Methods
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // endregion DialerPadView.DialerPadClickListener Methods
    // --------------------------------------------------------------------------------------------
    private fun getPermissionGrantedStartCallCallback(participantInfo: ParticipantInfo, retrievalNumber: String?): PermissionGrantedCallback {
        return PermissionGrantedCallback {
            if (participantInfo != viewModel.activeCallSessionLiveData.value?.participantInfoList?.firstOrNull() &&
                participantInfo != viewModel.getIncomingCall()?.pushNotificationCallInfo?.participantInfo) {
                viewModel.startCall(participantInfo, retrievalNumber)
            }
        }
    }

    interface ActiveCallFragmentListener {
        fun onLastCallEnded()
    }

    // --------------------------------------------------------------------------------------------
    // endregion DialerPadView.DialerPadClickListener Methods
    // --------------------------------------------------------------------------------------------

    companion object {

        fun newInstance(participantInfo: ParticipantInfo?, retrievalNumber: String?, openedFromNotification: Boolean): OneActiveCallFragment {
            val fragment = OneActiveCallFragment()
            val args = Bundle()
            args.putSerializable(Constants.Calls.PARAMS_PARTICIPANT_INFO, participantInfo)
            if (!TextUtils.isEmpty(retrievalNumber)) {
                args.putString(Constants.Calls.PARAMS_RETRIEVAL_NUMBER, retrievalNumber)
            }
            args.putBoolean(Constants.EXTRA_OPENED_FROM_NOTIFICATION, openedFromNotification)
            fragment.arguments = args
            return fragment
        }
    }
}