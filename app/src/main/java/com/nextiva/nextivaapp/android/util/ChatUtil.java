/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util;

import com.nextiva.nextivaapp.android.models.ChatConversation;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by adammacdonald on 3/12/18.
 */

public class ChatUtil {

    public static void sortConversations(ArrayList<ChatConversation> chatConversations) {
        Collections.sort(chatConversations, (lhs, rhs) -> {
            long left = lhs.getLastMessageTimestamp();
            long right = rhs.getLastMessageTimestamp();

            if (left > 0 && right > 0) {
                return Long.compare(right, left);

            } else if (left > 0) {
                return -1;

            } else {
                return 1;
            }
        });
    }
}
