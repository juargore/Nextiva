/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.listitems;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ServiceSettingsListItem;
import com.nextiva.nextivaapp.android.models.ServiceSettings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ServiceSettingsListItemTest {

    @Test
    public void constructor_correctlySetsDefaultValues() {
        ServiceSettingsListItem listItem = new ServiceSettingsListItem(new ServiceSettings("Type", "URI"), "Title", "SubTitle");

        assertEquals(listItem.getActionButtonOneResId(), 0);
        assertEquals(listItem.getActionButtonTwoResId(), 0);
        assertTrue(listItem.isClickable());
        assertFalse(listItem.isLongClickable());
    }
}
