/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.viewholders;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.nextiva.nextivaapp.android.BaseRobolectricTest;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.LoadingListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.HeaderViewHolder;
import com.nextiva.nextivaapp.android.models.ListHeaderRow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowDrawable;

import java.io.IOException;
import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
public class HeaderViewHolderTest extends BaseRobolectricTest {

    private HeaderViewHolder mViewHolder;

    @Mock
    private MasterListListener mMockMasterListListener;

    @Override
    public void setup() throws IOException {
        super.setup();
        MockitoAnnotations.initMocks(this);

        mViewHolder = new HeaderViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), mMockMasterListListener);
    }

    @Test
    public void onBind_setsTitleText() {
        HeaderListItem listItem = new HeaderListItem(new ListHeaderRow("Title"), new ArrayList<>(), true, false);
        mViewHolder.bind(listItem);

        TextView titleTextView = mViewHolder.itemView.findViewById(R.id.list_item_group_header_text_view);

        assertEquals("Title", titleTextView.getText().toString());
    }

    @Test
    public void onBind_clickable_setsClickable() {
        HeaderListItem listItem = new HeaderListItem(new ListHeaderRow("Title"), new ArrayList<>(), true, false);
        mViewHolder.bind(listItem);

        assertTrue(mViewHolder.itemView.isClickable());
    }

    @Test
    public void onBind_notClickable_setsNotClickable() {
        HeaderListItem listItem = new HeaderListItem(new ListHeaderRow("Title"), new ArrayList<>(), false, false);
        mViewHolder.bind(listItem);

        assertFalse(mViewHolder.itemView.isClickable());
    }

    @Test
    public void onBind_nullChildrenList_hidesArrowImageSwitcher() {
        HeaderListItem listItem = new HeaderListItem(new ListHeaderRow("Title"), null, false, false);
        mViewHolder.bind(listItem);

        ImageSwitcher arrowImageSwitcher = mViewHolder.itemView.findViewById(R.id.list_item_group_header_arrow_image_switcher);

        assertEquals(View.GONE, arrowImageSwitcher.getVisibility());
    }

    @Test
    public void onBind_emptyChildrenList_hidesArrowImageSwitcher() {
        HeaderListItem listItem = new HeaderListItem(new ListHeaderRow("Title"), new ArrayList<>(), false, false);
        mViewHolder.bind(listItem);

        ImageSwitcher arrowImageSwitcher = mViewHolder.itemView.findViewById(R.id.list_item_group_header_arrow_image_switcher);

        assertEquals(View.GONE, arrowImageSwitcher.getVisibility());
    }

    @Test
    public void onBind_populatedChildrenList_showsArrowImageSwitcher() {
        HeaderListItem listItem = new HeaderListItem(new ListHeaderRow("Title"),
                                                     new ArrayList<BaseListItem>() {{
                                                         add(new LoadingListItem());
                                                     }},
                                                     false, false);

        mViewHolder.bind(listItem);

        ImageSwitcher arrowImageSwitcher = mViewHolder.itemView.findViewById(R.id.list_item_group_header_arrow_image_switcher);

        assertEquals(View.VISIBLE, arrowImageSwitcher.getVisibility());
    }

    @Test
    public void onBind_expandedListItem_setsExpandedIcon() {
        HeaderListItem listItem = new HeaderListItem(new ListHeaderRow("Title"),
                                                     new ArrayList<BaseListItem>() {{
                                                         add(new LoadingListItem());
                                                     }},
                                                     true, false);

        mViewHolder.bind(listItem);

        ImageSwitcher arrowImageSwitcher = mViewHolder.itemView.findViewById(R.id.list_item_group_header_arrow_image_switcher);

        ShadowDrawable shadowDrawable = Shadows.shadowOf(((ImageView) arrowImageSwitcher.getCurrentView()).getDrawable());
        assertEquals(R.drawable.ic_group_header_arrow_down, shadowDrawable.getCreatedFromResId());
        assertEquals(1.0f, arrowImageSwitcher.getCurrentView().getScaleX());
    }

    @Test
    public void onBind_collapsedListItem_setsCollapsedIcon() {
        HeaderListItem listItem = new HeaderListItem(new ListHeaderRow("Title"),
                                                     new ArrayList<BaseListItem>() {{
                                                         add(new LoadingListItem());
                                                     }},
                                                     true, false);
        listItem.setExpanded(false);
        mViewHolder.bind(listItem);

        ImageSwitcher arrowImageSwitcher = mViewHolder.itemView.findViewById(R.id.list_item_group_header_arrow_image_switcher);

        ShadowDrawable shadowDrawable = Shadows.shadowOf(((ImageView) arrowImageSwitcher.getCurrentView()).getDrawable());
        assertEquals(R.drawable.ic_group_header_arrow_down, shadowDrawable.getCreatedFromResId());
        assertEquals(1.0f, arrowImageSwitcher.getCurrentView().getScaleX());
    }

    @Test
    public void onClick_togglesExpandedValue() {
        HeaderListItem listItem = new HeaderListItem(new ListHeaderRow(), new ArrayList<>(), true, false);
        mViewHolder.bind(listItem);

        if (mViewHolder.itemView.isClickable()) {
            mViewHolder.itemView.performClick();
        }

        assertFalse(listItem.isExpanded());
    }

    @Test
    public void onClick_callsToMasterListListener() {
        HeaderListItem listItem = new HeaderListItem(new ListHeaderRow(), new ArrayList<>(), true, false);
        mViewHolder.bind(listItem);

        if (mViewHolder.itemView.isClickable()) {
            mViewHolder.itemView.performClick();
        }

        verify(mMockMasterListListener).onContactHeaderListItemClicked(listItem);
    }
}
