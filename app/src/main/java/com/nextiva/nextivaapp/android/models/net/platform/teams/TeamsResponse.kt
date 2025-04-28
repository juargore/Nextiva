package com.nextiva.nextivaapp.android.models.net.platform.teams

import com.google.gson.annotations.SerializedName

data class TeamsResponse(@SerializedName("totalCount") var totalCount: Int?,
                            @SerializedName("data") var data: ArrayList<TeamResponse>)