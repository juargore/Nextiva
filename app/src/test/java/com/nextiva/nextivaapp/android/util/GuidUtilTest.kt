package com.nextiva.nextivaapp.android.util

import com.nextiva.nextivaapp.android.BasePowerMockTest
import com.nextiva.nextivaapp.android.db.DbManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.Random

@RunWith(PowerMockRunner::class)
@PrepareForTest(Random::class, GuidUtil::class)
class GuidUtilTest : BasePowerMockTest() {

    private var mockDbManager: DbManager = mock()
    private val mockRandom: Random = mock()
    private val emptyArrayList: ArrayList<String> = ArrayList()
    private val filledArrayList: ArrayList<String> = ArrayList()

    override fun setup() {
        super.setup()

        PowerMockito.mockStatic(Random::class.java)
        filledArrayList.add("123456")
        filledArrayList.add("234567")

        PowerMockito.whenNew(Random::class.java).withNoArguments().thenReturn(mockRandom)
        whenever(mockRandom.nextInt(900000)).thenReturn(23456, 245678)
    }

    @Test
    fun getRandomId_returnsRandomIdWithSixDigits() {
        val id = GuidUtil.getRandomId()
        assertEquals(6, id.toString().length)
    }

    @Test
    fun getUniqueContactId_IdDoesNotExist_createsNewUniqueId() {
        whenever(mockDbManager.rosterContactIds).thenReturn(emptyArrayList.toMutableList())
        assertEquals(123456, GuidUtil.getUniqueContactId(mockDbManager))
    }

    @Test
    fun getUniqueContactId_IdDoesExist_createsNewUniqueId() {
        whenever(mockDbManager.rosterContactIds).thenReturn(filledArrayList.toMutableList())
        assertEquals(345678, GuidUtil.getUniqueContactId(mockDbManager))
    }
}