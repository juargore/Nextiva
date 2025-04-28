package com.nextiva.nextivaapp.android.viewmodels

import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.amazonaws.services.chime.sdk.meetings.analytics.EventAnalyticsObserver
import com.amazonaws.services.chime.sdk.meetings.analytics.EventAttributes
import com.amazonaws.services.chime.sdk.meetings.analytics.EventName
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AttendeeInfo
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoFacade
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoObserver
import com.amazonaws.services.chime.sdk.meetings.audiovideo.PrimaryMeetingPromotionObserver
import com.amazonaws.services.chime.sdk.meetings.audiovideo.SignalUpdate
import com.amazonaws.services.chime.sdk.meetings.audiovideo.TranscriptEvent
import com.amazonaws.services.chime.sdk.meetings.audiovideo.VolumeUpdate
import com.amazonaws.services.chime.sdk.meetings.audiovideo.audio.activespeakerdetector.ActiveSpeakerObserver
import com.amazonaws.services.chime.sdk.meetings.audiovideo.audio.activespeakerpolicy.DefaultActiveSpeakerPolicy
import com.amazonaws.services.chime.sdk.meetings.audiovideo.contentshare.ContentShareObserver
import com.amazonaws.services.chime.sdk.meetings.audiovideo.contentshare.ContentShareStatus
import com.amazonaws.services.chime.sdk.meetings.audiovideo.metric.MetricsObserver
import com.amazonaws.services.chime.sdk.meetings.audiovideo.metric.ObservableMetric
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.RemoteVideoSource
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoRenderView
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileObserver
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileState
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.capture.CameraCaptureSource
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.capture.DefaultCameraCaptureSource
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.capture.DefaultSurfaceTextureCaptureSourceFactory
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.gl.DefaultEglCoreFactory
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.gl.EglCoreFactory
import com.amazonaws.services.chime.sdk.meetings.device.DeviceChangeObserver
import com.amazonaws.services.chime.sdk.meetings.device.MediaDevice
import com.amazonaws.services.chime.sdk.meetings.realtime.RealtimeObserver
import com.amazonaws.services.chime.sdk.meetings.realtime.TranscriptEventObserver
import com.amazonaws.services.chime.sdk.meetings.realtime.datamessage.DataMessage
import com.amazonaws.services.chime.sdk.meetings.realtime.datamessage.DataMessageObserver
import com.amazonaws.services.chime.sdk.meetings.session.Attendee
import com.amazonaws.services.chime.sdk.meetings.session.CreateAttendeeResponse
import com.amazonaws.services.chime.sdk.meetings.session.CreateMeetingResponse
import com.amazonaws.services.chime.sdk.meetings.session.MediaPlacement
import com.amazonaws.services.chime.sdk.meetings.session.Meeting
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSession
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionConfiguration
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionCredentials
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatus
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatusCode.AudioAuthenticationRejected
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatusCode.AudioCallAtCapacity
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatusCode.AudioCallEnded
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatusCode.AudioDisconnectAudio
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatusCode.AudioDisconnected
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatusCode.AudioInternalServerError
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatusCode.AudioJoinedFromAnotherDevice
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatusCode.AudioServiceUnavailable
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatusCode.VideoServiceFailed
import com.amazonaws.services.chime.sdk.meetings.utils.DefaultModality
import com.amazonaws.services.chime.sdk.meetings.utils.ModalityType
import com.amazonaws.services.chime.sdk.meetings.utils.logger.ConsoleLogger
import com.amazonaws.services.chime.sdk.meetings.utils.logger.LogLevel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.AssignCoHost
import com.nextiva.nextivaapp.android.constants.Enums.MediaCall.AttendeeTypes
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.MediaCallRepository
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.meetings.MeetingUtil
import com.nextiva.nextivaapp.android.meetings.RecordingTimer
import com.nextiva.nextivaapp.android.meetings.data.CoHostItem
import com.nextiva.nextivaapp.android.meetings.models.net.events.AttendeesItem
import com.nextiva.nextivaapp.android.meetings.models.net.events.MeetingsEventResponse
import com.nextiva.nextivaapp.android.meetings.models.net.media.MediaCallInfoResponse
import com.nextiva.nextivaapp.android.meetings.models.net.media.recordings.OngoingRecordMeetingResponse
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventDetail
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallAttendee
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallMetaData
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallPlacement
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallRequest
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallResponse
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isEmulator
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import java.util.Locale
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask

@HiltViewModel
class MeetingSessionViewModel @Inject constructor(
    application: Application,
    val mediaCallRepository: MediaCallRepository,
    var callManager: CallManager,
    val mDbManager: DbManager,
    val logManager: LogManager,
    val sessionManager: SessionManager
) : BaseViewModel(application), AudioVideoObserver,
    RealtimeObserver, VideoTileObserver,
    MetricsObserver, ActiveSpeakerObserver, DeviceChangeObserver, DataMessageObserver,
    ContentShareObserver, EventAnalyticsObserver, TranscriptEventObserver,
    PrimaryMeetingPromotionObserver {

    private val TAG = "MeetingSessionViewModel"
    private val TIMER_DELAY: Long = 1000
    private val TIMER_PERIOD: Long = 1000
    private val MINUTE_IN_SECONDS: Long = 60
    private val HOUR_IN_MINUTES: Long = 60
    val RECORDING_ATTENDEE_BASE_STRING = "recording"

    // Append to attendee name if it's for content share
    private val CONTENT_NAME_SUFFIX = "<<Content>>"

    private val DATA_MESSAGE_TOPIC = "chat"


    var startCallSuccessLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var mediaInfoLiveData: MutableLiveData<MediaCallInfoResponse> = MutableLiveData()
    var meetingEventInfoLiveData: MutableLiveData<MeetingsEventResponse> = MutableLiveData()
    var mediaCallResponse: MutableLiveData<MediaCallResponse> = MutableLiveData()
    var assignCoHostLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var goToActiveMeetingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var endMeetingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var meetingAudioModeLiveData: MutableLiveData<Int> = MutableLiveData()
    var meetingInfoButtonPressedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var hideMeetingInfoIfShowingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var recordingTimerLiveData: MutableLiveData<String> = MutableLiveData()
    var availableRemoteVideoSourceObserver: MutableLiveData<List<RemoteVideoSource>> =
        MutableLiveData()
    var unAvailableRemoteVideoSourceObserver: MutableLiveData<List<RemoteVideoSource>> =
        MutableLiveData()
    var videoStatusChangedObserver: MutableLiveData<MeetingSessionStatus> = MutableLiveData()
    var attendeesJoinedObserver: MutableLiveData<AttendeeInfo> = MutableLiveData()
    var attendeesDroppedObserver: MutableLiveData<Array<AttendeeInfo>> = MutableLiveData()
    var attendeesLeftObserver: MutableLiveData<Array<AttendeeInfo>> = MutableLiveData()
    var videoTileRemovedObserver: MutableLiveData<VideoTileState> = MutableLiveData()

    var recordingStateInfoLiveData: MutableLiveData<ArrayList<OngoingRecordMeetingResponse>> = MutableLiveData()
    var meetingDurationTimer: Timer = Timer()
    var isRecording: Boolean = false

    var recordingTimerObserver: MutableLiveData<RecordingTimer> = MutableLiveData()
    var recordingTimerObj: RecordingTimer = RecordingTimer()

    lateinit var calendarEvent: CalendarApiEventDetail
    lateinit var remoteVideoView: VideoRenderView

    var meetingRunTimeLiveData: MutableLiveData<Long> = MutableLiveData()
    var waitingForHost: MutableLiveData<Boolean> = MutableLiveData()
    var mediaCallMeetingId: MutableLiveData<String> = MutableLiveData()

    val logger = ConsoleLogger(LogLevel.DEBUG)

    private lateinit var meetingSession: MeetingSession
    private var coHostsAssigned: String = ""
    private var teammates: List<String> = emptyList()

    private var callId: String = ""


    fun setMeetingSession(meetingSession: MeetingSession) {
        this.meetingSession = meetingSession
    }

    fun getMeetingSession(): MeetingSession {
        return meetingSession
    }

    val credentials: MeetingSessionCredentials
        get() = meetingSession.configuration.credentials

    val configuration: MeetingSessionConfiguration
        get() = meetingSession.configuration

    val audioVideo: AudioVideoFacade
        get() = meetingSession.audioVideo

    val eglCoreFactory: EglCoreFactory = DefaultEglCoreFactory()


    val surfaceTextureCaptureSourceFactory = DefaultSurfaceTextureCaptureSourceFactory(
        logger,
        eglCoreFactory
    )

    var cameraCaptureSource: CameraCaptureSource = DefaultCameraCaptureSource(
        application,
        logger,
        surfaceTextureCaptureSourceFactory
    )

    var meetingTitle = ""
    private var meetingId = ""
    var meetingEventId = ""

    var isMuted = false
    var isCameraOn = false //Set default camera state here
    var isCameraPermissionNotGranted: Boolean? = null
    var isUsingCameraCaptureSource = true
    var isLocalVideoStarted = false
    var wasLocalVideoStarted = false
    var isUsingGpuVideoProcessor = false
    var isUsingCpuVideoProcessor = false
    var isUsingBackgroundBlur = false
    var isUsingBackgroundReplacement = false
    var isInstantMeeting = false
    var waitingStartTime = ""
    var userFullName = ""
    var userEmail = ""

    lateinit var userAttendeesItem: AttendeesItem
    private var joinedAttendees: ArrayList<AttendeeInfo> = ArrayList()

    init {
        getTeammates()
    }

    fun createMediaCallRequest(
        @AttendeeTypes.AttendeeType attendeeType: String,
        metaCallMetaData: MediaCallMetaData,
        @Enums.MediaCall.CallTypes.CallType callType: String,
        title: String,
        requestedAttendees: ArrayList<MediaCallAttendee>?,
    ): MediaCallRequest {


        logManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start)
        var arrayListAttendee = ArrayList<MediaCallAttendee>()
        if (requestedAttendees != null && requestedAttendees.size > 0)
            arrayListAttendee = requestedAttendees
        else
            arrayListAttendee.add(MediaCallAttendee(
                null,
                userEmail,
                null,
                null,
                userFullName,
                attendeeType,
                sessionManager.userInfo?.comNextivaUseruuid.toString()))

        //REMOVE AFTER INITIAL TESTING USED TO CONFIRM THE ATTENDEE LIST
        logManager.logToFile(Enums.Logging.STATE_INFO, GsonUtil.getJSON(arrayListAttendee))


        logManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_success)
        return MediaCallRequest(
            callType,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            null,
            metaCallMetaData,
            arrayListAttendee,
            title
        )
    }

    fun startMediaCall(
        @AttendeeTypes.AttendeeType attendeeType: String,
        metaCallMetaData: MediaCallMetaData,
        @Enums.MediaCall.CallTypes.CallType callType: String,
        title: String,
        requestedAttendees: ArrayList<MediaCallAttendee>?,
    ) {
        val mediaCallRequest = createMediaCallRequest(
            attendeeType,
            metaCallMetaData,
            callType,
            title,
            requestedAttendees
        )

        mCompositeDisposable.add(
            mediaCallRepository.startCall(mediaCallRequest)
                .subscribe { mediaCall ->
                    startCallSuccessLiveData.value =
                        (mediaCall.callId != null) //Probably won't need this
                    mediaCallResponse.postValue(mediaCall)
                    mediaCall.mediaCallMetaData?.mediaCallMeetingId.let {
                        waitingForHost.postValue(false)
                        mediaCallMeetingId.postValue(it)
                    }

                    callId = mediaCall.callId ?: ""
                    //meetingSession.configuration.credentials = MeetingSessionCredentials(mediaCall.attendees?.get(0)?.attendeeId.toString(), mediaCall.attendees?.get(0)?.uuid.toString(), mediaCall.attendees?.get(0)?.joinToken.toString())

                })

    }

    private fun processMeetingEventInfoResponse(meetingsEventResponse: MeetingsEventResponse?) {
        val requestedAttendees = ArrayList<MediaCallAttendee>()
        if (meetingsEventResponse != null) {
            if (meetingsEventResponse.attendees != null)
                for (attendee in meetingsEventResponse.attendees) {
                    if (attendee != null && attendee.userId.equals(calendarEvent.userUuid)) {
                        userAttendeesItem = attendee
                        requestedAttendees.add(MediaCallAttendee(
                            attendee.userId,
                            attendee.email,
                            attendee.status.toString(),
                            attendee.responseToken,
                            "${attendee.firstName} ${attendee.lastName}",
                            attendee.type,
                            attendee.userId))
/*
TEMPORARY to debug meeting sessions, will be removed after multi-attendee story */
                        LogUtil.d(TAG, "Actual User attendee user id: " + attendee.userId)
                        LogUtil.d(TAG, "Actual User attendee email: " + attendee.email)
                        LogUtil.d(TAG, "Actual User attendee status: " + attendee.status)
                        LogUtil.d(TAG,
                            "Actual User attendee responseToken: " + attendee.responseToken)
                        LogUtil.d(TAG, "Actual User attendee type: " + attendee.type)
                        break
                    }
/*
TEMPORARY to debug meeting sessions, will be removed after multi-attendee story*/
                    else {
                        if (attendee != null) {
                            LogUtil.d(TAG, "User attendee user id: " + attendee.userId)
                            LogUtil.d(TAG, "User attendee email: " + attendee.email)
                            LogUtil.d(TAG, "User attendee status: " + attendee.status)
                            LogUtil.d(TAG, "User attendee responseToken: " + attendee.responseToken)
                            LogUtil.d(TAG, "User attendee type: " + attendee.type)

                        }
                    }

                }
            val dialInPhone: List<String>? =
                if (meetingsEventResponse.dialInPhone != null) listOf(meetingsEventResponse.dialInPhone) else null
            val mediaCallMetaData = MediaCallMetaData(meetingsEventResponse.category,
                dialInPhone,
                meetingsEventResponse.meetingPin,
                meetingsEventResponse.mediaCallMeetingId,
                null)
            meetingsEventResponse.name?.let {
                userAttendeesItem.type?.let { it1 ->
                    if (it1 == AttendeeTypes.HOST || meetingsEventResponse.advancedSettings?.allowMeetingBeforeHost == true)
                        startMediaCall(
                            it1,
                            mediaCallMetaData,
                            Enums.MediaCall.CallTypes.REGULAR,
                            it,
                            requestedAttendees
                        )
                    else
                        meetingsEventResponse.mediaCallMeetingId.let {
                            waitingForHost.postValue(true)
                            mediaCallMeetingId.postValue(it)
                        }
                }
            }
        }
    }

    fun setMeetingId(mediaMeetingId: String) {
        meetingId = mediaMeetingId
    }

    fun getMeetingId(): String {
        return meetingId
    }

    fun getFormattedMeetingId(): String {
        if (meetingId.isNotEmpty())
            return MeetingUtil.formatMeetingId(meetingId)

        var callResponse: MediaCallResponse? = mediaCallResponse.value

        if (callResponse != null) {
            return MeetingUtil.formatMeetingId(callResponse.mediaCallMetaData?.mediaCallMeetingId
                ?: "")
        }

        return ""
    }

    fun getPhoneNumbers(): ArrayList<String>? {
        var callResponse: MediaCallResponse? = mediaCallResponse.value
        return arrayListOf(callResponse?.mediaCallMetaData?.dialInNumber.toString())
    }

    fun isHost(): Boolean {
        return this::userAttendeesItem.isInitialized && userAttendeesItem.type?.equals(AttendeeTypes.HOST) ?: false
    }

    fun isOnlyHost(): Boolean {
        var count = 0
        if (mediaCallResponse.value != null) {
            if (mediaCallResponse.value?.attendees != null) {
                for (attende in mediaCallResponse.value?.attendees!!) {
                    if ((attende.type.equals(AttendeeTypes.HOST) || attende.type.equals(
                            AttendeeTypes.MODERATOR)) && !attende.uuid?.equals(
                            userAttendeesItem.userId)!!
                    )
                        count++
                }
            }
        }

        return count == 0
    }

    fun getPossibleCoHost(): ArrayList<CoHostItem> {
        val attendees = ArrayList<CoHostItem>()
        if (mediaCallResponse.value?.attendees != null)
            for (attendee in mediaCallResponse.value?.attendees!!)
                if (attendee.name != null && attendee.attendeeId != null &&
                    attendee.type != null && attendee.uuid != null &&
                    (attendee.type?.equals(AttendeeTypes.MODERATOR)!! || attendee.type?.equals(
                        AttendeeTypes.REGULAR)!!) &&
                    (teammates.isNotEmpty() && teammates.contains(attendee.uuid))
                )
                    attendees.add(CoHostItem(
                        name = attendee.name,
                        type = attendee.type,
                        attendeeId = attendee.attendeeId,
                        wasModified = false
                    ))

        attendees.sortBy { it.name }
        return attendees
    }

    fun assignCoHost(coHosts: List<CoHostItem>) {
        coHostsAssigned = ""
        if (mediaCallResponse.value != null && mediaCallResponse.value?.callId != null) {
            val modifiedCoHost = coHosts.filter { it.wasModified }
            if (modifiedCoHost.isNotEmpty()) {
                var i = 0
                mCompositeDisposable.add(
                    Observable.fromIterable(modifiedCoHost)
                        .concatMapSingle {
                            mediaCallRepository.assignCoHost(
                                callId,
                                it.attendeeId ?: "",
                                it.type ?: ""
                            )
                        }
                        .subscribe { mediaCall ->
                            i++
                            if (mediaCall.callId != null && i == modifiedCoHost.size) {
                                mediaCallResponse.value = mediaCall
                                setCoHostsAssigned(modifiedCoHost)
                                assignCoHostLiveData.postValue(true)
                            } else if (mediaCall.callId == null) {
                                assignCoHostLiveData.postValue(false)
                            }
                        }
                )
            } else {
                assignCoHostLiveData.value = true
            }
        } else {
            assignCoHostLiveData.value = false
        }
    }


    fun createSessionConfiguration(mediaCall: MediaCallResponse?): MeetingSessionConfiguration? {
        var mediaPlacement = MediaPlacement("", "", "", "", "")
        var meeting = Meeting("", mediaPlacement, "", "")
        var attendee = Attendee("", "", "")

        mediaCall?.let {
            it.mediaCallDetail?.mediaCallPlacement?.let { placement ->
                mediaPlacement = MediaPlacement(
                    placement.audioFallbackUrl ?: "",
                    placement.audioHostUrl ?: "",
                    placement.signalingUrl ?: "",
                    placement.turnControlUrl ?: "",
                    ""
                )
            }
            meeting = Meeting(
                it.mediaCallDetail?.externalMeetingId ?: "",
                mediaPlacement,
                it.mediaCallDetail?.mediaRegion ?: "",
                it.callId ?: ""
            )

            if (!mediaCall.attendees.isNullOrEmpty())
                for (mediaAttendee in mediaCall.attendees!!) {
                    if (mediaAttendee.email == userEmail) {
                        LogUtil.d("MEETING",
                            "MEETING ATTENDEE CREATE FIRST: ${GsonUtil.getJSON(mediaAttendee)}")
                        attendee = Attendee(
                            mediaAttendee.attendeeId ?: "", mediaAttendee.uuid
                                ?: "", mediaAttendee.joinToken ?: ""
                        )
                        break
                    }

                }

        }

        return try {
            MeetingSessionConfiguration(
                CreateMeetingResponse(meeting),
                CreateAttendeeResponse(attendee),
                ::urlRewriter
            )
        } catch (exception: Exception) {
            FirebaseCrashlytics.getInstance().recordException(exception)
            null
        }
    }

    private fun urlRewriter(url: String): String {
        // You can change urls by url.replace("example.com", "my.example.com")
        return url
    }

    fun getMeetingInfo() {
        if (meetingId.isNotEmpty())
            requestMeetingInfo(meetingId)
    }

    fun getMeetingEventInfo() {
        if (meetingEventId.isNotEmpty())
            requestMeetingEventInfo(meetingEventId)
    }

    private fun getQuickDialNumber(): String {
        return if (mediaInfoLiveData.value == null &&
            mediaInfoLiveData.value?.dialIn == null &&
            mediaInfoLiveData.value?.dialIn == null &&
            !mediaInfoLiveData.value?.dialIn?.quickDialIn.isNullOrEmpty()
        )
            mediaInfoLiveData.value?.dialIn?.quickDialIn.toString()
        else
            if (meetingEventInfoLiveData.value != null)
                meetingEventInfoLiveData.value!!.dialInPhone + "," + meetingId
            else ""

    }

    fun getMeetingDialNumber(): String? {
        return MeetingUtil.getFormattedNumber(if (meetingEventInfoLiveData.value?.dialInPhone != null) meetingEventInfoLiveData.value!!.dialInPhone else "")
    }

    private fun getMediaCallPlacement(): MediaCallPlacement? {
        val callResponse: MediaCallResponse? = mediaCallResponse.value
        if (callResponse?.mediaCallDetail != null) {
            return callResponse.mediaCallDetail?.mediaCallPlacement
        }
        return null
    }

    fun getMeetingEndpointUrl(): String? {
        val callResponse: MediaCallResponse? = mediaCallResponse.value
        if (getMediaCallPlacement() != null) {
            return callResponse?.mediaCallDetail?.mediaCallPlacement?.signalingUrl
        }
        return ""
    }

    fun dialMeeting(
        activity: Activity,
        @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
    ) {
        callManager.makeCall(activity, analyticsScreenName, ParticipantInfo(numberToCall = getQuickDialNumber()), mCompositeDisposable)
    }

    fun getCoHostsAssigned(): String {
        return coHostsAssigned
    }


    fun clearCoHostsAssigned() {
        coHostsAssigned = ""
    }

    fun meetingInfoButtonPressed() {
        meetingInfoButtonPressedLiveData.postValue(true)
    }

    fun hideMeetingInfoIfShowing() {
        hideMeetingInfoIfShowingLiveData.postValue(true)
    }

    fun endMeeting() {
        if (!isEmulator()) {
            meetingSession.audioVideo.stop()
            meetingSession.audioVideo.stopLocalVideo()
            meetingSession.audioVideo.stopRemoteVideo()
        }
        endMeetingLiveData.postValue(true)

    }

    fun joinMeetingUrl(): String {
        if (mediaInfoLiveData.value != null)
            return mediaInfoLiveData.value!!.meetingUrl.toString()

        return ""
    }

    //DESIGNS have TOll Free numbers but there are no Tool Free numbers on the API yet
//ADD TOLL FREE NUMBER HERE
    fun getTollFreeNumber(): String {
        return ""
    }

    fun getTimerString(seconds: Long): String {

        if (seconds < (MINUTE_IN_SECONDS * HOUR_IN_MINUTES))
            return String.format(Locale.getDefault(),
                "%02d:%02d",
                (seconds % (MINUTE_IN_SECONDS * HOUR_IN_MINUTES)) / HOUR_IN_MINUTES,
                seconds % MINUTE_IN_SECONDS)

        return String.format(Locale.getDefault(),
            "%02d:%02d:%02d",
            seconds / 3600,
            (seconds % (MINUTE_IN_SECONDS * HOUR_IN_MINUTES)) / HOUR_IN_MINUTES,
            seconds % MINUTE_IN_SECONDS)

    }

    fun stopTimer() {
        meetingDurationTimer.cancel()
        meetingRunTimeLiveData.postValue(0)
    }

    fun startMeetingRunTime() {
        if (meetingRunTimeLiveData.value == null)
            meetingRunTimeLiveData.value = 0

        meetingDurationTimer = Timer()

        var presentTime: Long
        meetingDurationTimer.scheduleAtFixedRate(timerTask {
            presentTime = if (meetingRunTimeLiveData.value != null)
                meetingRunTimeLiveData.value!! + 1
            else
                1

            meetingRunTimeLiveData.postValue(presentTime)
        }, TIMER_DELAY, TIMER_PERIOD)

    }

    fun joinMeeting() {
/*
TEMPORARY to debug meeting sessions, will be removed after multi-attendee story
*/
        LogUtil.d("meetingSession attendeeId: ${meetingSession.configuration.credentials.attendeeId}")
        LogUtil.d("meetingSession externalUserId: ${meetingSession.configuration.credentials.externalUserId}")
        LogUtil.d("meetingSession joinToken: ${meetingSession.configuration.credentials.joinToken}")
        LogUtil.d("meetingSession meetingId: ${meetingSession.configuration.meetingId}")
        LogUtil.d("meetingSession audioHostURL: ${meetingSession.configuration.urls.audioHostURL}")
        LogUtil.d("meetingSession audioFallbackURL: ${meetingSession.configuration.urls.audioFallbackURL}")
        LogUtil.d("meetingSession ingestionURL: ${meetingSession.configuration.urls.ingestionURL}")
        LogUtil.d("meetingSession signalingURL: ${meetingSession.configuration.urls.signalingURL}")
        LogUtil.d("meetingSession turnControlURL: ${meetingSession.configuration.urls.turnControlURL}")
        LogUtil.d("meetingSession urlRewriter: ${meetingSession.configuration.urls.urlRewriter}")
        LogUtil.d("meetingSession externalMeetingId: ${meetingSession.configuration.externalMeetingId}")

        attachMeetingObservers()
        if (!isEmulator() && meetingSession.configuration.meetingId.isNotEmpty()) {
            meetingSession.audioVideo.start()
            meetingSession.audioVideo.startRemoteVideo()
        }
    }

    private fun attachMeetingObservers() {
        if (!isEmulator()) {
            meetingSession.audioVideo.addAudioVideoObserver(this)
            meetingSession.audioVideo.addVideoTileObserver(this)
            meetingSession.audioVideo.addContentShareObserver(this)
            meetingSession.audioVideo.addMetricsObserver(this)
            meetingSession.audioVideo.addDeviceChangeObserver(this)
            meetingSession.audioVideo.addEventAnalyticsObserver(this)
            meetingSession.audioVideo.addRealtimeObserver(this)
            meetingSession.audioVideo.addRealtimeTranscriptEventObserver(this)
            meetingSession.audioVideo.addRealtimeDataMessageObserver(DATA_MESSAGE_TOPIC, this)
            meetingSession.audioVideo.addActiveSpeakerObserver(DefaultActiveSpeakerPolicy(), this)
        }
    }


    fun getAttendeeName(attendeeId: String): String {
        return getAttendeeName(attendeeId, "")
    }

    fun getAttendeeName(attendeeId: String, externalUserId: String): String {
        logManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start)
        if ((attendeeId.isEmpty() && externalUserId.isEmpty())) {
            return "<UNKNOWN>"
        }

        if (joinedAttendees != null &&
            joinedAttendees.isNotEmpty()
        ) {
            for (attendeeInfo in joinedAttendees) {
                if (attendeeInfo != null &&
                    attendeeId == attendeeInfo.attendeeId
                ) {
                    for (attendee in meetingEventInfoLiveData.value?.attendees!!) {
                        if (attendee != null &&
                            attendeeInfo.externalUserId == attendee.email
                        ) {
                            return attendee.firstName + " " + attendee.lastName
                        }
                    }
                }
            }
        }


        if (meetingEventInfoLiveData.value != null &&
            meetingEventInfoLiveData.value!!.attendees != null &&
            meetingEventInfoLiveData.value!!.attendees!!.isNotEmpty()
        ) {
            for (attendee in meetingEventInfoLiveData.value?.attendees!!) {
                if (attendee != null &&
                    attendeeId == attendee.userId
                ) {
                    return attendee.firstName + " " + attendee.lastName
                }
            }
        }

        if (mediaCallResponse.value != null
            && mediaCallResponse.value?.attendees != null
        ) {
            for (attendee in mediaCallResponse.value?.attendees!!) {
                if (attendeeId == attendee.attendeeId) {
                    return attendee.name.toString()
                }
            }
        }

        val attendeeName =
            if (!externalUserId.isEmpty() &&
                externalUserId.contains('#')
            )
                externalUserId.split('#')[1]
            else
                externalUserId

        if (!attendeeName.isEmpty())
            return if (DefaultModality(attendeeId).hasModality(ModalityType.Content)) {
                "$attendeeName $CONTENT_NAME_SUFFIX"
            } else {
                attendeeName
            }

        return ""


    }

    fun getMediaCallMeetingId(): String? {
        return mediaCallResponse.value?.mediaCallDetail?.externalMeetingId
    }

// --------------------------------------------------------------------------------------------
// region PRIVATE FUNCTIONS
// --------------------------------------------------------------------------------------------

    private fun requestMeetingInfo(meetingId: String) {

        mCompositeDisposable.add(
            mediaCallRepository.callInfo(meetingId)
                .subscribe { mediaCallInfo ->
                    mediaInfoLiveData.value = mediaCallInfo
                })

    }

    private fun requestMeetingEventInfo(eventId: String) {

        mCompositeDisposable.add(
            mediaCallRepository.meetingEvent(eventId)
                .subscribe { meetingEventInfo ->
                    meetingEventInfoLiveData.value = meetingEventInfo
                    processMeetingEventInfoResponse(meetingEventInfo)
                })

    }

    private fun getTeammates() {
        Thread {
            teammates = mDbManager.teammateContactIds ?: emptyList()
        }.start()
    }

    private fun setCoHostsAssigned(coHosts: List<CoHostItem>) {
        coHostsAssigned = ""
        coHosts.forEach {
            if (it.type != null && it.type?.equals(AttendeeTypes.MODERATOR) == true)
                coHostsAssigned += "${it.name!!}, "
        }
        coHostsAssigned = if (coHostsAssigned.isNotEmpty()) coHostsAssigned.substring(0,
            coHostsAssigned.length - 2) else AssignCoHost.NO_COHOST
    }

    // --------------------------------------------------------------------------------------------
    // endregion PRIVATE FUNCTIONS
    // --------------------------------------------------------------------------------------------


    fun isMeetingActive(mediaCallMeetingId: String) {
        mCompositeDisposable.add(
            mediaCallRepository.getActiveMediaCall(mediaCallMeetingId)
                .subscribe { mediaCall ->
                    if (mediaCall.callId != null) {
                        mediaCallResponse.postValue(mediaCall)
                        waitingForHost.postValue(false)
                    } else
                        waitingForHost.postValue(true)
                })
    }

    fun droppedMeeting() {
        var attendeeId = ""
        mediaCallResponse.value?.let {
            if (this::userAttendeesItem.isInitialized)
                attendeeId =
                    it.attendees?.firstOrNull { attendee -> userAttendeesItem.userId == attendee.uuid }?.attendeeId
                        ?: ""
        }

        mCompositeDisposable.add(
            mediaCallRepository.setAttendeeMeetingAction(
                callId,
                attendeeId,
                Enums.AttendeeActionType.DROPPED
            ).subscribe()
        )
    }

    fun getNumPeopleJoined(): Int {
        var count = 0
        if (this::userAttendeesItem.isInitialized) {
            mediaCallResponse.value?.attendees?.let {
                it.forEach { attendee ->
                    if (attendee.joinStatus == Enums.MediaCall.JoinStatuses.JOINED && userAttendeesItem.userId != attendee.uuid)
                        count++
                }
            }
        }

        return count
    }

    fun getParticipants(): List<MediaCallAttendee>? {
        return if (this::userAttendeesItem.isInitialized) {
            mediaCallResponse.value?.attendees?.filter {
                it.joinStatus == Enums.MediaCall.JoinStatuses.JOINED && userAttendeesItem.userId != it.uuid
            }?.sortedBy { it.name }
        } else {
            null
        }
    }


    fun checkAndInformIsEmulator(): Boolean {
        val isEmulator: Boolean = isEmulator()
        if (isEmulator) {
            Toast.makeText(application,
                "This feature is disabled on emulators.",
                Toast.LENGTH_SHORT).show()
        }

        return isEmulator
    }

    fun isMeetingSession(): Boolean{
        return this::meetingSession.isInitialized
    }

    private fun meetingStatusChanged(sessionStatus: MeetingSessionStatus) {
        if (sessionStatus.statusCode != null) {
                when(sessionStatus.statusCode) {
                    AudioCallEnded,
                    VideoServiceFailed,
                    AudioJoinedFromAnotherDevice,
                    AudioDisconnectAudio,
                    AudioAuthenticationRejected,
                    AudioCallAtCapacity,
                    AudioInternalServerError,
                    AudioServiceUnavailable,
                    AudioDisconnected -> {
                        endMeeting()}
                    else ->{}

                }
        }
    }

    fun recordingState() {

        mCompositeDisposable.add(
            mediaCallRepository.recordingsForMeetings(callId)
                .subscribe { recordingInfo ->
                    if (recordingInfo != null && recordingInfo.size > 0) {
                        for (recording in recordingInfo) {
                            if (recording.active == true) {
                                isRecording = true
                                recordingTimerObj.startRecordingTimer()
                                break
                            }
                            else
                            {
                                isRecording = false
                            }
                        }

                    }
                    else
                    {
                        recordingTimerObj.startRecordingTimer()
                    }
                    recordingStateInfoLiveData.value = recordingInfo
                    recordingTimerObserver.postValue(recordingTimerObj)

                })

    }



//Overrides

    override val scoreCallbackIntervalMs: Int?
        get() = 1000

    override fun onAudioSessionStartedConnecting(reconnecting: Boolean) {
        if (reconnecting) {
// e.g. the network connection is dropped
        }
        LogUtil.d(TAG, "MEETING Observer onAudioSessionStartedConnecting: $reconnecting")
    }

    override fun onAudioSessionStarted(reconnecting: Boolean) {
// Meeting session starts.
// Can use realtime, devices APIs.
        LogUtil.d(TAG, "MEETING Observer onAudioSessionStarted: $reconnecting")
    }

    override fun onAudioSessionStopped(sessionStatus: MeetingSessionStatus) {
// See the "Stopping a session" section for details.
        LogUtil.d(TAG, "MEETING Observer onAudioSessionStopped: ${sessionStatus.statusCode}")
        meetingStatusChanged(sessionStatus)
    }

    override fun onCameraSendAvailabilityUpdated(available: Boolean) {
        LogUtil.d(TAG, "MEETING Observer onCameraSendAvailabilityUpdated: $available")
    }

    override fun onAudioSessionCancelledReconnect() {}
    override fun onAudioSessionDropped() {
        LogUtil.d(TAG, "MEETING Observer onAudioSessionDropped")
    }

    override fun onConnectionRecovered() {
        LogUtil.d(TAG, "MEETING Observer onConnectionRecovered")
    }

    override fun onRemoteVideoSourceAvailable(sources: List<RemoteVideoSource>) {
        LogUtil.d(TAG, "MEETING Observer onRemoteVideoSourceAvailable: ${sources.size}")
        availableRemoteVideoSourceObserver.postValue(sources)
    }

    override fun onRemoteVideoSourceUnavailable(sources: List<RemoteVideoSource>) {
        LogUtil.d(TAG, "MEETING Observer onRemoteVideoSourceUnavailable: ${sources.size}")
        availableRemoteVideoSourceObserver.postValue(sources)
    }

    override fun onConnectionBecamePoor() {
        LogUtil.d(TAG, "MEETING Observer onConnectionBecamePoor")
    }

    override fun onVideoSessionStartedConnecting() {
        LogUtil.d(TAG, "MEETING Observer onVideoSessionStartedConnecting")
    }

    override fun onVideoSessionStarted(sessionStatus: MeetingSessionStatus) {
        LogUtil.d(TAG, "MEETING Observer onVideoSessionStarted: ${sessionStatus.statusCode}")
// Video session starts.
// Can use video APIs.
        videoStatusChangedObserver.postValue(sessionStatus)
        meetingStatusChanged(sessionStatus)
    }

    override fun onVideoSessionStopped(sessionStatus: MeetingSessionStatus) {
        LogUtil.d(TAG, "MEETING Observer onVideoSessionStopped: ${sessionStatus.statusCode}")
        videoStatusChangedObserver.postValue(sessionStatus)
        meetingStatusChanged(sessionStatus)
    }

    override fun onEventReceived(name: EventName, attributes: EventAttributes) {
        LogUtil.d(TAG, "MEETING Observer onEventReceived name: $name")
        LogUtil.d(TAG, "MEETING Observer onEventReceived attributes: ${GsonUtil.getJSON(attributes)}")

        if(name.name == Enums.MediaCall.SessionStatus.Names.MEETING_FAILED ||
            name.name == Enums.MediaCall.SessionStatus.Names.MEETING_END)
        {
            endMeeting()
        }
    }

    override fun onActiveSpeakerDetected(attendeeInfo: Array<AttendeeInfo>) {
        LogUtil.d(TAG, "MEETING Observer onActiveSpeakerDetected: ${GsonUtil.getJSON(attendeeInfo.size)}")
    }

    override fun onActiveSpeakerScoreChanged(scores: Map<AttendeeInfo, Double>) {
//LogUtil.d("MEETING Observer onActiveSpeakerScoreChanged: ${scores.}")
    }

    override fun onContentShareStarted() {
        LogUtil.d(TAG, "MEETING Observer onContentShareStarted")
    }

    override fun onContentShareStopped(status: ContentShareStatus) {
        LogUtil.d(TAG, "MEETING Observer onContentShareStopped: $status")
    }

    override fun onMetricsReceived(metrics: Map<ObservableMetric, Any>) {
//LogUtil.d(TAG, "MEETING Observer onMetricsReceived: ${metrics.size}")
    }

    override fun onAudioDeviceChanged(freshAudioDeviceList: List<MediaDevice>) {
        LogUtil.d(TAG, "MEETING Observer onAudioDeviceChanged: ${freshAudioDeviceList.size}")
    }

    override fun onAttendeesDropped(attendeeInfo: Array<AttendeeInfo>) {
        LogUtil.d(TAG, "MEETING Observer onAttendeesDropped: ${attendeeInfo.size}")
        attendeesDroppedObserver.postValue(attendeeInfo)
    }

    override fun onAttendeesJoined(attendeeInfo: Array<AttendeeInfo>) {
        LogUtil.d(TAG, "MEETING Observer onAttendeesJoined: ${attendeeInfo.size}")

        //REMOVE AFTER INITIAL TESTING USED TO CONFIRM THE ATTENDEE LIST
        logManager.logToFile(Enums.Logging.STATE_INFO, GsonUtil.getJSON(attendeeInfo))
        for (attendee in attendeeInfo) {
            if(!joinedAttendees.contains(attendee)) {
                attendeesJoinedObserver.postValue(attendee)
                joinedAttendees.add(attendee)
            }
        }

    }

    override fun onAttendeesLeft(attendeeInfo: Array<AttendeeInfo>) {
        LogUtil.d(TAG, "MEETING Observer onAttendeesLeft: ${attendeeInfo.size}")
        attendeesLeftObserver.postValue(attendeeInfo)
    }

    override fun onAttendeesMuted(attendeeInfo: Array<AttendeeInfo>) {
        LogUtil.d(TAG, "MEETING Observer onAttendeesMuted: ${attendeeInfo.size}")
    }

    override fun onAttendeesUnmuted(attendeeInfo: Array<AttendeeInfo>) {
        LogUtil.d(TAG, "MEETING Observer onAttendeesUnmuted: ${attendeeInfo.size}")
    }

    override fun onSignalStrengthChanged(signalUpdates: Array<SignalUpdate>) {
//LogUtil.d(TAG, "MEETING Observer onSignalStrengthChanged: ${signalUpdates.size}")
    }

    override fun onVolumeChanged(volumeUpdates: Array<VolumeUpdate>) {
// LogUtil.d(TAG, "MEETING Observer onVolumeChanged: ${volumeUpdates.size}")
    }

    override fun onTranscriptEventReceived(transcriptEvent: TranscriptEvent) {
        LogUtil.d(TAG, "MEETING Observer onTranscriptEventReceived: $transcriptEvent")
    }

    override fun onDataMessageReceived(onDataMessageReceived: DataMessage) {
        LogUtil.d(TAG, "MEETING Observer onDataMessageReceived: ${onDataMessageReceived.data}")
    }

    override fun onVideoTileAdded(tileState: VideoTileState) {
        LogUtil.d(TAG, "MEETING Observer onVideoTileAdded: ${tileState.tileId}")
        // Ignore local video (see View local video), content video (seeScreen and content share)
        if (tileState.isLocalTile || tileState.isContent) return

        val videoRenderView =
        /* a VideoRenderView object in your application to show the video */
        meetingSession.audioVideo.bindVideoView(remoteVideoView, tileState.tileId)
    }

    override fun onVideoTilePaused(tileState: VideoTileState) {
        LogUtil.d(TAG, "MEETING Observer onVideoTilePaused: ${tileState.tileId}")
    }

    override fun onVideoTileRemoved(tileState: VideoTileState) {
        LogUtil.d(TAG, "MEETING Observer onVideoTileRemoved: ${tileState.tileId}")
        videoTileRemovedObserver.postValue(tileState)
    }

    override fun onVideoTileResumed(tileState: VideoTileState) {
        LogUtil.d(TAG, "MEETING Observer onVideoTileResumed: ${tileState.tileId}")
    }

    override fun onVideoTileSizeChanged(tileState: VideoTileState) {
        LogUtil.d(TAG, "MEETING Observer onVideoTileSizeChanged: ${tileState.tileId}")
    }
// Override End

}