package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.net.platform.AccountInformation
import com.nextiva.nextivaapp.android.models.net.platform.LogSubmit
import com.nextiva.nextivaapp.android.models.net.platform.featureFlags.FeatureFlag
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PlatformApi {
    @GET("rest-api/account/v1/account/summary/current")
    fun getAccountInformation(
                              @Header("x-api-key") sessionId: String?,
                        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?): Single<Response<AccountInformation>>

    @GET("rest-api/feature-toggle/v1/features")
    fun getFeatureFlags(
                        @Header("x-api-key") sessionId: String?,
                        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                        @Query(value = "names", encoded = true) names: String): Single<Response<ArrayList<FeatureFlag>?>>

    @POST("rest-api/logging/v1/logs")
    fun postLogs(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Body logSubmit: LogSubmit
    ): Single<Response<ResponseBody>>
}