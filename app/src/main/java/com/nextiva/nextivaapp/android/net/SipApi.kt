package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.net.sip.SipCallDetails
import com.nextiva.nextivaapp.android.models.net.sip.SipMergeCallsResponse
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface SipApi {
    @POST("rest-api/voice/v2/users/nway-calls")
    fun mergeCalls(@Header("x-api-key") sessionId: String?,
                   @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                   @Body callDetails: ArrayList<SipCallDetails>): Single<Response<SipMergeCallsResponse>>

    @GET("rest-api/voice/v2/users/nway-calls/call-details")
    fun getActiveCalls(@Header("x-api-key") sessionId: String?,
                       @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?): Single<Response<ArrayList<SipCallDetails>>>

    @POST("rest-api/voice/v2/users/nway-calls/{trackingId}/call-details")
    fun addCallToNWay(@Header("x-api-key") sessionId: String?,
                      @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                      @Path("trackingId") trackingId: String,
                      @Body callDetails: SipCallDetails): Single<Response<ResponseBody>>
}