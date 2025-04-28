/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.viewholders;

import static junit.framework.Assert.assertEquals;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.nextiva.nextivaapp.android.BaseRobolectricTest;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ChatHeaderViewHolder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
public class ChatHeaderViewHolderTest extends BaseRobolectricTest {

    private ChatHeaderViewHolder mViewHolder;

    @Override
    public void setup() throws IOException {
        super.setup();

        mViewHolder = new ChatHeaderViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null);
    }

    @Test
    public void bind_setsTitleText() {
        ChatHeaderListItem listItem = new ChatHeaderListItem("Title");

        mViewHolder.bind(listItem);

        TextView titleTextView = mViewHolder.itemView.findViewById(R.id.list_item_chat_header_title_text_view);

        assertEquals("Title", titleTextView.getText().toString());
        assertEquals(View.VISIBLE, titleTextView.getVisibility());
    }
}
