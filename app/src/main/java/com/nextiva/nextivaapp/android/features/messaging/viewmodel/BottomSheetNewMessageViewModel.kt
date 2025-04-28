package com.nextiva.nextivaapp.android.features.messaging.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.net.platform.GenerateGroupIdPostBody
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomSheetNewMessageViewModel @Inject constructor(
    application: Application,
    val dbManager: DbManager,
    val dbManagerKt: DbManagerKt,
    val platformContactsRepository: PlatformContactsRepository,
    val sessionManager: SessionManager,
    val smsManagementRepository: SmsManagementRepository,
    val schedulerProvider: SchedulerProvider,
    val sipManager: PJSipManager
) : BaseViewModel(application) {

    private var allTeams = sessionManager.allTeams

    private val _query = MutableStateFlow<String>("")
    var query = ""

    var defaultTeamName: String? = null

    private val _groupId: MutableLiveData<String?> = MutableLiveData(null)
    val groupId: LiveData<String?> = _groupId
    private var job: Job? = null

    enum class CurrentState { Recent, Search, Sms }

    var tempState = CurrentState.Recent
    val currentState = _query.combine(_groupId.asFlow()) { query, groupId ->
        when {
            query.isNotBlank() -> CurrentState.Search
            !groupId.isNullOrBlank() -> CurrentState.Sms
            else -> CurrentState.Recent
        }.apply {
            tempState = this
        }
    }

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    val activeCallLiveData = sipManager.activeCallLiveData
    var resetScrollPosition = false

    @OptIn(ExperimentalCoroutinesApi::class)
    val items = _query.flatMapLatest { query ->
        resetScrollPosition = true
        if (query.isEmpty()) {
            _recentContacts
        } else {
            _filteredLiveData
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _filteredLiveData: Flow<PagingData<BaseListItem>> = _query.flatMapLatest { query ->
        Pager(
            config = PagingConfig(
                pageSize = 50,
                prefetchDistance = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                dbManager.getContactTypePagingSource(
                    intArrayOf(
                        Enums.Contacts.ContactTypes.CONNECT_SHARED,
                        Enums.Contacts.ContactTypes.CONNECT_PERSONAL,
                        Enums.Contacts.ContactTypes.CONNECT_USER,
                        Enums.Contacts.ContactTypes.CONNECT_TEAM
                    ), query
                )
            }
        ).flow.cachedIn(viewModelScope)
            .map {
                it.filter { contact ->

                    /* val searchTermLowerCase = query.toLowerCase(Locale.current)
                    val phoneContains = contact.phoneNumbers?.any { phone ->
                        phone.strippedNumber?.contains(query) == true
                    } ?: false
                    val nameContains = contact.displayName?.toLowerCase(Locale.current)
                        ?.contains(searchTermLowerCase.toString()) == true

                    phoneContains || nameContains) */

                    if (contact.contactType == Enums.Contacts.ContactTypes.CONNECT_TEAM) {
                        if (!isTeamSmsEnabled(contact)) {
                            return@filter false
                        }
                    }

                    contact.allPhoneNumbers?.let { numbers ->
                        numbers.filter { number -> CallUtil.isValidSMSNumber(number.strippedNumber) }
                    }?.isNotEmpty().orFalse()
                }
            }.map {
                it.map { contact ->
                    (ConnectContactListItem(
                        contact,
                        query,
                        false,
                        isForSearch = true
                    )) as BaseListItem
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _recentContacts : Flow<PagingData<BaseListItem>> = _query.flatMapLatest { query ->
        Pager(
            config = PagingConfig(
                pageSize = 40,
                prefetchDistance = 60,
                initialLoadSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                dbManager.getRecentContactsPagingData(
                    intArrayOf(
                        Enums.Contacts.ContactTypes.CONNECT_PERSONAL,
                        Enums.Contacts.ContactTypes.CONNECT_SHARED,
                        Enums.Contacts.ContactTypes.CONNECT_USER,
                        Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW,
                        Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS,
                        Enums.Contacts.ContactTypes.CONNECT_TEAM
                    )
                )
            }
        ).flow
            .map { data ->
                data.map { nextivaContact ->
                ConnectContactListItem(nextivaContact, "") as BaseListItem
            }
        }.flowOn(Dispatchers.IO)
            .cachedIn(viewModelScope)
    }

    val selectedContacts = ArrayList<NextivaContact>()
    val addSelectedContact = MutableLiveData<NextivaContact>()
    val removeSelectedContact = MutableLiveData<Int>()

    fun contactSelected(nextivaContact: NextivaContact?) {
        nextivaContact?.let {
            addSelectedContact.value = it
        }
    }

    fun fetchRecentContacts() {
        platformContactsRepository.fetchRecentContacts()
            .subscribeOn(schedulerProvider.io())
            .subscribe(object : DisposableSingleObserver<Boolean>() {
                override fun onSuccess(success: Boolean) {}
                override fun onError(error: Throwable) {}
            })
    }

    fun isTeamSmsEnabled(contact: NextivaContact): Boolean {
        var matchedTeam: SmsTeam? = null

        allTeams.forEach { team ->
            contact.allPhoneNumbers?.firstOrNull { CallUtil.arePhoneNumbersEqual(it.strippedNumber, team.teamPhoneNumber) }?.let { matchedTeam = team }
        }

        return matchedTeam?.smsEnabled ?: false
    }

    fun contactAdded(nextivaContact: NextivaContact) {
        var representingTeam: SmsTeam? = null

        nextivaContact.allPhoneNumbers?.forEach { phoneNumber ->
            sessionManager.allTeams.forEach teamLoop@ { team ->
                if (CallUtil.arePhoneNumbersEqual(phoneNumber.strippedNumber, team.teamPhoneNumber)) {
                    representingTeam = team
                    return@teamLoop
                }
            }

            if (representingTeam != null) return@forEach
        }

        nextivaContact.representingTeam = representingTeam
        selectedContacts.add(nextivaContact)
        fetchGroupId()
    }

    fun removeContact(nextivaContact: NextivaContact) {
        val index = selectedContacts.indexOf(nextivaContact)
        selectedContacts.remove(nextivaContact)
        removeSelectedContact.value = index
        fetchGroupId()
    }

    fun selectedContactsCount() : Int {
        return selectedContacts.size
    }

    fun onSearchTermUpdated(searchTerm: String) {
        viewModelScope.launch { _query.emit(searchTerm) }
    }

    fun isMessageContactAlreadyAdded(nextivaContact: NextivaContact?) : Boolean {
        return when (nextivaContact?.userId) {
            null -> {
                selectedContacts.any { contact ->
                    contact.phoneNumbers?.any { phone ->
                        phone.strippedNumber == nextivaContact?.phoneNumbers?.get(0)?.strippedNumber
                    } ?: false
                }
            }
            else -> selectedContacts.count { it.userId == nextivaContact.userId } > 0
        }
    }

    fun getContactFromUserId(userId: String, contactCallback: (NextivaContact?) -> Unit) {
        mCompositeDisposable.add(
                dbManager.getContactFromContactTypeId(userId).subscribe { contact ->
                    contactCallback(contact)
                })
    }

    fun getContactFromPhoneNumber(phoneNumber: String, contactCallback: (NextivaContact?) -> Unit) {
        mCompositeDisposable.add(
                dbManager.getConnectContactFromPhoneNumber(phoneNumber).subscribe { contact ->
                    contactCallback(contact.value)
                })
    }

    fun smsPhoneNumberList(nextivaContact: NextivaContact): List<PhoneNumber> {
        val phoneNumbers = nextivaContact.phoneNumbers ?: return listOf()
        return phoneNumbers.filter {
            it.type != Enums.Contacts.PhoneTypes.WORK_EXTENSION &&
            CallUtil.isValidSMSNumber(it.strippedNumber)
        }
    }

    fun getGroupId(conversationDetails: SmsConversationDetails): String? {
        return dbManager.getGroupIdFrom(conversationDetails.getConversationId())
    }

    fun updateGroupId(groupId: String) {
        _groupId.value = groupId
    }

    fun fetchGroupId(){
        _loading.value = true
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {

            if(selectedContacts.isEmpty()) {
                _groupId.postValue(null)
                _loading.postValue(false)
                return@launch
            }

            var isTeamMessage = false
            val body = GenerateGroupIdPostBody()
            selectedContacts.forEach { contact ->
                if(contact.representingTeam?.teamId?.isNotBlank().orFalse()) {
                    isTeamMessage = true
                    body.teamIds.add(contact.representingTeam?.teamId.orEmpty())
                } else {
                    contact.phoneNumbers?.firstOrNull()?.number?.let { num ->
                        val number = num.split("x")[0].replace("[^0-9]".toRegex(), "")
                        if (number.isNotBlank().orFalse()) {
                            body.phoneNumbers.add(number)
                        }
                    }
                }
            }

            if(isTeamMessage) {
                // Group Id calculated using API Endpoint because this chat has a Team
                val groupId = smsManagementRepository.generateGroupId(body).orEmpty()
                Log.d("BottomSheetNewMessageViewModel", "[Fetched groupId]: $groupId")
                _groupId.postValue(groupId)
            } else {
                sessionManager.userDetails?.telephoneNumber?.let { number ->
                    val selfNumber = CallUtil.getStrippedNumberWithCountryCode(
                        number.split("x")[0].replace(
                            "[^0-9]".toRegex(),
                            ""
                        )
                    )
                    if(selfNumber.isNotBlank()) {
                        body.phoneNumbers.add(selfNumber)
                    }
                }
                // Group Id is just an sorted arrange of phone numbers
                _groupId.postValue(getPlainGroupId(body.phoneNumbers))
            }
            _loading.postValue(false)
        }
    }

    private fun getPlainGroupId(numbers: List<String>) : String {
        return numbers.toSet().sorted().joinToString("")
    }

    suspend fun getContactFromPhoneNumber(phoneNumber: String): NextivaContact? {
        return dbManagerKt.getContactFromPhoneNumberInThread(phoneNumber).value
    }
}
