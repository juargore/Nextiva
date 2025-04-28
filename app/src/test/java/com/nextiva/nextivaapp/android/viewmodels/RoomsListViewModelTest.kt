package com.nextiva.nextivaapp.android.viewmodels

import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.RoomsMasterListListener
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomAdmin
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMediaCallMetadata
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMember
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.ConnectRoomsListViewModel
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import javax.inject.Inject

@HiltAndroidTest
class RoomsListViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var mViewModel: ConnectRoomsListViewModel? = null

    @Mock
    private val mMockMasterListListener: RoomsMasterListListener? = null

    @Inject
    lateinit var roomsDbManager: RoomsDbManager

    @Inject
    lateinit var platformRoomsRepository: PlatformRoomsRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var presenceRepository: PresenceRepository

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var dbRoom: DbRoom

    override fun setup() {
        super.setup()
        hiltRule.inject()
        MockitoAnnotations.initMocks(this)

        var admins: List<ConnectRoomAdmin> = ArrayList<ConnectRoomAdmin>()
        var members: List<ConnectRoomMember> = ArrayList<ConnectRoomMember>()

        mViewModel = ConnectRoomsListViewModel(
                ApplicationProvider.getApplicationContext(),
                ApplicationProvider.getApplicationContext(),
                roomsDbManager,
                platformRoomsRepository,
                schedulerProvider,
                presenceRepository,
                sessionManager
        )

        dbRoom = DbRoom(roomId = "roomId",
                name = "123456",
                description = "Description",
                archived = "archived",
                admins = admins,
                members = members,
                recentActivityType = "type",
                recentActivityTimestamp = "recentActivityTimestamp",
                createdBy = "createdBy",
                createdTime = "createdTime",
                lastModifiedTime = "lastModifiedTime",
                lastModifiedBy = "lastModifiedBy",
                type = "type",
                requestorID = "requestorID",
                requestorMuteNotifications = false,
                requestorFavorite = false,
                requestorHideRoom = false,
                archivedBy = "archivedBy",
                archivedTime = "archivedTime",
                unarchivedBy = "unarchivedBy",
                unarchivedTime = "unarchivedTime",
                corpId = "corpId",
                favoriteInteractionError = "favoriteInteractionError",
                locked = false,
                mediaCallMetaData = ConnectRoomMediaCallMetadata(null, null, null, null, null, null),
                unreadMessageCount = 0,
                ownerId = ""
        )
    }


    @Test
    fun testMyRoomShowsMyRoomOnTitle() {
        dbRoom.type = RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM.value
        val displayName = mViewModel?.displayName(dbRoom)
        assertEquals(displayName, "My Room")
    }

    @Test
    fun testMyRoomShowsTitle() {
        val displayName = mViewModel?.displayName(dbRoom)
        assertEquals(displayName, "123456")
    }
}