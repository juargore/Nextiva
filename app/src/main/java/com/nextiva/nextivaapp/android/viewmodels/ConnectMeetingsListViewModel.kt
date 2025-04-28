package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MeetingListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.CalendarOrchestration
import com.nextiva.nextivaapp.android.constants.Enums.MediaCall.CallCategories.SCHEDULED
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CalendarRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.MediaCallRepository
import com.nextiva.nextivaapp.android.meetings.MeetingUtil
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventDetail
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallResponse
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.concurrent.thread

@HiltViewModel
class ConnectMeetingsListViewModel @Inject constructor(
    application: Application,
    private val calendarRepository: CalendarRepository,
    private val mediaCallRepository: MediaCallRepository,
) : BaseViewModel(application) {

    var meetingsMutableLiveData: MutableLiveData<ArrayList<BaseListItem>> = MutableLiveData()
    var apiSuccessLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var meetingsMap: Map<Long, CalendarApiEventDetail> = mapOf()
    private var activeMeetings: Map<String, MediaCallResponse> = mapOf()
    private var scheduledMeetings: Map<Long, CalendarApiEventDetail> = mapOf()
    private var paginationMeetings: Map<Long, CalendarApiEventDetail> = mapOf()
    private var isLoadingMeetings = AtomicBoolean(false)
    private var uiVisible: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    private var dataReady = MutableLiveData(false)
    var launchLoop = MediatorLiveData<Boolean>().apply {
        this.value = false
        this.addSource(dataReady) { newValue -> this.value = newValue && uiVisible.value.orFalse() }
        this.addSource(uiVisible) { newValue -> this.value = newValue && dataReady.value.orFalse() }
    }
    var scrollToTop: MutableLiveData<Int> = MutableLiveData(0)

    private var isTodayMeetingsThreadStarted = false
    private val threadLock = Any()


    fun setUiVisible(isVisible: Boolean) {
        uiVisible.value = isVisible
    }


    fun initialFetchMeetings(onSaveFinishedCallback: () -> Unit) {
        if (isLoadingMeetings.compareAndSet(false, true)) {
            dataReady.value = false // Kill periodic loop, since this function refreshes all

            val time0 = Calendar.getInstance()
            time0.add(Calendar.MINUTE, 15)

            val time1 = Calendar.getInstance()
            time1.add(Calendar.MONTH, 1)
            time1.set(Calendar.HOUR_OF_DAY, 23)
            time1.set(Calendar.MINUTE, 59)
            time1.set(Calendar.SECOND, 59)
            //Get initial load
            val futureMeetings =
                calendarRepository.getDbMeetings(time0.timeInMillis, time1.timeInMillis)
            if (futureMeetings.isNotEmpty()) {
                scheduledMeetings =
                    MeetingUtil.parseStringToCalendarApiEventMap(futureMeetings, false)
                val eventsArray = ArrayList<BaseListItem>()
                scheduledMeetings.forEach {
                    eventsArray.add(
                        MeetingListItem(
                            it.value,
                            it.value.startTime,
                            it.value.name,
                            getEventId(it.value),
                            SCHEDULED
                        )
                    )
                }
                apiSuccessLiveData.value = true
                meetingsMutableLiveData.postValue(eventsArray)
            } else {
                apiSuccessLiveData.value = false
            }

            //Update db from API, 2 months back 1 forward
            fetchMeetings {
                getTodayMeetings {
                    dataReady.value = true
                    onSaveFinishedCallback()
                }
            }
        }
    }

    fun fetchTodayMeetings(onSaveFinishedCallback: () -> Unit) {
        if (isLoadingMeetings.compareAndSet(false, true)) {
            getTodayMeetings {
                onSaveFinishedCallback()
            }
        }
    }

    fun getMoreMeetings() {
        val lastMeetingTimeInMillis =
            if(paginationMeetings.isNotEmpty())
                paginationMeetings.entries.last().value.endTime.toLong() + 1
            else if (scheduledMeetings.isNotEmpty())
                scheduledMeetings.entries.last().value.endTime.toLong() + 1
            else
                0L

        if (lastMeetingTimeInMillis > 0){
            val startTime = Calendar.getInstance()
            val date = SimpleDateFormat(CalendarOrchestration.DATE_FORMAT, Locale.getDefault())

            startTime.timeInMillis = lastMeetingTimeInMillis

            var startDate: String = date.format(startTime.time)
            startDate = StringBuffer(startDate).insert(startDate.length - 2, ":").toString()

            val endTime = Calendar.getInstance()
            endTime.timeInMillis = lastMeetingTimeInMillis
            endTime.add(Calendar.MONTH, 1)
            var endDate = date.format(endTime.time)
            endDate = StringBuffer(endDate).insert(endDate.length - 2, ":").toString()

            mCompositeDisposable.add(
                calendarRepository.getEvents(startDate, endDate).subscribe { eventResponse ->
                    if (!eventResponse.events.isNullOrEmpty()) {
                        val sortedMap = MeetingUtil.sortScheduledMeetings(
                            eventResponse.events,
                            startTime,
                            endTime,
                            Enums.MediaCall.CallCategories.INSTANT
                        ).toSortedMap()

                        sortedMap.forEach { it.value.status = Enums.MediaCall.CallStatuses.INACTIVE }

                        if(sortedMap.isNotEmpty()){
                            paginationMeetings = paginationMeetings + sortedMap
                            startTime.timeInMillis = Calendar.getInstance().timeInMillis
                            endTime.timeInMillis = Calendar.getInstance().timeInMillis
                            endTime.add(Calendar.MINUTE, 15)
                            sortInProgressMeetings(startTime,endTime)
                        }
                    }
                }
            )
        }
    }

    private fun getTodayMeetings(onSaveFinishedCallback: () -> Unit) {
        val date = SimpleDateFormat(CalendarOrchestration.DATE_FORMAT, Locale.getDefault())
        val now = Calendar.getInstance()
        val startTime = Calendar.getInstance()
        //when the user is not the host, all-day meetings starts at 18:00 of the day before
        startTime.add(Calendar.DAY_OF_YEAR, -1)
        startTime.set(Calendar.HOUR_OF_DAY, 17)
        startTime.set(Calendar.MINUTE, 59)
        startTime.set(Calendar.SECOND, 59)
        val endTime = Calendar.getInstance()
        endTime.add(Calendar.MINUTE, 15)
        val endDay = Calendar.getInstance()
        endDay.set(Calendar.HOUR_OF_DAY, 23)
        endDay.set(Calendar.MINUTE, 59)
        endDay.set(Calendar.SECOND, 59)
        var startDate = date.format(startTime.time)
        startDate = StringBuffer(startDate).insert(startDate.length - 2, ":").toString()
        var endDate = date.format(endDay.time)
        endDate = StringBuffer(endDate).insert(endDate.length - 2, ":").toString()

        val time1 = Calendar.getInstance()
        time1.add(Calendar.MONTH, 1)
        time1.set(Calendar.HOUR_OF_DAY, 23)
        time1.set(Calendar.MINUTE, 59)
        time1.set(Calendar.SECOND, 59)

        var thread =
        thread {
        val futureMeetings = calendarRepository.getDbMeetings(endTime.timeInMillis, time1.timeInMillis)
        if (futureMeetings.isNotEmpty()) {
            scheduledMeetings = MeetingUtil.parseStringToCalendarApiEventMap(futureMeetings, false)
        }

        mCompositeDisposable.add(
            calendarRepository.getEvents(startDate, endDate).subscribe({ eventResponse ->
                if (!eventResponse.events.isNullOrEmpty()) {
                    //get meetings only for today
                    startTime.add(Calendar.DAY_OF_YEAR, 1)
                    startTime.set(Calendar.HOUR_OF_DAY, 0)
                    startTime.set(Calendar.MINUTE, 0)
                    startTime.set(Calendar.SECOND, 0)
                    val sortedEvent = MeetingUtil.sortScheduledMeetings(
                        eventResponse.events,
                        startTime,
                        endTime,
                        Enums.MediaCall.CallCategories.INSTANT
                    ).toSortedMap()
                    if (sortedEvent.isNotEmpty()) {
                        meetingsMap = sortedEvent
                        getInProgressMeetings(now, endTime)
                        onSaveFinishedCallback()
                        isLoadingMeetings.set(false)
                        return@subscribe
                    }
                }
                if (scheduledMeetings.isNotEmpty()) {
                    sortInProgressMeetings(now, endTime)
                    onSaveFinishedCallback()
                    isLoadingMeetings.set(false)
                    return@subscribe
                }
                apiSuccessLiveData.value = false
                meetingsMap = mapOf()
                isLoadingMeetings.set(false)
                onSaveFinishedCallback()
            }, {
                onSaveFinishedCallback()
                isLoadingMeetings.set(false)
            })
        )
        }

        synchronized (threadLock) {
            if (!isTodayMeetingsThreadStarted) {
                try {
                    thread.start()
                }
                catch (e: IllegalThreadStateException)
                {
                    LogUtil.e("Get calendar events thread already started.")
                }
                isTodayMeetingsThreadStarted = true
            }
        }
    }

    private fun getInProgressMeetings(
        now: Calendar,
        endTime: Calendar
    ) {
        activeMeetings = emptyMap()
        var i = 0
        mCompositeDisposable.add(
            Observable.fromIterable(meetingsMap.toList())
                .flatMapSingle {
                    mediaCallRepository.getActiveMediaCall(getEventId(it.second))
                }.subscribe { mediaCallResponse ->
                    if (mediaCallResponse.mediaCallMetaData != null) {
                        mediaCallResponse.mediaCallMetaData?.mediaCallMeetingId.let {
                            activeMeetings = activeMeetings + Pair(it!!, mediaCallResponse)
                        }
                    }
                    i++
                    if (i == meetingsMap.size) {
                        sortInProgressMeetings(now, endTime)
                    }
                }
        )
    }

    private fun getEventId(event: CalendarApiEventDetail): String {
        return if (event.videoConfUrl != null)
            event.videoConfUrl.substring(event.videoConfUrl.lastIndexOf('/') + 1)
        else if (event.location != null)
            event.location.substring(event.location.lastIndexOf('/') + 1)
        else ""
    }

    private fun sortInProgressMeetings(now: Calendar, endTime: Calendar) {
        var unfinishedMeetings: Map<Long, CalendarApiEventDetail> = mapOf()
        var upcomingMeetings: Map<Long, CalendarApiEventDetail> = mapOf()
        var inProgressMeetings: Map<Long, CalendarApiEventDetail> = mapOf()
        var futureMeetings: Map<Long, CalendarApiEventDetail> = mapOf()
        meetingsMap.forEach {

            if (activeMeetings.contains(getEventId(it.value))) {
                activeMeetings[getEventId(it.value)].let { mediaCallResponse ->
                    if (mediaCallResponse?.attendees != null) {
                        mediaCallResponse.attendees!!
                            .forEach { mediaCallAttendee ->
                                if (mediaCallAttendee.joinStatus == Enums.MediaCall.JoinStatuses.JOINED) {
                                    it.value.attendees?.firstOrNull { calendarAttendee ->
                                        calendarAttendee.userId != null && calendarAttendee.userId == mediaCallAttendee.uuid
                                    }.let { attendee ->
                                        attendee?.status = Enums.MediaCall.JoinStatuses.JOINED
                                    }
                                }
                            }
                    }
                }
                it.value.status = Enums.MediaCall.CallStatuses.ACTIVE
                inProgressMeetings = inProgressMeetings + Pair(it.key, it.value)
            } else if (it.value.endTime.toLong() >= now.timeInMillis || it.value.allDay == true) {
                it.value.status = Enums.MediaCall.CallStatuses.NOT_STARTED
                unfinishedMeetings = unfinishedMeetings + Pair(it.key, it.value)
            } else if (it.value.startTime.toLong() >= now.timeInMillis && it.value.startTime.toLong() < endTime.timeInMillis) {
                it.value.status = Enums.MediaCall.CallStatuses.NOT_STARTED
                upcomingMeetings = upcomingMeetings + Pair(it.key, it.value)
            }
        }

        if (scheduledMeetings.isNotEmpty()) {
            futureMeetings = futureMeetings + scheduledMeetings
        }
        if(paginationMeetings.isNotEmpty()){
            futureMeetings = futureMeetings + paginationMeetings
        }

        val meetings = inProgressMeetings + unfinishedMeetings + upcomingMeetings + futureMeetings
        val eventsArray = ArrayList<BaseListItem>()
        meetings.forEach {
            eventsArray.add(
                MeetingListItem(
                    it.value,
                    it.value.startTime,
                    it.value.name,
                    getEventId(it.value),
                    SCHEDULED
                )
            )
        }
        apiSuccessLiveData.value = true
        meetingsMutableLiveData.postValue(eventsArray)

        val countTopMeetings =
            (inProgressMeetings.size + unfinishedMeetings.size + upcomingMeetings.size)
        if (scrollToTop.value != countTopMeetings) {
            scrollToTop.postValue(countTopMeetings)
        }
    }

    private fun fetchMeetings(onSaveFinishedCallback: () -> Unit){
        val startTime = Calendar.getInstance()
        startTime.add(Calendar.MONTH, -2)
        startTime.set(Calendar.HOUR_OF_DAY, 0)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.SECOND, 0)

        val endTime = Calendar.getInstance()
        endTime.add(Calendar.MONTH, 1)
        endTime.set(Calendar.HOUR_OF_DAY, 23)
        endTime.set(Calendar.MINUTE, 59)
        endTime.set(Calendar.SECOND, 59)

        val date = SimpleDateFormat(CalendarOrchestration.DATE_FORMAT, Locale.getDefault())
        var startDate: String = date.format(startTime.time)
        startDate = StringBuffer(startDate).insert(startDate.length - 2, ":").toString()
        var endDate = date.format(endTime.time)
        endDate = StringBuffer(endDate).insert(endDate.length - 2, ":").toString()
        calendarRepository.fetchEvents(
            startDate,
            endDate,
            startTime,
            endTime,
            mCompositeDisposable
        ) {
            onSaveFinishedCallback()
        }
    }
}