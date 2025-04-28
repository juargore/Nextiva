/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.net;

import com.nextiva.nextivaapp.android.models.UserDetails;
import com.nextiva.nextivaapp.android.models.net.AuthenticationBody;
import com.nextiva.nextivaapp.android.models.net.AuthenticationResponse;
import com.nextiva.nextivaapp.android.models.net.InvalidateTokenResponse;
import com.nextiva.nextivaapp.android.models.net.ValidateTokenResponse;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by adammacdonald on 2/2/18.
 */

public interface AuthenticationServiceApi {

    @Headers("Content-Type: application/vnd.nextiva.authn-v1.0+json")
    @POST("AuthenticationService/user/authenticate")
    Single<AuthenticationResponse> postAuthentication(
            @Header("X-AppVersion") String appVersionHeader,
            @Body AuthenticationBody body);

    @Headers("Content-Type: application/vnd.nextiva.authn-v1.0+json")
    @GET("AuthenticationService/user/validate")
    Single<ValidateTokenResponse> getValidateToken(
            @Header("X-AppVersion") String appVersionHeader,
            @Query("token") String token);

    @Headers("Content-Type: application/vnd.nextiva.authn-v1.0+json")
    @POST("AuthenticationService/user/invalidate")
    Single<InvalidateTokenResponse> postInvalidateToken(
            @Header("X-AppVersion") String appVersionHeader,
            @Query("token") String token);


    @Headers("Content-Type: application/vnd.nextiva.authn-v1.0+json")
    @GET("AuthenticationService/users/{token}")
    Single<UserDetails> getUserDetails(
            @Header("X-AppVersion") String appVersionHeader,
            @Path("token") String token);
}
