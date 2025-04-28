package com.nextiva.nextivaapp.android.models.net.platform.teams

import com.google.gson.annotations.SerializedName

data class TeamPhoneNumberResponse(@SerializedName("number") var number: String?,
@SerializedName("extension") var extension: String?,
@SerializedName("isPrimary") var isPrimary: Boolean?)