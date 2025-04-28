/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

import com.google.gson.annotations.SerializedName

data class ScheduledEventDetails(

    @field:SerializedName("eventId")
	val eventId: Int? = null,

    @field:SerializedName("description")
	val description: String? = null,

    @field:SerializedName("eventType")
	val eventType: String? = null,

    @field:SerializedName("endDateTime")
	val endDateTime: String? = null,

    @field:SerializedName("allDay")
	val allDay: Boolean? = null,

    @field:SerializedName("rrule")
	val rrule: String? = null,

    @field:SerializedName("startDateTime")
	val startDateTime: String? = null,

    @field:SerializedName("calendarId")
	val calendarId: Int? = null,

    @field:SerializedName("eventOwner")
	val eventOwner: EventOwner? = null,

    @field:SerializedName("invitees")
	val invitees: List<InviteesItem?>? = null,

    @field:SerializedName("recurringEvent")
	val recurringEvent: Boolean? = null,

    @field:SerializedName("name")
	val name: String? = null,

    @field:SerializedName("location")
	val location: String? = null,

    @field:SerializedName("status")
	val status: String? = null
)