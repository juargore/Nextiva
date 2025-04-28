/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

data class MediaCallMetaData(
    val mediaCallMeetingId: String? = null,
    val meetingSettings: MeetingSettings? = null,
    val hostPin: String? = null,
    val createdBy: String? = null,
    val createdByUuid: String? = null,
    val dialInNumber: List<String?>? = null,
    val category: String? = null,
    val corpAcctNum: String? = null,
    val personalizedMeetingId: String? = null,
    val roomEventDetails: RoomEventDetails? = null,
)