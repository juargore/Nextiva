/*
 * Copyright (c) 2024. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.models

import com.google.gson.annotations.SerializedName

data class IdentityVoice(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
