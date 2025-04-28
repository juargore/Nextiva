package com.nextiva.nextivaapp.android.adapters.viewholders

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallDetailDatetimeListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.CallDetailDatetimeViewHolder
import com.nextiva.nextivaapp.android.constants.Enums
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.robolectric.Shadows.shadowOf

class CallDetailDatetimeViewHolderTest : BaseRobolectricTest() {

    lateinit var viewHolder: CallDetailDatetimeViewHolder

    lateinit var titleTextView: TextView
    lateinit var subTitleTextView: TextView
    lateinit var callTypeImageView: ImageView

    override fun setup() {
        super.setup()

        viewHolder = CallDetailDatetimeViewHolder(FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null)

        titleTextView = viewHolder.itemView.findViewById(R.id.list_item_call_details_datetime_title_text_view)
        subTitleTextView = viewHolder.itemView.findViewById(R.id.list_item_call_details_datetime_sub_title_text_view)
        callTypeImageView = viewHolder.itemView.findViewById(R.id.list_item_call_details_datetime_call_type_image_view)
    }

    @Test
    fun bind_correctlySetsSubTitle() {
        val listItem = CallDetailDatetimeListItem(Enums.Calls.CallTypes.MISSED, "SubTitle")

        viewHolder.bind(listItem)

        assertEquals("SubTitle", subTitleTextView.text.toString())
    }

    @Test
    fun bind_blankCallType_correctlySetsTitle() {
        val listItem = CallDetailDatetimeListItem("", "SubTitle")

        viewHolder.bind(listItem)

        assertEquals("", titleTextView.text)
        assertEquals(ContextCompat.getColor(ApplicationProvider.getApplicationContext(), R.color.nextivaOrange), titleTextView.currentTextColor)
    }

    @Test
    fun bind_blankCallType_correctlySetsCallTypeImage() {
        val listItem = CallDetailDatetimeListItem("", "SubTitle")

        viewHolder.bind(listItem)

        assertNull(callTypeImageView.drawable)
    }

    @Test
    fun bind_placedCallType_correctlySetsTitle() {
        val listItem = CallDetailDatetimeListItem(Enums.Calls.CallTypes.PLACED, "SubTitle")

        viewHolder.bind(listItem)

        assertEquals("Outgoing Call", titleTextView.text)
        assertEquals(ContextCompat.getColor(ApplicationProvider.getApplicationContext(), R.color.nextivaOrange), titleTextView.currentTextColor)
    }

    @Test
    fun bind_placedCallType_correctlySetsCallTypeImage() {
        val listItem = CallDetailDatetimeListItem(Enums.Calls.CallTypes.PLACED, "SubTitle")

        viewHolder.bind(listItem)

        val shadowDrawable = shadowOf(callTypeImageView.drawable)
        assertEquals(R.drawable.ic_call_made, shadowDrawable.createdFromResId)
    }

    @Test
    fun bind_receivedCallType_correctlySetsTitle() {
        val listItem = CallDetailDatetimeListItem(Enums.Calls.CallTypes.RECEIVED, "SubTitle")

        viewHolder.bind(listItem)

        assertEquals("Incoming Call", titleTextView.text)
        assertEquals(ContextCompat.getColor(ApplicationProvider.getApplicationContext(), R.color.nextivaOrange), titleTextView.currentTextColor)
    }

    @Test
    fun bind_receivedCallType_correctlySetsCallTypeImage() {
        val listItem = CallDetailDatetimeListItem(Enums.Calls.CallTypes.RECEIVED, "SubTitle")

        viewHolder.bind(listItem)

        val shadowDrawable = shadowOf(callTypeImageView.drawable)
        assertEquals(R.drawable.ic_call_received, shadowDrawable.createdFromResId)
    }

    @Test
    fun bind_missedCallType_correctlySetsTitle() {
        val listItem = CallDetailDatetimeListItem(Enums.Calls.CallTypes.MISSED, "SubTitle")

        viewHolder.bind(listItem)

        assertEquals("Missed Call", titleTextView.text)
        assertEquals(ContextCompat.getColor(ApplicationProvider.getApplicationContext(), R.color.errorRed), titleTextView.currentTextColor)
    }

    @Test
    fun bind_missedCallType_correctlySetsCallTypeImage() {
        val listItem = CallDetailDatetimeListItem(Enums.Calls.CallTypes.MISSED, "SubTitle")

        viewHolder.bind(listItem)

        val shadowDrawable = shadowOf(callTypeImageView.drawable)
        assertEquals(R.drawable.ic_call_missed, shadowDrawable.createdFromResId)
    }
}