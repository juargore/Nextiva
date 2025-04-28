package com.nextiva.nextivaapp.android.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.db.AppDatabase
import com.nextiva.nextivaapp.android.db.model.DbSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SessionDaoTest : BaseRobolectricTest() {
    private lateinit var testDatabase: AppDatabase
    private lateinit var mSessionDao: SessionDao

    override fun setup() {
        super.setup()
        testDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).allowMainThreadQueries().build()
        mSessionDao = testDatabase.sessionDao()
        mSessionDao.insertSession(DbSession(null, "USER_AVATAR", "avatar_string"))
    }

    override fun after() {
        super.after()
        testDatabase.clearAllTables()
        testDatabase.close()
    }

    @Test
    fun getUserDetailFromKey_returnsUserDetail() {
        mSessionDao.getSessionFromKey("USER_AVATAR")
                .test()
                .assertNoErrors()

        assertEquals("avatar_string", mSessionDao.getSessionFromKey("USER_AVATAR").blockingGet().value)
        assertEquals("USER_AVATAR", mSessionDao.getSessionFromKey("USER_AVATAR").blockingGet().key)
    }

    @Test
    fun getUserDetailFromKeyInThread_returnsUserDetail() {
        assertEquals("avatar_string", mSessionDao.getSessionFromKeyInThread("USER_AVATAR").value)
        assertEquals("USER_AVATAR", mSessionDao.getSessionFromKeyInThread("USER_AVATAR").key)
    }

    @Test
    fun updateValue_updatesValue() {
        val userDetail = DbSession(null, "USER_AVATAR", "avatar_string_two")

        var cachedUserDetail = mSessionDao.getSessionFromKey("USER_AVATAR").blockingGet()

        assertNotEquals(userDetail.value, cachedUserDetail.value)

        mSessionDao.updateValue("USER_AVATAR", "avatar_string_two")

        cachedUserDetail = mSessionDao.getSessionFromKey("USER_AVATAR").blockingGet()

        assertEquals(userDetail.value, cachedUserDetail.value)
    }

    @Test
    fun insertUserDetail_detailDoesntExist_insertsUserDetail() {
        assertNull(mSessionDao.getSessionFromKeyInThread("LAST_DIALED_PHONE_NUMBER"))

        mSessionDao.insertSession(DbSession(null, "LAST_DIALED_PHONE_NUMBER", "2223334444"))

        val detail = mSessionDao.getSessionFromKeyInThread("LAST_DIALED_PHONE_NUMBER")
        assertNotNull(detail)
        assertEquals("2223334444", detail.value)
    }

    @Test
    fun insertUserDetail_detailExists_replacesExistingUserDetail() {
        assertNotNull(mSessionDao.getSessionFromKeyInThread("USER_AVATAR"))

        mSessionDao.insertSession(DbSession(null, "USER_AVATAR", "updated_avatar_string"))

        val detail = mSessionDao.getSessionFromKeyInThread("USER_AVATAR")
        assertNotNull(detail)
        assertEquals("updated_avatar_string", detail.value)
    }
}
