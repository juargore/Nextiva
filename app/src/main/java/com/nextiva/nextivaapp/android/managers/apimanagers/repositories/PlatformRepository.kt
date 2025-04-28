package com.nextiva.nextivaapp.android.managers.apimanagers.repositories

import android.app.Activity
import com.nextiva.nextivaapp.android.models.net.platform.BroadworksCredentials
import com.nextiva.nextivaapp.android.models.net.platform.LogSubmit
import com.nextiva.nextivaapp.android.models.net.platform.SelectiveCallRejection
import com.nextiva.nextivaapp.android.models.net.platform.user.device.Policies
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailDetails
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailRatingBody
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface PlatformRepository {
    fun getBroadworksCredentials(activity: Activity, sessionId: String, corpAcctNumber: String): Single<BroadworksCredentials?>

    fun getVoicemails(): Single<Boolean>

    fun getFeatureFlags(): Single<RxEvents.FeatureFlagsResponseEvent>

    fun getAccountInformation(): Single<RxEvents.AccountInformationResponseEvent>

    fun updateVoicemailsRating(
        voiceMailId: String,
        voicemailRatingBody: VoicemailRatingBody
    ): Single<Boolean?>

    fun getVoicemailDetails(voicemailId: String): Single<VoicemailDetails?>

    fun postLogs(logSubmit: LogSubmit): Single<RxEvents.LoggingResponseEvent>

    fun getDevicePolicies(accountNumber: String, sessionId: String, userUUID: String): Single<Policies?>

    fun isUserSuperAdmin(accountNumber: String, sessionId: String): Single<Boolean?>

    fun getSMSCampaignStatus(accountNumber: String, sessionId: String): Flow<String?>

    fun blockOrUnblockNumber(accountNumber: String, sessionId: String, userUUID: String, setting: SelectiveCallRejection): Flow<Boolean?>

    fun fetchBlockedNumbers(accountNumber: String, sessionId: String, userUUID: String, ): Flow<List<String>?>
}
