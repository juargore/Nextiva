package com.nextiva.nextivaapp.android.models.net.platform.voicemail

import com.google.gson.annotations.SerializedName

data class VoicemailsResponseBody(@SerializedName("data") var voicemailData: ArrayList<VoicemailDetails>?)