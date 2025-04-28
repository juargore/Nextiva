package com.nextiva.nextivaapp.android.models.net.platform.voicemail

import com.google.gson.annotations.SerializedName

//"direction": "INBOUND",
//"date": "2021-06-28",
//"answered": false,
//"call_start_time": "19:41:25",
//"contact_number": "+13152981901",
//"user_phone_number": null,
//"call_duration": 24,
//"voicemail_id": "f918cbad-78e1-4165-9eeb-4df8536f6786",
//"voicemail_duration": 13,
//"voicemail_transcript": null,
//"voicemail_transcript_rating": null,
//"voicemail_read": false,
//"recording_id": null,
//"recording_transcript": null,
//"recording_transcript_rating": null

data class VoicemailData(@SerializedName("direction") var direction: String? = "",
                         @SerializedName("date") var date: String? = "",
                         @SerializedName("answered") var answered: Boolean? = false,
                         @SerializedName("call_start_time") var callStartTime: String? = "",
                         @SerializedName("contact_number") var contactNumber: String? = "",
                         @SerializedName("user_phone_number") var userPhoneNumber: String? = "",
                         @SerializedName("call_duration") var callDuration: Int? = -1,
                         @SerializedName("voicemail_id") var voicemailId: String? = "",
                         @SerializedName("voicemail_duration") var voicemailDuration: Int? = -1,
                         @SerializedName("voicemail_transcript") var voicemailTranscript: String? = "",
                         @SerializedName("voicemail_transcript_rating") var voicemailTranscriptRating: String? = "",
                         @SerializedName("voicemail_read") var voicemailRead: Boolean? = false,
                         @SerializedName("recording_id") var recordingId: String? = "",
                         @SerializedName("recording_transcript") var recordingTranscript: String? = "",
                         @SerializedName("recording_transcript_rating") var recordingTranscriptRating: String? = "")