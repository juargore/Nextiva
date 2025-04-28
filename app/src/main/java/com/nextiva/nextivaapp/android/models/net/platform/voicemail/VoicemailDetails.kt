package com.nextiva.nextivaapp.android.models.net.platform.voicemail

import com.google.gson.annotations.SerializedName

data class VoicemailDetails(@SerializedName("voicemailId") var voicemailId: String?,
							@SerializedName("phoneNumber") var phoneNumber: String?,
							@SerializedName("caller_id") var callerId: String?,
							@SerializedName("timestamp") var timestamp: String?,
							@SerializedName("duration") var duration: Int?,
							@SerializedName("read") var read: Boolean?,
							@SerializedName("transcriptPreview") var transcriptPreview: String?,
							@SerializedName("transcriptText") var transcriptText: String?,
							@SerializedName("transcriptRating") var transcriptRating: String?,
							@SerializedName("transcriptError") var transcriptError: Boolean?,
							@SerializedName("fileType") var fileType: String?,
							@SerializedName("content") var content: String?)
