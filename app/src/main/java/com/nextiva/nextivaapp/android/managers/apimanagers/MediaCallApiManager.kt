package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Application
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.MediaCallRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.meetings.models.net.events.MeetingsEventResponse
import com.nextiva.nextivaapp.android.meetings.models.net.media.MediaCallInfoResponse
import com.nextiva.nextivaapp.android.meetings.models.net.media.recordings.OngoingRecordMeetingResponse
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallRequest
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallResponse
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.LogUtil
import io.reactivex.Single
import javax.inject.Inject

internal class MediaCallApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var netManager: NetManager,
    var schedulerProvider: SchedulerProvider,
    var sessionManager: SessionManager,
    var dbManager: DbManager
) : BaseApiManager(application, logManager), MediaCallRepository {

    override fun startCall(mediaCallRequest: MediaCallRequest): Single<MediaCallResponse> {
        return netManager.getMediaCallApi()?.let { api ->
            api.startCall(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                mediaCallRequest
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                        response.body() ?: MediaCallResponse()
                    } else {
                        logServerParseFailure(response)
                        MediaCallResponse()
                    }
                }
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    MediaCallResponse()
                }
                .observeOn(schedulerProvider.ui())
        } ?: Single.just(MediaCallResponse())
    }

    override fun assignCoHost(mediaCallId: String, attendeeId: String, attendeePatchAction: String): Single<MediaCallResponse> {
        return netManager.getMediaCallApi()?.let { api ->
            api.assignCoHost(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                mediaCallId,
                attendeeId,
                attendeePatchAction
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                        response.body() ?: MediaCallResponse()
                    } else {
                        logServerParseFailure(response)
                        MediaCallResponse()
                    }
                }
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    MediaCallResponse()
                }
                .observeOn(schedulerProvider.ui())
        } ?: Single.just(MediaCallResponse())
    }

    override fun meetingEvent(mediaEventId: String): Single<MeetingsEventResponse?> {
        return netManager.getMediaCallApi()?.let { api ->
            api.meetingEvent(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                mediaEventId
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                        response.body()
                    } else {
                        logServerParseFailure(response)
                        null
                    }
                }
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    null
                }
                .observeOn(schedulerProvider.ui())
        } ?: Single.just(null)
    }

    override fun callInfo(mediaCallId: String): Single<MediaCallInfoResponse?> {
        return netManager.getMediaCallApi()?.let { api ->
            api.meetingInfo(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                mediaCallId
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                        response.body()
                    } else {
                        logServerParseFailure(response)
                        null
                    }
                }
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    null
                }
                .observeOn(schedulerProvider.ui())
        } ?: Single.just(null)
    }

    override fun checkApiHealth(): Single<Boolean> {
        return netManager.getMediaCallApi()?.let { api ->
            api.checkApiHealth(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                "fakeid"
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    // 400 means that the call was successful but the meeting was not found.
                    if (response.code() == 400) {
                        true
                    } else {
                        false
                    }
                }
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    false
                }
                .observeOn(schedulerProvider.ui())
        } ?: Single.just(false)
    }

    override fun getActiveMediaCall(meetingId: String): Single<MediaCallResponse> {
        return netManager.getMediaCallApi()?.let { api ->
            api.getActiveMediaCall(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                meetingId
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                        response.body() ?: MediaCallResponse()
                    } else {
                        logServerParseFailure(response)
                        MediaCallResponse()
                    }
                }
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    MediaCallResponse()
                }
                .observeOn(schedulerProvider.ui())
        } ?: Single.just(MediaCallResponse())
    }

    override fun setAttendeeMeetingAction(mediaCallId: String, attendeeId: String, attendeeAction: String): Single<MediaCallResponse> {
        return netManager.getMediaCallApi()?.let { api ->
            api.attendeeMeetingAction(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                mediaCallId,
                attendeeId,
                attendeeAction
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                        response.body() ?: MediaCallResponse()
                    } else {
                        logServerParseFailure(response)
                        MediaCallResponse()
                    }
                }
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    MediaCallResponse()
                }
                .observeOn(schedulerProvider.ui())
        } ?: Single.just(MediaCallResponse())
    }

    override fun recordingsForMeetings(mediaCallId: String): Single<ArrayList<OngoingRecordMeetingResponse>?> {
        return netManager.getMediaCallApi()?.let { api ->
            api.getRecordingsForMeetings(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                mediaCallId
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    response.body()
                }
                .onErrorReturn { throwable ->
                    LogUtil.e("Error getting recordings for meeting: " + throwable.message)
                    logServerResponseError(throwable)
                    null
                }
                .observeOn(schedulerProvider.ui())
        } ?: Single.just(null)
    }
}
