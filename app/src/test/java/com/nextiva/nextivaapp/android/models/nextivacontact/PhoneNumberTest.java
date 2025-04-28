/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.nextivacontact;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

/**
 * Created by adammacdonald on 3/22/18.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class PhoneNumberTest {

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
    public void equals_sameInstance_returnsTrue() {
        PhoneNumber phoneNumber1 = new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1112223333", null);
        PhoneNumber phoneNumber2 = phoneNumber1;

        assertEquals(phoneNumber1, phoneNumber2);
    }

    @Test
    public void equals_differentObjectType_returnsFalse() {
        PhoneNumber phoneNumber = new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1112223333", null);

        assertNotEquals("", phoneNumber);
    }

    @Test
    public void equals_differentInstances_returnsTrueWhenEqual() {
        PhoneNumber phoneNumber1 = new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1112223333", null);
        PhoneNumber phoneNumber2 = new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1112223333", null);

        assertEquals(phoneNumber1, phoneNumber2);

        phoneNumber2.setNumber("9998887777");
        assertNotEquals(phoneNumber1, phoneNumber2);
        phoneNumber2.setNumber("1112223333");
        assertEquals(phoneNumber1, phoneNumber2);

        phoneNumber2.setType(Enums.Contacts.PhoneTypes.HOME_PHONE);
        assertNotEquals(phoneNumber1, phoneNumber2);
        phoneNumber2.setType(Enums.Contacts.PhoneTypes.WORK_PHONE);
        assertEquals(phoneNumber1, phoneNumber2);

        phoneNumber1.setNumber("");
        phoneNumber2.setNumber(null);
        assertNotEquals(phoneNumber1, phoneNumber2);
        phoneNumber1.setNumber("1112223333");
        phoneNumber2.setNumber("1112223333");
        assertEquals(phoneNumber1, phoneNumber2);

        phoneNumber2.setLabel("Cool label");
        assertNotEquals(phoneNumber1, phoneNumber2);
        phoneNumber1.setLabel("Cool label");
        assertEquals(phoneNumber1, phoneNumber2);

        phoneNumber1.setLabel("");
        phoneNumber2.setLabel(null);
        assertNotEquals(phoneNumber1, phoneNumber2);
        phoneNumber1.setLabel("Work");
        phoneNumber2.setLabel("Work");
        assertEquals(phoneNumber1, phoneNumber2);
    }
}
