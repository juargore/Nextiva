package com.nextiva.nextivaapp.android.adapters.listitems

import com.nextiva.nextivaapp.android.BasePowerMockTest
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallDetailListItem
import com.nextiva.nextivaapp.android.constants.Enums
import org.junit.Assert.assertFalse
import org.junit.Test

class CallDetailListItemTest : BasePowerMockTest() {

    @Test
    fun constructor_correctlySetsDefaultValues() {
        val listItem = CallDetailListItem(Enums.Calls.DetailViewTypes.PHONE_NUMBER,
                "Title",
                "SubTitle",
                R.drawable.ic_phone,
                R.drawable.ic_video,
                true)

        assertFalse(listItem.isClickable)
    }
}