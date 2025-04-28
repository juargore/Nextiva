/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import com.nextiva.nextivaapp.android.BaseRobolectricTest;

import org.jivesoftware.smack.packet.Presence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class IQBuilderUtilTest extends BaseRobolectricTest {

    @Test
    public void getVCardUpdatePresence_returnsCorrectValue() {
        Presence presence = IQBuilderUtil.getVCardUpdatePresence();

        assertEquals(Presence.Type.available, presence.getType());
        assertEquals(10, presence.getPriority());
        assertTrue(presence.toXML("jabber:client").toString().contains(NextivaXMPPConstants.VCARD_UPDATE_VALUE));
    }

}
