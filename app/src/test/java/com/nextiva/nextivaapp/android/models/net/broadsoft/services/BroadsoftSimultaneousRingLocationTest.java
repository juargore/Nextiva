/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;

import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation;

import org.junit.Test;

public class BroadsoftSimultaneousRingLocationTest {

    @Test
    public void copyConstructor_nullValues_setsNullValues() {
        SimultaneousRingLocation input = new SimultaneousRingLocation(null, null);

        BroadsoftSimultaneousRingLocation output = new BroadsoftSimultaneousRingLocation(input);
        assertNull(output.getAddress());
        assertNull(output.getAnswerConfirmationRequired());
    }

    @Test
    public void copyConstructor_completeValues_setsCorrectValues() {
        SimultaneousRingLocation input = new SimultaneousRingLocation("1234", false);

        BroadsoftSimultaneousRingLocation output = new BroadsoftSimultaneousRingLocation(input);
        assertEquals("1234", output.getAddress());
        assertFalse(output.getAnswerConfirmationRequired());
    }
}
