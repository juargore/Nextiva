package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.net.calendar.calendars.CalendarApiResponse
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface CalendarApi {

    @Headers("Content-Type: application/vnd.nextiva.calendarservice-v1.0+json")
    @GET("rest-api/calendar/v1/calendars/")
    fun getCalendars(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?
    ): Single<Response<CalendarApiResponse>?>


    @Headers("Content-Type: application/vnd.nextiva.calendarservice-v1.0+json")
    @GET("rest-api/calendar/v1/calendars/events")
    fun getEvents(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Query("ascendingSort") ascendingSort: Boolean = true,
            @Query("eventSortBy") eventSortBy: String,
            @Query("pageNumber") pageNumber: String,
            @Query("pageSize") pageSize: String,
            @Query("filterInfo[0].filterName") calendarIdName: String,
            @Query("filterInfo[0].filterValues") calendarId: String,
            @Query("filterInfo[1].filterName") startDateName: String,
            @Query("filterInfo[1].filterValue") startDate: String,
            @Query("filterInfo[2].filterName") endDateName: String,
            @Query("filterInfo[2].filterValue") endDate: String,
            @Query("liteResults") liteResults: Boolean = true,
            @Query("expandRecurrentEvents") expandRecurrentEvents: Boolean = true
    ): Single<Response<CalendarApiEventResponse>?>


}