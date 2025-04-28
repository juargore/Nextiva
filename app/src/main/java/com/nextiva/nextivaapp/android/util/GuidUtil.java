/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.db.DbManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by joedephillipo on 3/21/18.
 */

public class GuidUtil {

    public static int getUniqueContactId(@NonNull DbManager dbManager) {
        int uniqueId = getRandomId();
        ArrayList<Integer> idList = new ArrayList<>();

        for (String contactId : dbManager.getRosterContactIds()) {
            try {
                idList.add(Integer.parseInt(contactId));
            } catch (NumberFormatException exception) {
                FirebaseCrashlytics.getInstance().recordException(exception);
                // iOS sometimes sends a user ID of the user's email address
            }
        }

        while (idList.contains(uniqueId)) {
            uniqueId = getRandomId();
        }

        return uniqueId;
    }

    public static int getRandomId() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }
}
