package com.nextiva.nextivaapp.android.models.net.platform.voice

import com.google.gson.annotations.SerializedName

data class VoiceMessagesData(@SerializedName("messages") var voiceMessageItems: ArrayList<VoiceMessage>?)