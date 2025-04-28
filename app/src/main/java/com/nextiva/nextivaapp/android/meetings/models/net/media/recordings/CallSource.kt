/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

import com.google.gson.annotations.SerializedName

data class CallSource(

	@field:SerializedName("sourceId")
	val sourceId: String? = null,

	@field:SerializedName("sourceType")
	val sourceType: String? = null,

	@field:SerializedName("sourceName")
	val sourceName: String? = null
)