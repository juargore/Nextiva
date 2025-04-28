package com.nextiva.nextivaapp.android.models.net.platform.teams

import com.google.gson.annotations.SerializedName

data class TeamResponse(@SerializedName("id") var id: String?,
                        @SerializedName("legacyId") var legacyId: String?,
                        @SerializedName("name") var name: String?,
                        @SerializedName("isVoice") var isVoice: Boolean?,
                        @SerializedName("callGroupId") var callGroupId: String?,
                        @SerializedName("phoneNumbers") var phoneNumbers: ArrayList<TeamPhoneNumberResponse>?,
                        @SerializedName("memberCount") var memberCount: Int?,
                        @SerializedName("members") var members: ArrayList<TeamMemberResponse>?,
                        @SerializedName("smsEnabled") var smsEnabled: Boolean?)