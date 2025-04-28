package com.nextiva.nextivaapp.android.models.net.platform.presence

import com.google.gson.annotations.SerializedName

data class ConnectPresenceDevice(@SerializedName("device_type") var deviceType: Int)