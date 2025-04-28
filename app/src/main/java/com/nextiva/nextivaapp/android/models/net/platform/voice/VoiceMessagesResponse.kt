package com.nextiva.nextivaapp.android.models.net.platform.voice

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VoiceMessagesResponse(@SerializedName("totalCount") var totalCount: Int?,
                                   @SerializedName("first") var firstPage: String?,
                                   @SerializedName("prev") var previousPage: String?,
                                   @SerializedName("next") var nextPage: String?,
                                   @SerializedName("last") var lastPage: String?,
                                   @SerializedName("data") var data: ArrayList<VoiceMessagesData>?): Serializable