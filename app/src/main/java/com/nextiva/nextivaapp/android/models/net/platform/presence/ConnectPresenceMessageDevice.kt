/*
 * Copyright (c) 2021 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.platform.presence

import com.google.gson.annotations.SerializedName

data class ConnectPresenceMessageDevice(
        @SerializedName("device_type") var deviceType: Int,
        @SerializedName("device_id") var deviceId: String
)
