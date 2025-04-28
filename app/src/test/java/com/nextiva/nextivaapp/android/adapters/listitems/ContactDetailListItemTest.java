/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.listitems;

import static junit.framework.Assert.assertFalse;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactDetailListItem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ContactDetailListItemTest {

    @Test
    public void constructor_correctlySetsDefaultValues() {
        ContactDetailListItem listItem = new ContactDetailListItem(100, "Title", "SubTitle", R.drawable.ic_phone, R.drawable.ic_video, true);

        assertFalse(listItem.isClickable());
    }
}
