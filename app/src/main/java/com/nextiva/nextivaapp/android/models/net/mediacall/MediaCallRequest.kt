package com.nextiva.nextivaapp.android.models.net.mediacall

data class MediaCallRequest(
    var callType: String?,
    var corpAcctNum: String,
    var mediaCallAdvanceOption: MediaCallAdvanceOption?,
    var mediaCallMetaData: MediaCallMetaData?,
    var requestedAttendees: ArrayList<MediaCallAttendee>?,
    var title: String?
)
