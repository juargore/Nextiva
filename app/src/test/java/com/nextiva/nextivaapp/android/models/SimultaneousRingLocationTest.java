/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

public class SimultaneousRingLocationTest {

    @Test
    public void copyConstructor_emptyInput_setsCopiedValues() {
        SimultaneousRingLocation inputSimultaneousRingLocation = new SimultaneousRingLocation(null, null);
        SimultaneousRingLocation outputSimultaneousRingLocation = new SimultaneousRingLocation(inputSimultaneousRingLocation);

        assertNull(outputSimultaneousRingLocation.getPhoneNumber());
        assertNull(outputSimultaneousRingLocation.getAnswerConfirmationRequiredRaw());
    }

    @Test
    public void copyConstructor_completeInput_setsCopiedValues() {
        SimultaneousRingLocation inputSimultaneousRingLocation = new SimultaneousRingLocation("1234", true);
        SimultaneousRingLocation outputSimultaneousRingLocation = new SimultaneousRingLocation(inputSimultaneousRingLocation);

        assertEquals("1234", outputSimultaneousRingLocation.getPhoneNumber());
        assertNotSame(inputSimultaneousRingLocation.getPhoneNumber(), outputSimultaneousRingLocation.getPhoneNumber());
        assertTrue(outputSimultaneousRingLocation.getAnswerConfirmationRequiredRaw());
        assertNotSame(inputSimultaneousRingLocation.getAnswerConfirmationRequiredRaw(), outputSimultaneousRingLocation.getAnswerConfirmationRequiredRaw());
    }

    @Test
    public void getAnswerConfimationRequired_hasNullValue_returnsFalse() {
        SimultaneousRingLocation simultaneousRingLocation = new SimultaneousRingLocation();

        assertFalse(simultaneousRingLocation.getAnswerConfirmationRequired());
    }
}
