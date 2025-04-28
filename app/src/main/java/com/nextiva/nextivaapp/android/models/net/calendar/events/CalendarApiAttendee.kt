package com.nextiva.nextivaapp.android.models.net.calendar.events

data class CalendarApiAttendee(
        val email: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val userId: String? = null,
        val type: String? = null,
        val optional: Boolean? = null,
        val response: String? = null,
        val comment: String? = null,
        val responseToken: String? = null,
        var status: String? = null
)