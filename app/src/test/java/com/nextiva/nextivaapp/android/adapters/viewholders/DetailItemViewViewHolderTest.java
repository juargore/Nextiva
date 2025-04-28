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
import android.widget.ImageButton;

import androidx.test.core.app.ApplicationProvider;

import com.nextiva.nextivaapp.android.BaseRobolectricTest;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DetailItemViewListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.DetailItemViewViewHolder;
import com.nextiva.nextivaapp.android.view.DetailItemView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowDrawable;

import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
public class DetailItemViewViewHolderTest extends BaseRobolectricTest {

    private DetailItemViewViewHolder mViewHolder;

    private DetailItemViewListItem mFullListItem;

    @Mock
    private MasterListListener mMockMasterListListener;

    @Override
    public void setup() throws IOException {
        super.setup();
        MockitoAnnotations.initMocks(this);

        mViewHolder = new DetailItemViewViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), mMockMasterListListener);

        mFullListItem = new DetailItemViewListItem("Title", "Subtitle", R.drawable.ic_phone, R.drawable.ic_video, true, true) {
        };
    }

    @Test
    public void bind_listItemClickable_setsIsClickableCorrectly() {
        mViewHolder.bind(mFullListItem);

        assertTrue(mViewHolder.itemView.isClickable());
    }

    @Test
    public void bind_listItemNotClickable_setsIsClickableCorrectly() {
        mViewHolder.bind(mFullListItem);

        DetailItemViewListItem listItem = new DetailItemViewListItem("Title", "Subtitle", R.drawable.ic_phone, R.drawable.ic_video, false, true) {
        };
        mViewHolder.bind(listItem);

        assertFalse(mViewHolder.itemView.isClickable());
    }

    @Test
    public void bind_listItemLongClickable_setsIsLongClickableCorrectly() {
        mViewHolder.bind(mFullListItem);

        assertTrue(mViewHolder.itemView.isLongClickable());
    }

    @Test
    public void bind_listItemNotLongClickable_setsIsLongClickableCorrectly() {
        mViewHolder.bind(mFullListItem);

        DetailItemViewListItem listItem = new DetailItemViewListItem("Title", "Subtitle", R.drawable.ic_phone, R.drawable.ic_video, true, false) {
        };
        mViewHolder.bind(listItem);

        assertFalse(mViewHolder.itemView.isLongClickable());
    }

    @Test
    public void bind_titleShown() {
        mViewHolder.bind(mFullListItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);

        assertEquals("Title", detailItemView.getTitleText());
    }

    @Test
    public void bind_subTitleSet_subTitleShown() {
        mViewHolder.bind(mFullListItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);

        assertEquals("Subtitle", detailItemView.getSubTitleText());
        assertTrue(detailItemView.isSubTitleEnabled());
    }

    @Test
    public void bind_subTitleNotSet_subTitleHidden() {
        mViewHolder.bind(mFullListItem);

        DetailItemViewListItem listItem = new DetailItemViewListItem("Title", null, R.drawable.ic_phone, R.drawable.ic_video, true, true) {
        };
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);

        assertFalse(detailItemView.isSubTitleEnabled());
    }

    @Test
    public void bind_actionButtonOneDrawableSet_setsDrawable() {
        mViewHolder.bind(mFullListItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);
        ImageButton actionOneImageButton = detailItemView.findViewById(R.id.detail_item_view_action1_image_button);

        ShadowDrawable shadowDrawable = Shadows.shadowOf(actionOneImageButton.getDrawable());
        assertEquals(R.drawable.ic_phone, shadowDrawable.getCreatedFromResId());
    }

    @Test
    public void bind_actionButtonOneDrawableSet_displaysImageButton() {
        mViewHolder.bind(mFullListItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);
        ImageButton actionOneImageButton = detailItemView.findViewById(R.id.detail_item_view_action1_image_button);

        assertEquals(View.VISIBLE, actionOneImageButton.getVisibility());
    }

    @Test
    public void bind_actionButtonOneDrawableNotSet_hidesImageButton() {
        mViewHolder.bind(mFullListItem);

        DetailItemViewListItem listItem = new DetailItemViewListItem("Title", "Subtitle", 0, R.drawable.ic_video, true, true) {
        };
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);
        ImageButton actionOneImageButton = detailItemView.findViewById(R.id.detail_item_view_action1_image_button);

        assertEquals(View.GONE, actionOneImageButton.getVisibility());
    }

    @Test
    public void bind_actionButtonTwoDrawableSet_setsDrawable() {
        mViewHolder.bind(mFullListItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);
        ImageButton actionTwoImageButton = detailItemView.findViewById(R.id.detail_item_view_action2_image_button);

        ShadowDrawable shadowDrawable = Shadows.shadowOf(actionTwoImageButton.getDrawable());
        assertEquals(R.drawable.ic_video, shadowDrawable.getCreatedFromResId());
    }

    @Test
    public void bind_actionButtonTwoDrawableSet_displaysImageButton() {
        mViewHolder.bind(mFullListItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);
        ImageButton actionTwoImageButton = detailItemView.findViewById(R.id.detail_item_view_action2_image_button);

        assertEquals(View.VISIBLE, actionTwoImageButton.getVisibility());
    }

    @Test
    public void bind_actionButtonTwoDrawableNotSet_hidesImageButton() {
        mViewHolder.bind(mFullListItem);

        DetailItemViewListItem listItem = new DetailItemViewListItem("Title", "Subtitle", R.drawable.ic_phone, 0, true, true) {
        };
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);
        ImageButton actionTwoImageButton = detailItemView.findViewById(R.id.detail_item_view_action2_image_button);

        assertEquals(View.GONE, actionTwoImageButton.getVisibility());
    }

    @Test
    public void onClick_clickActionButtonOne_callsToMasterListListener() {
        mViewHolder.bind(mFullListItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);
        ImageButton actionOneImageButton = detailItemView.findViewById(R.id.detail_item_view_action1_image_button);

        if (actionOneImageButton.isClickable()) {
            actionOneImageButton.performClick();
        }

        verify(mMockMasterListListener).onDetailItemViewListItemAction1ButtonClicked(mFullListItem);
    }

    @Test
    public void onClick_clickActionButtonTwo_callsToMasterListListener() {
        mViewHolder.bind(mFullListItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);
        ImageButton actionTwoImageButton = detailItemView.findViewById(R.id.detail_item_view_action2_image_button);

        if (actionTwoImageButton.isClickable()) {
            actionTwoImageButton.performClick();
        }

        verify(mMockMasterListListener).onDetailItemViewListItemAction2ButtonClicked(mFullListItem);
    }

    @Test
    public void onClick_clickListItem_callsToMasterListListener() {
        mViewHolder.bind(mFullListItem);

        if (mViewHolder.itemView.isClickable()) {
            mViewHolder.itemView.performClick();
        }

        verify(mMockMasterListListener).onDetailItemViewListItemClicked(mFullListItem);
    }

    @Test
    public void onLongClick_longClickListItem_callsToMasterListListener() {
        mViewHolder.bind(mFullListItem);

        if (mViewHolder.itemView.isLongClickable()) {
            mViewHolder.itemView.performLongClick();
        }

        verify(mMockMasterListListener).onDetailItemViewListItemLongClicked(mFullListItem);
    }
}
