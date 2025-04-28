package com.nextiva.nextivaapp.android.models.net.calendar.events

data class CalendarApiRecurrence(
        val cancelledEvents: List<CalendarApiCancelledEvent>? = null,
        val interval: Int? = null,
        val pattern: String? = null,
        val recurrenceEndBy: String? = null,
        val repeatBySetPos: List<Int>? = null,
        val repeatCount: Int? = null,
        val repeatOn: List<CalendarApiRepeatOn>? = null,
        val repeatOnDaysOfMonth: List<Int>? = null,
        val repeatOnMonths: List<String>? = null,
        val repeatUntil: String? = null,
        val repeatWkst: String? = null
)