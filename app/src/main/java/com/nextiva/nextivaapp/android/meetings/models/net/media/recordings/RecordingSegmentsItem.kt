/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

import com.google.gson.annotations.SerializedName

data class RecordingSegmentsItem(

	@field:SerializedName("duration")
	val duration: Any? = null,

	@field:SerializedName("fileName")
	val fileName: String? = null,

	@field:SerializedName("size")
	val size: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("attachmentId")
	val attachmentId: Any? = null,

	@field:SerializedName("updatedOn")
	val updatedOn: String? = null,

	@field:SerializedName("createdOn")
	val createdOn: String? = null,

	@field:SerializedName("recordingSegmentId")
	val recordingSegmentId: String? = null,

	@field:SerializedName("recordingInstance")
	val recordingInstance: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)