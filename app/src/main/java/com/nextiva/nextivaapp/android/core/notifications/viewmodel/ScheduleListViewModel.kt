package com.nextiva.nextivaapp.android.core.notifications.viewmodel

import android.app.Application
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.mediators.SchedulesRemoteMediator
import com.nextiva.nextivaapp.android.core.notifications.api.SchedulesRepository
import com.nextiva.nextivaapp.android.core.notifications.models.Schedule
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ScheduleListViewModel @Inject constructor(
    application: Application,
    private val dbManager: DbManager,
    private val presenceRepository: PresenceRepository,
    private val schedulesRepository: SchedulesRepository
) : BaseViewModel(application) {

    @OptIn(ExperimentalPagingApi::class)
    val schedules: Flow<PagingData<Schedule>> = Pager(
        config = PagingConfig(pageSize = 40, prefetchDistance = 60, initialLoadSize = 100, enablePlaceholders = true),
        pagingSourceFactory = { dbManager.schedulesPagingSource },
        remoteMediator = SchedulesRemoteMediator(schedulesRepository, dbManager) // Pass it here
    ).flow

    fun getDndScheduleFlow(): Flow<Schedule?> = dbManager.dndScheduleFlow

    fun setPresenceDndSchedule(scheduleId: String) {
        presenceRepository.setPresenceDndSchedule(scheduleId)
    }

    fun deletePresenceDndSchedule() {
        presenceRepository.deletePresenceDndSchedule()
    }
}