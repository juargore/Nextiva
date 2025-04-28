/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;

import android.text.TextUtils;

import com.nextiva.nextivaapp.android.BasePowerMockTest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class NextivaAnywhereLocationTest extends BasePowerMockTest {

    private static MockedStatic<TextUtils> textUtils;

    @BeforeClass
    public static void global() {
        textUtils = Mockito.mockStatic(TextUtils.class);
        textUtils.when(() -> TextUtils.isEmpty(any(CharSequence.class))).thenAnswer((Answer<Boolean>) invocation -> {
            CharSequence a = (CharSequence) invocation.getArguments()[0];
            return !(a != null && a.length() > 0);
        });
        textUtils.when(() -> TextUtils.isEmpty(null)).thenAnswer((Answer<Boolean>) invocation -> true);
        textUtils.when(() -> TextUtils.equals(any(CharSequence.class), any(CharSequence.class))).thenAnswer(invocation -> {
            CharSequence a = (CharSequence) invocation.getArguments()[0];
            CharSequence b = (CharSequence) invocation.getArguments()[1];
            if (a == b) {
                return true;
            }
            int length;
            if (a != null && b != null && (length = a.length()) == b.length()) {
                if (a instanceof String && b instanceof String) {
                    return a.equals(b);
                } else {
                    for (int i = 0; i < length; i++) {
                        if (a.charAt(i) != b.charAt(i)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        });
    }

    @AfterClass
    public static void tearDown() {
        textUtils.close();
    }

    @Test
    public void copyConstructor_nullValues_setsNullValues() {
        NextivaAnywhereLocation input = new NextivaAnywhereLocation("1234", null, null, null, null, null);

        NextivaAnywhereLocation output = new NextivaAnywhereLocation(input);
        assertEquals("1234", output.getPhoneNumber());
        assertNull(output.getDescription());
        assertNull(output.getActiveRaw());
        assertNull(output.getCallControlEnabledRaw());
        assertNull(output.getPreventDivertingCallsRaw());
        assertNull(output.getAnswerConfirmationRequiredRaw());
    }

    @Test
    public void copyConstructor_completeValues_setsCorrectValues() {
        NextivaAnywhereLocation input = new NextivaAnywhereLocation("1234", "description", true, false, true, false);

        NextivaAnywhereLocation output = new NextivaAnywhereLocation(input);
        assertEquals("1234", output.getPhoneNumber());
        assertEquals("description", output.getDescription());
        assertTrue(output.getActiveRaw());
        assertFalse(output.getCallControlEnabledRaw());
        assertTrue(output.getPreventDivertingCallsRaw());
        assertFalse(output.getAnswerConfirmationRequiredRaw());
    }

    @Test
    public void getActive_hasNullValue_returnsFalse() {
        NextivaAnywhereLocation input = new NextivaAnywhereLocation("1234", null, null, null, null, null);

        assertFalse(input.getActive());
    }

    @Test
    public void getCallControlEnabled_hasNullValue_returnsFalse() {
        NextivaAnywhereLocation input = new NextivaAnywhereLocation("1234", null, null, null, null, null);

        assertFalse(input.getCallControlEnabled());
    }

    @Test
    public void getPreventDivertingCalls_hasNullValue_returnsFalse() {
        NextivaAnywhereLocation input = new NextivaAnywhereLocation("1234", null, null, null, null, null);

        assertFalse(input.getPreventDivertingCalls());
    }

    @Test
    public void getAnswerConfirmationRequired_hasNullValue_returnsFalse() {
        NextivaAnywhereLocation input = new NextivaAnywhereLocation("1234", null, null, null, null, null);

        assertFalse(input.getAnswerConfirmationRequired());
    }

    @Test
    public void equals_sameInstance_returnsTrue() {
        NextivaAnywhereLocation nextivaAnywhereLocation1 = new NextivaAnywhereLocation(
                "2223334444",
                "Desc",
                true,
                false,
                true,
                false);
        NextivaAnywhereLocation nextivaAnywhereLocation2 = nextivaAnywhereLocation1;

        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);
    }

    @Test
    public void equals_differentObjectType_returnsFalse() {
        NextivaAnywhereLocation nextivaAnywhereLocation = new NextivaAnywhereLocation(
                "2223334444",
                "Desc",
                true,
                false,
                true,
                false);

        assertNotEquals("", nextivaAnywhereLocation);
    }

    @Test
    public void equals_differentInstances_returnsTrueWhenEqual() {
        NextivaAnywhereLocation nextivaAnywhereLocation1 = new NextivaAnywhereLocation(
                "2223334444",
                "Desc",
                true,
                false,
                true,
                false);

        NextivaAnywhereLocation nextivaAnywhereLocation2 = new NextivaAnywhereLocation(
                "2223334444",
                "Desc",
                true,
                false,
                true,
                false);

        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);

        nextivaAnywhereLocation2.setPhoneNumber("1112223333");
        assertNotEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);
        nextivaAnywhereLocation2.setPhoneNumber("2223334444");
        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);

        nextivaAnywhereLocation2.setPhoneNumber("(222) 333-4444");
        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);
        nextivaAnywhereLocation2.setPhoneNumber("2223334444");
        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);

        nextivaAnywhereLocation2.setDescription("Different Desc");
        assertNotEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);
        nextivaAnywhereLocation2.setDescription("Desc");
        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);

        nextivaAnywhereLocation1.setDescription("");
        nextivaAnywhereLocation2.setDescription(null);
        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);
        nextivaAnywhereLocation1.setDescription("Desc");
        nextivaAnywhereLocation2.setDescription("Desc");
        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);

        nextivaAnywhereLocation2.setActive(false);
        assertNotEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);
        nextivaAnywhereLocation2.setActive(true);
        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);

        nextivaAnywhereLocation2.setCallControlEnabled(true);
        assertNotEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);
        nextivaAnywhereLocation2.setCallControlEnabled(false);
        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);

        nextivaAnywhereLocation2.setPreventDivertingCalls(false);
        assertNotEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);
        nextivaAnywhereLocation2.setPreventDivertingCalls(true);
        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);

        nextivaAnywhereLocation2.setAnswerConfirmationRequired(true);
        assertNotEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);
        nextivaAnywhereLocation2.setAnswerConfirmationRequired(false);
        assertEquals(nextivaAnywhereLocation1, nextivaAnywhereLocation2);
    }
}
