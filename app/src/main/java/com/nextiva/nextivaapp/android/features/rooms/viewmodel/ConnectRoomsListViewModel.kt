package com.nextiva.nextivaapp.android.features.rooms.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.liveData
import androidx.paging.map
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsHeaderListItem
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsListItem
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.ListHeaderRow
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectRoomsListViewModel @Inject constructor(application: Application, val nextivaApplication: Application, val dbManager: RoomsDbManager,
                                                    private val platformRoomsRepository: PlatformRoomsRepository, val schedulerProvider: SchedulerProvider,
                                                    private val presenceRepository: PresenceRepository, private val sessionManager: SessionManager) : BaseViewModel(application) {

    private var favoriteHeaderListItem: ConnectRoomsHeaderListItem =
            ConnectRoomsHeaderListItem(ListHeaderRow(nextivaApplication.getString(R.string.connect_rooms_group_header_favorites)),
                    RoomsEnums.ConnectRoomsGroups.FAVORITES,
                    dbManager.getConnectRoomsGroupCount(RoomsEnums.ConnectRoomsGroups.FAVORITES),
                    isExpanded = false)
    private var roomsHeaderListItem: ConnectRoomsHeaderListItem =
            ConnectRoomsHeaderListItem(ListHeaderRow(nextivaApplication.getString(R.string.connect_rooms_group_header_rooms)),
                    RoomsEnums.ConnectRoomsGroups.ROOMS,
                    dbManager.getConnectRoomsGroupCount(RoomsEnums.ConnectRoomsGroups.ROOMS),
                    isExpanded = true)

    val searchTermMutableLiveData = MutableLiveData<String?>()
    var isCurrentlySearching = false
    var finishedSearching = false
    var shouldScrollToTop = false
    var hasLoaded: Boolean = false
    val searchTerm: String?
        get() { return searchTermMutableLiveData.value }

    var ids: ArrayList<String> = ArrayList()

    val listItemsLiveData: MediatorLiveData<PagingData<BaseListItem>> = MediatorLiveData()
    private val groupedListItemsLiveData: LiveData<PagingData<BaseListItem>> = Pager(PagingConfig(pageSize = 50, prefetchDistance = 50, enablePlaceholders = true)) {
        dbManager.getConnectRoomsPagingSource(favoriteHeaderListItem.isExpanded, roomsHeaderListItem.isExpanded)
    }.liveData
            .map {
                it
                    .filter {
                        sessionManager.userInfo?.comNextivaUseruuid?.let { it1 -> it.isMember(it1) } == true &&
                        (it.type?.equals(RoomsEnums.ConnectRoomsTypes.MY_ROOM.value) == true ||
                         it.type?.equals(RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM.value) == true ||
                         it.type?.equals(RoomsEnums.ConnectRoomsTypes.PRIVATE_ROOM.value) == true ||
                         it.type?.equals(RoomsEnums.ConnectRoomsTypes.PUBLIC_ROOM.value) == true)
                    }
                        .map { dbRoom ->
                            val connectRoomsListItem = ConnectRoomsListItem(
                                    dbRoom,
                                    if (dbRoom.requestorFavorite == true && !ids.contains(dbRoom.roomId) && favoriteHeaderListItem.isExpanded) {
                                        ids.add(dbRoom.roomId)
                                    RoomsEnums.ConnectRoomsGroups.FAVORITES
                                } else {
                                    RoomsEnums.ConnectRoomsGroups.ROOMS
                                }
                        )
                        connectRoomsListItem as BaseListItem
                    }
                    .insertSeparators { before, after -> getSeparator(before, after, RoomsEnums.ConnectRoomsGroups.ROOMS) }
                    .insertSeparators { before, after -> getSeparator(before, after, RoomsEnums.ConnectRoomsGroups.FAVORITES) }
            }
            .cachedIn(viewModelScope)

    init {
        listItemsLiveData.addSource(groupedListItemsLiveData) {
            ids.clear()
            listItemsLiveData.value = it
        }
    }

    private fun getSeparator(before: BaseListItem?, after: BaseListItem?, groupValue: RoomsEnums.ConnectRoomsGroups): ConnectRoomsHeaderListItem? {
        val beforeGroupValue = when (before) {
            is ConnectRoomsListItem -> before.groupValue
            is ConnectRoomsHeaderListItem -> before.itemType
            else -> null
        }

        val afterGroupValue = when (after) {
            is ConnectRoomsListItem -> after.groupValue
            is ConnectRoomsHeaderListItem -> after.itemType
            else -> null
        }

        if (before == null && after == null) {
            return when (groupValue) {
                RoomsEnums.ConnectRoomsGroups.FAVORITES -> favoriteHeaderListItem
                else -> roomsHeaderListItem
            }
        } else if ((before is ConnectRoomsHeaderListItem && before.itemType == groupValue) ||
            (after is ConnectRoomsHeaderListItem && after.itemType == groupValue)) {
            return null
        } else if (beforeGroupValue != afterGroupValue) {
            return when {
                groupValue == RoomsEnums.ConnectRoomsGroups.FAVORITES &&
                        beforeGroupValue == null -> favoriteHeaderListItem
                groupValue == RoomsEnums.ConnectRoomsGroups.ROOMS &&
                        beforeGroupValue != RoomsEnums.ConnectRoomsGroups.ROOMS &&
                        afterGroupValue != RoomsEnums.ConnectRoomsGroups.FAVORITES -> roomsHeaderListItem
                else -> null
            }
        }

        return null
    }

    fun displayName(room: DbRoom?, isToolbarTitle: Boolean = false) : String {
        var displayName = ""

        room?.let { room ->
            if (room.typeEnum() == RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM) {
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
                room.name?.let { displayName = it }
            }
        }

        return displayName
    }

    fun headerListItemClicked(headerListItem: ConnectRoomsHeaderListItem) {
        // TODO - this may be needed when Favorites is added
    }

    fun onSearchTermUpdated(searchTerm: String?) {
        //TODO
    }

    fun setFavorite(roomsListItem: ConnectRoomsListItem) {
        (roomsListItem.room.requestorFavorite)?.let { isFavorite ->
            platformRoomsRepository.setRoomsFavorite(
                roomsListItem.room.roomId,
                !isFavorite)
                .subscribe()
        }
    }

    fun fetchRooms(forceRefresh: Boolean, onSaveFinishedCallback: () -> Unit) {
        platformRoomsRepository.fetchRooms(forceRefresh, mCompositeDisposable) {
            platformRoomsRepository.fetchMyRoom(forceRefresh, mCompositeDisposable) {
                onSaveFinishedCallback()
            }
        }
    }
}