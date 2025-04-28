/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

data class Settings(
    val general: General? = null,
    val guest: Guest? = null,
    val videoAndAudio: VideoAndAudio? = null
)