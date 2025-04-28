/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.net;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.BroadsoftMobileConfigResponse;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Url;

/**
 * Created by joedephillipo on 2/22/18.
 */

public interface BroadsoftMobileApi {

    @Headers("Accept: application/xml")
    @GET()
    Single<Response<BroadsoftMobileConfigResponse>> getMobilConfig(
            @Header("X-AppVersion") String appVersionHeader,
            @Url String url);

}
