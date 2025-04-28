/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

import com.google.gson.annotations.SerializedName

data class UsersItem(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("downloadUrl")
	val downloadUrl: Any? = null,

	@field:SerializedName("uuid")
	val uuid: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)