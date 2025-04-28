package com.nextiva.nextivaapp.android.core.notifications.api

import io.reactivex.Single

interface SchedulesRepository {
    fun getUserHours(pageToFetch: Int): Single<ScheduleResponseDto>

    fun deleteSchedule(scheduleId: String): Single<Boolean>

    fun saveUserSchedule(userScheduleRequest: UserScheduleRequest): Single<UserScheduleResponse>

    fun updateUserSchedule(userScheduleRequest: UserScheduleRequest, scheduleId: String): Single<UserScheduleResponse>

    fun getObservedHolidayList(): Single<ArrayList<Holiday>>
}