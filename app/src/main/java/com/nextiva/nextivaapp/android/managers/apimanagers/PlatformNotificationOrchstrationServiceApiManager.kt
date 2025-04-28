package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Application
import android.os.Build
import android.text.TextUtils
import com.google.firebase.FirebaseApp
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformNotificationOrchestrationServiceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.net.platform.DeviceBody
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PlatformNotificationOrchstrationServiceApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var schedulerProvider: SchedulerProvider,
    var dbManager: DbManager,
    var netManager: NetManager,
    var sharedPreferencesManager: SharedPreferencesManager,
    var sessionManager: SessionManager,
) : BaseApiManager(application, logManager), PlatformNotificationOrchestrationServiceRepository {

    private fun getPlatformDeviceId(): String {
        var platformDeviceId = sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_PUSH_DEVICE_ID, "")

        if (TextUtils.isEmpty(platformDeviceId)) {
            platformDeviceId = "Android" + UUID.randomUUID().toString().replace("-", "")
            sharedPreferencesManager.setString(SharedPreferencesManager.PLATFORM_PUSH_DEVICE_ID, platformDeviceId)
        }

        return platformDeviceId
    }

    override fun registerForSmsPushNotifications(firebaseToken: String?) {
        logManager.logToFile(Enums.Logging.STATE_INFO, "Starting to register for SMS Push Notifications")

        getDevice(getPlatformDeviceId())
            .subscribeOn(schedulerProvider.io())
            .subscribe(object: DisposableSingleObserver<DeviceBody>() {
                override fun onSuccess(deviceBody: DeviceBody) {
                    logManager.logToFile(Enums.Logging.STATE_INFO, "Successful getting of device: $deviceBody")
                    handleDeviceResponse(deviceBody, firebaseToken)
                }

                override fun onError(e: Throwable) {
                    logManager.logToFile(Enums.Logging.STATE_INFO, "Error getting device. May not exist.")
                    createDevice(firebaseToken)
                        .subscribeOn(schedulerProvider.io())
                        .subscribe(object : DisposableSingleObserver<Boolean>() {
                            override fun onSuccess(success: Boolean) {
                                if (success) {
                                    logManager.logToFile(Enums.Logging.STATE_INFO, "Success creating device.")

                                } else {
                                    logManager.logToFile(Enums.Logging.STATE_ERROR, "Failed to create device.")
                                }
                            }

                            override fun onError(e: Throwable) {
                                logManager.logToFile(Enums.Logging.STATE_ERROR, "Failed to create device: $e")
                            }
                        })
                }
            })
    }

    private fun handleDeviceResponse(deviceBody: DeviceBody, firebaseToken: String?) {
        if (deviceBody.firebaseRegistrationToken != firebaseToken) {
            deleteDevice(getPlatformDeviceId())
                .flatMap { createDevice(firebaseToken) }
                .subscribe(object : DisposableSingleObserver<Boolean>() {
                    override fun onSuccess(success: Boolean) {
                        if (success) {
                            logManager.logToFile(Enums.Logging.STATE_INFO, "Successfully updated device token.")

                        } else {
                            logManager.logToFile(Enums.Logging.STATE_ERROR, "Failed to update device token.")
                        }
                    }

                    override fun onError(e: Throwable) {
                        logManager.logToFile(Enums.Logging.STATE_ERROR, "Failed to update device token: $e")
                    }
                })
        } else {
            logManager.logToFile(Enums.Logging.STATE_INFO, "Device token matches ours. No action needed.")
        }
    }

    override fun createDevice(firebaseToken: String?): Single<Boolean> {
        return Single.fromCallable { sessionManager.sessionId }
            .subscribeOn(schedulerProvider.io())
            .flatMap { sessionId ->
                if (netManager.getPlatformNotificationOrchestrationServiceApi() != null && !firebaseToken.isNullOrEmpty()) {
                    netManager.getPlatformNotificationOrchestrationServiceApi()!!
                        .createDevice(
                            sessionManager.sessionId,
                            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                            DeviceBody(
                                getPlatformDeviceId(),
                                firebaseToken,
                                FirebaseApp.getInstance().options.projectId,
                                "MOBILE",
                                "ANDROID@" + Build.VERSION.RELEASE
                            )
                        )
                        .subscribeOn(schedulerProvider.io())
                        .map { response ->
                            if (response.isSuccessful) {
                                logManager.logToFile(Enums.Logging.STATE_INFO, "Device created successfully.")
                                logServerSuccess(response)
                                response.body()?.let { deviceBody ->
                                    handleDeviceResponse(deviceBody, firebaseToken)
                                }
                                true
                            } else {
                                logManager.logToFile(Enums.Logging.STATE_INFO, "Creating new device failed: ${response.errorBody()}")
                                logServerParseFailure(response)
                                false
                            }
                        }
                } else {
                    Single.just(false)
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun deleteDevice(deviceId: String?): Single<Boolean> {
        return Single.fromCallable { sessionManager.sessionId }
            .subscribeOn(schedulerProvider.io())
            .flatMap { sessionId ->
                if (netManager.getPlatformNotificationOrchestrationServiceApi() != null) {
                    netManager.getPlatformNotificationOrchestrationServiceApi()!!
                        .deleteDevice(
                            sessionManager.sessionId,
                            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                            deviceId ?: getPlatformDeviceId()
                        )
                        .subscribeOn(schedulerProvider.io())
                        .map { response ->
                            if (response.isSuccessful) {
                                logManager.logToFile(Enums.Logging.STATE_INFO, "${deviceId ?: getPlatformDeviceId()} deviceId deleted successfully.")
                                logServerSuccess(response)
                                true
                            } else {
                                logManager.logToFile(Enums.Logging.STATE_INFO, "${deviceId ?: getPlatformDeviceId()} deviceId delete failed.")
                                logServerParseFailure(response)
                                false
                            }
                        }
                } else {
                    Single.just(false)
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun getDevice(deviceId: String?): Single<DeviceBody?> {
        return Single.fromCallable { sessionManager.sessionId }
            .subscribeOn(schedulerProvider.io())
            .flatMap { sessionId ->
                if (netManager.getPlatformNotificationOrchestrationServiceApi() != null) {
                    netManager.getPlatformNotificationOrchestrationServiceApi()!!
                        .getDevice(
                            sessionManager.sessionId,
                            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                            deviceId ?: getPlatformDeviceId()
                        )
                        .subscribeOn(schedulerProvider.io())
                        .flatMap { response ->
                            if (response.isSuccessful) {
                                logServerSuccess(response)
                                LogUtil.d("getDevice: " + GsonUtil.getJSON(response.body()))
                                Single.just(response.body())
                            } else {
                                logServerParseFailure(response)
                                Single.just(null)
                            }
                        }
                } else {
                    Single.just(null)
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }
}