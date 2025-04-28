package com.nextiva.nextivaapp.android.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.db.AppDatabase
import com.nextiva.nextivaapp.android.mocks.values.DbMeetingList
import org.junit.Assert.assertEquals
import org.junit.Test

class MeetingDaoTest : BaseRobolectricTest() {
    private lateinit var testDatabase: AppDatabase
    private lateinit var mMeetingDao: MeetingDao

    override fun setup() {
        super.setup()

        testDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).allowMainThreadQueries().build()
        mMeetingDao = testDatabase.meetingDao()
    }

    override fun after() {
        super.after()
        testDatabase.clearAllTables()
        testDatabase.close()
    }

    @Test
    fun insertMeetings_noMeetingsExist(){
        mMeetingDao.insert(DbMeetingList.getNextivaMeetingTestList())
        val cachedList = mMeetingDao.getMeetingsBetweenDates(0L,1676976400000)
        assertEquals(3,cachedList.size)
    }

    @Test
    fun deleteMeetings_deleteMeetings(){
        mMeetingDao.insert(DbMeetingList.getNextivaMeetingTestList())
        mMeetingDao.deleteMeetingsOutOfTime(1674312400000)
        val cachedList = mMeetingDao.getMeetingsBetweenDates(0L,1676976400000)
        assertEquals(2,cachedList.size)
    }
}