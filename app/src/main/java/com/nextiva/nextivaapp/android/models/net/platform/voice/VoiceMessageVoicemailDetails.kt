package com.nextiva.nextivaapp.android.models.net.platform.voice

import com.google.gson.annotations.SerializedName

data class VoiceMessageVoicemailDetails(@SerializedName("duration") var duration: Int?,
                                        @SerializedName("transcript") var transcript: String?,
                                        @SerializedName("caller_id") var callerId: String,
                                        @SerializedName("transcript_rating") var transcriptRating: String?,
                                        @SerializedName("transcript_error") var transcriptError: Boolean?)