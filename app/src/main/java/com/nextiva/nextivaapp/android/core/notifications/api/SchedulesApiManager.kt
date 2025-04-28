package com.nextiva.nextivaapp.android.core.notifications.api

import android.app.Application
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.BaseApiManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SchedulesApiManager @Inject constructor(
    var application: Application,
    var presenceRepository: PresenceRepository,
    var logManager: LogManager,
    var netManager: NetManager,
    var dbManager: DbManager,
    var schedulerProvider: SchedulerProvider,
    var sessionManager: SessionManager
) : BaseApiManager(application, logManager), SchedulesRepository {

    override fun saveUserSchedule(userScheduleRequest: UserScheduleRequest): Single<UserScheduleResponse> {
        return netManager.getSchedulesApi().saveSchedule(
            sessionId = sessionManager.sessionId,
            corpAccountNumber1 = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            corpAccountNumber2 = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            userScheduleRequest = userScheduleRequest
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        return@map responseBody
                    }
                } else {
                    logServerParseFailure(response)
                }
                UserScheduleResponse()
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                UserScheduleResponse()
            }
    }

    override fun getUserHours(pageToFetch: Int): Single<ScheduleResponseDto> {
        if (pageToFetch > 1) {
            return Single.just(ScheduleResponseDto(false, 0, ArrayList()))
        }

        return netManager.getSchedulesApi().getUserHours(
            sessionId = sessionManager.sessionId,
            corpAccountNumber1 = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            ownerId = sessionManager.userInfo?.comNextivaUseruuid,
            corpAccountNumber2 = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        )
            .flatMap { response ->
                presenceRepository.getPresenceDndSchedule()
                    .map { dndScheduleId ->
                        response.body()?.schedules?.firstOrNull { dndScheduleId == it.id }?.isDndSchedule = true
                        return@map response.body() ?: ScheduleResponseDto(false, 0, ArrayList())
                    }
            }
            .subscribeOn(schedulerProvider.io())
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                ScheduleResponseDto(false, 0, ArrayList())
            }
    }

    override fun deleteSchedule(scheduleId: String): Single<Boolean> {
        return netManager.getSchedulesApi().deleteSchedule(
            sessionId = sessionManager.sessionId,
            corpAccountNumber1 = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            ownerId = sessionManager.userInfo?.comNextivaUseruuid,
            corpAccountNumber2 = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            scheduleId = scheduleId
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    dbManager.deleteScheduleByScheduleId(scheduleId)
                    return@map true
                } else {
                    logServerParseFailure(response)
                }
                false
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
    }

    override fun updateUserSchedule(
        userScheduleRequest: UserScheduleRequest,
        scheduleId: String
    ): Single<UserScheduleResponse> {
        return netManager.getSchedulesApi().updateSchedule(
            sessionId = sessionManager.sessionId,
            corpAccountNumber1 = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            corpAccountNumber2 = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            userScheduleRequest = userScheduleRequest,
            scheduleId = scheduleId
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        dbManager.database.runInTransaction {
                            dbManager.insertSchedules(
                                false,
                                arrayListOf(responseBody),
                                null
                            )
                        }
                        return@map responseBody
                    }
                } else {
                    logServerParseFailure(response)
                }
                UserScheduleResponse()
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                UserScheduleResponse()
            }
    }

    override fun getObservedHolidayList(): Single<ArrayList<Holiday>> {
        return netManager.getSchedulesApi().getHolidaysList(
            sessionId = sessionManager.sessionId,
            corpAccountNumber1 = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            corpAccountNumber2 = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        return@map responseBody
                    }
                } else {
                    logServerParseFailure(response)
                }
                java.util.ArrayList<Holiday>()
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                java.util.ArrayList<Holiday>()
            }
    }
}
