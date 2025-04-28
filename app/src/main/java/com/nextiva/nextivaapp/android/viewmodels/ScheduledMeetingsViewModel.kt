package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.MediaCall.CallCategories
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CalendarRepository
import com.nextiva.nextivaapp.android.meetings.MeetingUtil
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ScheduledMeetingsViewModel @Inject constructor(
    application: Application,
    private val calendarRepository: CalendarRepository
) : BaseViewModel(application) {

    var meetingsMutableLiveData: MutableLiveData<List<CalendarApiEventDetail>> = MutableLiveData()
    var progressIndicatorMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var isLoadingMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var lastEndTime: Calendar = Calendar.getInstance()


    fun getMeetings() {
        meetingsMutableLiveData.value = emptyList()
        progressIndicatorMutableLiveData.postValue(true)
        val startTime = Calendar.getInstance()
        val endTime = Calendar.getInstance()
        val date = SimpleDateFormat(Enums.CalendarOrchestration.DATE_FORMAT, Locale.getDefault())


        startTime.add(Calendar.MONTH, -6)
        var startDate: String = date.format(startTime.time)
        startDate = StringBuffer(startDate).insert(startDate.length - 2, ":").toString()

        var endDate = date.format(endTime.time)
        endDate = StringBuffer(endDate).insert(endDate.length - 2, ":").toString()

        val meetings = calendarRepository.getDbMeetings(0,endTime.timeInMillis - 1)

        if (meetings.isNotEmpty()) {
            val events = MeetingUtil.parseStringToCalendarApiEventMap(meetings, true)
            val eventsList = events.toSortedMap().toList().map { it.second }.reversed()
            getLastMeetingTime(eventsList)
            meetingsMutableLiveData.postValue(eventsList)
        } else {
            mCompositeDisposable.add(
                calendarRepository.getEvents(startDate, endDate).subscribe { eventResponse ->
                    if (!eventResponse.events.isNullOrEmpty()) {
                        meetingsMutableLiveData.postValue(
                            MeetingUtil.sortScheduledMeetings(
                                eventResponse.events,
                                startTime,
                                endTime,
                                CallCategories.SCHEDULED
                            ).toSortedMap().toList().reversed().map { it.second })
                    } else {
                        progressIndicatorMutableLiveData.postValue(false)
                    }
                }
            )
        }


    }

    fun getMoreMeetings() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = lastEndTime.timeInMillis
        val date = SimpleDateFormat(Enums.CalendarOrchestration.DATE_FORMAT, Locale.getDefault())

        var endDate = date.format(calendar.time)
        endDate = StringBuffer(endDate).insert(endDate.length - 2, ":").toString()

        calendar.add(Calendar.MONTH, -1)
        var startDate: String = date.format(calendar.time)
        startDate = StringBuffer(startDate).insert(startDate.length - 2, ":").toString()

        mCompositeDisposable.add(
            calendarRepository.getEvents(startDate, endDate).subscribe { eventResponse ->
                if (!eventResponse.events.isNullOrEmpty()) {
                    val sortedMap = MeetingUtil.sortScheduledMeetings(
                        eventResponse.events,
                        calendar,
                        lastEndTime,
                        CallCategories.SCHEDULED
                    ).toSortedMap().toList().reversed().map { it.second }

                    if (sortedMap.isNotEmpty()) {
                        getLastMeetingTime(sortedMap)
                        isLoadingMutableLiveData.postValue(false)
                        meetingsMutableLiveData.postValue(sortedMap)
                    } else {
                        lastEndTime.timeInMillis = calendar.timeInMillis
                    }
                }
            }
        )
    }

    private fun getLastMeetingTime(eventsList: List<CalendarApiEventDetail>) {
        lastEndTime = Calendar.getInstance()
        lastEndTime.timeInMillis = eventsList.last().startTime.toLong() - 1
    }
}