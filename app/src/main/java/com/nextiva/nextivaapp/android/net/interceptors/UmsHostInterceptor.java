/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.net.interceptors;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.constants.Enums;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UmsHostInterceptor implements Interceptor {

    private String mHost;

    @Inject
    public UmsHostInterceptor() {
    }

    public void setHost(String host) {
        mHost = host;
    }

    public boolean isHostSetup() {
        return !TextUtils.isEmpty(mHost);
    }

    // --------------------------------------------------------------------------------------------
    // Interceptor Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        if (mHost != null) {
            HttpUrl newUrl = request.url().newBuilder().host(mHost).build();

            return chain.proceed(new Request.Builder()
                                         .url(newUrl)
                                         .headers(request.headers())
                                         .header("User-Agent", Enums.Net.USER_AGENT)
                                         .method(request.method(), request.body())
                                         .build());
        }

        return chain.proceed(request);
    }
    // --------------------------------------------------------------------------------------------
}
