package com.nextiva.nextivaapp.android.models.net.platform.voice

import com.google.gson.annotations.SerializedName

data class VoiceMessageCallDetails(
    @SerializedName("date") var date: String?,
    @SerializedName("call_start_time") var callStartTime: String?,
    @SerializedName("call_duration") var callDuration: Int?,
    @SerializedName("voicemail_id") var voicemailId: String?,
    @SerializedName("voicemail_read") var voicemailRead: Boolean?,
    @SerializedName("answered") var answered: Boolean?,
    @SerializedName("voicemail") var isVoicemail: Boolean?,
    @SerializedName("voicemail_transcript") var voicemailTranscript: String?,
    @SerializedName("voicemail_transcript_rating") var voicemailTranscriptRating: String?,
    @SerializedName("voicemail_transcript_error") var voicemailTranscriptError: Boolean?,
    @SerializedName("caller_id") var callerId: String?,
    @SerializedName("note") var note: String?)