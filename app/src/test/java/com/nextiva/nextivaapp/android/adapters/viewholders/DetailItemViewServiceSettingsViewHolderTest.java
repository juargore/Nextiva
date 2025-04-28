/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.viewholders;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;

import com.nextiva.nextivaapp.android.BaseRobolectricTest;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ServiceSettingsListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.DetailItemViewServiceSettingsViewHolder;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.view.DetailItemView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
public class DetailItemViewServiceSettingsViewHolderTest extends BaseRobolectricTest {

    private DetailItemViewServiceSettingsViewHolder mViewHolder;

    @Mock
    private MasterListListener mMockMasterListListener;

    @Override
    public void setup() throws IOException {
        super.setup();
        MockitoAnnotations.initMocks(this);

        mViewHolder = new DetailItemViewServiceSettingsViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), mMockMasterListListener);
    }

    @Test
    public void bind_phoneNumberSubTitleSet_subTitleShown() {
        ServiceSettingsListItem listItem = new ServiceSettingsListItem(new ServiceSettings("Type", "URI"), "Title", "3334445555");
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);

        assertEquals("(333) 444-5555", detailItemView.getSubTitleText());
        assertTrue(detailItemView.isSubTitleEnabled());
    }

    @Test
    public void bind_regularSubTitleSet_subTitleShown() {
        ServiceSettingsListItem listItem = new ServiceSettingsListItem(new ServiceSettings("Type", "URI"), "Title", "On");
        mViewHolder.bind(listItem);

        DetailItemView detailItemView = mViewHolder.itemView.findViewById(R.id.list_item_detail_item_view);

        assertEquals("On", detailItemView.getSubTitleText());
        assertTrue(detailItemView.isSubTitleEnabled());
    }
}
