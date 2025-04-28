/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallDetail

data class StartRecordMeetingResponse(
	val callId: String? = null,
	val settings: Settings? = null,
	val notificationRequired: Boolean? = null,
	val mediaCallDetail: MediaCallDetail? = null,
	val attendees: List<AttendeesItem?>? = null,
	val timezone: String? = null,
	val invitesStatus: String? = null,
	val screenShareStatus: String? = null,
	val recordingToken: String? = null,
	val callType: String? = null,
	val videoStatus: String? = null,
	val lockStatus: String? = null,
	val callStatus: String? = null,
	val lastUpdate: Long? = null,
	val startTime: Long? = null,
	val corpAcctNum: Int? = null,
	val mediaCallMetaData: MediaCallMetaData? = null,
	val muteAllStatus: String? = null
)

