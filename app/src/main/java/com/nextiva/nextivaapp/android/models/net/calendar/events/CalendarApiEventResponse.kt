package com.nextiva.nextivaapp.android.models.net.calendar.events

data class CalendarApiEventResponse(
        val ascendingSort: Boolean? = null,
        val events: Map<String, CalendarApiEventDetail>? = null,
        val pageNumber: Int? = null,
        val pageSize: Int? = null,
        val total: Int? = null,
        val totalPages: Int? = null
)