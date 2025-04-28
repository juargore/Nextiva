package com.nextiva.nextivaapp.android.models.net.platform.voice

import com.google.gson.annotations.SerializedName

data class VoiceMessageState(@SerializedName("messageId") var messageId: String?,
    @SerializedName("readStatus") var readStatus: String?,
    @SerializedName("deleted") var deleted: Boolean?)