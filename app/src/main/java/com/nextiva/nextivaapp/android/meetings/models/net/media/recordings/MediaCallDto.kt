/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallDetail

data class MediaCallDto(

    @field:SerializedName("callId")
	val callId: String? = null,

    @field:SerializedName("settings")
	val settings: String? = null,

    @field:SerializedName("mediaCallProvider")
	val mediaCallProvider: String? = null,

    @field:SerializedName("notificationRequired")
	val notificationRequired: Boolean? = null,

    @field:SerializedName("mediaCallDetail")
	val mediaCallDetail: MediaCallDetail? = null,

    @field:SerializedName("attendees")
	val attendees: List<AttendeesItem?>? = null,

    @field:SerializedName("timezone")
	val timezone: String? = null,

    @field:SerializedName("recordingToken")
	val recordingToken: String? = null,

    @field:SerializedName("title")
	val title: String? = null,

    @field:SerializedName("callType")
	val callType: String? = null,

    @field:SerializedName("callSource")
	val callSource: CallSource? = null,

    @field:SerializedName("callStatus")
	val callStatus: String? = null,

    @field:SerializedName("lastUpdate")
	val lastUpdate: String? = null,

    @field:SerializedName("startTime")
	val startTime: String? = null,

    @field:SerializedName("endTime")
	val endTime: String? = null,

    @field:SerializedName("mediaCallAdvanceOption")
	val mediaCallAdvanceOption: String? = null,

    @field:SerializedName("corpAcctNum")
	val corpAcctNum: Int? = null,

    @field:SerializedName("mediaCallMetaData")
	val mediaCallMetaData: MediaCallMetaData? = null
)