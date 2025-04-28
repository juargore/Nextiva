package com.nextiva.nextivaapp.android.models.net.mediacall

data class MediaCallResponse(
    var attendees: ArrayList<MediaCallAttendee>? = null,
    var callId: String? = null,
    var callSource: MediaCallSource? = null,
    var callStatus: String? = null,
    var callType: String? = null,
    var corpAcctNum: Int? = null,
    var endTime: String? = null, // "2020-12-13T02:43:44.621+00:00"
    var invitesStatus: String? = null,
    var lastUpdate: String? = null, // "2020-12-13T02:43:44.621+00:00",
    var lockStatus: String? = null,
    var mediaCallAdvanceOption: MediaCallAdvanceOption? = null,
    var mediaCallDetail: MediaCallDetail? = null,
    var mediaCallMetaData: MediaCallMetaData? = null,
    var mediaCallProvider: String? = null,
    var muteAllStatus: String? = null,
    var notificationRequired: Boolean? = null,
    var recordingToken: String? = null,
    var screenShareStatus: String? = null,
    var schemaVersion: String? = null,
    var startTime: String? = null, // "2020-12-13T02:43:44.621+00:00"
    var title: String? = null,
    var videoStatus: String? = null
)
