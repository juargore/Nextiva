package com.nextiva.nextivaapp.android.models.net.platform.presence

import com.google.gson.annotations.SerializedName

data class ConnectPresenceDndSchedule(@SerializedName("userID") var userId: String,
                                      @SerializedName("corpAccountNumber") var corpAccountNumber: Int,
                                      @SerializedName("scheduleId") var scheduleId: String)