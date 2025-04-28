package com.nextiva.nextivaapp.android.managers.apimanagers.repositories

import com.nextiva.nextivaapp.android.models.net.calendar.calendars.CalendarApiResponse
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventResponse
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.util.Calendar

interface CalendarRepository {

    fun getCalendars(): Single<CalendarApiResponse>

    fun getEvents(
            startDate: String,
            endDate: String
    ): Single<CalendarApiEventResponse>

    fun fetchEvents(
        startDate: String,
        endDate: String,
        startTime: Calendar,
        endTime: Calendar,
        compositeDisposable: CompositeDisposable,
        onSaveFinishedCallback: () -> Unit
    )

    fun getDbMeetings(startDate: Long, endDate: Long): List<String>
}