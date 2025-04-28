package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.BlockingNumberManager
import com.nextiva.nextivaapp.android.models.ContactQuery
import com.nextiva.nextivaapp.android.models.ListHeaderRow
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectContactsListViewModel @Inject constructor(
    application: Application,
    val nextivaApplication: Application,
    val dbManager: DbManager,
    private val platformContactsRepository: PlatformContactsRepository,
    val schedulerProvider: SchedulerProvider,
    private val presenceRepository: PresenceRepository,
    private val blockingManger: BlockingNumberManager
) : BaseViewModel(application) {

    private var favoriteHeaderListItem: ConnectContactHeaderListItem =
            ConnectContactHeaderListItem(ListHeaderRow(nextivaApplication.getString(R.string.connect_contacts_group_header_favorites)),
                    Enums.Platform.ConnectContactGroups.FAVORITES,
                    dbManager.getConnectGroupCount(Enums.Platform.ConnectContactGroups.FAVORITES),
                    isLoading = false,
                    isExpanded = false)
    private var teammatesHeaderListItem: ConnectContactHeaderListItem =
            ConnectContactHeaderListItem(ListHeaderRow(nextivaApplication.getString(R.string.connect_contacts_group_header_teammates)),
                    Enums.Platform.ConnectContactGroups.TEAMMATES,
                    dbManager.getConnectGroupCount(Enums.Platform.ConnectContactGroups.TEAMMATES),
                    isLoading = false,
                    isExpanded = false)
    private var businessHeaderListItem: ConnectContactHeaderListItem =
            ConnectContactHeaderListItem(ListHeaderRow(nextivaApplication.getString(R.string.connect_contacts_group_header_business)),
                    Enums.Platform.ConnectContactGroups.BUSINESS,
                    dbManager.getConnectGroupCount(Enums.Platform.ConnectContactGroups.BUSINESS),
                    isLoading = false,
                    isExpanded = false)
    private var allHeaderListItem: ConnectContactHeaderListItem =
            ConnectContactHeaderListItem(ListHeaderRow(nextivaApplication.getString(R.string.connect_contacts_group_header_all_contacts)),
                    Enums.Platform.ConnectContactGroups.ALL_CONTACTS,
                    dbManager.getConnectGroupCount(Enums.Platform.ConnectContactGroups.ALL_CONTACTS),
                    isLoading = false,
                    isExpanded = true)

    private val query = MutableStateFlow(ContactQuery())

    enum class CurrentPosition{
        RecentContacts, AllItems, Search, None
    }

    private var currentPosition = CurrentPosition.None
    var resetScrollPosition = false
    private var currentSearch : String? = null

    val blockedNumbersLiveData: LiveData<List<String>> = blockingManger.observeBlockedNumbers().asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    var listItems: Flow<PagingData<BaseListItem>> = query.flatMapLatest { contactQuery ->
        when (contactQuery.state) {
            ContactQuery.PositionState.AllItems -> {
                if (currentPosition != CurrentPosition.AllItems) {
                    currentPosition = CurrentPosition.AllItems
                    resetScrollPosition = true
                }
                _allItems
            }

            ContactQuery.PositionState.RecentContacts -> {
                if (currentPosition != CurrentPosition.RecentContacts) {
                    currentPosition = CurrentPosition.RecentContacts
                    resetScrollPosition = true
                }
                getRecentContacts()
                _recentContacts
            }

            ContactQuery.PositionState.Search -> {
                if (currentPosition != CurrentPosition.Search) {
                    currentPosition = CurrentPosition.Search
                    resetScrollPosition = true
                }
                _filteredData
            }
        }
    }.flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)


    @OptIn(ExperimentalCoroutinesApi::class)
    private val _allItems = query.flatMapLatest {
        Pager(
            config = PagingConfig(
                pageSize = 200,
                prefetchDistance = 60,
                initialLoadSize = 100,
                enablePlaceholders = false
            ),
        pagingSourceFactory = {
            dbManager.getConnectContactsPagingSource(favoriteHeaderListItem.isExpanded, teammatesHeaderListItem.isExpanded, businessHeaderListItem.isExpanded, allHeaderListItem.isExpanded)
        }).flow.map { pagingData ->
            pagingData.map { model ->
                val nextivaContact = model.contact
                val isContactBlocked = blockingManger.areAllNumbersForContactBlocked(nextivaContact.toNextivaContact().allPhoneNumbers)
                ConnectContactListItem(
                    strippedContact = nextivaContact,
                    groupValue = model.groupValue,
                    isBlocked = isContactBlocked
                ) as BaseListItem
            }
            .insertSeparators { before, after -> getSeparator(before, after, Enums.Platform.ConnectContactGroups.FAVORITES) }
            .insertSeparators { before, after -> getSeparator(before, after, Enums.Platform.ConnectContactGroups.TEAMMATES) }
            .insertSeparators { before, after -> getSeparator(before, after, Enums.Platform.ConnectContactGroups.BUSINESS) }
            .insertSeparators { before, after -> getSeparator(before, after, Enums.Platform.ConnectContactGroups.ALL_CONTACTS) }
        }
    }.flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)


    @OptIn(ExperimentalCoroutinesApi::class)
    private val _filteredData : Flow<PagingData<BaseListItem>> = query.flatMapLatest { contactQuery ->
        Pager(
            config = PagingConfig(
                pageSize = 200,
                prefetchDistance = 60,
                initialLoadSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                dbManager.getContactTypePagingSource(contactQuery.filter, contactQuery.query)
            }
        ).flow.map { data ->
            if(currentSearch != contactQuery.query) {
                resetScrollPosition = true
                currentSearch = contactQuery.query
            }
            data.map { contact ->
                contact.presence = dbManager.getPresenceFromContactTypeIdInThread(contact.userId)
                ConnectContactListItem(
                    nextivaContact = contact,
                    searchTerm = contactQuery.query.orEmpty()
                ).apply {
                    isBlocked = blockingManger.areAllNumbersForContactBlocked(contact.allPhoneNumbers)
                } as BaseListItem
            }
        }
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _recentContacts : Flow<PagingData<BaseListItem>> = query.flatMapLatest { contactQuery ->
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
                ConnectContactListItem(
                    nextivaContact = nextivaContact,
                    searchTerm = ""
                ).apply {
                    isBlocked = blockingManger.areAllNumbersForContactBlocked(nextivaContact.allPhoneNumbers)
                } as BaseListItem
            }
        }
    }

    private fun getSeparator(before: BaseListItem?, after: BaseListItem?, groupValue: String): ConnectContactHeaderListItem? {
        val beforeGroupValue = when (before) {
            is ConnectContactListItem -> before.groupValue
            is ConnectContactHeaderListItem -> before.itemType
            else -> null
        }

        val afterGroupValue = when (after) {
            is ConnectContactListItem -> after.groupValue
            is ConnectContactHeaderListItem -> after.itemType
            else -> null
        }

        if (before == null && after == null) {
            return when (groupValue) {
                Enums.Platform.ConnectContactGroups.FAVORITES -> favoriteHeaderListItem
                Enums.Platform.ConnectContactGroups.TEAMMATES -> teammatesHeaderListItem
                Enums.Platform.ConnectContactGroups.BUSINESS -> businessHeaderListItem
                else -> allHeaderListItem
            }

        } else if ((before is ConnectContactHeaderListItem && before.itemType == groupValue) ||
                (after is ConnectContactHeaderListItem && after.itemType == groupValue)) {
            return null

        } else if (beforeGroupValue != afterGroupValue) {
            return when {
                groupValue == Enums.Platform.ConnectContactGroups.FAVORITES &&
                        beforeGroupValue == null -> favoriteHeaderListItem
                groupValue == Enums.Platform.ConnectContactGroups.TEAMMATES &&
                        beforeGroupValue != Enums.Platform.ConnectContactGroups.TEAMMATES &&
                        beforeGroupValue != Enums.Platform.ConnectContactGroups.ALL_CONTACTS &&
                        beforeGroupValue != Enums.Platform.ConnectContactGroups.BUSINESS &&
                        afterGroupValue != Enums.Platform.ConnectContactGroups.FAVORITES  -> teammatesHeaderListItem
                groupValue == Enums.Platform.ConnectContactGroups.BUSINESS &&
                        beforeGroupValue != Enums.Platform.ConnectContactGroups.BUSINESS &&
                        beforeGroupValue != Enums.Platform.ConnectContactGroups.ALL_CONTACTS &&
                        afterGroupValue != Enums.Platform.ConnectContactGroups.FAVORITES &&
                        afterGroupValue != Enums.Platform.ConnectContactGroups.TEAMMATES -> businessHeaderListItem
                groupValue == Enums.Platform.ConnectContactGroups.ALL_CONTACTS &&
                        beforeGroupValue != Enums.Platform.ConnectContactGroups.ALL_CONTACTS &&
                        afterGroupValue != Enums.Platform.ConnectContactGroups.FAVORITES &&
                        afterGroupValue != Enums.Platform.ConnectContactGroups.TEAMMATES &&
                        afterGroupValue != Enums.Platform.ConnectContactGroups.BUSINESS -> allHeaderListItem
                else -> null
            }
        }

        return null
    }
    
    fun headerListItemClicked(headerListItem: ConnectContactHeaderListItem) {
        when (headerListItem.itemType) {
            Enums.Platform.ConnectContactGroups.FAVORITES -> {
                if (favoriteHeaderListItem.isExpanded) {
                    teammatesHeaderListItem.isExpanded = false
                    businessHeaderListItem.isExpanded = false
                    allHeaderListItem.isExpanded = false
                } else {
                    allHeaderListItem.isExpanded = true
                }
            }
            else -> {
                favoriteHeaderListItem.isExpanded = false
            }
        }
    }

    fun getContactFromListItem(listItem: ConnectContactListItem, onContactCallback: ((NextivaContact?) -> Unit)) {
        listItem.nextivaContact?.let(onContactCallback)

        listItem.strippedContact?.contactTypeId?.let { contactTypeId ->
            mCompositeDisposable.add(
                    dbManager.getContactFromContactTypeId(contactTypeId)
                            .subscribe { contact -> onContactCallback(contact) })
        }
    }

    fun setFavorite(contactListItem: ConnectContactListItem) {
        (contactListItem.strippedContact?.favorite ?: contactListItem.nextivaContact?.isFavorite)?.let { isFavorite ->
            (contactListItem.strippedContact?.contactTypeId ?: contactListItem.nextivaContact?.userId)?.let { contactId ->
                platformContactsRepository.setContactFavorite(contactId,
                        if (contactListItem.strippedContact?.contactType == Enums.Contacts.ContactTypes.CONNECT_USER)
                            Enums.Contacts.ContactTypesValue.CONNECT_USER else
                            Enums.Contacts.ContactTypesValue.CONNECT_SHARED,
                        !isFavorite).subscribe()
            }
        }
    }

    fun updateFilter(filter: IntArray) {
        viewModelScope.launch {
            this@ConnectContactsListViewModel.query.emit(
                query.value.copy(filter = filter)
            )
        }
    }

    fun queryUpdated(q: String, selectedFilter: IntArray) {
        if (q.isEmpty()) {
            ContactQuery(q, selectedFilter, ContactQuery.PositionState.RecentContacts)
        } else {
            ContactQuery(q, selectedFilter, ContactQuery.PositionState.Search)
        }.let { contactQuery ->
            viewModelScope.launch { query.emit(contactQuery) }
        }
    }

    fun onFocusChanged(q: String, selectedFilter: IntArray) {
        if (currentPosition != CurrentPosition.RecentContacts && q.isEmpty()) {
            viewModelScope.launch {
                this@ConnectContactsListViewModel.query.emit(
                    ContactQuery(q, selectedFilter, ContactQuery.PositionState.RecentContacts)
                )
            }
        }
    }

    fun setAllItems() {
        viewModelScope.launch {
            this@ConnectContactsListViewModel.query.emit(
                ContactQuery()
            )
        }
    }

    fun getCurrentState() = currentPosition

    fun fetchContacts(forceRefresh: Boolean, onSaveFinishedCallback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            platformContactsRepository.fetchContacts( forceRefresh, {
                presenceRepository.getPresences()
                onSaveFinishedCallback()
            }, {
                onSaveFinishedCallback()
            })
        }
    }

    fun hasLocalContacts(): Boolean {
        return dbManager.localContactsCount > 0
    }


    fun getContact(contactTypeId: String, onContactCallback: (NextivaContact?) -> Unit) {
        mCompositeDisposable.add(
        dbManager.getContactFromContactTypeId(contactTypeId)
                .subscribe { contact ->
                    onContactCallback(contact)
                })
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