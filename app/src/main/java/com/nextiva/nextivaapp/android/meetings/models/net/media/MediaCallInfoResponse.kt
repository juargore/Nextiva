/*
 * Copyright (c) 2022. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media

import com.google.gson.annotations.SerializedName

data class MediaCallInfoResponse(
    @field:SerializedName("meetingId")
    val meetingId: String? = null,
    @field:SerializedName("dialIn")
    val dialIn: DialIn? = null,
    @field:SerializedName("meetingUrl")
    val meetingUrl: String? = null,
)
