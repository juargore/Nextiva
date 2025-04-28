package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceDndSchedule
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceMessageBody
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceResponse
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceResponseBody
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceSetBody
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PresenceApi {
    @GET("rest-api/presence-service/v1/corp/status")
    fun getPresences(@Header("x-api-key") sessionId: String?,
                     @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                     @Query(value = "pageNumber") pageNumber: String,
                     @Query("pageSize") pageSize: String): Single<Response<ConnectPresenceResponseBody>>

    @POST("rest-api/presence-service/v1/user/setting")
    fun setPresence(@Header("x-api-key") sessionId: String?,
                    @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                    @Body presence: ConnectPresenceSetBody): Single<Response<ConnectPresenceResponse>>

    @PUT("rest-api/presence-service/v1/user/setting/message")
    fun setPresenceMessage(@Header("x-api-key") sessionId: String?,
                    @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                    @Body presence: ConnectPresenceMessageBody): Single<Response<ConnectPresenceResponse>>

    @GET("rest-api/presence-service/v1/users/current/ping")
    fun sendPresenceHeartbeat(@Header("x-api-key") sessionId: String?,
                              @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?): Single<Response<Void>>

    @PUT("rest-api/presence-service/v1/users/{userId}/dndschedule")
    fun setPresenceDndSchedule(@Header("x-api-key") sessionId: String?,
                               @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                               @Path("userId") userId: String,
                               @Body presence: ConnectPresenceDndSchedule): Single<Response<ConnectPresenceDndSchedule>>

    @GET("rest-api/presence-service/v1/users/{userId}/dndschedule")
    fun getPresenceDndSchedule(@Header("x-api-key") sessionId: String?,
                               @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                               @Path("userId") userId: String): Single<Response<ConnectPresenceDndSchedule>>

    @DELETE("rest-api/presence-service/v1/users/{userId}/dndschedule")
    fun deletePresenceDndSchedule(@Header("x-api-key") sessionId: String?,
                               @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                               @Path("userId") userId: String): Single<Response<Void>>
}