/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.net.interceptors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

import javax.inject.Inject;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class UmsAuthenticator implements Authenticator {

    private String mAuthorizationHeader;

    @Inject
    public UmsAuthenticator() {
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        mAuthorizationHeader = authorizationHeader;
    }

    // --------------------------------------------------------------------------------------------
    // Authenticator Methods
    // --------------------------------------------------------------------------------------------
    @Nullable
    @Override
    public Request authenticate(@NonNull Route route, @NonNull Response response) {
        Request.Builder builder = response.request().newBuilder();
        builder.header("Authorization", mAuthorizationHeader);

        okhttp3.Response priorResponse = response.priorResponse();
        if (priorResponse != null && priorResponse.request() != null) {
            builder.method(priorResponse.request().method(), priorResponse.request().body());
        }
        builder.header("User-Agent", Enums.Net.USER_AGENT);
        return builder.build();
    }
    // --------------------------------------------------------------------------------------------
}
