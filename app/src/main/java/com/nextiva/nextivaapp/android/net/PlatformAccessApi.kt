package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.net.platform.BroadworksCredentials
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface PlatformAccessApi {
    @GET("oauth-api/identity/voice")
    fun getBroadworksCredentials(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAcctNumber: String?,
        ): Single<Response<BroadworksCredentials?>?>
}