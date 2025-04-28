/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

data class VideoAndAudio(
	val screenShareStatus: String? = null,
	val muteAllStatus: String? = null,
	val videoStatus: String? = null,
	val allowToMute: String? = null
)