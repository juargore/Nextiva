package com.nextiva.nextivaapp.android.models.net.calendar.events

data class CalendarApiCancelledEvent(
        val cancelDate: String,
        val cancelEvent: String,
        val description: String
)