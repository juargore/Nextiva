package com.nextiva.nextivaapp.android.models.net.platform.voicemail

import com.google.gson.annotations.SerializedName

data class VoicemailPatchData(@SerializedName("op") var operation: String,
                              @SerializedName("path") var path: String,
                              @SerializedName("value") var value: Boolean)