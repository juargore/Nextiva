package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectContactListItemViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectHeaderViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectParticipantsListViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectTextButtonViewState
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BottomSheetParticipantsViewModel @Inject constructor(
    application: Application, val dbManager: DbManager,
    val schedulerProvider: SchedulerProvider,
    val sessionManager: SessionManager
) : BaseViewModel(application) {

    val onCloseButtonClicked: MutableLiveData<Unit> = MutableLiveData()

    private val _participantsListViewStateFlow: MutableStateFlow<ConnectParticipantsListViewState?> =
        MutableStateFlow(null)
    val participantsListViewStateFlow: StateFlow<ConnectParticipantsListViewState?>
        get() = _participantsListViewStateFlow

    fun createContactList(participantList: List<ParticipantInfo>) {
        viewModelScope.launch(Dispatchers.IO) {
            participantList.map {
                Pair(
                    it,
                    it.contactId?.let { contactId ->
                        dbManager.getContactFromContactTypeId(contactId).blockingGet()
                    } ?: NextivaContact(it.numberToCall, Enums.Contacts.ContactTypes.UNKNOWN)
                )
            }.let { data ->
                data.map {
                    val participantInfo = it.first
                    val nextivaContact = it.second
                    val filteredPhoneNumber =
                        nextivaContact.allPhoneNumbers?.filter { phoneNumber ->
                            CallUtil.getStrippedNumberWithCountryCode(phoneNumber.strippedNumber) == CallUtil.getStrippedNumberWithCountryCode(
                                it.first.numberToCall
                            )
                        }?.let { list -> ArrayList(list) } ?: ArrayList<PhoneNumber>().apply {
                            add(
                                PhoneNumber(
                                    Enums.Contacts.ContactTypes.UNKNOWN,
                                    participantInfo.numberToCall
                                )
                            )
                        }

                    ConnectContactListItemViewState(
                        contactName = buildContactName(participantInfo, nextivaContact),
                        contactPhoneNumbers = filteredPhoneNumber,
                        avatarInfo = createAvatarInfo(nextivaContact),
                        hasLeftNWay = participantInfo.hasLeftNWay
                    )
                }
            }.let { itemViewStateList ->
                _participantsListViewStateFlow.emit(
                    ConnectParticipantsListViewState(
                        participantsList = itemViewStateList,
                        headerViewState = buildHeaderViewState(),
                        closeButtonViewState = buildCloseButtonViewState()
                    )
                )
            }
        }
    }

    private fun buildCloseButtonViewState() = ConnectTextButtonViewState(
        onButtonClicked = {
            onCloseButtonClicked.value = Unit
        },
        text = application.getString(R.string.general_close_list)
    )

    private fun buildHeaderViewState() = ConnectHeaderViewState(
        onCloseButtonClick = {
            onCloseButtonClicked.value = Unit
        },
        shouldShowRoundedCornerShape = true
    )

    private fun buildContactName(participantInfo: ParticipantInfo, nextivaContact: NextivaContact?): String {
        var contactName = when {
            !participantInfo.displayName.isNullOrEmpty() || nextivaContact?.displayName.isNullOrEmpty() -> participantInfo.displayName
                ?: ""

            !nextivaContact?.firstName.isNullOrEmpty() && !nextivaContact?.lastName.isNullOrEmpty() ->
                "${nextivaContact?.firstName ?: ""} ${nextivaContact?.lastName ?: ""}"

            !nextivaContact?.firstName.isNullOrEmpty() -> nextivaContact?.firstName
                ?: ""

            !nextivaContact?.lastName.isNullOrEmpty() -> nextivaContact?.lastName
                ?: ""

            else -> application.getString(R.string.call_participants_unknown)
        }
        if (participantInfo.numberToCall == sessionManager.phoneNumberInformation.phoneNumber){
            return "$contactName ${application.getString(R.string.call_participants_self_indicator)}"
        }
        return contactName
    }
    private fun createAvatarInfo(nextivaContact: NextivaContact?): AvatarInfo? {
        var avatarInfo = nextivaContact?.avatarInfo
        avatarInfo?.setIsConnect(true)

        if (avatarInfo == null){
            val avatarBuilder = AvatarInfo.Builder().isConnect(true)
            avatarInfo = avatarBuilder
                .setDisplayName("")
                .build()
        }

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


}