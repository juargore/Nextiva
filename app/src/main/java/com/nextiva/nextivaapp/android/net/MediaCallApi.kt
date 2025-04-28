package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.meetings.models.net.events.MeetingsEventResponse
import com.nextiva.nextivaapp.android.meetings.models.net.media.MediaCallInfoResponse
import com.nextiva.nextivaapp.android.meetings.models.net.media.recordings.OngoingRecordMeetingResponse
import com.nextiva.nextivaapp.android.meetings.models.net.media.recordings.StartRecordMeetingResponse
import com.nextiva.nextivaapp.android.meetings.models.net.media.recordings.StopRecordsMeetingResponse
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallRequest
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallResponse
import com.squareup.okhttp.ResponseBody
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MediaCallApi {

    @Headers("Content-Type: application/vnd.nextiva.mediacallservice-v1.0+json")
    @POST("rest-api/mediacall/v1/chime/mediacalls")
    fun startCall(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Body jsonObject: MediaCallRequest
    ): Single<Response<MediaCallResponse>?>

    @PUT("/rest-api/mediacall/v1/chime/mediacalls/{mediacall-id}/attendees/{attendeeId}/{attendee-patch-action}")
    fun assignCoHost(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Path(value = "mediacall-id") mediaCallId: String,
            @Path(value = "attendeeId") attendeeId: String,
            @Path(value = "attendee-patch-action") attendeePatchAction: String
    ): Single<Response<MediaCallResponse>?>

    @GET("/rest-api/mediacall/v1/mediacalls/meta-data/meetings/{mediacall-id}/info")
    fun meetingInfo(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Path(value = "mediacall-id") mediaCallId: String
    ): Single<Response<MediaCallInfoResponse>?>

    @GET("/rest-api/meeting/v1/meetings/event/{mediaevent-id}")
    fun meetingEvent(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Path(value = "mediaevent-id") mediaEventId: String
    ): Single<Response<MeetingsEventResponse>?>

    @GET("/rest-api/meeting/v1/meetings/event/{mediaevent-id}")
    fun checkApiHealth(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path(value = "mediaevent-id") mediaEventId: String): Single<Response<ResponseBody>>

    @GET("/rest-api/mediacall/v1/chime/mediacalls/meetings/{meeting-id}")
    fun getActiveMediaCall(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path(value = "meeting-id") meetingId: String
    ): Single<Response<MediaCallResponse>?>


    @POST("rest-api/mediacall/v1/chime/mediacalls/{mediacall-id}/{attendeeId}/{attendee-action}")
    fun attendeeMeetingAction(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path(value = "mediacall-id") mediaCallId: String,
        @Path(value = "attendeeId") attendeeId: String,
        @Path(value = "attendee-action") attendeeAction: String
    ): Single<Response<MediaCallResponse>?>

    @POST("rest-api/mediacall/v1/chime/mediacalls/{mediacall-id}/recordings")
    fun startRecordingMeeting(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path(value = "mediacall-id") mediaCallId: String
    ): Single<Response<StartRecordMeetingResponse>?>

    @GET("/rest-api/mediacall/v1/chime/mediacalls/{mediacall-id}/recordings")
    fun getRecordingsForMeetings(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path(value = "mediacall-id") mediacallId: String
    ): Single<Response<ArrayList<OngoingRecordMeetingResponse>>?>

    @DELETE("/rest-api/mediacall/v1/chime/mediacalls/{mediacall-id}/recordings{recording-id}")
    fun stopRecordingMeeting(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Header("check-requester") checkRequester: Boolean,
        @Path(value = "mediacall-id") mediacallId: String,
        @Path(value = "recording-id") recordingId: String
    ): Single<Response<StopRecordsMeetingResponse>?>
}
