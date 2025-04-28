package com.nextiva.nextivaapp.android.models.net.platform.voice

import com.google.gson.annotations.SerializedName

data class VoiceMessageParticipant(
    @SerializedName("email") var email: String?,
    @SerializedName("phoneNumber") var phoneNumber: String?,
    @SerializedName("teamUuid") var teamUuid: String?,
    @SerializedName("userUuid") var userUuid: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("contactId") var contactId: String?,
    @SerializedName("phoneLocation") var phoneLocation: String?,
    @SerializedName("contactType") var contactType: String?,
    @SerializedName("jobTitle") var jobTitle: String?)