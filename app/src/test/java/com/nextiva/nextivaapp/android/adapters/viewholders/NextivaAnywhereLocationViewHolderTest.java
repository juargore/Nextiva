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
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.nextiva.nextivaapp.android.BaseRobolectricTest;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.NextivaAnywhereLocationListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.NextivaAnywhereLocationViewHolder;
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.view.DetailItemView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
public class NextivaAnywhereLocationViewHolderTest extends BaseRobolectricTest {

    private NextivaAnywhereLocationViewHolder mViewHolder;

    @Mock
    private MasterListListener mMockMasterListListener;

    @Override
    public void setup() throws IOException {
        super.setup();
        MockitoAnnotations.initMocks(this);

        mViewHolder = new NextivaAnywhereLocationViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), mMockMasterListListener);
    }

    @Test
    public void bind_setsIsClickableCorrectly() {
        NextivaAnywhereLocation location = new NextivaAnywhereLocation("1", "description", false, true, true, true);

        NextivaAnywhereLocationListItem listItem = new NextivaAnywhereLocationListItem(location, "Title", "Subtitle");
        mViewHolder.bind(listItem);

        assertTrue(mViewHolder.itemView.isClickable());
    }

    @Test
    public void bind_titleShown() {
        NextivaAnywhereLocation location = new NextivaAnywhereLocation("1", "description", false, true, true, true);

        NextivaAnywhereLocationListItem listItem = new NextivaAnywhereLocationListItem(location, "Title", "Subtitle");
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_nextiva_anywhere_location_detail_item_view);

        assertEquals("Title", detailItemView.getTitleText());
    }

    @Test
    public void bind_subTitleSet_subTitleShown() {
        NextivaAnywhereLocation location = new NextivaAnywhereLocation("1", "description", false, true, true, true);

        NextivaAnywhereLocationListItem listItem = new NextivaAnywhereLocationListItem(location, "Title", "Subtitle");
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_nextiva_anywhere_location_detail_item_view);

        assertEquals("Subtitle", detailItemView.getSubTitleText());
        assertTrue(detailItemView.isSubTitleEnabled());
    }

    @Test
    public void bind_subTitleNotSet_subTitleHidden() {
        NextivaAnywhereLocation location = new NextivaAnywhereLocation("1", "description", false, true, true, true);

        NextivaAnywhereLocationListItem listItem = new NextivaAnywhereLocationListItem(location, "Title", null);
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_nextiva_anywhere_location_detail_item_view);

        assertFalse(detailItemView.isSubTitleEnabled());
    }

    @Test
    public void bind_listItemActive_correctlySetsActiveCheckBox() {
        NextivaAnywhereLocation location = new NextivaAnywhereLocation("1", "description", true, true, true, true);

        NextivaAnywhereLocationListItem listItem = new NextivaAnywhereLocationListItem(location, "Title", "Subtitle");
        mViewHolder.bind(listItem);

        TextView textView = mViewHolder.itemView.findViewById(R.id.list_item_nextiva_anywhere_location_enabled_text_view);

        assertEquals("On", textView.getText().toString());
    }

    @Test
    public void bind_listItemNotActive_correctlySetsActiveCheckBox() {
        NextivaAnywhereLocation location = new NextivaAnywhereLocation("1", "description", false, true, true, true);

        NextivaAnywhereLocationListItem listItem = new NextivaAnywhereLocationListItem(location, "Title", "Subtitle");
        mViewHolder.bind(listItem);

        TextView textView = mViewHolder.itemView.findViewById(R.id.list_item_nextiva_anywhere_location_enabled_text_view);

        assertEquals("Off", textView.getText().toString());
    }

    @Test
    public void onClick_callsToMasterListListener() {
        NextivaAnywhereLocation location = new NextivaAnywhereLocation("1", "description", false, true, true, true);

        NextivaAnywhereLocationListItem listItem = new NextivaAnywhereLocationListItem(location, "Title", "Subtitle");
        mViewHolder.bind(listItem);

        if (mViewHolder.itemView.isClickable()) {
            mViewHolder.itemView.performClick();
        }

        verify(mMockMasterListListener).onDetailItemViewListItemClicked(listItem);
    }
}
