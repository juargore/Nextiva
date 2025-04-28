package com.nextiva.nextivaapp.android.net

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AttachmentApi {
    @GET("rest-api/attachment/v3/attachments/count-by-entity")
    fun testAttachmentApi(@Header("x-api-key") sessionId: String?,
                          @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                          @Query("entityTypes") entityTypes: ArrayList<String>): Single<Response<Void>>
}