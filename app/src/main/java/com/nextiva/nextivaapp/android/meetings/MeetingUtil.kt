/*
 * Copyright (c) 2022. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings

import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.MediaCall.CallCategories.INSTANT
import com.nextiva.nextivaapp.android.constants.Enums.MediaCall.CallCategories.SCHEDULED
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiAttendee
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventDetail
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.GsonUtil
import java.util.Calendar
import java.util.TimeZone
import java.util.regex.Pattern

/**
 * Created by Thaddeus Dannar on 9/20/22.
 */
class MeetingUtil {

    companion object {
        /**
         * Formats meeting id's according to designs.
         * @param meetingId only supports digits and must be at least 7 digits to format
         * @return a meetingId containing none digits other then spaces, or less than 7 characters will return the id unaltered.
         * Example: 0123 45 6789
         * */
        @JvmStatic
        fun formatMeetingId(meetingId: String): String {
            val cleanedId = meetingId.replace(" ", "")

            fun String.addSpaceAtIndex(index: Int) =
                StringBuilder(this).apply { insert(index, ' ') }.toString()

            return if (Pattern.matches("[0-9]+", cleanedId) && meetingId.length >= 7)
                cleanedId.addSpaceAtIndex(6).addSpaceAtIndex(4)
            else
                meetingId
        }

        //Duplicated from CallUtil to simplify moving MeetingUtil out of the project
        fun getFormattedNumber(phoneNumber: String?): String? {
                if(!phoneNumber.isNullOrEmpty()) {
                    var formattedNumber =
                        CallUtil.phoneNumberFormatNumberDefaultCountry(phoneNumber)
                    return if (CallUtil.isCountryCodeAdded(phoneNumber)) {
                        formattedNumber
                    } else {
                        "+1 $formattedNumber"
                    }
                }

            return phoneNumber
        }


        fun sortScheduledMeetings(
            events: Map<String, CalendarApiEventDetail>,
            startTime: Calendar,
            endTime: Calendar,
            meetingType: String,
        ): Map<Long, CalendarApiEventDetail> {
            var mapEvent: Map<Long, CalendarApiEventDetail> = mapOf()
            var singleMeetings = events.map { it.value }
                .filter {
                    (it.videoConfUrl != null && it.videoConfUrl.contains(Enums.CalendarOrchestration.NEXTIVA_URL_FILTER)) ||
                            (it.location != null && it.location.contains(Enums.CalendarOrchestration.NEXTIVA_URL_FILTER)) ||
                            (it.description != null && it.description.contains(Enums.CalendarOrchestration.NEXTIVA_URL_FILTER))
                }

            if (singleMeetings.isNotEmpty()) {
                singleMeetings = singleMeetings.filter {
                    userDeclined(it.attendees, it.userUuid)
                }

                for (meetingEvent in singleMeetings) {
                    if (meetingEvent.startTime.toLong() >= startTime.timeInMillis && meetingEvent.startTime.toLong() <= endTime.timeInMillis && meetingEvent.allDay == false) {
                        mapEvent = mapEvent + Pair(
                            meetingsAtSameTime(meetingEvent.startTime.toLong(), mapEvent),
                            meetingEvent
                        )
                    } else if (meetingEvent.allDay == true) {
                        val tm = TimeZone.getTimeZone(meetingEvent.timeZone).rawOffset
                        meetingEvent.startTime = (meetingEvent.startTime.toLong() + tm).toString()
                        meetingEvent.endTime = (meetingEvent.endTime.toLong() + tm).toString()
                        val sameDay = isSameDay(
                            meetingEvent.startTime,
                            startTime.get(Calendar.YEAR),
                            startTime.get(Calendar.DAY_OF_YEAR)
                        )
                        if ((meetingType == INSTANT && sameDay) || (meetingType == SCHEDULED && meetingEvent.endTime.toLong() <= endTime.timeInMillis && !sameDay)) {
                            mapEvent = mapEvent + Pair(
                                meetingsAtSameTime(meetingEvent.startTime.toLong(), mapEvent),
                                meetingEvent
                            )
                        }
                    }
                }
            }

            return mapEvent
        }

        private fun meetingsAtSameTime(
            meetingInMillis: Long,
            mapEvent: Map<Long, CalendarApiEventDetail>,
        ): Long {
            var millis = meetingInMillis
            while (mapEvent.contains(millis)) {
                millis += 1
            }
            return millis
        }

        fun getPeopleJoined(calendarEvent: CalendarApiEventDetail): Int {
            var count = 0
            calendarEvent.attendees?.forEach { it ->
                if (it.status == Enums.MediaCall.JoinStatuses.JOINED)
                    count++
            }
            return count
        }

        fun isSameDay(meetingTime: String?, year: Int, dayYear: Int): Boolean {
            if (meetingTime == null || !meetingTime.matches("^0-9+$".toRegex()))
                return false
            val meeting = Calendar.getInstance()
            meeting.timeInMillis = meetingTime.toLong()
            if (meeting.get(Calendar.YEAR) != year)
                return false
            if (meeting.get(Calendar.DAY_OF_YEAR) != dayYear)
                return false

            return true
        }

        private fun userDeclined(
            attendees: List<CalendarApiAttendee>?,
            userUuid: String?,
        ): Boolean {
            userUuid?.let { usrId ->
                attendees?.let {
                    val attendee =
                        it.firstOrNull { att -> att.userId != null && att.userId == usrId }
                    attendee?.let { a ->
                        return a.response != Enums.AttendeeResponseType.DECLINED
                    }
                }
            }
            return true
        }

        fun parseStringToCalendarApiEventMap(eventsStringList: List<String>, isOld: Boolean): Map<Long, CalendarApiEventDetail> {
            var sortedEvent: Map<Long, CalendarApiEventDetail> = mapOf()
            eventsStringList.forEach {
                val m = GsonUtil.getObject(CalendarApiEventDetail::class.java,it)
                m.status = if(isOld) Enums.MediaCall.CallStatuses.COMPLETED else Enums.MediaCall.CallStatuses.INACTIVE
                sortedEvent = sortedEvent + Pair(m.startTime.toLong(), m)
            }
            return sortedEvent
        }
    }
}