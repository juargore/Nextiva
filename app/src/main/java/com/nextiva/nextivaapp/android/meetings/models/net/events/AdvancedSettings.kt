/*
 * Copyright (c) 2022. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.events

data class AdvancedSettings(
    val allowMeetingBeforeHost: Boolean? = null,
    val automaticRecordingWhenStarts: Boolean? = null,
    val allowStartBeforeSchedule: Boolean? = null,
)