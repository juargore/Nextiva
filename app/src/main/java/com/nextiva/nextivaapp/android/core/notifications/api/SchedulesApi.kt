package com.nextiva.nextivaapp.android.core.notifications.api

import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomsResponse
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

interface SchedulesApi {

    // http://10.103.51.53:8080/swagger-ui/index.html#/
    // http://10.103.51.53:8080/swagger-ui/index.html#/Schedules/UserSchedules
    // http://10.103.51.53:8080/swagger-ui/index.html#/Schedules%2FLocation%20Schedules/getLocationSchedulesUsingGET

    @GET("/rest-api/v2/schedules/account/{corpAccountNumber}")
    fun getUserSchedules(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber1: String?,
        @Header("nextiva-context-ssoActiveProfileId") ssoActiveProfileId: String?,
        @Path("corpAccountNumber") corpAccountNumber2: String?,
        @Query("pageSize") pageSize: Int,
        @Query("pageNumber") pageNumber: Int
    ): Single<Response<ConnectRoomsResponse>>

    @GET("/rest-api/schedules-service/v2/schedules/account/{corpAccountNumber}")
    fun getUserHours(@Header("x-api-key") sessionId: String?,
                     @Header("nextiva-context-corpAcctNumber") corpAccountNumber1: String?,
                     @Path("corpAccountNumber") corpAccountNumber2: String?,
                     @Query("ownerId") ownerId: String?): Single<Response<ScheduleResponseDto>>

    @DELETE("/rest-api/schedules-service/v2/schedules/account/{corpAccountNumber}/{scheduleId}")
    fun deleteSchedule(@Header("x-api-key") sessionId: String?,
                    @Header("nextiva-context-corpAcctNumber") corpAccountNumber1: String?,
                    @Path("corpAccountNumber") corpAccountNumber2: String?,
                    @Path("scheduleId") scheduleId: String,
                    @Query("ownerId") ownerId: String?): Single<Response<ScheduleResponseDto>>
    @POST("/rest-api/schedules-service/v2/schedules/account/{corpAccountNumber}")
    fun saveSchedule(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber1: String?,
        @Path("corpAccountNumber") corpAccountNumber2: String?,
        @Body userScheduleRequest: UserScheduleRequest?): Single<Response<UserScheduleResponse>>

    @PUT("/rest-api/schedules-service/v2/schedules/account/{corpAccountNumber}/{scheduleId}")
    fun updateSchedule(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber1: String?,
        @Path("corpAccountNumber") corpAccountNumber2: String?,
        @Path("scheduleId") scheduleId: String,
        @Body userScheduleRequest: UserScheduleRequest?): Single<Response<UserScheduleResponse>>
    @GET("/rest-api/schedules-service/v2/schedules/account/{corpAccountNumber}/holidays")
    fun getHolidaysList(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber1: String?,
        @Path("corpAccountNumber") corpAccountNumber2: String?
    ): Single<Response<ArrayList<Holiday>>>

}
