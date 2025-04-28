/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.nextivacontact;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

import android.text.TextUtils;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;

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
public class NextivaCommunicationTest {

    private static MockedStatic<TextUtils> textUtils;

    @BeforeClass
    public static void global() {
        textUtils = Mockito.mockStatic(TextUtils.class);
        textUtils.when(() -> TextUtils.isEmpty(any(CharSequence.class))).thenAnswer((Answer<Boolean>) invocation -> {
            CharSequence a = (CharSequence) invocation.getArguments()[0];
            return !(a != null && a.length() > 0);
        });
        textUtils.when(() -> TextUtils.isEmpty(null)).thenAnswer((Answer<Boolean>) invocation -> true);
    }

    @AfterClass
    public static void tearDown() {
        textUtils.close();
    }


    @Test
    public void getAssembledPhoneNumber_blankPhoneNumber_returnsNull() {
        PhoneNumber nextivaCommunication = new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "", null, null);

        assertNull(nextivaCommunication.getAssembledPhoneNumber());
    }

    @Test
    public void getAssembledPhoneNumber_onlyPhoneNumber_returnsPhoneNumber() {
        PhoneNumber nextivaCommunication = new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "123456789", null, null);

        assertEquals("123456789", nextivaCommunication.getAssembledPhoneNumber());
    }

    @Test
    public void getAssembledPhoneNumber_phoneNumberAndConferenceId_returnsFormattedPhoneNumberAndConferenceId() {
        PhoneNumber nextivaCommunication = new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "123456789", "1111", null);

        assertEquals("123456789,1111#", nextivaCommunication.getAssembledPhoneNumber());
    }

    @Test
    public void getAssembledPhoneNumber_phoneNumberConferenceIdAndPin_returnsFormattedPhoneNumberConferenceIdAndPin() {
        PhoneNumber nextivaCommunication = new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "123456789", "1111", "2222");

        assertEquals("123456789,1111#,2222#", nextivaCommunication.getAssembledPhoneNumber());
    }

    @Test
    public void getAssembledPhoneNumber_phoneNumberAndPin_returnsFormattedPhoneNumberAndPin() {
        PhoneNumber nextivaCommunication = new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "123456789", null, "2222");

        assertEquals("123456789,2222#", nextivaCommunication.getAssembledPhoneNumber());
    }
}
