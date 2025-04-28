package com.nextiva.nextivaapp.android.features.rooms.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.common.UiUtil.Companion.toMutableLiveData
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMember
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.model.RoomListItem
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.threeten.bp.Instant
import javax.inject.Inject
import kotlin.collections.List
import kotlin.collections.contains
import kotlin.collections.count
import kotlin.collections.filter
import kotlin.collections.firstOrNull
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.collections.mutableMapOf
import kotlin.collections.set

@HiltViewModel
class TeamChatListViewModel @Inject constructor(application: Application, val nextivaApplication: Application,
                                                private val roomsDbManager: RoomsDbManager, private val dbManager: DbManager,
                                                private val platformRoomsRepository: PlatformRoomsRepository, val schedulerProvider: SchedulerProvider,
                                                private val presenceRepository: PresenceRepository,
                                                private val calendarManager: CalendarManager, private val sessionManager: SessionManager) : BaseViewModel(application) {

    private val formatterManager = FormatterManager.getInstance()

    val searchTermMutableLiveData = MutableLiveData<String?>()
    val searchTerm: String?
        get() { return searchTermMutableLiveData.value }
    val presenceMap = mutableMapOf<String, LiveData<DbPresence>>()
    var conversations = MutableLiveData<List<RoomListItem>>()
    private val allConversations: LiveData<List<RoomListItem>> =
        roomsDbManager.getAllRooms().map { list ->
            list.filter { item ->
                item.isChat()
            }.map { room ->
                val presenceUserId = singleMemberId(room)
                presenceUserId?.let { userId ->
                    if (!presenceMap.contains(userId)) {
                        presenceMap[userId] = dbManager.getPresenceLiveDataFromContactTypeId(userId)
                        presenceMap[userId]?.observeForever { }
                    }
                }
                RoomListItem(room, presenceUserId)
            }
        }

    init {
        conversations = allConversations.toMutableLiveData()
    }

    fun displayName(room: DbRoom?) : String {
        var displayName = ""

        room?.let { room ->
            val members = membersExcludingMe(room)
            displayName = members?.map { it.displayName ?: it.workPhone }?.joinToString(", ") ?: displayName
            displayName = displayName.reversed().replaceFirst(" ,", " & ").reversed()
        }

        return displayName
    }

    fun displayNameCount(room: DbRoom?) : String {
        var displayNameCount = ""

        room?.let { room ->
            membersExcludingMe(room)?.count()?.let { count ->
                if (count > 2) {
                    displayNameCount = " +${count}"
                }
            }
        }

        return displayNameCount
    }

    fun latestTimestamp(room: DbRoom) : String {
        // example value from api: 2023-01-13T19:38:48.190137028Z
        room.recentActivityTimestamp?.let {
            return formatterManager.format_humanReadableForConnectMainListItems(
                nextivaApplication,
                calendarManager,
                Instant.from(formatterManager.dateFormatter_8601ExtendedDatetimeTimeZoneNineMs.parse(it))
            )
        }
        return ""
    }

    fun latestMessageText(room: DbRoom) : String {
        return ""
    }

    fun avatarInfo(room: DbRoom, presence: DbPresence?) : AvatarInfo {
        val members = membersExcludingMe(room)
        val count = members?.count() ?: 0
        if (count == 0) {
            return AvatarInfo.Builder()
                .isConnect(true)
                .build()
        } else if (count == 1) {
            return AvatarInfo.Builder()
                .isConnect(true)
                .setDisplayName(members?.firstOrNull()?.displayName)
                .setPresence(presence)
                .build()
        }

        val avatarInfo = AvatarInfo.Builder().build()
        avatarInfo.setIsConnect(true)
        avatarInfo.setCounter(count)
        avatarInfo.textColor = R.color.connectSecondaryDarkBlue
        return avatarInfo
    }

    fun unreadCount(room: DbRoom) : Int {
        return room.unreadMessageCount ?: 0
    }

    private fun membersExcludingMe(room: DbRoom) : List<ConnectRoomMember>? {
        val myUserUuid = sessionManager.userInfo?.comNextivaUseruuid
        if (room.members?.count() == 1) {
            return room.members
        }
        return room.members?.filter { it.userUuid != myUserUuid }
    }

    private fun singleMemberId(room: DbRoom?) : String? {
        room?.let { room ->
            val membersExcludingMe = membersExcludingMe(room)
            if (membersExcludingMe?.count() == 1) {
                return membersExcludingMe?.firstOrNull()?.userUuid
            }
        }
        return null
    }

    fun onSearchTermUpdated(searchTerm: String?) {
        //TODO
    }

    fun fetchRooms(forceRefresh: Boolean, onSaveFinishedCallback: () -> Unit) {
        platformRoomsRepository.fetchRooms(forceRefresh, mCompositeDisposable) {
            platformRoomsRepository.fetchMyRoom(forceRefresh, mCompositeDisposable) {
                onSaveFinishedCallback()
            }
        }
    }
}