package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceDevice
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceDndSchedule
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceMessageBody
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceMessageDevice
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceResponse
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceResponseBody
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceSetBody
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.Volatile

@Singleton
internal class PresenceApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var netManager: NetManager,
    var schedulerProvider: SchedulerProvider,
    var sessionManager: SessionManager,
    var sharedPreferencesManager: SharedPreferencesManager,
    var dbManager: DbManager
) : BaseApiManager(application, logManager), PresenceRepository {

    private val pageSize = 200
    private val mobileDeviceType = 3
    private val presenceList: ArrayList<ConnectPresenceResponse> = ArrayList()

    @Volatile
    private var presenceSync = false

    private fun getPlatformDeviceId(): String {
        var platformDeviceId = sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_PUSH_DEVICE_ID, "")

        if (TextUtils.isEmpty(platformDeviceId)) {
            platformDeviceId = "Android" + UUID.randomUUID().toString().replace("-", "")
            sharedPreferencesManager.setString(SharedPreferencesManager.PLATFORM_PUSH_DEVICE_ID, platformDeviceId)
        }

        return platformDeviceId
    }

    override fun getPresences() {
        getAllPresences(1)
    }

    private fun getAllPresences(pageNumber: Int) {
        synchronized("presence") {
            if(presenceSync) return
            presenceSync = true
        }
        presenceList.clear()
        requestPresencePage(pageNumber)
            .subscribe(object : DisposableSingleObserver<ConnectPresenceResponseBody>() {
                override fun onSuccess(presenceReturn: ConnectPresenceResponseBody) {

                    presenceReturn.statusList
                        ?.firstOrNull { it.uuid == sessionManager.userInfo?.comNextivaUseruuid }
                        ?.takeIf { sessionManager.isConnectUserPresenceAutomatic && it.presenceState != Enums.Contacts.PresenceStates.CONNECT_ONLINE }
                        ?.let { status ->
                            // user has auto-status but current state isn't online after login -> update status manually
                            setPresence(Enums.Contacts.ConnectPresenceStates.AUTOMATIC, status.customMessage)
                        }

                    presenceReturn.total?.let { totalCount ->
                        val totalPages = kotlin.math.ceil(totalCount.toDouble() / pageSize).toInt()

                        presenceReturn.statusList?.let { presenceList.addAll(it) }
                        addCurrentUserPresence(presenceList)

                        if (pageNumber < totalPages) {
                            val newPageNum = pageNumber + 1
                            getAllPresences(newPageNum)
                        } else {
                            dbManager.updateConnectPresences(presenceList)
                        }
                    }
                    syncOff()
                }

                override fun onError(e: Throwable) {
                    logServerResponseError(e)
                    syncOff()
                }
            })
    }

    private fun syncOff() {
        synchronized("presence") {
            presenceSync = false
        }
    }

    private fun requestPresencePage(pageNumber: Int): Single<ConnectPresenceResponseBody> {
        return netManager.getPresenceApi().getPresences(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            pageNumber.toString(), pageSize.toString()
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                var totalCount = 0

                if (response.isSuccessful) {
                    val presenceList: ArrayList<ConnectPresenceResponse> = ArrayList()

                    response.body()?.let { responseBody ->
                        responseBody.statusList?.forEach { presenceList.add(it) }
                        totalCount = responseBody.total ?: 0
                    }

                    logServerSuccess(response)

                    ConnectPresenceResponseBody(presenceList, totalCount)
                } else {
                    logServerParseFailure(response)
                    ConnectPresenceResponseBody(null, null)
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                ConnectPresenceResponseBody(null, null)
            }
    }

    private fun addCurrentUserPresence(presenceList: ArrayList<ConnectPresenceResponse>) {
        val ourUuid = sessionManager.userInfo?.comNextivaUseruuid

        presenceList.firstOrNull { it.uuid == ourUuid }?.let {
            sessionManager.setConnectUserPresence(
                DbPresence(
                    null,
                    it.uuid,
                    it.presenceState,
                    it.customMessage,
                    it.userSettingStatusExpiresAt,
                    it.inCall
                ),
                it.userSettingStatus == Enums.Contacts.ConnectPresenceStates.AUTOMATIC
            )
        }
    }

    override fun setPresence(state: Int, message: String?) {
        setPresence(state, message, null, null)
    }

    private fun setPresenceMessage(message: String?) {
        netManager.getPresenceApi().setPresenceMessage(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            ConnectPresenceMessageBody(
                sessionManager.userInfo?.comNextivaUseruuid,
                message,
                ConnectPresenceMessageDevice(mobileDeviceType, getPlatformDeviceId())
            )
        )
            .subscribeOn(schedulerProvider.io())
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .subscribe(object : DisposableSingleObserver<Response<ConnectPresenceResponse>>() {
                override fun onSuccess(response: Response<ConnectPresenceResponse>) {
                    logServerSuccess(response)
                }
                override fun onError(e: Throwable) {
                    logServerResponseError(e)
                }
            })
    }

    override fun setPresence(
        state: Int,
        message: String?,
        expiresAtDateTime: String?,
        expiresAtDuration: Int?
    ) {
        netManager.getPresenceApi().setPresence(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            ConnectPresenceSetBody(
                state,
                message,
                sessionManager.userInfo?.comNextivaUseruuid,
                expiresAtDateTime,
                expiresAtDuration,
                ConnectPresenceDevice(mobileDeviceType)
            )
        )
            .subscribeOn(schedulerProvider.io())
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .subscribe(object : DisposableSingleObserver<Response<ConnectPresenceResponse>>() {
                override fun onSuccess(response: Response<ConnectPresenceResponse>) {
                    setPresenceMessage(message)
                    logServerSuccess(response)

                    response.body()?.let { responseBody ->
                        val uuid = sessionManager.userInfo?.comNextivaUseruuid
                        sessionManager.setConnectUserPresence(
                            DbPresence(
                                null,
                                uuid,
                                when (responseBody.status) {
                                    Enums.Contacts.ConnectPresenceStates.ONLINE -> Enums.Contacts.PresenceStates.CONNECT_ONLINE
                                    Enums.Contacts.ConnectPresenceStates.ACTIVE -> Enums.Contacts.PresenceStates.CONNECT_ACTIVE
                                    Enums.Contacts.ConnectPresenceStates.DND -> Enums.Contacts.PresenceStates.CONNECT_DND
                                    Enums.Contacts.ConnectPresenceStates.AWAY -> Enums.Contacts.PresenceStates.CONNECT_AWAY
                                    Enums.Contacts.ConnectPresenceStates.BUSY -> Enums.Contacts.PresenceStates.CONNECT_BUSY
                                    Enums.Contacts.ConnectPresenceStates.BE_RIGHT_BACK -> Enums.Contacts.PresenceStates.CONNECT_BE_RIGHT_BACK
                                    Enums.Contacts.ConnectPresenceStates.OUT_OF_OFFICE -> Enums.Contacts.PresenceStates.CONNECT_OUT_OF_OFFICE
                                    else -> Enums.Contacts.PresenceStates.CONNECT_OFFLINE
                                },
                                message, responseBody.userSettingStatusExpiresAt,
                                responseBody.inCall
                            ),
                            responseBody.userSettingStatus == Enums.Contacts.ConnectPresenceStates.AUTOMATIC
                        )
                    }
                }

                override fun onError(e: Throwable) {
                    logServerResponseError(e)
                }
            })
    }

    override fun sendPresencePing() {
        netManager.getPresenceApi().sendPresenceHeartbeat(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        )
            .subscribeOn(schedulerProvider.io())
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .subscribe(object : DisposableSingleObserver<Response<Void>>() {
                override fun onSuccess(response: Response<Void>) {
                    logServerSuccess(response)
                }

                override fun onError(e: Throwable) {
                    logServerResponseError(e)
                }
            })
    }

    override fun setPresenceDndSchedule(scheduleId: String) {
        sessionManager.userInfo?.comNextivaCorpAccountNumber?.let {
            ConnectPresenceDndSchedule(sessionManager.userInfo?.comNextivaUseruuid.toString(), it, scheduleId)
        }?.let {
            netManager.getPresenceApi().setPresenceDndSchedule(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                sessionManager.userInfo?.comNextivaUseruuid.toString(),
                it
            )
                .subscribeOn(schedulerProvider.io())
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    null
                }
                .subscribe(object : DisposableSingleObserver<Response<ConnectPresenceDndSchedule>>() {
                    override fun onSuccess(response: Response<ConnectPresenceDndSchedule>) {
                        if (response.isSuccessful) {
                            dbManager.setDndSchedule(scheduleId)
                        }
                        logServerSuccess(response)
                    }

                    override fun onError(e: Throwable) {
                        logServerResponseError(e)
                    }
                })
        }
    }

    override fun getPresenceDndSchedule(): Single<String?> {
        return netManager.getPresenceApi().getPresenceDndSchedule(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            sessionManager.userInfo?.comNextivaUseruuid.toString()
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    response.body()?.scheduleId?.let { scheduleId ->
                        dbManager.setDndSchedule(scheduleId)
                    }

                    logServerSuccess(response)
                }

                response.body()?.scheduleId ?: ""
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                ""
            }
    }

    override fun deletePresenceDndSchedule() {
        netManager.getPresenceApi().deletePresenceDndSchedule(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            sessionManager.userInfo?.comNextivaUseruuid.toString()
        )
            .subscribeOn(schedulerProvider.io())
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .subscribe(object : DisposableSingleObserver<Response<Void>>() {
                override fun onSuccess(response: Response<Void>) {
                    if (response.isSuccessful) {
                        dbManager.deleteDndSchedules()
                    }
                    logServerSuccess(response)
                }

                override fun onError(e: Throwable) {
                    logServerResponseError(e)
                }
            })
    }

    override fun checkApiHealth(): Single<Boolean> {
        return netManager.getPresenceApi().sendPresenceHeartbeat(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        )
            .subscribeOn(schedulerProvider.io())
            .map {
                it.isSuccessful
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
    }
}