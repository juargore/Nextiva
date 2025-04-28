package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddMemberRequest(
        @SerializedName("members") var members: List<String>,
        @SerializedName("memberDetails") var memberDetails: List<AddMemberDetails>,
        @SerializedName("sender") var sender: AddMemberDetails
): Serializable

data class AddMemberDetails(
        @SerializedName("id") var id: String,
        @SerializedName("firstName") var firstName: String?,
        @SerializedName("lastName") var lastName: String?,
        @SerializedName("email") var email: String?
): Serializable
