/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;

import android.text.TextUtils;

import com.nextiva.nextivaapp.android.constants.Enums;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by adammacdonald on 3/22/18.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class CallLogEntryTest {

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
    public void getHumanReadableName_hasUiName_returnsUiName() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals("UI Name", callLogEntry.getHumanReadableName());
    }

    @Test
    public void getHumanReadableName_hasDisplayName_returnsDisplayName() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                null,
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals("Display Name", callLogEntry.getHumanReadableName());
    }

    @Test
    public void getCallLogId_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals("callLogId", callLogEntry.getCallLogId());
    }

    @Test
    public void setCallLogId_setsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setCallLogId("newId");

        assertEquals("newId", callLogEntry.getCallLogId());
    }

    @Test
    public void getDisplayName_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals("Display Name", callLogEntry.getDisplayName());
    }

    @Test
    public void setDisplayName_setsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setDisplayName("New Name");

        assertEquals("New Name", callLogEntry.getDisplayName());
    }

    @Test
    public void getCallInstant_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals(1523661568L, callLogEntry.getCallInstant().getEpochSecond());
    }

    @Test
    public void setCallDateTime_setsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setCallTime(1523915116000L);

        assertEquals(1523915116L, callLogEntry.getCallInstant().getEpochSecond());
    }

    @Test
    public void getCountryCode_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals("1", callLogEntry.getCountryCode());
    }

    @Test
    public void setCountryCode_setsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setCountryCode("2");

        assertEquals("2", callLogEntry.getCountryCode());
    }

    @Test
    public void getPhoneNumber_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals("1112223333", callLogEntry.getPhoneNumber());
    }

    @Test
    public void setPhoneNumber_setsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setPhoneNumber("3332221111");

        assertEquals("3332221111", callLogEntry.getPhoneNumber());
    }

    @Test
    public void getCallType_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals(Enums.Calls.CallTypes.MISSED, callLogEntry.getCallType());
    }

    @Test
    public void setCallType_setsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setCallType(Enums.Calls.CallTypes.PLACED);

        assertEquals(Enums.Calls.CallTypes.PLACED, callLogEntry.getCallType());
    }

    @Test
    public void getAvatar_returnsCorrectValue() {
        byte[] byteArray = new byte[] {2};

        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                byteArray,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals(byteArray, callLogEntry.getAvatar());
    }

    @Test
    public void setAvatar_setsCorrectValue() {
        byte[] byteArray1 = new byte[] {2};
        byte[] byteArray2 = new byte[] {3};

        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                byteArray1,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setAvatar(byteArray2);

        assertEquals(byteArray2, callLogEntry.getAvatar());
    }

    @Test
    public void getUiName_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals("UI Name", callLogEntry.getUiName());
    }

    @Test
    public void setUiName_setsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setUiName("New Name");

        assertEquals("New Name", callLogEntry.getUiName());
    }

    @Test
    public void getPresenceState_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals(Enums.Contacts.PresenceStates.AVAILABLE, callLogEntry.getPresenceState());
    }

    @Test
    public void setPresenceState_setsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setPresenceState(Enums.Contacts.PresenceStates.BUSY);

        assertEquals(Enums.Contacts.PresenceStates.BUSY, callLogEntry.getPresenceState());
    }

    @Test
    public void getIsRead_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setIsRead(true);

        assertTrue(callLogEntry.getIsRead());
    }

    @Test
    public void setIsRead_setsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setIsRead(true);

        assertTrue(callLogEntry.getIsRead());
    }

    @Test
    public void getIsRead_nullValue_returnsFalse() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertFalse(callLogEntry.getIsRead());
    }


    @Test
    public void getStatusText_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals("Test", callLogEntry.getStatusText());
    }


    @Test
    public void setStatusText_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setStatusText("Not Test");

        assertEquals("Not Test", callLogEntry.getStatusText());
    }


    @Test
    public void equals_sameInstance_returnsTrue() {
        CallLogEntry callLogEntry1 = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        CallLogEntry callLogEntry2 = callLogEntry1;

        assertEquals(callLogEntry1, callLogEntry2);
    }

    @Test
    public void equals_differentObjectType_returnsFalse() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertNotEquals("", callLogEntry);
    }

    @Test
    public void getCallDuration_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals(123, callLogEntry.getCallDuration());
    }

    @Test
    public void setCallDuration_returnsCorrectValue() {
        CallLogEntry callLogEntry = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                null,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "Test",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        callLogEntry.setCallDuration(321);

        assertEquals(321, callLogEntry.getCallDuration());
    }

    @Test
    public void equals_differentInstances_returnsTrueWhenEqual() {
        byte[] avatarOne = new byte[] {2};
        byte[] avatarTwo = new byte[] {3};

        CallLogEntry callLogEntry1 = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                avatarOne,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "My Status",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        CallLogEntry callLogEntry2 = new CallLogEntry(
                "callLogId",
                "Display Name",
                1523661568000L,
                "1",
                "1112223333",
                Enums.Calls.CallTypes.MISSED,
                avatarOne,
                "UI Name",
                Enums.Contacts.PresenceStates.AVAILABLE,
                -10,
                "My Status",
                0,
                "jid@jid.im",
                Enums.Contacts.PresenceTypes.AVAILABLE,
                123,
                "");

        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry2.setCallLogId("differentCallLogId");
        assertNotEquals(callLogEntry1, callLogEntry2);
        callLogEntry2.setCallLogId("callLogId");
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry1.setCallLogId("");
        callLogEntry2.setCallLogId(null);
        assertEquals(callLogEntry1, callLogEntry2);
        callLogEntry1.setCallLogId("callLogId");
        callLogEntry2.setCallLogId("callLogId");
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry2.setDisplayName("Different Name");
        assertNotEquals(callLogEntry1, callLogEntry2);
        callLogEntry2.setDisplayName("Display Name");
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry1.setDisplayName("");
        callLogEntry2.setDisplayName(null);
        assertEquals(callLogEntry1, callLogEntry2);
        callLogEntry1.setDisplayName("Display Name");
        callLogEntry2.setDisplayName("Display Name");
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry2.setCallTime(1523915116000L);
        assertNotEquals(callLogEntry1, callLogEntry2);
        callLogEntry2.setCallTime(1523661568000L);
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry2.setCountryCode("9");
        assertNotEquals(callLogEntry1, callLogEntry2);
        callLogEntry2.setCountryCode("1");
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry1.setCountryCode("");
        callLogEntry2.setCountryCode(null);
        assertEquals(callLogEntry1, callLogEntry2);
        callLogEntry1.setCountryCode("1");
        callLogEntry2.setCountryCode("1");
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry2.setPhoneNumber("411");
        assertNotEquals(callLogEntry1, callLogEntry2);
        callLogEntry2.setPhoneNumber("1112223333");
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry1.setPhoneNumber("");
        callLogEntry2.setPhoneNumber(null);
        assertEquals(callLogEntry1, callLogEntry2);
        callLogEntry1.setPhoneNumber("1112223333");
        callLogEntry2.setPhoneNumber("1112223333");
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry2.setCallType(Enums.Calls.CallTypes.RECEIVED);
        assertNotEquals(callLogEntry1, callLogEntry2);
        callLogEntry2.setCallType(Enums.Calls.CallTypes.MISSED);
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry1.setCallType("");
        callLogEntry2.setCallType(null);
        assertEquals(callLogEntry1, callLogEntry2);
        callLogEntry1.setCallType(Enums.Calls.CallTypes.MISSED);
        callLogEntry2.setCallType(Enums.Calls.CallTypes.MISSED);
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry2.setAvatar(avatarTwo);
        assertNotEquals(callLogEntry1, callLogEntry2);
        callLogEntry2.setAvatar(avatarOne);
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry1.setAvatar(null);
        callLogEntry2.setAvatar(null);
        assertEquals(callLogEntry1, callLogEntry2);
        callLogEntry1.setAvatar(avatarTwo);
        callLogEntry2.setAvatar(avatarTwo);
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry1.setIsRead(true);
        callLogEntry2.setIsRead(null);
        assertNotEquals(callLogEntry1, callLogEntry2);
        callLogEntry1.setIsRead(true);
        callLogEntry2.setIsRead(true);
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry1.setIsRead(false);
        callLogEntry2.setIsRead(null);
        assertEquals(callLogEntry1, callLogEntry2);
        callLogEntry1.setIsRead(true);
        callLogEntry2.setIsRead(true);
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry2.setUiName("My UI Name");
        assertNotEquals(callLogEntry1, callLogEntry2);
        callLogEntry2.setUiName("UI Name");
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry1.setUiName("");
        callLogEntry2.setUiName(null);
        assertEquals(callLogEntry1, callLogEntry2);
        callLogEntry1.setUiName("UI Name");
        callLogEntry2.setUiName("UI Name");
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry2.setPresenceState(Enums.Contacts.PresenceStates.BUSY);
        assertNotEquals(callLogEntry1, callLogEntry2);
        callLogEntry2.setPresenceState(Enums.Contacts.PresenceStates.AVAILABLE);
        assertEquals(callLogEntry1, callLogEntry2);


        callLogEntry2.setStatusText("This is a Status");
        assertNotEquals(callLogEntry1, callLogEntry2);
        callLogEntry2.setStatusText("My Status");
        assertEquals(callLogEntry1, callLogEntry2);

        callLogEntry1.setStatusText("");
        callLogEntry2.setStatusText(null);
        assertEquals(callLogEntry1, callLogEntry2);
        callLogEntry1.setStatusText("My Status");
        callLogEntry2.setStatusText("My Status");
        assertEquals(callLogEntry1, callLogEntry2);

    }
}
