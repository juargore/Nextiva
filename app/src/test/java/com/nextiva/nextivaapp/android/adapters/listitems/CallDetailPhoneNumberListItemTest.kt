package com.nextiva.nextivaapp.android.adapters.listitems

import com.nextiva.nextivaapp.android.BasePowerMockTest
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallDetailPhoneNumberListItem
import com.nextiva.nextivaapp.android.constants.Enums
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CallDetailPhoneNumberListItemTest : BasePowerMockTest() {

    @Test
    fun constructor_correctlySetsDefaultValues() {
        val listItem = CallDetailPhoneNumberListItem(
                "Title",
                "SubTitle",
                R.drawable.ic_phone,
                R.drawable.ic_video)

        assertEquals(Enums.Calls.DetailViewTypes.PHONE_NUMBER, listItem.viewType)
        assertFalse(listItem.isClickable)
        assertTrue(listItem.isLongClickable)
    }
}