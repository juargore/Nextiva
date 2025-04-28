package com.nextiva.nextivaapp.android.viewmodels

import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomAdmin
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMediaCallMetadata
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMember
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.TeamChatListViewModel
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.threeten.bp.Month
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@HiltAndroidTest
class TeamChatListViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    private var mViewModel: TeamChatListViewModel? = null

    @Inject
    lateinit var roomsDbManager: RoomsDbManager

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var platformRoomsRepository: PlatformRoomsRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var presenceRepository: PresenceRepository

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var calendarManager: CalendarManager

    private lateinit var dbRoom: DbRoom

    private val zoneId = ZoneId.systemDefault()

    override fun setup() {
        super.setup()
        hiltRule.inject()
        MockitoAnnotations.initMocks(this)

        whenever(calendarManager.nowInstant).thenReturn(ZonedDateTime.of(2008, Month.SEPTEMBER.value, 23, 11, 3, 25, 123000000, ZoneId.of("GMT-7")).toInstant())

        var admins: List<ConnectRoomAdmin> = ArrayList<ConnectRoomAdmin>()
        var members: List<ConnectRoomMember> = ArrayList<ConnectRoomMember>()

        mViewModel = TeamChatListViewModel(
            ApplicationProvider.getApplicationContext(),
            ApplicationProvider.getApplicationContext(),
            roomsDbManager,
            dbManager,
            platformRoomsRepository,
            schedulerProvider,
            presenceRepository,
            calendarManager,
            sessionManager
        )

        dbRoom = DbRoom(roomId = "roomId",
            name = "123456",
            description = "Description",
            archived = "archived",
            admins = admins,
            members = members,
            recentActivityType = "type",
            recentActivityTimestamp = "2023-01-13T19:38:48.190137028Z",
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
            ownerId = "")
    }

    @Test
    fun testLatestTimestamp() {
        whenever(calendarManager.nowInstant)
            .thenAnswer {
                ZonedDateTime.of(
                    2009,
                    Month.JANUARY.value,
                    10,
                    11,
                    3,
                    25,
                    123000000,
                    zoneId
                ).toInstant()
            }
        val formatterManager = FormatterManager.getInstance()

        val test1 = ZonedDateTime.of(
            2009,
            Month.JANUARY.value,
            10,
            14,
            3,
            25,
            123000000,
            zoneId
        ).toInstant()
        test1.atZone(zoneId)
        dbRoom.recentActivityTimestamp = formatterManager.dateFormatter_8601ExtendedDatetimeTimeZoneNineMs.format(test1)
        assertEquals("2:03 pm", mViewModel?.latestTimestamp(dbRoom))

        val test2 = ZonedDateTime.of(
            2009,
            Month.JANUARY.value,
            9,
            14,
            3,
            25,
            123000000,
            zoneId
        ).toInstant()
        test2.atZone(zoneId)
        dbRoom.recentActivityTimestamp = formatterManager.dateFormatter_8601ExtendedDatetimeTimeZoneNineMs.format(test2)
        assertEquals("Yesterday", mViewModel?.latestTimestamp(dbRoom))

        val test3 = ZonedDateTime.of(
            2009,
            Month.JANUARY.value,
            4,
            14,
            3,
            25,
            123000000,
            zoneId
        ).toInstant()
        test3.atZone(zoneId)
        dbRoom.recentActivityTimestamp = formatterManager.dateFormatter_8601ExtendedDatetimeTimeZoneNineMs.format(test3)
        assertEquals("Sunday", mViewModel?.latestTimestamp(dbRoom))

        val test4 = ZonedDateTime.of(
            2009,
            Month.JANUARY.value,
            1,
            14,
            3,
            25,
            123000000,
            zoneId
        ).toInstant()
        test4.atZone(zoneId)
        dbRoom.recentActivityTimestamp = formatterManager.dateFormatter_8601ExtendedDatetimeTimeZoneNineMs.format(test4)
        assertEquals("Jan 01", mViewModel?.latestTimestamp(dbRoom))
    }

    @Test
    fun testUnreadCount() {
        assertEquals(mViewModel?.unreadCount(dbRoom), 0)

        dbRoom.unreadMessageCount = 2
        assertEquals(mViewModel?.unreadCount(dbRoom), 2)
    }
}