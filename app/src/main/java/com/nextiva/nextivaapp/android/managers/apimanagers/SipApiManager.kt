package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Application
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SipRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.net.sip.SipCallDetails
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import io.reactivex.Single
import javax.inject.Inject

class SipApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var netManager: NetManager,
    var sipManager: PJSipManager,
    var schedulerProvider: SchedulerProvider,
    var sessionManager: SessionManager,
    var dbManager: DbManager
) : BaseApiManager(application, logManager), SipRepository {

    override fun getActiveCalls(): Single<ArrayList<SipCallDetails>?> {
        return Single.fromCallable { sessionManager.sessionId }
            .subscribeOn(schedulerProvider.io())
            .flatMap { sessionId ->
                netManager.getSipApi().getActiveCalls(
                    sessionManager.sessionId,
                    sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
                )
                    .subscribeOn(schedulerProvider.io())
                    .map { response ->
                        if (response.isSuccessful) {
                            logServerSuccess(response)
                            response.body()
                        } else {
                            logServerParseFailure(response)
                            arrayListOf()
                        }
                    }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
    }

    override fun mergeCalls(): Single<Boolean> {
        val sipCallList = sipManager.getActiveCalls()

        sipCallList?.firstOrNull { it.isCallConference }?.conferenceId?.let { conferenceId ->
            sipCallList.firstOrNull { !it.isCallConference }?.let { merging ->
                val details = SipCallDetails(
                    "UNMUTED",
                    null,
                    merging.id.toString(),
                    merging.participantInfoList.firstOrNull { it.contactId?.isNotEmpty().orFalse() }?.contactId,
                    merging.participantInfoList.first { it.numberToCall?.isNotEmpty().orFalse() }.numberToCall
                )

                return addParticipantToNWay(conferenceId, details)
            }
        }

        val callDetails: ArrayList<SipCallDetails> = ArrayList()

        callDetails.add(
            SipCallDetails(
                "UNMUTED",
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                null,
                sessionManager.userInfo?.comNextivaUseruuid,
                sessionManager.phoneNumberInformation.phoneNumber
            )
        )

        sipCallList?.forEach { sipCall ->
            callDetails.add(1,
                SipCallDetails(
                    "UNMUTED",
                    null,
                    sipCall.trackingId,
                    sipCall.participantInfoList.firstOrNull { it.contactId != null }?.contactId,
                    sipCall.participantInfoList.first { it.numberToCall != null }.numberToCall
                )
            )
        }

        return Single.fromCallable { sessionManager.sessionId }
            .subscribeOn(schedulerProvider.io())
            .flatMap { sessionId ->
                netManager.getSipApi().mergeCalls(
                    sessionManager.sessionId,
                    sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                    callDetails
                )
                    .subscribeOn(schedulerProvider.io())
                    .map { response ->
                        if (response.isSuccessful) {
                            logServerSuccess(response)
                            sipManager.getActiveCalls()?.firstOrNull { it.isActive }?.let { sipCall ->
                                sipCall.isCallConference = true

                                response.body()?.id?.let { sipCall.conferenceId = it }

                                val participantList: ArrayList<ParticipantInfo> = ArrayList()

                                response.body()?.callDetails?.forEach { callDetail ->
                                    val contact = dbManager.getConnectContactFromPhoneNumberInThread(callDetail.phoneNbr).value

                                    participantList.add(
                                        ParticipantInfo().apply {
                                            this.numberToCall = callDetail.phoneNbr ?: ""
                                            this.displayName = contact?.uiName ?: callDetail.phoneNbr?.let { CallUtil.getFormattedNumber(it) } ?: ""
                                            this.contactId = contact?.userId
                                            this.trackingId = callDetail.extTrackingId
                                        }
                                    )
                                }

                                sipCall.participantInfoList = participantList
                                sipManager.updateActivePassiveWithSipCall(sipCall)
                            }

                            true
                        } else {
                            logServerParseFailure(response)
                            false
                        }
                    }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
    }

    private fun addParticipantToNWay(trackingId: String, detailsToAdd: SipCallDetails): Single<Boolean> {
        return Single.fromCallable { sessionManager.sessionId }
            .subscribeOn(schedulerProvider.io())
            .flatMap { sessionId ->
                netManager.getSipApi().addCallToNWay(
                    sessionManager.sessionId,
                    sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                    trackingId,
                    detailsToAdd
                )
                    .subscribeOn(schedulerProvider.io())
                    .map { response ->
                        if (response.isSuccessful) {
                            logServerSuccess(response)

//                            sipManager.callSessionList.firstOrNull { it.isCallActive }?.let { callSession ->
//                                callSession.isCallConference = true
//                                val callInfoList = callSession.callInfoArrayList
//                                val contact = dbManager.getConnectContactFromPhoneNumberInThread(detailsToAdd.phoneNbr).value
//
//                                callInfoList.add(CallInfo.Builder()
//                                    .setNumberToCall(detailsToAdd.phoneNbr ?: "")
//                                    .setDisplayName(contact?.uiName ?: detailsToAdd.phoneNbr ?: "")
//                                    .setNextivaContact(contact)
//                                    .setTrackingId(detailsToAdd.extTrackingId)
//                                    .build())
//
//                                callSession.callInfoArrayList = callInfoList
//                                sipManager.setSessionUpdated(callSession)
//                            }

                            true
                        } else {
                            logServerParseFailure(response)
                            false
                        }
                    }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
    }
}