/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

import com.google.gson.annotations.SerializedName

data class OngoingRecordMeetingResponse(

    @field:SerializedName("callId")
	val callId: String? = null,

    @field:SerializedName("recordingSegments")
	val recordingSegments: List<RecordingSegmentsItem?>? = null,

    @field:SerializedName("name")
	val name: String? = null,

    @field:SerializedName("active")
	val active: Boolean? = null,

    @field:SerializedName("updatedOn")
	val updatedOn: String? = null,

    @field:SerializedName("recordingId")
	val recordingId: String? = null,

    @field:SerializedName("createdOn")
	val createdOn: String? = null,

    @field:SerializedName("corpAcctNum")
	val corpAcctNum: Int? = null,

    @field:SerializedName("users")
	val users: List<UsersItem?>? = null,

    @field:SerializedName("status")
	val status: String? = null
)