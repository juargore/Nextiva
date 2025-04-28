package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetContactSelectionFragment.Companion.CONTACT_SELECTION_ADD_CALL
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetContactSelectionFragment.Companion.CONTACT_SELECTION_TRANSFER_CALL
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetContactSelectionFragment.Companion.CONTACT_SELECTION_TRANSFER_TO_MOBILE
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.ContactQuery
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectContactListItemViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectContactSelectionViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectHeaderViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectTextButtonViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BottomSheetContactSelectionViewModel @Inject constructor(
    application: Application,
    val dbManager: DbManager,
    val sharedPreferencesManager: SharedPreferencesManager,
    private val platformContactsRepository: PlatformContactsRepository,
    val schedulerProvider: SchedulerProvider
) : BaseViewModel(application) {
    val onCloseButtonClicked: MutableLiveData<Unit> = MutableLiveData()
    val onContactItemClickedLiveData: MutableLiveData<Pair<NextivaContact, String>> =
        MutableLiveData()
    val query = MutableStateFlow(ContactQuery())
    private val _contactSelectionViewStateFlow: MutableStateFlow<ConnectContactSelectionViewState> =
        MutableStateFlow(ConnectContactSelectionViewState())
    val contactSelectionViewStateFlow: StateFlow<ConnectContactSelectionViewState>
        get() = _contactSelectionViewStateFlow

    enum class CurrentPosition {
        RecentContacts, Search, None
    }

    private var currentPosition = CurrentPosition.None
    private var currentSearch: String? = null
    private var searchText: String = ""
    val pattern = Regex("[0-9#+()-*]+")

    private fun buildContactSelectionViewState(contactSelectionType: String) =
        ConnectContactSelectionViewState(
            title = getTitle(contactSelectionType),
            icon = getIcon(contactSelectionType),
            subTitle = getSubTitle(contactSelectionType),
            prefillTextFieldValue = getPrefillTextFieldValue(contactSelectionType),
            connectHeaderViewState = createConnectHeaderViewState(),
            cancelTxtBtnViewState = createCancelTxtBtnViewState(),
            textFieldChangedListener = createTextFieldChangedListener(),
            isDigitOnly = isDigitOnly(contactSelectionType),
            trailingIcon = R.string.fa_phone_icon,
            selectionInfoIcon = getSelectionInfoIcon(contactSelectionType),
            shouldShowRecentContactsSection = shouldShowRecentContactsSection(contactSelectionType),
            onTrailingIconClick = { onTrailingIconClick() }
        )

    private fun isDigitOnly(contactSelectionType: String): Boolean {
        return when (contactSelectionType) {
            CONTACT_SELECTION_TRANSFER_TO_MOBILE -> true
            else -> false
        }
    }

    private fun getSelectionInfoIcon(contactSelectionType: String): Int? {
        return when (contactSelectionType) {
            CONTACT_SELECTION_TRANSFER_CALL -> R.string.fa_phone_arrow_right
            CONTACT_SELECTION_ADD_CALL -> R.string.fa_phone_plus
            else -> null
        }
    }

    private fun shouldShowRecentContactsSection(contactSelectionType: String): Boolean {
        return contactSelectionType != CONTACT_SELECTION_TRANSFER_TO_MOBILE
    }

    private fun getPrefillTextFieldValue(contactSelectionType: String): String? {
        return if (contactSelectionType == CONTACT_SELECTION_TRANSFER_TO_MOBILE) {
            val prefillNumber =
                sharedPreferencesManager.getString(SharedPreferencesManager.THIS_PHONE_NUMBER, "")
                    .takeIf { it.isNotEmpty() }
                    ?.let { CallUtil.cleanPhoneNumber(it) }
            searchText = prefillNumber ?: ""
            prefillNumber
        } else {
            null
        }
    }

    fun onContactSelectionTypeUpdated(contactSelectionType: String) {
        _contactSelectionViewStateFlow.update {
            buildContactSelectionViewState(contactSelectionType)
        }
    }

    private fun updateSearchViewState(trailingIcon: Int?, shouldShowSearchStyleView: Boolean? = false) {
        _contactSelectionViewStateFlow.update {
            it.copy(
                trailingIcon = trailingIcon,
                shouldShowSearchStyleView = shouldShowSearchStyleView
            )
        }
    }

    private fun getTitle(contactSelectionType: String): String {
        val resId = when (contactSelectionType) {
            CONTACT_SELECTION_ADD_CALL -> R.string.connect_contact_selection_make_call
            CONTACT_SELECTION_TRANSFER_CALL -> R.string.connect_contact_selection_transfer_call_title
            CONTACT_SELECTION_TRANSFER_TO_MOBILE -> R.string.connect_contact_selection_transfer_to_mobile_title
            else -> null
        }
        return resId?.let { application.getString(it) } ?: ""
    }

    private fun getIcon(contactSelectionType: String) =
        if (contactSelectionType == CONTACT_SELECTION_ADD_CALL) {
            R.string.fa_phone_plus
        } else {
            R.string.fa_phone_arrow_right
        }

    private fun getSubTitle(contactSelectionType: String): String {
        val resId = when (contactSelectionType) {
            CONTACT_SELECTION_ADD_CALL -> R.string.connect_contact_selection_make_call_subtitle
            CONTACT_SELECTION_TRANSFER_CALL -> R.string.connect_contact_selection_transfer_call_subtitle
            CONTACT_SELECTION_TRANSFER_TO_MOBILE -> R.string.connect_contact_selection_transfer_to_mobile_subtitle
            else -> null
        }
        return resId?.let { application.getString(it) } ?: ""
    }

    private fun createConnectHeaderViewState() = ConnectHeaderViewState(
        onCloseButtonClick = { onCloseButtonClicked.value = Unit },
        shouldShowRoundedCornerShape = true
    )

    private fun createCancelTxtBtnViewState() = ConnectTextButtonViewState(
        text = application.getString(R.string.general_cancel),
        onButtonClicked = { onCloseButtonClicked.value = Unit },
        textColor = R.color.connectSecondaryDarkBlue
    )

    private fun createTextFieldChangedListener() = { text: String ->
        onTextChanged(text)
        _contactSelectionViewStateFlow.update {
            it.copy(
                shouldClearCurrentSearch = false
            )
        }
    }

    private fun onTextChanged(text: String){
        if (text.isNotEmpty() && text.length >= 3 && text.matches(pattern)) {
            updateSearchViewState(R.string.fa_phone_icon, true)
        } else {
            updateSearchViewState(null, text.isNotEmpty())
        }
        searchText = text
        queryUpdated(text)
    }

    fun clearCurrentSearch(){
        _contactSelectionViewStateFlow.update {
            it.copy(
                shouldClearCurrentSearch = true
            )
        }
        onTextChanged("")
    }

    private fun onTrailingIconClick() {
        val contact = dbManager.getConnectContactFromPhoneNumberInThread(searchText)
        onContactItemClickedLiveData.value = Pair(
            contact.value ?: NextivaContact(
                searchText, Enums.Contacts.ContactTypes.UNKNOWN
            ), searchText
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    var listItems: Flow<PagingData<ConnectContactListItemViewState>> =
        query.flatMapLatest { contactQuery ->
            when (contactQuery.state) {
                ContactQuery.PositionState.RecentContacts -> {
                    if (currentPosition != CurrentPosition.RecentContacts) {
                        currentPosition = CurrentPosition.RecentContacts
                    }
                    getRecentContacts()
                    _recentContacts
                }

                ContactQuery.PositionState.Search -> {
                    if (currentPosition != CurrentPosition.Search) {
                        currentPosition = CurrentPosition.Search
                    }
                    _filteredData
                }

                else -> {
                    if (currentPosition != CurrentPosition.RecentContacts) {
                        currentPosition = CurrentPosition.RecentContacts
                    }
                    _recentContacts
                }
            }
        }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _filteredData: Flow<PagingData<ConnectContactListItemViewState>> =
        query.flatMapLatest { contactQuery ->
            Pager(
                config = PagingConfig(
                    pageSize = 40,
                    prefetchDistance = 60,
                    initialLoadSize = 100,
                    enablePlaceholders = true
                ),
                pagingSourceFactory = {
                    dbManager.getContactTypePagingSource(contactQuery.filter, contactQuery.query)
                }
            ).flow.map { data ->
                if (currentSearch != contactQuery.query) {
                    currentSearch = contactQuery.query
                }
                data.map { contact ->
                    contact.presence =
                        dbManager.getPresenceFromContactTypeIdInThread(contact.userId)
                    getConnectContactListItemViewState(contact, contactQuery.query)
                }
            }
        }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _recentContacts: Flow<PagingData<ConnectContactListItemViewState>> =
        query.flatMapLatest { contactQuery ->
            Pager(
                config = PagingConfig(
                    pageSize = 40,
                    prefetchDistance = 60,
                    initialLoadSize = 100,
                    enablePlaceholders = true
                ),
                pagingSourceFactory = {
                    dbManager.getRecentContactsPagingData(contactQuery.filter)
                }
            ).flow.map { data ->
                data.map { nextivaContact ->
                    getConnectContactListItemViewState(nextivaContact)
                }
            }
        }

    private fun getConnectContactListItemViewState(
        nextivaContact: NextivaContact?,
        query: String? = ""
    ): ConnectContactListItemViewState {
        val contactName = nextivaContact?.uiName ?: ""
        val contactPhoneNumber = getContactPhoneNumber(nextivaContact?.allPhoneNumbers)
        val avatarInfo = createAvatarInfo(nextivaContact)
        return ConnectContactListItemViewState(
            id = nextivaContact?.userId,
            contactName = contactName,
            contactPhoneNumbers = contactPhoneNumber,
            avatarInfo = avatarInfo,
            searchTerm = query,
            onItemClick = { phoneNumber ->
                nextivaContact?.let {
                    onContactItemClickedLiveData.value = Pair(nextivaContact, phoneNumber)
                }
            },
        )
    }

    private fun getContactPhoneNumber(allPhoneNumbers: MutableList<PhoneNumber>?): ArrayList<PhoneNumber> {
        return ArrayList(allPhoneNumbers ?: emptyList())
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

    fun queryUpdated(q: String) {
        val selectedFilter = intArrayOf(
            Enums.Contacts.ContactTypes.CONNECT_PERSONAL,
            Enums.Contacts.ContactTypes.CONNECT_SHARED,
            Enums.Contacts.ContactTypes.CONNECT_USER,
            Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS,
        )
        if (q.isEmpty()) {
            ContactQuery(q, selectedFilter, ContactQuery.PositionState.RecentContacts)
        } else {
            ContactQuery(q, selectedFilter, ContactQuery.PositionState.Search)
        }.let { contactQuery ->
            viewModelScope.launch { query.emit(contactQuery) }
        }
    }

    private fun getRecentContacts() {
        platformContactsRepository.fetchRecentContacts()
            .subscribeOn(schedulerProvider.io())
            .subscribe(object : DisposableSingleObserver<Boolean>() {
                override fun onSuccess(success: Boolean) {}
                override fun onError(error: Throwable) {}
            })
    }

    fun compositeDisposableClear() {
        mCompositeDisposable.clear()
        platformContactsRepository.clearCompositeDisposable()
    }

}