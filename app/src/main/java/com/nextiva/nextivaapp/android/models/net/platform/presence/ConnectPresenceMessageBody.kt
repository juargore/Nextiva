/*
 * Copyright (c) 2021 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.platform.presence

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectPresenceMessageBody(
        @SerializedName("userId") var userId: String?,
        @SerializedName("message") var message: String?,
        @SerializedName("device") var device: ConnectPresenceMessageDevice
) : Serializable
