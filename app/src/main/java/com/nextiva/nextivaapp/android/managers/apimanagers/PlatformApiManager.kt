package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Activity
import android.app.Application
import android.text.TextUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.ResponseCodes.ClientFailureResponses.NOT_FOUND
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.net.platform.BroadworksCredentials
import com.nextiva.nextivaapp.android.models.net.platform.LogSubmit
import com.nextiva.nextivaapp.android.models.net.platform.SelectiveCallRejection
import com.nextiva.nextivaapp.android.models.net.platform.featureFlags.FeatureFlags
import com.nextiva.nextivaapp.android.models.net.platform.user.device.Policies
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailDetails
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailRatingBody
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailsResponseBody
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.LogUtil
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

//    Dev (labs)
//    URL: https://accessmanagement.dev.nextiva.xyz

//    RC
//    URL: https://accessmanagement.rc.nextiva.xyz

//    Prod (prod, rc, alpha, nightly)
//    URL: https://accessmanagement.nextiva.com

//    Auth Header: Bearer {Access Token from okta}

@Singleton
internal class PlatformApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var netManager: NetManager,
    var schedulerProvider: SchedulerProvider,
    var sessionManager: SessionManager,
    var dbManager: DbManager
) : BaseApiManager(application, logManager), PlatformRepository {

    override fun getBroadworksCredentials(activity: Activity, sessionId: String, corpAcctNumber: String): Single<BroadworksCredentials?> {

        return if (sessionId.isNotEmpty() && netManager.getPlatformAccessApi() != null) {
            netManager.getPlatformAccessApi()!!
                .getBroadworksCredentials(sessionId, corpAcctNumber)
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                    } else {
                        logServerParseFailure(response)
                    }
                    response.body() ?: BroadworksCredentials()
                }
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    BroadworksCredentials()
                }
                .observeOn(schedulerProvider.ui())
        } else {
            Single.just(BroadworksCredentials())
        }
    }

    override fun getFeatureFlags(): Single<RxEvents.FeatureFlagsResponseEvent> {
        val sessionId = sessionManager.sessionId
        val corpAcctNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        LogUtil.d("Session: $sessionId")
        LogUtil.d("Corp: $corpAcctNumber")
        return if (!sessionId.isNullOrEmpty() && netManager.getPlatformApi() != null) {
            netManager.getPlatformApi()!!.getFeatureFlags(
                sessionId,
                corpAcctNumber,
                TextUtils.join(",", mApplication.resources.getStringArray(R.array.feature_flags))
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                        val featureFlags = FeatureFlags(response.body())
                        sessionManager.featureFlags.featureFlags?.forEach { cachedFlag ->
                            featureFlags.featureFlags?.firstOrNull { responseFlag ->
                                responseFlag.name == cachedFlag.name
                            }?.isManuallyDisabled = cachedFlag.isManuallyDisabled
                        }
                        sessionManager.featureFlags = FeatureFlags(response.body())
                        RxEvents.FeatureFlagsResponseEvent(true)
                    } else {
                        logServerParseFailure(response)
                        RxEvents.FeatureFlagsResponseEvent(false)
                    }
                }
                .onErrorReturn {
                    RxEvents.FeatureFlagsResponseEvent(false)
                }
        } else {
            Single.just(RxEvents.FeatureFlagsResponseEvent(false))
        }
    }

    override fun getAccountInformation(): Single<RxEvents.AccountInformationResponseEvent> {
        val sessionId = sessionManager.sessionId
        val corpAcctNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        LogUtil.d("Session: $sessionId")
        LogUtil.d("Corp: $corpAcctNumber")
        return if (!sessionId.isNullOrEmpty() && netManager.getPlatformApi() != null) {
            netManager.getPlatformApi()!!.getAccountInformation(
                sessionId,
                corpAcctNumber
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful && response.body() != null) {
                        logServerSuccess(response)
                        sessionManager.accountInformation = response.body()
                        RxEvents.AccountInformationResponseEvent(true)
                    } else {
                        logServerParseFailure(response)
                        RxEvents.AccountInformationResponseEvent(false)
                    }
                }
                .onErrorReturn { RxEvents.AccountInformationResponseEvent(false) }
                .observeOn(schedulerProvider.ui())
        } else {
            Single.just(RxEvents.AccountInformationResponseEvent(false))
        }
    }

    override fun getVoicemails(): Single<Boolean> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Will fetch voicemails")
        val visualVoiceFeatureFlag = sessionManager.featureFlags.featureFlags?.firstOrNull {
            it.name.equals(Enums.Platform.FeatureFlags.VISUAL_VOICE_MAIL)
        }

        if (visualVoiceFeatureFlag == null || visualVoiceFeatureFlag.isEnabled == false || !sessionManager.isVoicemailTranscriptionEnabled) return Single.just(false)

        val sessionId = sessionManager.sessionId
        val corpAcctNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        LogUtil.d("Session: $sessionId")
        LogUtil.d("Corp: $corpAcctNumber")
        return if (!sessionId.isNullOrEmpty()) {
            netManager.getVoicemailApi().getAllVoicemails(
                sessionId,
                corpAcctNumber
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                    } else {
                        logServerParseFailure(response)
                    }
                    response.body()
                }
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    VoicemailsResponseBody(null)
                }
                .map { voicemails ->
                    if (voicemails.voicemailData != null) {
                        dbManager.insertVoicemailTranscriptions(voicemails.voicemailData)
                        true
                    } else {
                        false
                    }
                }
        } else {
            Single.just(false)
        }
    }

    override fun updateVoicemailsRating(voiceMailId: String, voicemailRatingBody: VoicemailRatingBody): Single<Boolean?> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Will update voicemail rating")

        val sessionId = sessionManager.sessionId
        val corpAcctNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        LogUtil.d("Session: $sessionId")
        LogUtil.d("Corp: $corpAcctNumber")
        val arrayListRatingbody = ArrayList<VoicemailRatingBody>()
        arrayListRatingbody.add(voicemailRatingBody)
        return if (!sessionId.isNullOrEmpty()) {
            netManager.getVoicemailApi().updateVoicemailRating(
                sessionId,
                corpAcctNumber,
                "*/*",
                voiceMailId,
                arrayListRatingbody
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                    } else {
                        logServerParseFailure(response)
                    }
                    response.isSuccessful
                }.onErrorReturn {
                    logServerResponseError(it)
                    false
                }
                .observeOn(schedulerProvider.ui())
        } else {
            Single.just(false)
        }
    }

    override fun getVoicemailDetails(voicemailId: String): Single<VoicemailDetails?> {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Will get voicemail details")

        val sessionId = sessionManager.sessionId
        val corpAcctNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        LogUtil.d("Session: $sessionId")
        LogUtil.d("Corp: $corpAcctNumber")
        return if (!sessionId.isNullOrEmpty() && netManager.getPlatformApi() != null) {
            netManager.getVoicemailApi()!!.getVoicemailDetails(
                sessionId,
                corpAcctNumber,
                voicemailId
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                        response.body()?.let {
                            return@map it
                        }
                    } else {
                        logServerParseFailure(response)
                        FirebaseCrashlytics.getInstance().recordException(Exception(response.toString()))
                        if (response.code() == NOT_FOUND) {
                            return@map VoicemailDetails(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                            )
                        }
                        return@map null
                    }
                }
                .onErrorReturn {
                    FirebaseCrashlytics.getInstance().recordException(it)
                    null
                }
        } else {
            Single.just(null)
        }
    }

    override fun postLogs(logSubmit: LogSubmit): Single<RxEvents.LoggingResponseEvent> {
        val sessionId = sessionManager.sessionId
        val corpAcctNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        LogUtil.d("Session: $sessionId")
        LogUtil.d("Corp: $corpAcctNumber")
        return if (!sessionId.isNullOrEmpty() && netManager.getPlatformApi() != null) {
            netManager.getPlatformApi()!!.postLogs(
                sessionId,
                corpAcctNumber,
                logSubmit
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                        RxEvents.LoggingResponseEvent(true)
                    } else {
                        logServerParseFailure(response)
                        FirebaseCrashlytics.getInstance().recordException(Exception(response.toString()))
                        RxEvents.LoggingResponseEvent(false)
                    }
                }
                .onErrorReturn {
                    FirebaseCrashlytics.getInstance().recordException(it)
                    RxEvents.LoggingResponseEvent(false)
                }
                .observeOn(schedulerProvider.ui())
        } else {
            Single.just(RxEvents.LoggingResponseEvent(false))
        }
    }


    override fun getDevicePolicies(accountNumber: String, sessionId: String, userUUID: String): Single<Policies?> {
        return if (netManager.getUsersApi() != null) {
            netManager.getUsersApi()!!.getDevicesPolicies(sessionId, accountNumber, userUUID)
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            return@map it
                        }
                    } else {
                        logServerParseFailure(response)
                        FirebaseCrashlytics.getInstance().recordException(Exception(response.toString()))
                        null
                    }
                }
                .onErrorReturn {
                    FirebaseCrashlytics.getInstance().recordException(it)
                    null
                }
        } else {
            Single.just(null)
        }
    }

    override fun isUserSuperAdmin(accountNumber: String, sessionId: String): Single<Boolean?> {
        return if (netManager.getUsersApi() != null) {
            netManager.getUsersApi()!!.getUserInfo(sessionId, accountNumber)
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    if (response.isSuccessful) {
                        logServerSuccess(response)
                        return@map response.body()?.superAdmin ?: false
                    } else {
                        logServerParseFailure(response)
                        FirebaseCrashlytics.getInstance().recordException(Exception(response.toString()))
                        null
                    }
                }
                .onErrorReturn {
                    FirebaseCrashlytics.getInstance().recordException(it)
                    null
                }
        } else {
            Single.just(null)
        }
    }

    override fun getSMSCampaignStatus(accountNumber: String, sessionId: String) = flow {
        if (netManager.getUsersApi() != null) {
            val call = netManager.getUsersApi()!!.getSMSCampaignStatus(sessionId, accountNumber)
            try {
                val response = call.execute()
                if (response.isSuccessful && response.body() != null) {
                    logServerSuccess(response)
                    val responseBodyString = response.body()!!.string()
                    emit(responseBodyString)
                } else {
                    logServerParseFailure(response)
                    emit(null)
                }
            } catch (e: IOException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                emit(null)
            }
        } else {
            emit(null)
        }
    }

    override fun blockOrUnblockNumber(
        accountNumber: String,
        sessionId: String,
        userUUID: String,
        setting: SelectiveCallRejection
    ): Flow<Boolean?> = flow {
        val api = netManager.getUsersApi() ?: return@flow emit(null)
        try {
            val response = api.blockNumber(sessionId, accountNumber, userUUID, setting).execute()
            val isSuccess = response.code() == 200 || response.code() == 409

            emit(isSuccess)

            if (!isSuccess) {
                logServerParseFailure(response)
            }
        } catch (e: IOException) {
            emit(null)
        }
    }

    override fun fetchBlockedNumbers(
        accountNumber: String,
        sessionId: String,
        userUUID: String
    ): Flow<List<String>?> = flow {
        val api = netManager.getUsersApi() ?: return@flow emit(null)
        try {
            val response = api.fetchBlockedNumbers(sessionId, accountNumber, userUUID)
            if (response.isSuccessful && response.code() == 200) {
                if (!response.body()?.currentSelectiveCallRejectionConditions.isNullOrEmpty()) {
                    emit(response.body()?.currentSelectiveCallRejectionConditions?.get(0)?.numbers ?: emptyList())
                } else {
                    emit(emptyList())
                }
            } else {
                emit(emptyList())
            }
        } catch (e: IOException) {
            emit(null)
        }
    }
}
