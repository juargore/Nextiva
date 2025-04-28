/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.media.recordings

import com.google.gson.annotations.SerializedName

data class RoomEventDetails(

    @field:SerializedName("invitees")
	val invitees: List<InviteesItem?>? = null,

    @field:SerializedName("host")
	val host: Host? = null,

    @field:SerializedName("roomId")
	val roomId: String? = null
)