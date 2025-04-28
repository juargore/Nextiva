package com.nextiva.nextivaapp.android.features.rooms.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMember
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class BottomSheetRoomDetailsViewModel @Inject constructor(application: Application, val nextivaApplication: Application,
                                                          val sessionManager: SessionManager, val dbManager: DbManager,
                                                          val roomsDbManager: RoomsDbManager,
                                                          val platformRoomsRepository: PlatformRoomsRepository) : BaseViewModel(application) {

    var roomId: MutableLiveData<String?> = MutableLiveData(null)
    val contactListItemLiveData = MutableLiveData<ArrayList<BaseListItem>>()

    var dbRoom: LiveData<DbRoom?> = roomId.switchMap { roomId ->
        roomsDbManager.getRoom(roomId ?: "").let { room ->
            roomId?.let { fetchRoomDetails(roomId) }
            room
        }
    }

    private val roomMembers: LiveData<List<ConnectRoomMember>> = dbRoom.switchMap {
        MutableLiveData<List<ConnectRoomMember>>(it?.members)
    }

    init {
        roomMembers.observeForever {
            setup()
        }
    }

    private fun fetchRoomDetails(roomId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            platformRoomsRepository.getRoom(roomId)?.let { room ->
                if(room.lastModifiedTime != dbRoom.value?.lastModifiedTime) {
                    roomsDbManager.saveRooms(arrayListOf(room))
                        .subscribe()
                }
            }
        }
    }

    fun setFavorite() {
        val dbRoom = dbRoom.value ?: return
        (dbRoom.requestorFavorite)?.let { isFavorite ->
            (dbRoom.roomId)?.let { roomId ->
                platformRoomsRepository.setRoomsFavorite(roomId, !isFavorite).subscribe()
            }
        }
    }

    fun allowLeaveRoom(): Boolean {
        val dbRoom = dbRoom.value ?: return false
        val currentUser = sessionManager.userInfo?.comNextivaUseruuid ?: return false
        if (dbRoom.createdBy == currentUser || !dbRoom.isMember(currentUser)) {
            // can't leave your own room, or a room you're not a member of
            return false
        }

        val roomType = dbRoom.typeEnum()
        return roomType == RoomsEnums.ConnectRoomsTypes.MY_ROOM ||
            roomType == RoomsEnums.ConnectRoomsTypes.PUBLIC_ROOM ||
            roomType == RoomsEnums.ConnectRoomsTypes.PRIVATE_ROOM
    }

    fun leaveRoom() {
        val dbRoom = dbRoom.value ?: return
        if (!allowLeaveRoom()) return

        platformRoomsRepository.leaveRoom(dbRoom.roomId).subscribe(object : DisposableSingleObserver<Boolean>() {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    roomsDbManager.deleteRoom(dbRoom.roomId)
                }
            }

            override fun onError(e: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        })
    }

    private fun setup() {
        val dbRoom = dbRoom.value ?: return
        val currentUser = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()

        val contactList = ArrayList<BaseListItem>()
        dbRoom.members?.forEach { member ->
            member.userUuid?.let { userUuid ->
                mCompositeDisposable.add(
                    dbManager.getContactFromContactTypeId(userUuid).subscribe { contact ->
                        contactList.add(ConnectContactListItem(contact, false, userUuid == currentUser))
                        val compareById: Comparator<BaseListItem> =
                            Comparator<BaseListItem> {
                                    o1, o2 -> comparator(o1, o2)
                            }
                        Collections.sort(contactList, compareById)
                        contactListItemLiveData.postValue(contactList)
                    }
                )
            }
        }
    }

    fun displayName(isToolbarTitle: Boolean = false) : String {
        var displayName = ""
        val dbRoom = dbRoom.value ?: return displayName

        if (dbRoom.typeEnum() == RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM) {
            var myRoom = nextivaApplication.getString(R.string.my_status_my_room)
            displayName = myRoom

            sessionManager.userDetails?.fullName?.let {
                if (!TextUtils.isEmpty(it)) {
                    displayName = if (isToolbarTitle) {
                        var roomTitle = nextivaApplication.getString(R.string.connect_room_toolbar_title)
                        "$it's $roomTitle"
                    } else {
                        "$myRoom ($it)"
                    }
                }
            }
        } else {
            dbRoom.name?.let { displayName = it }
        }

        return displayName
    }

    fun comparator(baseListItem: BaseListItem, baseListItem2: BaseListItem): Int {
        val connectContactListItem = baseListItem as ConnectContactListItem
        val connectContactListItem2 = baseListItem2 as ConnectContactListItem
        if (connectContactListItem.nextivaContact != null
                && connectContactListItem.nextivaContact?.uiName != null
                && connectContactListItem2.nextivaContact != null
                && connectContactListItem2.nextivaContact?.uiName != null) {
            return connectContactListItem.nextivaContact?.uiName!!.toLowerCase(Locale.current)
                    .compareTo(connectContactListItem2.nextivaContact!!.uiName!!.toLowerCase(Locale.current))
        } else {
            return 0
        }
    }
}