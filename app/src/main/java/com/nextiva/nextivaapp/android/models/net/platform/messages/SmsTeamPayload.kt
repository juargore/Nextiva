package com.nextiva.nextivaapp.android.models.net.platform.messages

import com.google.gson.annotations.SerializedName

data class SmsTeamPayload(
    @SerializedName("teamName") var name: String?,
    @SerializedName("teamId") var id: String?,
    @SerializedName("teamPhoneNumber") var phoneNumber: String?,
    @SerializedName("legacyId") var legacyId: String?
)