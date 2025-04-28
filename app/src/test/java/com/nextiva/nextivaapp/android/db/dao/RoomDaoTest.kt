//package com.nextiva.nextivaapp.android.db.dao
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomAdmin
//import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMediaCallMetadata
//import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMember
//import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDatabase
//import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
//import com.nextiva.nextivaapp.android.util.getOrAwaitValue
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.Assert.assertEquals
//import org.junit.Rule
//import org.junit.Test
//import org.robolectric.annotation.LooperMode
//import org.robolectric.shadows.ShadowLooper
//
//@OptIn(ExperimentalCoroutinesApi::class)
//@LooperMode(LooperMode.Mode.PAUSED)
//class RoomDaoTest : BaseRobolectricTest() {
//
//    private lateinit var testDatabase: RoomsDatabase
//
//    private lateinit var dbRoom: DbRoom
//
//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()
//
//    val roomId = "roomId"
//
//    val dispatcher = StandardTestDispatcher()
//
//    override fun setup() {
//        super.setup()
//        Dispatchers.setMain(dispatcher)
//        testDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), RoomsDatabase::class.java).allowMainThreadQueries().build()
//
//        var admins: List<ConnectRoomAdmin> = ArrayList<ConnectRoomAdmin>()
//        var members: List<ConnectRoomMember> = ArrayList<ConnectRoomMember>()
//        dbRoom = DbRoom(roomId = roomId,
//                name = "123456",
//                description = "Description",
//                archived = "archived",
//                admins = admins,
//                members = members,
//                recentActivityType = "type",
//                recentActivityTimestamp = "recentActivityTimestamp",
//                createdBy = "createdBy",
//                createdTime = "createdTime",
//                lastModifiedTime = "lastModifiedTime",
//                lastModifiedBy = "lastModifiedBy",
//                type = "type",
//                requestorID = "requestorID",
//                requestorMuteNotifications = false,
//                requestorFavorite = false,
//                requestorHideRoom = false,
//                archivedBy = "archivedBy",
//                archivedTime = "archivedTime",
//                unarchivedBy = "unarchivedBy",
//                unarchivedTime = "unarchivedTime",
//                corpId = "corpId",
//                favoriteInteractionError = "favoriteInteractionError",
//                locked = false,
//                mediaCallMetaData = ConnectRoomMediaCallMetadata(null, null, null, null, null, null),
//                unreadMessageCount = 0,
//                ownerId = ""
//        )
//    }
//
//    override fun after() {
//        super.after()
//        testDatabase.clearAllTables()
//        testDatabase.close()
//    }

//    @Test
//    fun insertContact() = runTest {
//        assertEquals(1, testDatabase.roomDao().insertConnectRoom(dbRoom))
//        ShadowLooper.idleMainLooper()
//        assertEquals(roomId, testDatabase.roomDao().getRoom(roomId)?.roomId)
//    }

//    @Test
//    fun testSetRoomFavorite() = runTest {
//        assertEquals(1, testDatabase.roomDao().insertConnectRoom(dbRoom))
//        testDatabase.roomDao().setFavorite(roomId, true)
//        ShadowLooper.idleMainLooper()
//        assertEquals(true, testDatabase.roomDao().getRoom(roomId).requestorFavorite)
//
//        testDatabase.roomDao().setFavorite(roomId, false)
//        ShadowLooper.idleMainLooper()
//        assertEquals(false, testDatabase.roomDao().getRoom(roomId).requestorFavorite)
//    }

//    @Test
//    fun testRoomCount() = runTest {
//        assertEquals(1, testDatabase.roomDao().insertConnectRoom(dbRoom))
//        ShadowLooper.idleMainLooper()
//        assertEquals(1, testDatabase.roomDao().connectRoomsCount.getOrAwaitValue())
//    }
//
//    @Test
//    fun testRoomFavoriteCount() = runBlocking {
//        val dbRoomFavorite = dbRoom.copy(requestorFavorite = true)
//        assertEquals(1, testDatabase.roomDao().insertConnectRoom(dbRoomFavorite))
//        ShadowLooper.idleMainLooper()
//        val count = testDatabase.roomDao().connectRoomsFavoritesCount.getOrAwaitValue();
//        assertEquals(1, count)
//    }
//}
