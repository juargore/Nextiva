package com.nextiva.nextivaapp.android.managers.apimanagers.repositories

import io.reactivex.Single

interface PresenceRepository {
    fun getPresences()

    fun setPresence(state: Int, message: String?)

    fun setPresence(state: Int, message: String?, expiresAtDateTime: String?, expiresAtDuration: Int?)

    fun sendPresencePing()

    fun checkApiHealth(): Single<Boolean>

    fun setPresenceDndSchedule(scheduleId: String)

    fun getPresenceDndSchedule(): Single<String?>

    fun deletePresenceDndSchedule()
}