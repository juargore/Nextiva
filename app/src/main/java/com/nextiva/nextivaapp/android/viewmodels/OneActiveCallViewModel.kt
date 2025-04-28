package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneNumberUtils
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.constants.Enums.AudioDevices.AudioDevice
import com.nextiva.nextivaapp.android.constants.Enums.FontAwesomeIconType
import com.nextiva.nextivaapp.android.constants.Enums.Logging.STATE_INFO
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.SipApiManager
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.AudioDeviceInfo
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.ConferenceCallee
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.IncomingCall
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SingleEvent
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.CallSession
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.ApplicationUtil
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.view.compose.viewstate.ButtonStateEnum
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectActiveCallButtonViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectActiveCallViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectAvatarViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectButtonType
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectCallBannerViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectCallerInfoViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectHeaderViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectTextButtonViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.DrawableIcons
import com.nextiva.nextivaapp.android.view.compose.viewstate.FontAwesomeIcons
import com.nextiva.nextivaapp.android.view.compose.viewstate.MenuItem
import com.nextiva.nextivaapp.android.view.compose.viewstate.PopupDetails
import com.nextiva.pjsip.pjsip_lib.sipservice.CallState
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import com.nextiva.pjsip.pjsip_lib.sipservice.SipConnectionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OneActiveCallViewModel @Inject constructor(
    application: Application,
    private val nextivaApplication: Application,
    private val sessionManager: SessionManager,
    private val sipManager: PJSipManager,
    private val schedulerProvider: SchedulerProvider,
    private val sipApiManager: SipApiManager,
    private val dbManager: DbManager,
    private val permissionManager: PermissionManager,
    private val analyticsManager: AnalyticsManager,
    private val avatarManager: AvatarManager,
    private val settingsManager: SettingsManager,
    private val mLogManager: LogManager
) : BaseViewModel(application) {

    val ACTIVE_CALL_TRANSFER_OPTION_CALL_NUMBER_FIRST_INDEX = 0
    val ACTIVE_CALL_TRANSFER_OPTION_TRANSFER_TO_NUMBER_INDEX = 1

    private val holdDebounceDelay = 1000

    private var mIsHoldDebounce = false
    private val mHoldDebounceHandler = Handler(Looper.getMainLooper())
    private val mHoldDebounceRunnable =
        Runnable { mIsHoldDebounce = false }

    var connectionLost = false

    val startNewCallMutableLiveData = MutableLiveData<SingleEvent<Boolean>>()

    val activeCallSessionLiveData: LiveData<SipCall?> = sipManager.activeCallLiveData
    val activeCallDurationLiveData: LiveData<String> = sipManager.activeCallDurationLiveData
    val passiveCallSessionLiveData: LiveData<SipCall?> = sipManager.passiveCallLiveData
    val isStackStartedLiveData: LiveData<Boolean?> = sipManager.isStackStartedLiveData
    val sipConnectionStatusLiveData: LiveData<SipConnectionStatus> = sipManager.sipConnectionStatusLiveData
    val onKeyPadButtonClickedLiveData: MutableLiveData<Unit> = MutableLiveData()
    val onAddOrMergeButtonClickedLiveData: MutableLiveData<Unit> = MutableLiveData()
    val onAddCallPopupOptionClickedLiveData: MutableLiveData<Unit> = MutableLiveData()
    val onTransferToMobileOptionClickedLiveData: MutableLiveData<Unit> = MutableLiveData()
    val onCallTransferOptionClickedLiveData: MutableLiveData<Unit> = MutableLiveData()
    val onSpeakerButtonClickedLiveData: MutableLiveData<Unit> = MutableLiveData()
    val onPassiveCallResumeButtonClickedLiveData: MutableLiveData<Unit> = MutableLiveData()
    val dismissBottomSheetLiveData: MutableLiveData<Unit> = MutableLiveData()
    val onActiveCallerInfoCLickedLiveData: MutableLiveData<List<ParticipantInfo>> = MutableLiveData()
//    val activeCallStatusLiveData = sipManager.activeCallStatusLiveData.map { status ->
//        return@map if (!TextUtils.equals(
//                status,
//                application.getString(R.string.in_call_ongoing_call)
//            )
//        ) status else ""
//    }
    private val _activeCallViewStateFlow: MutableStateFlow<ConnectActiveCallViewState> =
        MutableStateFlow(buildActiveCallViewState())
    val activeCallViewStateFlow: StateFlow<ConnectActiveCallViewState>
        get() = _activeCallViewStateFlow

    val audioDevices: LiveData<Pair<AudioDevice, List<AudioDevice>>> = sipManager.audioDeviceManager.availableAudioDevices
    private val _audioDevicesMenuItems = MutableLiveData<Pair<AudioDevice, List<AudioDeviceInfo>>>()
    val audioDevicesMenuItems get() : LiveData<Pair<AudioDevice, List<AudioDeviceInfo>>> = _audioDevicesMenuItems

    override fun onCleared() {
        super.onCleared()
     //   sipManager.stopRinger()
        mHoldDebounceHandler.removeCallbacks(mHoldDebounceRunnable)
    }

    fun startCall(participantInfo: ParticipantInfo) {
        if (participantInfo.contactId == null) {
            loadContactInfo(participantInfo)
        }

        sipManager.registerAndMakeCall(participantInfo)
    }

    fun startCall(participantInfo: ParticipantInfo, retrievalNumber: String?) {
        if (participantInfo.contactId == null) {
            loadContactInfo(participantInfo)
        }

        sipManager.registerAndMakeCall(participantInfo, retrievalNumber)
    }

    private fun createHeaderViewState(): ConnectHeaderViewState {
        return ConnectHeaderViewState(
            shouldShowRoundedCornerShape = true,
            onCloseButtonClick = { cancelTransfer() }
        )
    }

    fun onWarmTransferInitiated() {
        _activeCallViewStateFlow.update {
            it.copy(
                headerViewState = createHeaderViewState(),
                completeTransferButton = createCompleteTextButtonViewState(),
                cancelTransferButton = createCancelTextButtonViewState(),
                muteButton = muteButtonViewState(),
                holdButton = holdButtonViewState(),
                keyPadButton = keyButtonViewState()
            )
        }
    }

    private fun createCancelTextButtonViewState() = ConnectTextButtonViewState(
        text = application.getString(R.string.call_transfer_cancel),
        buttonType = ConnectButtonType.SECONDARY,
        textColor = R.color.connectSecondaryDarkBlue,
        leadingIcon = R.string.fa_times,
        isButtonEnabled = true,
        onButtonClicked = {
            cancelTransfer()
        }
    )

    private fun createCompleteTextButtonViewState() = ConnectTextButtonViewState(
        text = application.getString(R.string.active_call_complete_transfer),
        buttonType = ConnectButtonType.PRIMARY,
        textColor = R.color.connectWhite,
        leadingIcon = R.string.fa_check,
        isButtonEnabled = true,
        onButtonClicked = {
            warmTransferCall()
            dismissBottomSheetLiveData.value = Unit
        }
    )

    private fun cancelTransfer() {
        analyticsManager.logEvent(
            ScreenName.ACTIVE_CALL,
            Enums.Analytics.EventName.END_CALL_TRANSFER_BUTTON_PRESSED
        )
        endCall()
        dismissBottomSheetLiveData.value = Unit
        onWarmTransferDismissed()
    }

    fun onWarmTransferDismissed() {
        _activeCallViewStateFlow.update {
            it.copy(
                headerViewState = null,
                completeTransferButton = null,
                cancelTransferButton = null
            )
        }
    }

    private fun isHoldDebounced(): Boolean {
        return if (mIsHoldDebounce) {
            false

        } else {
            mIsHoldDebounce = true
            mHoldDebounceHandler.removeCallbacks(mHoldDebounceRunnable)
            mHoldDebounceHandler.postDelayed(mHoldDebounceRunnable, holdDebounceDelay.toLong())
            true
        }
    }

    fun isCallSwappable(): Boolean {
         return (sipManager.getActiveCalls()?.size ?: 0) > 1
    }

    fun toggleNewCallSwap() {
        if (!settingsManager.isOldActiveCallLayoutEnabled) {
            if (isCallSwappable()) {
                swapCalls()
            }
        } else {
            if (isCallSwappable()) {
                swapCalls()

            } else {
                startNewCallMutableLiveData.postValue(SingleEvent(true))
            }
        }
    }

    fun toggleMute() {
        sipManager.getCurrentCall()?.let { currentCall ->
            if (currentCall.isLocalMute) {
                sipManager.unmuteCall(currentCall.id)
            } else {
                sipManager.muteCall(currentCall.id)
            }
        }
    }

    fun toggleHold() {
        if (isHoldDebounced()) {
            sipManager.getCurrentCall()?.let { currentCall ->
                if (currentCall.isLocalHold) {
                    sipManager.unholdCall(currentCall.id, false)
                } else {
                    sipManager.holdCall(currentCall.id, true)
                }
            }
        }
    }

    fun endCall() {
        val currentCall = sipManager.getCurrentCall()
        if (currentCall?.id != null) {
            mLogManager.logToFile(STATE_INFO, "Call ended with a valid id: ${currentCall.id}")
            sipManager.endCall(currentCall.id)
        } else {
            val activeCalls = sipManager.getActiveCalls()
            if (!activeCalls.isNullOrEmpty()) {
                mLogManager.logToFile(STATE_INFO, "Call ended iterating activeCalls")
                activeCalls.forEach { call ->
                    sipManager.endCall(call.id)
                }
            } else {
                mLogManager.logToFile(STATE_INFO, "Call ended stopping the stack")
                sipManager.stopStackIfNecessary()
            }
        }
    }

    fun swapCalls() {
        // Hold is affected when swapping a call, so the debounce will need to be fired and checked.
        if (isHoldDebounced()) {
            sipManager.swapCalls()
        }
    }

    fun playDialerKeyPress(dialerKey: String) {
        activeCallSessionLiveData.value?.id?.let { sipManager.playDtmfTone(it, dialerKey) }
    }

    fun getIncomingCall(): IncomingCall? {
        return sipManager.incomingCall
    }

    fun isCallQueued(): Boolean {
        return sipManager.isCallQueued
    }

    fun loadContactInfo(info: ParticipantInfo) {
        info.numberToCall?.let { number ->
            dbManager.getConnectContactFromPhoneNumber(number)
                .subscribe(object : DisposableSingleObserver<DbResponse<NextivaContact>>() {
                    override fun onSuccess(response: DbResponse<NextivaContact>) {
                        val contact = response.value ?: return
                        info.displayName = contact.uiName
                        info.contactId = contact.userId
                    }

                    override fun onError(e: Throwable) {
                        LogUtil.e("CallInfo: Error fetching contact info.")
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                })
        }
    }

    fun isCallPullCall(): Boolean {
//        sessionManager.featureAccessCodes?.featureAccessCodesList?.firstOrNull {
//            it.codeName == Enums.Service.FeatureAccessCodes.CALL_RETRIEVE
//        }?.let { pullCallCode ->
//            return TextUtils.equals(pullCallCode.code, sipManager.phoneNumber)
//        }

        return false
    }

    fun isConnectEnabled(): Boolean {
        return sessionManager.isNextivaConnectEnabled
    }

    //region Transfer Calls
    fun getTransferOptionsArrayList(callNumber: String?): ArrayList<String> {
        return arrayListOf(
            nextivaApplication.getString(
                R.string.active_call_transfer_call_number_first,
                callNumber
            ),
            nextivaApplication.getString(
                R.string.active_call_transfer_transfer_to_number,
                callNumber
            )
        )
    }

    fun transferCall(transferNumber: String?) {
        sipManager.getCurrentCall()?.let { currentCall ->
            transferNumber?.let {
                sipManager.blindTransfer(currentCall.id, CallUtil.cleanPhoneNumber(transferNumber))
            }
        }
    }

    fun warmTransferCall() {
        sipManager.completeWarmTransfer()
    }
    //endregion Transfer Calls

    //region Conference
    fun startConferenceCall(numberToConference: ParticipantInfo?) {
       // sipManager.startConferenceCall(numberToConference, localVideo, remoteVideo)
    }

    fun getConferenceCalleesArrayList(callSession: CallSession): ArrayList<ConferenceCallee> {
        val conferenceCallees = ArrayList<ConferenceCallee>()
        var displayName: String?

//        for (callInfo in callSession.callInfoArrayList) {
//            displayName = when {
//                !callInfo.displayName.isNullOrEmpty() || !callInfo.nextivaContact?.displayName.isNullOrEmpty() -> callInfo.displayName
//                !callInfo.nextivaContact?.firstName.isNullOrEmpty() && !callInfo.nextivaContact?.lastName.isNullOrEmpty() ->
//                    "${callInfo.nextivaContact?.firstName ?: ""} ${callInfo.nextivaContact?.lastName ?: ""}"
//
//                !callInfo.nextivaContact?.firstName.isNullOrEmpty() -> callInfo.nextivaContact?.firstName
//                !callInfo.nextivaContact?.lastName.isNullOrEmpty() -> callInfo.nextivaContact?.lastName
//                else -> callInfo.numberToCall
//            }
//
//            conferenceCallees.add(ConferenceCallee(displayName, callInfo.numberToCall))
//        }

        return conferenceCallees
    }

    fun isActiveCallConferenceCall(): Boolean {
        return sipManager.isPresentCallConference()
    }

    private fun isHoldCallConferenceCall(): Boolean {
        return sipManager.isPassiveCallConference()
    }

    fun mergeCall() {
        sipApiManager.mergeCalls()
            .observeOn(schedulerProvider.ui())
            .subscribe(object : DisposableSingleObserver<Boolean>() {
                override fun onSuccess(success: Boolean) {
                    if (success) {
                        activeCallSessionLiveData.value?.let { sipCall ->
                            updateCallerInfoView(sipCall.participantInfoList, sipCall.isCallConference)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }

    fun showMergeCallOption(): Boolean {
        return if (isHoldCallConferenceCall()) {
            true
        } else {
            isLineConnectedOrTrying(activeCallSessionLiveData) && isLineConnectedOrTrying(passiveCallSessionLiveData)
        }
    }

    fun setIsDisplayCallIssueWarningEnabled(enabled: Boolean) {
       // sipManager.setIsDisplayCallIssueWarningEnabled(enabled)
    }


    private fun isLineConnectedOrTrying(liveDataCallSession: LiveData<SipCall?>?): Boolean {
        liveDataCallSession?.value?.let { session ->
            return session.state == CallState.CONNECTED || session.state == CallState.TRYING
        }

        return false
    }

    private fun buildActiveCallViewState() =
        ConnectActiveCallViewState(
            activeCallerInfo = callerInfoViewState(),
            holdButton = holdButtonViewState(),
            muteButton = muteButtonViewState(),
            keyPadButton = keyButtonViewState(),
            speakerButton = speakerButtonViewState(),
            addCallButton = addOrMergeCallButtonViewState(),
            moreButton = moreButtonViewState(),
            sipConnectionStatus = sipConnectionStatusLiveData.value ?: SipConnectionStatus.GOOD,
            wasSipConnectionBannerDismissed = false,
            onEndCallButtonClicked = { onEndCallButtonClicked() },
            onActiveCallerInfoClick = { onActiveCallerInfoClick() }
        )

    private fun onActiveCallerInfoClick() {
        activeCallSessionLiveData.value?.let {
            onActiveCallerInfoCLickedLiveData.value = it.participantInfoList
        }
    }

    private fun callerInfoViewState() = ConnectCallerInfoViewState(
        callerDisplayName = "",
        callerPhoneNumber = "",
        callTimer = "",
        shouldShowTimer = true
    )

    private fun moreButtonViewState() = ConnectActiveCallButtonViewState(
        fontAwesomeIcons = FontAwesomeIcons(
            normalIcon = R.string.fa_ellipsis_v,
            activatedIcon = R.string.fa_ellipsis_v,
            iconType = FontAwesomeIconType.SOLID
        ),
        buttonTitle = application.getString(R.string.active_call_more),
        onButtonClicked = {
            updateMoreButtonActiveState()
        },
        popupMenu = createMoreOptionPopupDetails()
    )

    private fun addOrMergeCallButtonViewState() = ConnectActiveCallButtonViewState(
        fontAwesomeIcons = FontAwesomeIcons(
            normalIcon = R.string.fa_plus,
            activatedIcon = R.string.fa_plus,
            iconType = FontAwesomeIconType.SOLID
        ),
        buttonTitle = application.getString(R.string.active_call_add_call_button),
        onButtonClicked = {
            onAddOrMergeCallButtonClicked()
        }
    )

    private fun onAddOrMergeCallButtonClicked() {
        onAddOrMergeButtonClickedLiveData.value = Unit
    }

    private fun speakerButtonViewState() = ConnectActiveCallButtonViewState(
        fontAwesomeIcons = FontAwesomeIcons(
            normalIcon = R.string.fa_volume,
            activatedIcon = R.string.fa_volume,
            iconType = FontAwesomeIconType.REGULAR
        ),
        buttonTitle = application.getString(R.string.active_call_speaker),
        popupMenu = PopupDetails()
    )

    private fun keyButtonViewState() = ConnectActiveCallButtonViewState(
        drawableIcons = DrawableIcons(
            normalIcon = R.drawable.ic_keypad,
            activatedIcon = R.drawable.ic_keypad
        ),
        buttonTitle = application.getString(R.string.active_call_keypad),
        onButtonClicked = {
            onKeypadButtonClicked()
        }
    )

    private fun muteButtonViewState() = ConnectActiveCallButtonViewState(
        drawableIcons = DrawableIcons(
            normalIcon = R.drawable.ic_microphone,
            activatedIcon = R.drawable.ic_microphone_slash,
            iconSize = R.dimen.material_text_display1
        ),
        buttonTitle = application.getString(R.string.active_call_mute),
        onButtonClicked = {
            onMuteCallButtonClicked()
        }
    )

    private fun holdButtonViewState() = ConnectActiveCallButtonViewState(
        fontAwesomeIcons = FontAwesomeIcons(
            normalIcon = R.string.fa_pause,
            activatedIcon = R.string.fa_play,
            iconType = FontAwesomeIconType.SOLID
        ),
        buttonTitle = application.getString(R.string.active_call_hold),
        onButtonClicked = {
            onHoldButtonClicked()
        }
    )


    private fun onEndCallButtonClicked() {
        analyticsManager.logEvent(
            ScreenName.ACTIVE_CALL,
            Enums.Analytics.EventName.END_CALL_BUTTON_PRESSED
        )
        endCall()
    }

    private fun onMuteCallButtonClicked() {
        analyticsManager.logEvent(
            ScreenName.ACTIVE_CALL,
            if (_activeCallViewStateFlow.value.muteButton.buttonState == ButtonStateEnum.ACTIVATED)
                Enums.Analytics.EventName.MUTE_BUTTON_DESELECTED else
                Enums.Analytics.EventName.MUTE_BUTTON_SELECTED
        )
        toggleMute()
    }

    private fun onHoldButtonClicked() {
        analyticsManager.logEvent(
            ScreenName.ACTIVE_CALL,
            if (_activeCallViewStateFlow.value.holdButton.buttonState == ButtonStateEnum.ACTIVATED)
                Enums.Analytics.EventName.HOLD_BUTTON_DESELECTED else
                Enums.Analytics.EventName.HOLD_BUTTON_SELECTED
        )
        toggleHold()
    }

    private fun onKeypadButtonClicked() {
        onKeyPadButtonClickedLiveData.value = Unit
    }

    private fun onSpeakerButtonClicked() {
        onSpeakerButtonClickedLiveData.value = Unit
    }

    private fun getCallPhoneNumberDisplayTxt(
        phoneNumber: String?,
        callInfoSize: Int,
        isConference: Boolean
    ): String {
        return if (callInfoSize > 1 || isConference) {
            application.getString(R.string.active_call_participants_count, callInfoSize.toString())
        } else {
            phoneNumber ?: ""
        }
    }

    fun updateCallerInfoView(participantInfoArrayList: ArrayList<ParticipantInfo>, isConference: Boolean) {
        val filteredList: ArrayList<ParticipantInfo> = if (isConference) ArrayList(participantInfoArrayList.filter { !it.hasLeftNWay }) else participantInfoArrayList
        val displayName = getCallerDisplayName(filteredList)
        val callerPhoneNumber =
            filteredList.firstOrNull { it.numberToCall != sessionManager.phoneNumberInformation.phoneNumber }?.numberToCall?.let {
                getCallerPhoneNumber(it)
            } ?: ""

        val callerPhoneNumberDisplayTxt =
            getCallPhoneNumberDisplayTxt(callerPhoneNumber, filteredList.size, isConference)

        _activeCallViewStateFlow.update {
            it.copy(
                activeCallerInfo = it.activeCallerInfo?.copy(
                    callerDisplayName = displayName,
                    callerPhoneNumber = callerPhoneNumberDisplayTxt,
                    shouldShowTimer = true,
                    conferenceParticipantCount = if (isConference && filteredList.size > 1) {
                        (filteredList.size - 1).toString()
                    } else {
                        null
                    }
                ),
                moreButton = if (isConference && filteredList.size > 1) {
                    it.moreButton.copy(
                        popupMenu = createMoreOptionPopupDetails(false)
                    )
                } else {
                    it.moreButton
                }
            )
        }
        updateAvatarInfo(callerPhoneNumber, displayName, onDbResponse = {
            updateActiveCallerAvatarViewState(it)
        })
    }

    fun updatePassiveCallBannerInfoView(participantInfoArrayList: ArrayList<ParticipantInfo>, isConference: Boolean) {
        val filteredList: ArrayList<ParticipantInfo> = if (isConference) ArrayList(participantInfoArrayList.filter { !it.hasLeftNWay }) else participantInfoArrayList
        val displayName = getCallerDisplayName(filteredList)
        val callerPhoneNumber = filteredList.firstOrNull()?.numberToCall?.let { getCallerPhoneNumber(it) } ?: ""

        val callerPhoneNumberDisplayTxt = getCallPhoneNumberDisplayTxt(callerPhoneNumber, filteredList.size, isConference)

        _activeCallViewStateFlow.update {
            it.copy(
                callBannerInfo = if (filteredList.isEmpty()) {
                    null
                } else {
                    it.callBannerInfo?.copy(
                        callerDisplayName = displayName,
                        callerPhoneNumber = callerPhoneNumberDisplayTxt,
                        onResumeButtonClicked = {
                            onPassiveCallResumeButtonClickedLiveData.value = Unit
                        }
                    ) ?: ConnectCallBannerViewState(
                        callerDisplayName = displayName,
                        callerPhoneNumber = callerPhoneNumberDisplayTxt,
                        onResumeButtonClicked = {
                            onPassiveCallResumeButtonClickedLiveData.value = Unit
                        }
                    )
                }
            )
        }
        updateAvatarInfo(callerPhoneNumber, displayName, onDbResponse = {
            updatePassiveCallerAvatarViewState(it)
        })
    }

    private fun updateAvatarInfo(phoneNumber: String?, displayName: String, onDbResponse: (AvatarInfo?)->Unit) {
        val avatarBuilder = AvatarInfo.Builder().isConnect(true)
        var avatarInfo: AvatarInfo?

        dbManager.getConnectContactFromPhoneNumber(phoneNumber)
            .subscribe(object : DisposableSingleObserver<DbResponse<NextivaContact>>() {
                override fun onSuccess(response: DbResponse<NextivaContact>) {
                    avatarInfo = response.value?.getAvatarInfo(true) ?: avatarBuilder
                        .setDisplayName(displayName)
                        .build()
                    onDbResponse.invoke(avatarInfo)
                }

                override fun onError(e: Throwable) {
                    avatarInfo = avatarBuilder
                        .setDisplayName(displayName)
                        .build()
                    onDbResponse.invoke(avatarInfo)
                }
            })
    }

    private fun updateActiveCallerAvatarViewState(avatarInfo: AvatarInfo?) {
        _activeCallViewStateFlow.update {
            it.copy(
                activeCallerInfo = it.activeCallerInfo?.copy(
                    avatarViewState = avatarInfo?.let { avatarInfo ->
                        it.activeCallerInfo.avatarViewState?.copy(
                            avatarBitMap = avatarManager.getBitmap(avatarInfo),
                            avatarInfo = avatarInfo
                        )
                    } ?: run {
                        ConnectAvatarViewState()
                    }
                ),
            )
        }
    }

    private fun updatePassiveCallerAvatarViewState(avatarInfo: AvatarInfo?) {
        _activeCallViewStateFlow.update {
            it.copy(
                callBannerInfo = it.callBannerInfo?.copy(
                    avatarViewState = avatarInfo?.let { avatarInfo ->
                        it.activeCallerInfo?.avatarViewState?.copy(
                            avatarBitMap = avatarManager.getBitmap(avatarInfo),
                            avatarInfo = avatarInfo
                        )
                    } ?: run {
                        ConnectAvatarViewState()
                    }
                ),
            )
        }
    }

    private fun getCallerPhoneNumber(phoneNumber: String): String {
        val phoneNumberDTMFArray = CallUtil.separatePhoneNumberFromDTMFTones(phoneNumber)
        return phoneNumberDTMFArray.firstOrNull()?.let { numberToFormat ->
            PhoneNumberUtils.formatNumber(numberToFormat, Locale.getDefault().country)
        } ?: ""
    }

    private fun getCallerDisplayName(participantInfoArrayList: java.util.ArrayList<ParticipantInfo>): String {

        val firstContactName = participantInfoArrayList
            .firstOrNull { it.numberToCall != sessionManager.phoneNumberInformation.phoneNumber }
            .let { callInfo ->
                when {
                    !callInfo?.displayName.isNullOrBlank() -> callInfo?.displayName!!
                    else -> {
                        val contact = dbManager.getConnectContactFromPhoneNumberInThread(callInfo?.numberToCall)
                        contact.value?.displayName ?: callInfo?.numberToCall
                        ?: application.getString(R.string.general_unavailable)
                    }
                }

            }

        return when (participantInfoArrayList.size) {
            0 -> ""
            1 -> firstContactName
            else -> {
                application.resources.getQuantityString(
                    R.plurals.contact_name_plurals,
                    participantInfoArrayList.size - 1,
                    firstContactName,
                    participantInfoArrayList.size - 1
                )
            }
        }
    }

    fun updateSipConnectionStatus(status: SipConnectionStatus) {
        _activeCallViewStateFlow.update {
            it.copy(sipConnectionStatus = status)
        }
    }

    fun updateCallTimerInfo(callTimer: String) {
        _activeCallViewStateFlow.update {
            it.copy(activeCallerInfo = it.activeCallerInfo?.copy(callTimer = formatCallTimer(callTimer)))
        }
    }

    fun updateCallStatus(callStatus: String){
        _activeCallViewStateFlow.update { it.copy(activeCallerInfo = it.activeCallerInfo?.copy(callTimer = callStatus))
        }
    }

    private fun formatCallTimer(callTimer: String): String {
        val parts = callTimer.split(":")
        if (parts.size > 2) {
            val (hours, minutes, seconds) = parts
            return if (hours == "00") {
                "$minutes:$seconds"
            } else {
                "$hours:$minutes:$seconds"
            }
        }
        return ""
    }

    fun updateMuteButtonState(muteButtonState: ButtonStateEnum) {
        val currentState = _activeCallViewStateFlow.value
        if (currentState.muteButton.buttonState != ButtonStateEnum.ACTIVATED) {
            _activeCallViewStateFlow.update {
                it.copy(
                    muteButton = it.muteButton.copy(buttonState = muteButtonState),
                )
            }
        }
    }

    fun updateHoldButtonState(holdButtonState: ButtonStateEnum) {
        if (_activeCallViewStateFlow.value.holdButton.buttonState != ButtonStateEnum.ACTIVATED) {
            _activeCallViewStateFlow.update {
                it.copy(
                    holdButton = it.holdButton.copy(
                        buttonState = holdButtonState
                    )
                )
            }
        }
    }

    fun updateSpeakerButtonState(speakerButtonState: ButtonStateEnum) {
        if (_activeCallViewStateFlow.value.speakerButton.buttonState != ButtonStateEnum.ACTIVATED) {
            _activeCallViewStateFlow.update {
                it.copy(
                    speakerButton = it.speakerButton.copy(buttonState = speakerButtonState),
                )
            }
        }
    }

    fun updateMoreButtonState(moreButtonState: ButtonStateEnum){
        _activeCallViewStateFlow.update { it.copy(moreButton = it.moreButton.copy(buttonState = moreButtonState))
        }
    }

    fun updateAddCallButtonState(addCallButtonState: ButtonStateEnum){
        if (_activeCallViewStateFlow.value.addCallButton.buttonState != ButtonStateEnum.ACTIVATED) {
            _activeCallViewStateFlow.update {
                it.copy(addCallButton = it.addCallButton.copy(buttonState = addCallButtonState))
            }
        }
    }

    fun updateKeyPadButtonState(keyPadButtonState: ButtonStateEnum){
        if (_activeCallViewStateFlow.value.keyPadButton.buttonState != ButtonStateEnum.ACTIVATED) {
            _activeCallViewStateFlow.update {
                it.copy(keyPadButton = it.keyPadButton.copy(buttonState = keyPadButtonState))
            }
        }
    }

    fun updateHoldButtonActiveState(isActive: Boolean) {
        val newState = if (isActive) ButtonStateEnum.ACTIVATED else ButtonStateEnum.NORMAL
        _activeCallViewStateFlow.update {
            it.copy(holdButton = it.holdButton.copy(buttonState = newState))
        }
    }

    fun updateMuteButtonActiveState(isActive: Boolean) {
        val newButtonState = if (isActive) ButtonStateEnum.ACTIVATED else ButtonStateEnum.NORMAL
        _activeCallViewStateFlow.update {
            it.copy(muteButton = it.muteButton.copy(buttonState = newButtonState))
        }
    }

    private fun updateMoreButtonActiveState() {
        val currentButtonState = _activeCallViewStateFlow.value.moreButton.buttonState
        val newButtonState = if (currentButtonState == ButtonStateEnum.NORMAL) {
            ButtonStateEnum.ACTIVATED
        } else {
            ButtonStateEnum.NORMAL
        }
        _activeCallViewStateFlow.update {
            it.copy(
                moreButton = it.moreButton.copy(buttonState = newButtonState)
            )
        }
    }

    private fun updateSpeakerMenuItems(speakerMenuItems: List<MenuItem>) {
        _activeCallViewStateFlow.update {
            it.copy(
                speakerButton = it.speakerButton.copy(
                    popupMenu = it.speakerButton.popupMenu.copy(
                        popUpItems = speakerMenuItems,
                        shouldShowSelection = true
                    ),
                    onButtonClicked = {
                        onSpeakerButtonClicked()
                    }
                )
            )
        }
    }

    private fun switchSpeakerIcon(
        @StringRes icon: Int,
        description: String,
        buttonState: ButtonStateEnum,
        buttonTitle: String
    ) {
        _activeCallViewStateFlow.value = _activeCallViewStateFlow.value.copy(
            speakerButton = _activeCallViewStateFlow.value.speakerButton.copy(
                fontAwesomeIcons = FontAwesomeIcons(
                    icon,
                    icon,
                    if (buttonState == ButtonStateEnum.ACTIVATED) {
                        FontAwesomeIconType.SOLID
                    } else {
                        FontAwesomeIconType.REGULAR
                    }
                ),
                contentDescription = description,
                buttonState = buttonState,
                buttonTitle = buttonTitle
            )
        )
    }

    fun shouldShowSpeakerPopup(): Boolean {
        return !_activeCallViewStateFlow.value.speakerButton.popupMenu.popUpItems.isNullOrEmpty()
    }

    private fun createSpeakerMenuItems(
        pair: Pair<AudioDevice, List<AudioDeviceInfo>>,
        onClick: ((AudioDevice) -> Unit)
    ): List<MenuItem> {
        return pair.second.takeIf { it.size >= 3 }?.map { audioDeviceInfo ->
            MenuItem(
                name = audioDeviceInfo.deviceName,
                icon = audioDeviceInfo.textResId,
                isSelected = pair.first == audioDeviceInfo.audioDevice,
                onItemClicked = { _ ->
                    disableAudioDeviceButton()
                    onClick(audioDeviceInfo.audioDevice)
                }
            )
        } ?: emptyList()
    }

    private fun createMoreOptionPopupDetails(isCallTransferEnabled: Boolean = true): PopupDetails {
        return PopupDetails(listOf(
            MenuItem(
                name = application.getString(R.string.active_call_transfer),
                icon = R.string.fa_phone_arrow_right,
                isEnabled = isCallTransferEnabled,
                fontAwesomeIconType = FontAwesomeIconType.SOLID,
                isSelected = false,
                onItemClicked = {
                    onCallTransferOptionClickedLiveData.value = Unit
                }
            ),
            MenuItem(
                name = application.getString(R.string.transfer_to_mobile),
                icon = R.string.fa_laptop_mobile,
                fontAwesomeIconType = FontAwesomeIconType.SOLID,
                isSelected = false,
                onItemClicked = {
                    onTransferToMobileOptionClickedLiveData.value = Unit
                }
            )
        ), onMenuDismissed = {
            updateMoreButtonActiveState()
        })
    }

    fun getAudioDeviceImageAndText(device: AudioDevice): AudioDeviceInfo {
        val textResId: Int
        val drawable: Drawable?
        val description: String
        val deviceName: String?

        when (device) {
            AudioDevice.SPEAKER_PHONE -> {
                textResId = R.string.fa_volume
                drawable = AppCompatResources.getDrawable(application, R.drawable.ic_speaker)
                description = application.getString(R.string.call_audio_speaker)
                deviceName = application.getString(R.string.call_audio_speaker)
            }
            AudioDevice.WIRED_HEADSET -> {
                textResId = R.string.fa_headings
                drawable = AppCompatResources.getDrawable(application, R.drawable.ic_headset_24)
                description = application.getString(R.string.general_headset)
                deviceName = application.getString(R.string.general_headset)
            }
            AudioDevice.EARPIECE -> {
                textResId = R.string.fa_phone
                drawable = AppCompatResources.getDrawable(application, R.drawable.ic_phone)
                description = application.getString(R.string.phone_type_general)
                deviceName = application.getString(R.string.phone_type_general)
            }
            AudioDevice.BLUETOOTH -> {
                textResId = R.string.fa_bluetooth_b
                drawable = AppCompatResources.getDrawable(application, R.drawable.ic_bluetooth_24)
                description = application
                    .getString(R.string.permission_notifications_type_bluetooth)
                deviceName = sipManager.getBluetoothName()
                        ?: application.getString(R.string.permission_notifications_type_bluetooth)
            }
            AudioDevice.NONE -> {
                textResId = R.string.fa_phone_volume
                drawable = AppCompatResources.getDrawable(application, R.drawable.ic_phone)
                description = application.getString(R.string.phone_type_general)
                deviceName = application.getString(R.string.phone_type_microphone)
            }
        }

        val color = if (ApplicationUtil.isNightModeEnabled(application, settingsManager)) {
            R.color.white
        } else {
            R.color.black
        }

        val tintedDrawable = drawable?.apply {
            DrawableCompat.setTint(
                this,
                ContextCompat.getColor(application, color)
            )
        }

        val formattedDescription = description.lowercase().replaceFirstChar { it.uppercaseChar() }

        val formattedDeviceName = deviceName.lowercase().replaceFirstChar { it.uppercase() }

        return AudioDeviceInfo(textResId, tintedDrawable, formattedDescription, formattedDeviceName, device)
    }

    /**
     * Sets the correct image in the speaker button view depending which audio device is currently
     * selected for the call: SPEAKER, EARPIECE, WIRED_HEADSET or BLUETOOTH
     *
     * @param currentDevice [AudioDevice], the audio device
     */
    private fun setSpeakerIcon(currentDevice: AudioDevice) {
        val audioDeviceInfo = getAudioDeviceImageAndText(device = currentDevice)
        val description = application.getString(R.string.call_audio_options, audioDeviceInfo.description)
        switchSpeakerIcon(
            audioDeviceInfo.textResId,
            description,
            ButtonStateEnum.ACTIVATED,
            application.getString(R.string.active_call_speaker_audio)
        )
    }

    fun createMenuItems(
        pair: Pair<AudioDevice, List<AudioDeviceInfo>>,
        onClick: ((AudioDevice) -> Unit)
    ) {
        val devices = pair.second
        val speakerMenuItems = createSpeakerMenuItems(pair, onClick)
        updateSpeakerMenuItems(speakerMenuItems)
        if (devices.size < 3) {
            switchSpeakerIcon(
                R.string.fa_volume,
                application.getString(R.string.active_call_speaker),
                if(pair.first == AudioDevice.SPEAKER_PHONE) ButtonStateEnum.ACTIVATED else ButtonStateEnum.NORMAL,
                application.getString(R.string.active_call_speaker)
            )
        } else {
            setSpeakerIcon(pair.first)
        }
    }

    fun disableAudioDeviceButton() {
        _activeCallViewStateFlow.update { activeCallViewState ->
            activeCallViewState.copy(
                speakerButton = activeCallViewState.speakerButton.copy(
                    onButtonClicked = null
                )
            )
        }
    }

    fun switchAddCallButton(
        drawableIcon: Int? = null,
        title: String,
        fontAwesomeIcon: Int? = null
    ) {
        _activeCallViewStateFlow.value = _activeCallViewStateFlow.value.copy(
            addCallButton = _activeCallViewStateFlow.value.addCallButton.copy(
                drawableIcons = drawableIcon?.let {
                    DrawableIcons(normalIcon = it, activatedIcon = it)
                },
                buttonTitle = title,
                fontAwesomeIcons = fontAwesomeIcon?.let {
                    FontAwesomeIcons(normalIcon = it, activatedIcon = it)
                },
            )
        )
    }

    fun setTransferButtonState(isEnabled: Boolean) {
        _activeCallViewStateFlow.update {
            it.copy(
                completeTransferButton = it.completeTransferButton?.copy(
                    isButtonEnabled = isEnabled
                )
            )
        }
    }

    fun configureDeviceMenu(current: AudioDevice, deviceList: List<AudioDevice>) {
        _audioDevicesMenuItems.value = Pair(
            current,
            mutableListOf<AudioDeviceInfo>().apply {
                addAll(deviceList.map { getAudioDeviceImageAndText(it) })
            }
        )
    }

    fun setAudioDevice(device: AudioDevice) {
        sipManager.setAudioDevice(device)
    }

    fun toggleSpeaker() {
        sipManager.toggleSpeaker()
    }
}