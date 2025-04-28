package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import androidx.paging.map
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BottomSheetSelectContactListViewModel @Inject constructor(application: Application, nextivaApplication: Application,
                                                                val dbManager: DbManager, private val platformContactsRepository: PlatformContactsRepository,
                                                                val schedulerProvider: SchedulerProvider) : BaseViewModel(application) {

    private val searchTermMutableLiveData = MutableLiveData<String>()
    private val isSearchingLiveData = MutableLiveData<Boolean>()
    var isCurrentlySearching = false
    var finishedSearching = false
    val searchTerm: String?
        get() { return searchTermMutableLiveData.value }

    var contactTypes: IntArray? = null
        get() { return field ?: IntArray(0) }

    val contactTypeLiveData: LiveData<PagingData<BaseListItem>> = Pager(PagingConfig(pageSize = 50, prefetchDistance = 50, enablePlaceholders = true)) {
        dbManager.getContactTypePagingSource(contactTypes, searchTerm ?: "")
    }.liveData
            .map { it.map { contact -> ConnectContactListItem(contact, searchTerm ?: "", false) as BaseListItem } }
            .cachedIn(viewModelScope)

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

    fun getIsSearchingLiveData(): LiveData<Boolean> {
        return isSearchingLiveData
    }

    fun setSearching(searching: Boolean) {
        isSearchingLiveData.value = searching
        finishedSearching = isCurrentlySearching && !searching
        isCurrentlySearching = searching
    }

    fun onSearchTermUpdated(searchTerm: String) {
        searchTermMutableLiveData.value = searchTerm
    }

    fun fetchContacts(forceRefresh: Boolean, onSaveFinishedCallback: () -> Unit) {
        platformContactsRepository.fetchContacts(forceRefresh, {
            onSaveFinishedCallback()
        }, {})
    }
}