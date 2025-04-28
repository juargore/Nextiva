package com.nextiva.nextivaapp.android.models.net.platform.teams

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TeamMemberResponse(@SerializedName("id") var id: String?,
                              @SerializedName("name") var name: String?) : Serializable