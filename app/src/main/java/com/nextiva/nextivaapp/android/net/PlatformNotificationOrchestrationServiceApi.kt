package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.net.platform.DeviceBody
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PlatformNotificationOrchestrationServiceApi {
    @GET("/rest-api/platform-notification/v1/devices/{deviceId}")
    fun getDevice(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Path("deviceId") deviceId: String?): Single<Response<DeviceBody>>

    @POST("/rest-api/platform-notification/v1/devices/register")
    fun createDevice(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Body body: DeviceBody): Single<Response<DeviceBody>>

    @DELETE("/rest-api/platform-notification/v1/devices/{deviceId}")
    fun deleteDevice(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Path("deviceId") deviceId: String?): Single<Response<ResponseBody>>
}