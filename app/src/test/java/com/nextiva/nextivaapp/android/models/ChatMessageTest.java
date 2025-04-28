/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

import android.text.TextUtils;

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
public class ChatMessageTest {

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
    public void getFullName_hasFullName_returnsFullName() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setGuestFirstName("Jim");
        chatMessage.setGuestLastName("Smith");

        assertEquals("Jim Smith", chatMessage.getGuestFullName());
    }

    @Test
    public void getFullName_hasNoLastName_returnsFirstName() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setGuestFirstName("Jim");

        assertEquals("Jim", chatMessage.getGuestFullName());
    }

    @Test
    public void getFullName_hasNoName_returnsNull() {
        ChatMessage chatMessage = new ChatMessage();

        assertNull(chatMessage.getGuestFullName());
    }
}
