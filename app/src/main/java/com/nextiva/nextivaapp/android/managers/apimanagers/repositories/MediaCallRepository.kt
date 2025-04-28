package com.nextiva.nextivaapp.android.managers.apimanagers.repositories

import com.nextiva.nextivaapp.android.meetings.models.net.events.MeetingsEventResponse
import com.nextiva.nextivaapp.android.meetings.models.net.media.MediaCallInfoResponse
import com.nextiva.nextivaapp.android.meetings.models.net.media.recordings.OngoingRecordMeetingResponse
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallRequest
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallResponse
import io.reactivex.Single

interface MediaCallRepository {

    fun startCall(mediaCallRequest: MediaCallRequest): Single<MediaCallResponse>

    fun assignCoHost(mediaCallId: String, attendeeId: String, attendeePatchAction: String): Single<MediaCallResponse>

    fun callInfo(mediaCallId: String): Single<MediaCallInfoResponse?>

    fun meetingEvent(mediaEventId: String): Single<MeetingsEventResponse?>

    fun checkApiHealth(): Single<Boolean>

    fun getActiveMediaCall(meetingId: String): Single<MediaCallResponse>

    fun setAttendeeMeetingAction(mediaCallId: String,attendeeId: String, attendeeAction: String): Single<MediaCallResponse>

    fun recordingsForMeetings(mediaCallId: String): Single<ArrayList<OngoingRecordMeetingResponse>?>
}