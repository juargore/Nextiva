/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.viewholders;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.nextiva.nextivaapp.android.BaseRobolectricTest;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConferencePhoneNumberListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConferencePhoneNumberViewHolder;
import com.nextiva.nextivaapp.android.view.DetailItemView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
public class ConferencePhoneNumberViewHolderTest extends BaseRobolectricTest {

    private ConferencePhoneNumberViewHolder mViewHolder;

    @Mock
    private MasterListListener mMockMasterListListener;

    @Override
    public void setup() throws IOException {
        super.setup();
        MockitoAnnotations.initMocks(this);

        mViewHolder = new ConferencePhoneNumberViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), mMockMasterListListener);
    }

    @Test
    public void bind_setsIsClickableCorrectly() {
        ConferencePhoneNumberListItem listItem = new ConferencePhoneNumberListItem(1, "Title", "Subtitle", "ConferenceID", "PIN", "AssembledNumber");
        mViewHolder.bind(listItem);

        assertFalse(mViewHolder.itemView.isClickable());
    }

    @Test
    public void bind_setsIsLongClickableCorrectly() {
        ConferencePhoneNumberListItem listItem = new ConferencePhoneNumberListItem(1, "Title", "Subtitle", "ConferenceID", "PIN", "AssembledNumber");
        mViewHolder.bind(listItem);

        assertTrue(mViewHolder.itemView.isLongClickable());
    }

    @Test
    public void bind_titleShown() {
        ConferencePhoneNumberListItem listItem = new ConferencePhoneNumberListItem(1, "Title", "Subtitle", "ConferenceID", "PIN", "AssembledNumber");
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_conference_detail_item_view);

        assertEquals("Title", detailItemView.getTitleText());
    }

    @Test
    public void bind_subtitleShown() {
        ConferencePhoneNumberListItem listItem = new ConferencePhoneNumberListItem(1, "Title", "Subtitle", "ConferenceID", "PIN", "AssembledNumber");
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_conference_detail_item_view);

        assertEquals("Subtitle", detailItemView.getSubTitleText());
    }

    @Test
    public void bind_conferenceIdShown() {
        ConferencePhoneNumberListItem listItem = new ConferencePhoneNumberListItem(1, "Title", "Subtitle", "ConferenceID", "PIN", "AssembledNumber");
        mViewHolder.bind(listItem);

        TextView conferenceIdTextView = mViewHolder.itemView.findViewById(R.id.list_item_conference_conference_id_value_text_view);

        assertEquals("ConferenceID", conferenceIdTextView.getText().toString());
    }

    @Test
    public void bind_securityPinShown() {
        ConferencePhoneNumberListItem listItem = new ConferencePhoneNumberListItem(1, "Title", "Subtitle", "ConferenceID", "PIN", "AssembledNumber");
        mViewHolder.bind(listItem);

        TextView securityPinTextView = mViewHolder.itemView.findViewById(R.id.list_item_conference_security_pin_value_text_view);

        assertEquals("PIN", securityPinTextView.getText().toString());
    }

    @Test
    public void onClick_clickActionButtonOne_callsToMasterListListener() {
        ConferencePhoneNumberListItem listItem = new ConferencePhoneNumberListItem(1, "Title", "Subtitle", "ConferenceID", "PIN", "AssembledNumber");
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_conference_detail_item_view);
        ImageButton actionOneImageButton = detailItemView.findViewById(R.id.detail_item_view_action1_image_button);

        if (actionOneImageButton.isClickable()) {
            actionOneImageButton.performClick();
        }

        verify(mMockMasterListListener).onDetailItemViewListItemAction1ButtonClicked(listItem);
    }

    @Test
    public void onClick_clickActionButtonTwo_callsToMasterListListener() {
        ConferencePhoneNumberListItem listItem = new ConferencePhoneNumberListItem(1, "Title", "Subtitle", "ConferenceID", "PIN", "AssembledNumber");
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_conference_detail_item_view);
        ImageButton actionTwoImageButton = detailItemView.findViewById(R.id.detail_item_view_action2_image_button);

        if (actionTwoImageButton.isClickable()) {
            actionTwoImageButton.performClick();
        }

        verify(mMockMasterListListener).onDetailItemViewListItemAction2ButtonClicked(listItem);
    }

    @Test
    public void onLongClick_longClickListItem_callsToMasterListListener() {
        ConferencePhoneNumberListItem listItem = new ConferencePhoneNumberListItem(1, "Title", "Subtitle", "ConferenceID", "PIN", "AssembledNumber");
        mViewHolder.bind(listItem);

        if (mViewHolder.itemView.isLongClickable()) {
            mViewHolder.itemView.performLongClick();
        }

        verify(mMockMasterListListener).onDetailItemViewListItemLongClicked(listItem);
    }
}
