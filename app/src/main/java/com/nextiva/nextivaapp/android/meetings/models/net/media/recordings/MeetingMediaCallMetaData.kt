/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

import com.google.gson.annotations.SerializedName

data class MeetingMediaCallMetaData(

    @field:SerializedName("meetingName")
	val mediaCallMeetingId: String? = null,

    @field:SerializedName("meetingName")
	val personalizedMeetingId: String? = null,

    @field:SerializedName("meetingName")
	val roomEventDetails: RoomEventDetails? = null,

    @field:SerializedName("meetingName")
	val meetingName: String? = null,

    @field:SerializedName("scheduledEventDetails")
	val scheduledEventDetails: ScheduledEventDetails? = null,

    @field:SerializedName("category")
	val category: String? = null
)