/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.net.platform.SelectiveCallRejection
import com.nextiva.nextivaapp.android.models.net.platform.user.device.Policies
import com.nextiva.nextivaapp.android.models.net.platform.user.info.UserAdmin
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Created by Thaddeus Dannar on 3/1/23.
 */
interface UsersApi {

    @Headers("Content-Type: application/json")
    @GET("/rest-api/voice-settings/users/{user-uuid}/device/policies")
    fun getDevicesPolicies(
        @Header("x-api-key") authorizationHeader: String?,
        @Header("nextiva-context-corpacctnumber") corpAcctNumber: String?,
        @Path("user-uuid") userUUID: String,
    ): Single<Response<Policies>>

    @Headers("Content-Type: application/json")
    @GET("/rest-api/iam/v2/users/current/info")
    fun getUserInfo(
        @Header("x-api-key") authorizationHeader: String?,
        @Header("nextiva-context-corpacctnumber") corpAcctNumber: String?
    ): Single<Response<UserAdmin>>

    @Headers("Content-Type: application/json")
    @GET("/rest-api/sms/v1/corp-accounts/current/campaigns/status")
    fun getSMSCampaignStatus(
        @Header("x-api-key") authorizationHeader: String?,
        @Header("nextiva-context-corpacctnumber") corpAcctNumber: String?
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("/rest-api/voice-settings-orchestration-service/v2/users/{user-uuid}/call-routing/selective-call-rejection")
    fun blockNumber(
        @Header("x-api-key") authorizationHeader: String?,
        @Header("nextiva-context-corpacctnumber") corpAcctNumber: String?,
        @Path("user-uuid") userUUID: String,
        @Body setting: SelectiveCallRejection
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("/rest-api/voice-settings-orchestration-service/v2/users/{user-uuid}/call-routing/selective-call-rejection")
    suspend fun fetchBlockedNumbers(
        @Header("x-api-key") authorizationHeader: String?,
        @Header("nextiva-context-corpacctnumber") corpAcctNumber: String?,
        @Path("user-uuid") userUUID: String
    ): Response<SelectiveCallRejection>
}
