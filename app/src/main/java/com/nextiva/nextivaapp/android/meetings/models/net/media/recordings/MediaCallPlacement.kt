/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

data class MediaCallPlacement(
	val screenDataUrl: String? = null,
	val screenViewingUrl: String? = null,
	val signalingUrl: String? = null,
	val audioFallbackUrl: String? = null,
	val screenSharingUrl: String? = null,
	val turnControlUrl: String? = null,
	val audioHostUrl: String? = null
)