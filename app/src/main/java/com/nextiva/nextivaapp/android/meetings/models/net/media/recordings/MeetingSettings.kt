/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

data class MeetingSettings(
    val hostUuids: List<String?>? = null,
    val allowBeforeHost: Boolean? = null,
    val guest: Guest? = null,
    val autoRecording: Boolean? = null,
    val privateMeeting: Boolean? = null,
    val participantUuids: Any? = null
)