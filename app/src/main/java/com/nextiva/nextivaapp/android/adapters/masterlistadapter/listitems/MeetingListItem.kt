package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventDetail

class MeetingListItem() : BaseListItem() {

    lateinit var calendarEvent: CalendarApiEventDetail
    lateinit var startTime: String
    var title: String? = null
    var callId: String? = null
    lateinit var type: String

    constructor(calendarEvent: CalendarApiEventDetail, startTime: String, title: String?, callId: String, type: String) : this() {

        this.startTime = startTime
        this.calendarEvent = calendarEvent
        this.title = title
        this.callId = callId
        this.type = type
    }
}