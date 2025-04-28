/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.listitems;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConferencePhoneNumberListItem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ConferencePhoneNumberListItemTest {

    @Test
    public void constructor_correctlySetsDefaultValues() {
        ConferencePhoneNumberListItem listItem = new ConferencePhoneNumberListItem(100, "Title", "SubTitle", "1234", "4321", "AssembledNumber");

        assertEquals(listItem.getActionButtonOneResId(), R.drawable.ic_phone);
        assertEquals(listItem.getActionButtonTwoResId(), R.drawable.ic_video);
        assertFalse(listItem.isClickable());
        assertTrue(listItem.isLongClickable());
    }
}
