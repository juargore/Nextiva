/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.net.interceptors;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.constants.Enums;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by adammacdonald on 2/5/18.
 */

public class NextivaAuthenticationServiceInterceptor implements Interceptor {

    // --------------------------------------------------------------------------------------------
    // Interceptor Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();

        Request request = original.newBuilder()
                .header("Accept", "application/vnd.nextiva.authn-v1.0+json")
                .header("Authorization", "LabAuthnService")
                .header("User-Agent", Enums.Net.USER_AGENT)
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }
    // --------------------------------------------------------------------------------------------
}
