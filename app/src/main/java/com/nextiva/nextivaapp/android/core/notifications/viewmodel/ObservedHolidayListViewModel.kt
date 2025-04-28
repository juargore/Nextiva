package com.nextiva.nextivaapp.android.core.notifications.viewmodel

import android.app.Application
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.nextiva.nextivaapp.android.core.notifications.api.Holiday
import com.nextiva.nextivaapp.android.core.notifications.models.HolidaySelection
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class ObservedHolidayListViewModel @Inject constructor(
    application: Application,
    private val presenceRepository: PresenceRepository,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val sessionManager: SessionManager
) : BaseViewModel(application) {

    var observedHolidaySelectionList: List<HolidaySelection> = listOf()

    fun getObservedHolidayList(): ArrayList<Holiday> {
        val holidayListString = sharedPreferencesManager.getString(
            SharedPreferencesManager.NOTIFICATION_SCHEDULE_OBSERVED_HOLIDAY_LIST,
            ""
        )
        val gson = Gson()
        return gson.fromJson(
            holidayListString,
            object : TypeToken<ArrayList<Holiday>>() {}.type
        )
    }
}