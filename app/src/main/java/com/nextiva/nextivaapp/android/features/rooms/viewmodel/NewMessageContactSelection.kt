package com.nextiva.nextivaapp.android.features.rooms.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.common.ui.BottomSheetSelectContactsInterface
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.view.RoomConversationActivity
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.CallUtil
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewMessageContactSelection(
    val dbManager: DbManager,
    val roomsDbManager: RoomsDbManager,
    val sessionManager: SessionManager
) : BottomSheetSelectContactsInterface {

    val compositeDisposable = CompositeDisposable()
    override val searchTermMutableLiveData = MutableLiveData<String>()

    override val searchTerm: String?
        get() { return searchTermMutableLiveData.value }

    override val allListItemsLiveData: Flow<PagingData<BaseListItem>> = Pager(PagingConfig(pageSize = 50, prefetchDistance = 50, enablePlaceholders = true)) {
        dbManager.getContactTypePagingSource(intArrayOf(Enums.Contacts.ContactTypes.CONNECT_USER), searchTerm ?: "")
    }.flow
        .map {
            it.map { contact ->
                ConnectContactListItem(contact, searchTerm ?: "", false) as BaseListItem
            }
        }

    override val selectedContacts = ArrayList<NextivaContact>()
    override val addSelectedContact = MutableLiveData<NextivaContact>()
    override val removeSelectedContact = MutableLiveData<Int>()

    override fun contactSelected(nextivaContact: NextivaContact?) {
        nextivaContact?.let {
            addSelectedContact.value = it
        }
    }

    override fun nextIntent(context: Context): Intent {
        val userIdList = selectedContacts.map { it.userId }.toMutableList()
        sessionManager.userInfo?.comNextivaUseruuid?.let { userIdList.add(it) }

        val chatRoom = roomsDbManager.getRoomInThread(userIdList)
        val roomId = chatRoom?.roomId  ?: ""
        val title = selectedContacts.map { it.displayName }.joinToString(", ")
        val memberUuids = selectedContacts.map { it.userId }.toTypedArray()
        val titleCount = if (memberUuids.count() > 2) " +${memberUuids.count()}" else ""
        return RoomConversationActivity.newIntent(context, roomId, title, titleCount, memberUuids)
    }

    override fun contactAdded(nextivaContact: NextivaContact) {
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
    }

    override fun getContactFromUserId(userId: String, contactCallback: (NextivaContact?) -> Unit) {
        compositeDisposable.add(
            dbManager.getContactFromContactTypeId(userId).subscribe { contact ->
                contactCallback(contact)
            }
        )
    }

    override fun getContactFromPhoneNumber(phoneNumber: String, contactCallback: (NextivaContact?) -> Unit) {
        compositeDisposable.add(
            dbManager.getConnectContactFromPhoneNumber(phoneNumber).subscribe { contact ->
                contactCallback(contact.value)
            }
        )
    }

}
