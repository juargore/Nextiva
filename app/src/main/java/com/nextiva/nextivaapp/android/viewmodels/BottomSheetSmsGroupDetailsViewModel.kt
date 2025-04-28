package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.util.extensions.orTrue
import com.nextiva.nextivaapp.android.util.extensions.orZero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BottomSheetSmsGroupDetailsViewModel @Inject constructor(
    application: Application, val nextivaApplication: Application,
    val dbManager: DbManager, val sessionManager: SessionManager,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel(application) {

    private val _preload: MutableLiveData<ArrayList<NextivaContact>> = MutableLiveData()
    val preload: LiveData<ArrayList<NextivaContact>> = _preload

    val contactListItemLiveData = MutableLiveData<ArrayList<BaseListItem>>()

    var nextivaContactsInConversation: MutableLiveData<NextivaContact> = MutableLiveData()

    private val contactComparator = Comparator<ConnectContactListItem> { a, b ->

        when {
            a.nextivaContact?.uiName?.firstOrNull()?.isLetter() == false && b.nextivaContact?.uiName?.firstOrNull()?.isLetter() !=null -> 1
            a.nextivaContact?.uiName?.firstOrNull()?.isLetter() == true && b.nextivaContact?.uiName?.firstOrNull()?.isLetter() == false -> -1
            else -> {
                val compare =
                    (a.nextivaContact?.uiName ?: "").compareTo(b.nextivaContact?.uiName ?: "", true)
                when {
                    compare < 0 -> -1
                    compare > 0 -> 1
                    else -> 0
                }
            }
        }
    }

    fun setup(
        participants: ArrayList<String>?,
        conversationDetails: SmsConversationDetails,
        participantsCallback: (List<NextivaContact?>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val contactsListItems: ArrayList<BaseListItem> = ArrayList()
            val participantContacts: ArrayList<NextivaContact> = ArrayList()
            val userUuid = conversationDetails.ourUuid
            val userTelephone = CallUtil.getCountryCode() + sessionManager.userDetails?.telephoneNumber
            var contact: NextivaContact
            val allTeams = sessionManager.allTeams

            conversationDetails.getAllTeams().forEach { team ->
                allTeams.firstOrNull { it.teamId == team.teamId }?.members?.forEach {
                    contact = dbManager.getConnectContactFromUuidInThread(it.id).value
                        ?: NextivaContact(SmsParticipant(it.name, null, null, it.id, team.teamId, null))
                    contactsListItems.add(ConnectContactListItem(contact, false))
                }
            }

            participants?.distinct()?.forEach { participant ->
                participant.nullIfEmpty()?.let {
                    contact = dbManager.getConnectContactFromPhoneNumberInThread(participant).value
                        ?: NextivaContact(participant, Enums.Contacts.ContactTypes.CONNECT_UNKNOWN)

                    participantContacts.add(contact)
                    contactsListItems.add(ConnectContactListItem(contact, false))
                }
            }

            (contactsListItems as? ArrayList<ConnectContactListItem>)?.removeIf { it.nextivaContact?.userId == userUuid ||
                    it.nextivaContact?.allPhoneNumbers?.any { number -> CallUtil.arePhoneNumbersEqual(userTelephone, number.strippedNumber) } == true }

            contact = dbManager.getConnectContactFromPhoneNumberInThread(userTelephone).value
                ?: dbManager.getConnectContactFromUuidInThread(userUuid)?.value ?: NextivaContact(userTelephone)

            contactsListItems.add(ConnectContactListItem(
                    contact,
                    showIcons = false,
                showSelfIndicator = true))

            (contactsListItems as? ArrayList<ConnectContactListItem>)?.sortWith(contactComparator)

            withContext(Dispatchers.Main) {
                contactListItemLiveData.value = (contactsListItems as? ArrayList<ConnectContactListItem>)?.distinctBy { it.nextivaContact?.userId } as? ArrayList<BaseListItem>
                participantsCallback(participantContacts)
            }
        }
    }

    fun getPreloadNumbers(conversationDetails: SmsConversationDetails) : ArrayList<String>? {
        return ArrayList<String>().apply {
            conversationDetails.getParticipantsList()?.mapNotNull { smsParticipant ->
                smsParticipant.phoneNumber
            }?.let { addAll(it) }
            conversationDetails.teams?.mapNotNull { smsTeam ->
                smsTeam.teamPhoneNumber
            }?.let { addAll(it) }
        }.filter { phoneNumber ->
            !phoneNumber.contains(sessionManager.userDetails?.telephoneNumber.orEmpty()) &&
                    !sessionManager.userDetails?.telephoneNumber?.contains(phoneNumber).orTrue()
        }.let {
            ArrayList(it)
        }
    }

    fun getPreloadContacts(conversationDetails: SmsConversationDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            val teamsPhones = mutableMapOf<String, SmsTeam>()
            val phones = mutableSetOf<String>()
            val teamMember = mutableSetOf<String>().apply {
                conversationDetails.teamMembers?.forEach { participant ->
                    if (participant.phoneNumber?.isNotBlank().orFalse()) {
                        add(participant.phoneNumber.orEmpty())
                    }
                }
            }
            conversationDetails.getParticipantsList()?.mapNotNull { smsParticipant ->
                smsParticipant.phoneNumber?.let { phoneNumber ->
                    if (!teamMember.contains(phoneNumber)) {
                        phones.add(phoneNumber)
                    }
                }
            }
            conversationDetails.teams?.forEach { team ->
                if (team.teamPhoneNumber?.isNotBlank().orFalse())
                    teamsPhones[team.teamPhoneNumber.orEmpty()] = team
                    if (!teamMember.contains(team.teamPhoneNumber)) {
                        phones.add(team.teamPhoneNumber.orEmpty())
                    }
            }

            phones.mapNotNull { phoneNumber ->
                if (CallUtil.isValidSMSNumber(phoneNumber)) {
                    (dbManager.getConnectContactFromPhoneNumberInThread(phoneNumber).value
                        ?: NextivaContact(
                            phoneNumber,
                            Enums.Contacts.ContactTypes.CONNECT_UNKNOWN,
                        )
                    ).apply {
                        representingTeam = teamsPhones[phoneNumber]
                    }
                } else {
                    null
                }
            }.sortedWith(comparator = Comparator{ a, b ->
                when {
                    a.userId?.isNotBlank().orTrue() && b.userId?.isNotBlank().orTrue() -> {
                        a.displayName?.compareTo(b.displayName.orEmpty()).orZero()
                    }
                    a.userId?.isNotBlank().orTrue() && b.userId?.isBlank().orTrue() -> -1
                    a.userId?.isBlank().orTrue() && b.userId?.isNotBlank().orTrue() -> 1
                    else -> a.phoneNumbers?.firstOrNull()?.strippedNumber?.compareTo(b.phoneNumbers?.firstOrNull()?.strippedNumber.orEmpty()).orZero()
                }
            }).let {
                _preload.postValue(ArrayList(it))
            }
        }
    }
}