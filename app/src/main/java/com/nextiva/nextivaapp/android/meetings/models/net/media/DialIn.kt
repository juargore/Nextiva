/*
 * Copyright (c) 2022. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media

import com.google.gson.annotations.SerializedName

data class DialIn(
    @field:SerializedName("quickDialIn")
    val quickDialIn: String? = null,
)