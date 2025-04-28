package com.nextiva.nextivaapp.android.core.common.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.models.NextivaContact
import kotlinx.coroutines.flow.Flow

interface BottomSheetSelectContactsInterface {

    val searchTermMutableLiveData: MutableLiveData<String>

    val searchTerm: String?

    val allListItemsLiveData: Flow<PagingData<BaseListItem>>

    val selectedContacts: ArrayList<NextivaContact>
    val addSelectedContact: MutableLiveData<NextivaContact>
    val removeSelectedContact: MutableLiveData<Int>

    fun contactSelected(nextivaContact: NextivaContact?) {
        nextivaContact?.let {
            addSelectedContact.value = it
        }
    }

    fun nextIntent(context: Context): Intent

    fun contactAdded(nextivaContact: NextivaContact)

    fun removeContact(nextivaContact: NextivaContact) {
        val index = selectedContacts.indexOf(nextivaContact)
        selectedContacts.remove(nextivaContact)
        removeSelectedContact.value = index
    }

    fun removeAllContacts() {
        selectedContacts.clear()
    }

    fun selectedContactsCount(): Int {
        return selectedContacts.size
    }

    fun onSearchTermUpdated(searchTerm: String) {
        searchTermMutableLiveData.value = searchTerm
    }

    fun isMessageContactAlreadyAdded(nextivaContact: NextivaContact?): Boolean {
        return selectedContacts.count { it.userId == nextivaContact?.userId } > 0
    }

    fun getContactFromUserId(userId: String, contactCallback: (NextivaContact?) -> Unit)

    fun getContactFromPhoneNumber(phoneNumber: String, contactCallback: (NextivaContact?) -> Unit)
}