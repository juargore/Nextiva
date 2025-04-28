package com.nextiva.nextivaapp.android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.filters.ContactsSearchFilter
import com.nextiva.nextivaapp.android.managers.interfaces.LocalContactsManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import javax.inject.Inject

@HiltViewModel
class BottomSheetViewLocalContactsViewModel @Inject constructor(
    val dbManager: DbManager,
    val dbManagerKt: DbManagerKt,
    val schedulerProvider: SchedulerProvider,
    val localContactsManager: LocalContactsManager,
    val sipManager: PJSipManager
) : ViewModel(), ContactsSearchFilter.ContactSearchFilterCallback {

    private val searchTermMutableLiveData = MutableLiveData<String>()
    private var baseListItemsLiveData: MediatorLiveData<ArrayList<BaseListItem>> = MediatorLiveData()
    private val dbLiveData: LiveData<List<NextivaContact>> = dbManager.getContactsLiveData(Enums.Contacts.CacheTypes.LOCAL)
    val activeCallLiveData = sipManager.activeCallLiveData
    private var initialSelectedContacts = setOf<String>()
    private var maxSelectableItems = 0
    private var numOfSelectedItems = 0

    private val compositeDisposable = CompositeDisposable()

    enum class SelectAllState {
        Idle, SelectAll, Jump, None
    }

    enum class ActionLabel {
        Import, SelectAll, DeselectAll, None
    }

    var shouldSelectAll: SelectAllState = SelectAllState.Idle
        set(value) {
            if (value == SelectAllState.SelectAll && field == SelectAllState.Idle) {
                field = value
            } else if (value == SelectAllState.Jump && field == SelectAllState.SelectAll) {
                field = value
            } else if (value == SelectAllState.None) {
                field = value
            }
        }

    val searchTerm: String?
        get() {
            return searchTermMutableLiveData.value
        }

    var isImporting: Boolean = false
    var preselectedContact: NextivaContact? = null

    private data class ContactImportState(val contact: NextivaContact, var importState: Int?)

    private var contactImportStates: ArrayList<ContactImportState> = ArrayList()
    private var lookupKeyList: List<String> = ArrayList()

    private val contactsSearchFilter = ContactsSearchFilter(this)
    private val localContactComparator = compareBy<NextivaContact> {
        it.uiName?.firstOrNull()?.isLetter() == false
    }.thenBy(String.CASE_INSENSITIVE_ORDER) {
        it.uiName ?: ""
    }

    init {
        localContactsManager.localContacts.subscribe()
    }

    fun setInitialSelectedContacts(selectedContacts: List<NextivaContact>) {
        initialSelectedContacts = mutableSetOf<String>().apply {
            selectedContacts.forEach{ this.add(it.userId) }
        }
    }

    fun refreshContacts() {
        refreshContacts(isImporting = false, preselected = null)
    }

    fun refreshContacts(isImporting: Boolean, preselected: NextivaContact?) {
        this.isImporting = isImporting
        this.preselectedContact = preselected

        baseListItemsLiveData.removeSource(dbLiveData)
        baseListItemsLiveData.addSource(dbLiveData) { nextivaContactsList ->
            refreshLookupKeysAndFilterContacts(nextivaContactsList)
        }
    }

    private fun refreshLookupKeysAndFilterContacts(nextivaContactsList: List<NextivaContact>?) {
        dbManager.businessContactLookupKeysAndPrimaryWorkEmails
            .observeOn(schedulerProvider.ui())
            .subscribeWith(object : DisposableSingleObserver<List<String>>() {
                override fun onSuccess(lookupKeyList: List<String>) {
                    refreshLookupKeysAndFilterContactsSuccess(lookupKeyList, nextivaContactsList)
                }

                override fun onError(e: Throwable) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }).also { disposable ->
                compositeDisposable.add(disposable)
            }
    }

    internal fun refreshLookupKeysAndFilterContactsSuccess(
        lookupKeyList: List<String>,
        nextivaContactsList: List<NextivaContact>?
    ) {
        this.lookupKeyList = lookupKeyList
        if (isImporting && contactImportStates.size != nextivaContactsList?.size) {
            refreshImportStatesLookupKeysAndFilter(nextivaContactsList)
        }

        contactsSearchFilter.filter(nextivaContactsList, searchTermMutableLiveData.value)
    }

    private fun refreshImportStatesLookupKeysAndFilter(nextivaContactsList: List<NextivaContact>?) {
        contactImportStates = ArrayList()
        nextivaContactsList?.forEach { contact ->
            val state = ContactImportState(contact, getImportState(contact)).apply {
                if (importState != Enums.Platform.ConnectContactListItemImportState.IMPORTED &&
                    (initialSelectedContacts.contains(contact.userId) || shouldSelectAll == SelectAllState.SelectAll)
                ) {
                    importState = Enums.Platform.ConnectContactListItemImportState.SELECTED
                }
            }
            contactImportStates.add(state)
        }

        if (shouldSelectAll == SelectAllState.SelectAll) {
            shouldSelectAll = SelectAllState.Jump
        }
        initialSelectedContacts = setOf()
    }

    override fun onContactSearchFilterResults(
        nextivaContactsList: MutableList<NextivaContact>?,
        isFullList: Boolean
    ) {
        Single.fromCallable {
            val sortedContactsList =
                nextivaContactsList?.apply { sortWith(localContactComparator) } ?: mutableListOf()
            maxSelectableItems = 0
            numOfSelectedItems = 0

            val listItems = sortedContactsList.map { contact ->
                ConnectContactListItem(
                    contact,
                    searchTerm ?: "",
                    if (this.isImporting) getImportState(contact) else null
                ).apply {
                    if (this.importState != Enums.Platform.ConnectContactListItemImportState.IMPORTED) {
                        maxSelectableItems++
                        if (this.importState == Enums.Platform.ConnectContactListItemImportState.SELECTED) {
                            numOfSelectedItems++
                        }
                    }
                }
            }.also { list ->
                preselectedContact?.userId?.let { userId ->
                    list.find { it.nextivaContact?.userId == userId }?.importState =
                        Enums.Platform.ConnectContactListItemImportState.SELECTED
                }
            }
            listItems as ArrayList<BaseListItem>
        }.subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribeWith(object : DisposableSingleObserver<ArrayList<BaseListItem>>() {
                override fun onSuccess(listItems: ArrayList<BaseListItem>) {
                    baseListItemsLiveData.value = listItems
                    preselectedContact = null
                }

                override fun onError(e: Throwable) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }).also { disposable ->
                compositeDisposable.add(disposable)
            }
    }

    fun getBaseListItemsLiveData(): LiveData<ArrayList<BaseListItem>> {
        return baseListItemsLiveData
    }

    fun onSearchTermUpdated(searchTerm: String) {
        searchTermMutableLiveData.value = searchTerm
        refreshContacts(isImporting, null)
    }

    fun updateContactImportStates(nextivaContact: NextivaContact, importState: Int) {
        contactImportStates.firstOrNull { state -> state.contact.userId == nextivaContact.userId }
            ?.let {
                if(importState == Enums.Platform.ConnectContactListItemImportState.SELECTED) {
                    numOfSelectedItems++
                } else if(importState == Enums.Platform.ConnectContactListItemImportState.UNSELECTED){
                    numOfSelectedItems--
                }
                it.importState = importState
            }
    }

    fun toggleAllImportStates() {
        val newState = if (maxSelectableItems != numOfSelectedItems) {
            Enums.Platform.ConnectContactListItemImportState.SELECTED
        } else {
            Enums.Platform.ConnectContactListItemImportState.UNSELECTED
        }

        baseListItemsLiveData.value?.forEach { listItem ->
            listItem as ConnectContactListItem
            contactImportStates.firstOrNull {
                it.contact.userId == listItem.nextivaContact?.userId
            }?.let {

                if (it.importState != Enums.Platform.ConnectContactListItemImportState.IMPORTED) {
                    it.importState = newState
                }
            }
        }
    }

    fun resetImportStates() {
        contactImportStates = ArrayList()
        initialSelectedContacts = setOf()
    }

    private fun getImportState(contact: NextivaContact): Int? {
        contact.lookupKey?.let { lookupKey ->
            if (lookupKey.isNotEmpty()) {
                if (lookupKeyList.contains(lookupKey)) {
                    updateContactImportStates(
                        contact,
                        Enums.Platform.ConnectContactListItemImportState.IMPORTED
                    )
                    return Enums.Platform.ConnectContactListItemImportState.IMPORTED
                }
            }
        }

        return when {
            contact == preselectedContact -> Enums.Platform.ConnectContactListItemImportState.SELECTED
            contactImportStates.firstOrNull { it.contact.userId == contact.userId }?.importState != null ->
                contactImportStates.firstOrNull { it.contact.userId == contact.userId }?.importState
            else -> Enums.Platform.ConnectContactListItemImportState.UNSELECTED
        }
    }

    fun getUnimportedContactsSize(): Int {
        return contactImportStates.count { it.importState != Enums.Platform.ConnectContactListItemImportState.IMPORTED }
    }

    fun getSelectedContactsSize(): Int {
        return contactImportStates.count { it.importState == Enums.Platform.ConnectContactListItemImportState.SELECTED }
    }

    fun getSelectedContacts(): List<NextivaContact> {
        return contactImportStates.filter { it.importState == Enums.Platform.ConnectContactListItemImportState.SELECTED }
            .map { it.contact }
    }

    fun getActionLabel(): ActionLabel {
        return when {
            !isImporting -> ActionLabel.Import
            maxSelectableItems == 0 -> ActionLabel.None
            maxSelectableItems == numOfSelectedItems -> ActionLabel.DeselectAll
            else -> ActionLabel.SelectAll
        }
    }

    fun clearCompositeDisposable() {
        compositeDisposable.clear()
    }

    suspend fun getContactFromPhoneNumber(phoneNumber: String): NextivaContact? {
        return dbManagerKt.getContactFromPhoneNumberInThread(phoneNumber).value
    }
}