/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

/**
 * Created by adammacdonald on 3/23/18.
 */

public interface KeyStoreManager {

    String PASSWORD = "com.nextiva.nextivaapp.android.PASSWORD";
    String ACCESS_DEVICE_PASSWORD = "com.nextiva.nextivaapp.android.ACCESS_DEVICE_PASSWORD";
    String TOKEN = "com.nextiva.nextivaapp.android.TOKEN";
    String SESSION_ID = "com.nextiva.nextivaapp.android.SESSIONID";
    String SELECTED_TENANT = "com.nextiva.nextivaapp.android.SELECTED_TENANT";
    String USER_INFO = "com.nextiva.nextivaapp.android.USER_INFO";
    String IDENTITY_VOICE = "com.nextiva.nextivaapp.android.IDENTITY_VOICE";
    String PUSH_NOTIFICATION_REGISTRATION_ID = "com.nextiva.nextivaapp.android.PUSH_NOTIFICATION_REGISTRATION_ID";

    @Retention(SOURCE)
    @StringDef( {
            PASSWORD,
            ACCESS_DEVICE_PASSWORD,
            TOKEN,
            SESSION_ID,
            SELECTED_TENANT,
            USER_INFO,
            IDENTITY_VOICE,
            PUSH_NOTIFICATION_REGISTRATION_ID
    })
    @interface KeyStoreAlias {
    }

    void addAlias(@KeyStoreAlias String alias);

    void deleteAlias(@KeyStoreAlias String alias);

    String encryptString(@KeyStoreAlias String alias, String inputText);

    String decryptString(@KeyStoreAlias String alias, String cipherText);
}
