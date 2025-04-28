package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectAvatarViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectCallTransferSelectionViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectCallerInfoViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectHeaderViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectTextButtonViewState
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BottomSheetTransferSelectionViewModel @Inject constructor(
    application: Application, val dbManager: DbManager,
    private val platformContactsRepository: PlatformContactsRepository,
    val schedulerProvider: SchedulerProvider,
    val sipManager: PJSipManager,
    val avatarManager: AvatarManager
) : BaseViewModel(application) {
    val onCloseButtonClicked: MutableLiveData<Unit> = MutableLiveData()
    val onChangeTransferClickedLiveData: MutableLiveData<Unit> = MutableLiveData()
    val onWarmTransferOptionSelectedLiveData: MutableLiveData<Unit> = MutableLiveData()

    private val _callTransferSelectionViewStateFlow: MutableStateFlow<ConnectCallTransferSelectionViewState> =
        MutableStateFlow(ConnectCallTransferSelectionViewState())
    val callTransferSelectionViewStateFlow: StateFlow<ConnectCallTransferSelectionViewState>
        get() = _callTransferSelectionViewStateFlow

    fun onViewCreated(selectedParticipantInfo: ParticipantInfo?) {
        val builtContact = NextivaContact(selectedParticipantInfo?.numberToCall, Enums.Contacts.ContactTypes.UNKNOWN)

        if (selectedParticipantInfo?.sessionId != null) {
            dbManager.getConnectContactFromPhoneNumber(selectedParticipantInfo?.numberToCall)
                .observeOn(schedulerProvider.ui())
                .subscribe(object : DisposableSingleObserver<DbResponse<NextivaContact>>() {
                    override fun onSuccess(t: DbResponse<NextivaContact>) {
                        updateContactSelectionViewState(t.value, selectedParticipantInfo)
                    }

                    override fun onError(e: Throwable) {
                        updateContactSelectionViewState(builtContact, selectedParticipantInfo)
                    }
                })
        } else {
            updateContactSelectionViewState(builtContact, selectedParticipantInfo)
        }
    }

    fun compositeDisposableClear() {
        mCompositeDisposable.clear()
        platformContactsRepository.clearCompositeDisposable()
    }

    private fun createAvatarInfo(nextivaContact: NextivaContact?): AvatarInfo? {
        val avatarInfo = nextivaContact?.avatarInfo
        avatarInfo?.setIsConnect(true)
        if (nextivaContact?.aliases?.lowercase(Locale.ROOT)
                ?.contains(Constants.Contacts.Aliases.XBERT_ALIASES) == true
        ) {
            avatarInfo?.iconResId = R.drawable.xbert_avatar
        } else {
            avatarInfo?.iconResId = setIconBasedOnContactType(
                nextivaContact?.contactType ?: Enums.Contacts.ContactTypes.UNKNOWN
            )
        }
        return avatarInfo
    }

    private fun setIconBasedOnContactType(contactType: Int): Int {
        var iconId = 0
        if (contactType == Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW) {
            iconId = R.drawable.avatar_callflow
        } else if (contactType == Enums.Contacts.ContactTypes.CONNECT_TEAM) {
            iconId = R.drawable.avatar_team
        } else if (contactType == Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS) {
            iconId = R.drawable.avatar_callcenter
        }
        return iconId
    }

    private fun createConnectCallerInfoViewState(
        selectedContact: NextivaContact?,
        numberToCall: String?
    ): ConnectCallerInfoViewState {
        val phoneNumber = numberToCall ?: selectedContact?.allPhoneNumbers?.let {
            if (it.isNotEmpty()) it[0].toString() else ""
        } ?: ""
        val avatarBitmap = createAvatarInfo(selectedContact)?.let { avatarManager.getBitmap(it) }

        return ConnectCallerInfoViewState(
            callerDisplayName = selectedContact?.uiName,
            callerPhoneNumber = phoneNumber,
            avatarViewState = ConnectAvatarViewState(avatarBitMap = avatarBitmap)
        )
    }

    private fun createCancelTxtBtnViewState(): ConnectTextButtonViewState {
        return ConnectTextButtonViewState(
            text = application.getString(R.string.general_cancel),
            onButtonClicked = { onCloseButtonClicked.value = Unit },
            textColor = R.color.connectSecondaryDarkBlue
        )
    }

    private fun createConnectHeaderViewState(): ConnectHeaderViewState {
        return ConnectHeaderViewState(
            onCloseButtonClick = { onCloseButtonClicked.value = Unit },
            shouldShowRoundedCornerShape = true
        )
    }

    private fun updateViewState(
        callerInfoViewState: ConnectCallerInfoViewState,
        cancelTxtBtnViewState: ConnectTextButtonViewState,
        connectHeaderViewState: ConnectHeaderViewState,
        onBlindOptionSelected: () -> Unit,
        onWarmOptionSelected: () -> Unit,
        onChangeTransferSelected: () -> Unit
    ) {
        _callTransferSelectionViewStateFlow.update {
            it.copy(
                callerInfoViewState = callerInfoViewState,
                cancelTxtBtnViewState = cancelTxtBtnViewState,
                connectHeaderViewState = connectHeaderViewState,
                onBlindOptionSelected = onBlindOptionSelected,
                onWarmOptionSelected = onWarmOptionSelected,
                onChangeTransferSelected = onChangeTransferSelected
            )
        }
    }

    private fun updateContactSelectionViewState(selectedContact: NextivaContact?, selectedParticipantInfo: ParticipantInfo?) =
        selectedContact?.let {
            val connectCallerInfoViewState = createConnectCallerInfoViewState(selectedContact, selectedParticipantInfo?.numberToCall)
            val cancelTxtBtnViewState = createCancelTxtBtnViewState()
            val connectHeaderViewState = createConnectHeaderViewState()
            val onBlindOptionSelected = { onBlindOptionClicked(selectedParticipantInfo?.numberToCall) }
            val onWarmOptionSelected = { onWarmTransferOptionSelectedLiveData.value = Unit }
            val onChangeTransferSelected = { onChangeTransferClickedLiveData.value = Unit }

            updateViewState(
                connectCallerInfoViewState,
                cancelTxtBtnViewState,
                connectHeaderViewState,
                onBlindOptionSelected,
                onWarmOptionSelected,
                onChangeTransferSelected
            )
        }

    private fun onBlindOptionClicked(numberToCall: String?) {
        numberToCall?.let {
            sipManager.getActiveCalls()?.firstOrNull { it.isCurrent }?.let { call ->
                sipManager.blindTransfer(call.id, numberToCall)
            }
        }
    }
}