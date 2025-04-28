package com.nextiva.nextivaapp.android.managers.apimanagers.repositories

import com.nextiva.nextivaapp.android.models.net.platform.DeviceBody
import io.reactivex.Single

interface PlatformNotificationOrchestrationServiceRepository {
    fun registerForSmsPushNotifications(firebaseToken: String?)

    fun getDevice(deviceId: String?): Single<DeviceBody?>

    fun deleteDevice(deviceId: String?): Single<Boolean>

    fun createDevice(firebaseToken: String?): Single<Boolean>
}