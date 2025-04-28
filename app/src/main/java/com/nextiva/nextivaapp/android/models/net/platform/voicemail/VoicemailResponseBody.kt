package com.nextiva.nextivaapp.android.models.net.platform.voicemail

import com.google.gson.annotations.SerializedName

data class VoicemailResponseBody(@SerializedName("data") var voicemailData: ArrayList<VoicemailData>?)