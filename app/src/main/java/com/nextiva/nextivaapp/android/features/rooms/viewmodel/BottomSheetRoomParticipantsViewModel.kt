package com.nextiva.nextivaapp.android.features.rooms.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.liveData
import androidx.paging.map
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableCompletableObserver
import javax.inject.Inject

@HiltViewModel
class BottomSheetRoomParticipantsViewModel@Inject constructor(application: Application,
                                                              val dbManager: DbManager,
                                                              val roomsDbManager: RoomsDbManager,
                                                              val sessionManager: SessionManager,
                                                              val platformRoomsRepository: PlatformRoomsRepository,
                                                              val schedulerProvider: SchedulerProvider) : BaseViewModel(application) {

    private val searchTermMutableLiveData = MutableLiveData<String>()
    val searchTerm: String?
        get() { return searchTermMutableLiveData.value }

    var dbRoom: MutableLiveData<DbRoom?> = MutableLiveData(null)
    val allListItemsLiveData: LiveData<PagingData<BaseListItem>> = dbRoom.switchMap {
        Pager(PagingConfig(pageSize = 50, prefetchDistance = 50, enablePlaceholders = true)) {
            dbManager.getContactTypePagingSource(
                intArrayOf(Enums.Contacts.ContactTypes.CONNECT_USER),
                searchTerm ?: ""
            )
        }.liveData
            .map {
                it.filter { contact ->
                    val matchingMember = dbRoom.value?.members?.firstOrNull { member -> member.userUuid == contact.userId }
                    (contact.contactType == Enums.Contacts.ContactTypes.CONNECT_USER) && (matchingMember == null)
                }
            }
            .map {
                it.map { contact ->
                    ConnectContactListItem(contact, searchTerm ?: "", false) as BaseListItem
                }
            }
            .cachedIn(viewModelScope)
    }

    val selectedContacts = ArrayList<NextivaContact>()
    val addSelectedContact = MutableLiveData<NextivaContact>()
    private val removeSelectedContact = MutableLiveData<Int>()

    fun contactSelected(nextivaContact: NextivaContact?) {
        nextivaContact?.let {
            addSelectedContact.value = it
        }
    }

    fun contactAdded(nextivaContact: NextivaContact) {
        selectedContacts.add(nextivaContact)
    }

    fun removeContact(nextivaContact: NextivaContact) {
        val index = selectedContacts.indexOf(nextivaContact)
        selectedContacts.remove(nextivaContact)
        removeSelectedContact.value = index
    }

    fun removeAllContacts() {
        selectedContacts.clear()
    }

    fun selectedContactsCount() : Int {
        return selectedContacts.size
    }

    fun onSearchTermUpdated(searchTerm: String) {
        searchTermMutableLiveData.value = searchTerm
    }

    fun isMessageContactAlreadyAdded(nextivaContact: NextivaContact?) : Boolean {
        return selectedContacts.contains(nextivaContact)
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

    fun sendMembers(completion: () -> Unit) {
        val roomId = dbRoom.value?.roomId ?: return
        mCompositeDisposable.add(
            platformRoomsRepository.addRoomMembers(roomId, selectedContacts)
                .subscribe { sendChatMessageResponse ->
                    val roomsList = sendChatMessageResponse.roomsList
                    if (roomsList != null) {
                        roomsDbManager.saveRooms(roomsList)
                            .observeOn(schedulerProvider.ui())
                            .subscribe(object : DisposableCompletableObserver() {
                                override fun onComplete() {
                                    completion()
                                }

                                override fun onError(e: Throwable) {
                                    completion()
                                }
                            })
                    } else {
                        completion()
                    }
                }
        )
    }
}
