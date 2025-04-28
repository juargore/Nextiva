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
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class ServiceSettingsTest {

    @Test
    public void copyConstructor_emptyInput_setsCopiedValues() {
        ServiceSettings inputServiceSettings = new ServiceSettings("type", "uri");
        ServiceSettings outputServiceSettings = new ServiceSettings(inputServiceSettings);

        assertEquals("type", outputServiceSettings.getType());
        assertNotSame(inputServiceSettings.getType(), outputServiceSettings.getType());
        assertEquals("uri", outputServiceSettings.getUri());
        assertNotSame(inputServiceSettings.getUri(), outputServiceSettings.getUri());
        assertNull(outputServiceSettings.getActiveRaw());
        assertNull(outputServiceSettings.getRingSplashEnabledRaw());
        assertNull(outputServiceSettings.getNumberOfRings());
        assertNull(outputServiceSettings.getRemoteOfficeNumber());
        assertNull(outputServiceSettings.getForwardToPhoneNumber());
        assertNull(outputServiceSettings.getAlertAllLocationsForClickToDialCallsRaw());
        assertNull(outputServiceSettings.getAlertAllLocationsForGroupPagingCallsRaw());
        assertNull(outputServiceSettings.getNextivaAnywhereLocationsList());
        assertNull(outputServiceSettings.getDontRingWhileOnCallRaw());
        assertNull(outputServiceSettings.getSimultaneousRingLocationsList());
    }

    @Test
    public void copyConstructor_completeInput_setsCopiedValues() {
        ArrayList<NextivaAnywhereLocation> nextivaAnywhereLocationsList = new ArrayList<NextivaAnywhereLocation>() {{
            add(new NextivaAnywhereLocation("1111", "description1", true, false, false, false));
            add(new NextivaAnywhereLocation("2222", "description2", false, true, false, false));
            add(new NextivaAnywhereLocation("3333", "description3", false, false, true, false));
            add(new NextivaAnywhereLocation("4444", "description4", false, false, false, true));
        }};

        ArrayList<SimultaneousRingLocation> simultaneousRingLocationsList = new ArrayList<SimultaneousRingLocation>() {{
            add(new SimultaneousRingLocation("1111", null));
            add(new SimultaneousRingLocation("2222", true));
            add(new SimultaneousRingLocation("3333", false));
            add(new SimultaneousRingLocation(null, true));
            add(new SimultaneousRingLocation(null, false));
            add(new SimultaneousRingLocation(null, null));
        }};

        ServiceSettings inputServiceSettings = new ServiceSettings("type", "uri", true, false, 10, "1234", "0987", true, false, nextivaAnywhereLocationsList, true, simultaneousRingLocationsList);
        ServiceSettings outputServiceSettings = new ServiceSettings(inputServiceSettings);

        assertEquals("type", outputServiceSettings.getType());
        assertNotSame(inputServiceSettings.getType(), outputServiceSettings.getType());
        assertEquals("uri", outputServiceSettings.getUri());
        assertNotSame(inputServiceSettings.getUri(), outputServiceSettings.getUri());
        assertTrue(outputServiceSettings.getActiveRaw());
        assertNotSame(inputServiceSettings.getActiveRaw(), outputServiceSettings.getActiveRaw());
        assertFalse(outputServiceSettings.getRingSplashEnabledRaw());
        assertNotSame(inputServiceSettings.getRingSplashEnabledRaw(), outputServiceSettings.getRingSplashEnabledRaw());
        assertEquals(Integer.valueOf(10), outputServiceSettings.getNumberOfRings());
        assertNotSame(inputServiceSettings.getNumberOfRings(), outputServiceSettings.getNumberOfRings());
        assertEquals("1234", outputServiceSettings.getRemoteOfficeNumber());
        assertNotSame(inputServiceSettings.getRemoteOfficeNumber(), outputServiceSettings.getRemoteOfficeNumber());
        assertEquals("0987", outputServiceSettings.getForwardToPhoneNumber());
        assertNotSame(inputServiceSettings.getForwardToPhoneNumber(), outputServiceSettings.getForwardToPhoneNumber());
        assertTrue(outputServiceSettings.getAlertAllLocationsForClickToDialCallsRaw());
        assertNotSame(inputServiceSettings.getAlertAllLocationsForClickToDialCallsRaw(), outputServiceSettings.getAlertAllLocationsForClickToDialCallsRaw());
        assertFalse(outputServiceSettings.getAlertAllLocationsForGroupPagingCallsRaw());
        assertNotSame(inputServiceSettings.getAlertAllLocationsForGroupPagingCallsRaw(), outputServiceSettings.getAlertAllLocationsForGroupPagingCallsRaw());
        assertEquals(nextivaAnywhereLocationsList, outputServiceSettings.getNextivaAnywhereLocationsList());
        assertNotSame(inputServiceSettings.getNextivaAnywhereLocationsList(), outputServiceSettings.getNextivaAnywhereLocationsList());
        assertTrue(outputServiceSettings.getDontRingWhileOnCallRaw());
        assertNotSame(inputServiceSettings.getDontRingWhileOnCallRaw(), outputServiceSettings.getDontRingWhileOnCallRaw());
        assertEquals(simultaneousRingLocationsList, outputServiceSettings.getSimultaneousRingLocationsList());
        assertNotSame(inputServiceSettings.getSimultaneousRingLocationsList(), outputServiceSettings.getSimultaneousRingLocationsList());
    }

    @Test
    public void getActive_hasNullValue_returnsFalse() {
        ServiceSettings serviceSettings = new ServiceSettings("type", "uri");

        assertFalse(serviceSettings.getActive());
    }

    @Test
    public void getRingSplash_hasNullValue_returnsFalse() {
        ServiceSettings serviceSettings = new ServiceSettings("type", "uri");

        assertFalse(serviceSettings.getRingSplashEnabled());
    }

    @Test
    public void getAlertAllLocationsForClickToDialCalls_hasNullValue_returnsFalse() {
        ServiceSettings serviceSettings = new ServiceSettings("type", "uri");

        assertFalse(serviceSettings.getAlertAllLocationsForClickToDialCalls());
    }

    @Test
    public void getAlertAllLocationsForGroupPagingCalls_hasNullValue_returnsFalse() {
        ServiceSettings serviceSettings = new ServiceSettings("type", "uri");

        assertFalse(serviceSettings.getAlertAllLocationsForGroupPagingCalls());
    }

    @Test
    public void getDontRingWhileOnCall_hasNullValue_returnsFalse() {
        ServiceSettings serviceSettings = new ServiceSettings("type", "uri");

        assertFalse(serviceSettings.getDontRingWhileOnCall());
    }
}
