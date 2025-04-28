package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Application
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbMeeting
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CalendarRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.meetings.MeetingUtil
import com.nextiva.nextivaapp.android.models.net.calendar.calendars.CalendarApiResponse
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventResponse
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.GsonUtil
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import java.util.Calendar
import javax.inject.Inject
import kotlin.concurrent.thread

internal class CalendarApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var netManager: NetManager,
    var schedulerProvider: SchedulerProvider,
    var sessionManager: SessionManager,
    var dbManager: DbManager,
    var sharedPreferencesManager: SharedPreferencesManager
) : BaseApiManager(application, logManager), CalendarRepository {

    override fun getCalendars(): Single<CalendarApiResponse> {
        return if (netManager.getCalendarApi() != null) {
            netManager.getCalendarApi()!!.getCalendars(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                    } else {
                        logServerParseFailure(response)
                    }
                    response.body()
                }
        } else {
            Single.just(CalendarApiResponse())
        }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                CalendarApiResponse()
            }
            .map { calendarResponse ->
                calendarResponse
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun getEvents(startDate: String, endDate: String): Single<CalendarApiEventResponse> {
        return if (netManager.getCalendarApi() != null) {
            netManager.getCalendarApi()!!.getCalendars(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                    } else {
                        logServerParseFailure(response)
                    }
                    response.body()
                }
                .flatMap { calendarResponse ->
                    if (!calendarResponse.calendars.isNullOrEmpty()) {
                        var calendars = ""
                        for (calendar in calendarResponse.calendars) {
                            if (calendar.value.calendarId != null)
                                calendars += calendar.value.calendarId.toString() + ","
                        }
                        calendars = if (calendars.lastIndexOf(",") > 0) calendars.substring(0, calendars.length - 1) else calendars

                        netManager.getCalendarApi()!!.getEvents(
                            sessionId = sessionManager.sessionId,
                            corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                            eventSortBy = Enums.CalendarOrchestration.EVENT_ID,
                            pageNumber = calendarResponse.pageNumber.toString(),
                            pageSize = Enums.CalendarOrchestration.PAGE_SIZE,
                            calendarIdName = Enums.CalendarOrchestration.CALENDAR_ID,
                            calendarId = calendars,
                            startDateName = Enums.CalendarOrchestration.START_DATE,
                            startDate = startDate,
                            endDateName = Enums.CalendarOrchestration.END_DATE,
                            endDate = endDate
                        ).subscribeOn(schedulerProvider.io())
                            .map { response ->
                                if (response.isSuccessful) {
                                    logServerSuccess(response)
                                } else {
                                    logServerParseFailure(response)
                                }
                                response.body()
                            }
                    } else {
                        Single.just(CalendarApiEventResponse())
                    }
                }
        } else {
            Single.just(CalendarApiEventResponse())
        }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                CalendarApiEventResponse()
            }.map { calendarEventResponse ->
                calendarEventResponse
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun fetchEvents(
        startDate: String,
        endDate: String,
        startTime: Calendar,
        endTime: Calendar,
        compositeDisposable: CompositeDisposable,
        onSaveFinishedCallback: () -> Unit
    ) {
        compositeDisposable.add(
            getEvents(startDate, endDate).subscribe ({ eventsList ->
                thread {
                    if (eventsList.events != null) {
                        val sortedEvent = MeetingUtil.sortScheduledMeetings(
                            eventsList.events,
                            startTime,
                            endTime,
                            Enums.MediaCall.CallCategories.SCHEDULED
                        ).toSortedMap()
                        val meetings = ArrayList<DbMeeting>()
                        for (event in sortedEvent) {
                            meetings.add(
                                DbMeeting(
                                    calendarId = event.value.calendarId!!,
                                    name = event.value.name!!,
                                    startTime = event.value.startTime.toLong(),
                                    createdBy = event.value.createdBy!!,
                                    meetingInfo = GsonUtil.getJSON(event.value)
                                )
                            )
                        }

                        dbManager.saveMeetings(meetings, startTime.timeInMillis)
                            .observeOn(schedulerProvider.ui())
                            .subscribe(object : DisposableCompletableObserver() {
                                override fun onComplete() {
                                    onSaveFinishedCallback()
                                }

                                override fun onError(e: Throwable) {
                                    onSaveFinishedCallback()
                                }
                            })
                    } else {
                        onSaveFinishedCallback()
                    }
                }.start()
            }, {
                onSaveFinishedCallback()
            })
        )
    }

    override fun getDbMeetings(startDate: Long, endDate: Long): List<String> {
        return dbManager.getMeetingsBetweenDates(startDate, endDate)
    }
}
