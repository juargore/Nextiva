/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util;

import android.text.TextUtils;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by adammacdonald on 2/2/18.
 */

public class GsonUtil {
    public static <T> T getObject(Class<T> clazz, String json) {
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new GsonBuilder().create();
            try {
                if (!TextUtils.isEmpty(json)) {
                    InputStream stream = new ByteArrayInputStream(json.getBytes
                            (StandardCharsets.UTF_8));
                    JsonReader reader = new JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    return gson.fromJson(reader, clazz);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String getJSON(Object object) {
        if (object == null) {
            return null;
        }

        Gson gson = new GsonBuilder().create();
        return gson.toJson(object);
    }
}
