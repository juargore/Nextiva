/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;

import org.junit.Test;

public class BroadsoftBroadWorksAnywhereLocationPostBodyTest {

    @Test
    public void copyConstructor_nullValues_setsNullValues() {
        NextivaAnywhereLocation input = new NextivaAnywhereLocation("1234", null, null, null, null, null);

        BroadsoftBroadWorksAnywhereLocationPostBody output = new BroadsoftBroadWorksAnywhereLocationPostBody(input);
        assertEquals("1234", output.getPhoneNumber());
        assertNull(output.getDescription());
        assertNull(output.getActive());
        assertNull(output.getCallControlEnabled());
        assertNull(output.getPreventDivertingCalls());
        assertNull(output.getAnswerConfirmationRequired());
    }

    @Test
    public void copyConstructor_completeValues_setsCorrectValues() {
        NextivaAnywhereLocation input = new NextivaAnywhereLocation("1234", "description", true, false, true, false);

        BroadsoftBroadWorksAnywhereLocationPostBody output = new BroadsoftBroadWorksAnywhereLocationPostBody(input);
        assertEquals("1234", output.getPhoneNumber());
        assertEquals("description", output.getDescription());
        assertTrue(output.getActive());
        assertFalse(output.getCallControlEnabled());
        assertTrue(output.getPreventDivertingCalls());
        assertFalse(output.getAnswerConfirmationRequired());
    }
}
