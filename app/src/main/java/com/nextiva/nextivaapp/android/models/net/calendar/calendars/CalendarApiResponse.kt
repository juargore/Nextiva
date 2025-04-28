package com.nextiva.nextivaapp.android.models.net.calendar.calendars

data class CalendarApiResponse(
        val ascendingSort: Boolean? = null,
        val calendars: Map<String, CalendarApiAdditionalProp>? = null,
        val pageNumber: Int? = null,
        val pageSize: Int? = null,
        val totalPages: Int? = null
)