package com.nextiva.nextivaapp.android.adapters.viewholders

import android.content.Context
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallDetailPhoneNumberListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.CallDetailPhoneNumberViewHolder
import com.nextiva.nextivaapp.android.view.DetailItemView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mock
import java.io.IOException

class CallDetailPhoneNumberViewHolderTest : BaseRobolectricTest() {

    lateinit var viewHolder: CallDetailPhoneNumberViewHolder

    @Mock
    private val mMockMasterListListener: MasterListListener? = null

    @Throws(IOException::class)
    override fun setup() {
        super.setup()
        viewHolder = CallDetailPhoneNumberViewHolder(FrameLayout(ApplicationProvider.getApplicationContext<Context>()), ApplicationProvider.getApplicationContext<Context>(), mMockMasterListListener)
    }

    @Test
    fun bind_phoneNumberSubTitleSet_subTitleShown() {
        val listItem = CallDetailPhoneNumberListItem("Title", "3334445555", 0, 0)
        viewHolder.bind(listItem)

        val detailItemView = viewHolder.itemView.findViewById<DetailItemView>(R.id.list_item_detail_item_view)

        assertEquals("(333) 444-5555", detailItemView.subTitleText)
    }

}