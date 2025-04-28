package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.net.PendoRequestBody
import com.nextiva.nextivaapp.android.models.net.PendoResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PendoApi {

    @POST("v1/aggregation")
    suspend fun getPendoData(
        @Header("appKey") appPendoKey: String,
        @Header("x-pendo-integration-key") integrationKey: String,
        @Body body: PendoRequestBody
    ) : Response<PendoResponseBody>
}