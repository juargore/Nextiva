package com.nextiva.nextivaapp.android.viewmodels.setcallsettings

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.listeners.SetCallSettingsListener
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation
import com.nextiva.nextivaapp.android.models.ServiceSettings
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.functions.Consumer
import javax.inject.Inject

@HiltViewModel
class SetCallSettingsNextivaAnywhereViewModel @Inject constructor(application: Application, var userRepository: UserRepository, var sessionManager: SessionManager) : BaseViewModel(application) {

    var serviceSettings: ServiceSettings? = null
    var pendingAlertAllLocations: Boolean? = null
    var setCallSettingsListener: SetCallSettingsListener? = null
    var serviceSettingsGetResponseEventLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var serviceSettingsSaveResponseEventLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private val serviceSettingsGetResponseEventConsumer = Consumer<RxEvents.ServiceSettingsGetResponseEvent> { event ->
        if (event.isSuccessful && event.serviceSettings != null) {
            serviceSettings = event.serviceSettings

            val data = Intent()
            data.putExtra(Constants.EXTRA_SERVICE_SETTINGS, serviceSettings)

            setCallSettingsListener?.onCallSettingsRetrieved(data)
            sessionManager.nextivaAnywhereServiceSettings = serviceSettings
        }

        serviceSettingsGetResponseEventLiveData.value = event.isSuccessful
    }

    private val saveServiceSettingsConsumer = Consumer<RxEvents.ServiceSettingsGetResponseEvent> { event ->
        if (event.isSuccessful && event.serviceSettings != null) {
            serviceSettings?.setAlertAllLocationsForClickToDialCalls(event.serviceSettings.alertAllLocationsForClickToDialCallsRaw)

            val data = Intent()
            data.putExtra(Constants.EXTRA_SERVICE_SETTINGS, serviceSettings)

            setCallSettingsListener?.onCallSettingsSaved(data)
        }

        serviceSettingsSaveResponseEventLiveData.value = event.isSuccessful
    }

    fun saveSessionServiceSettings() {
        sessionManager.nextivaAnywhereServiceSettings = serviceSettings
    }

    fun getSingleServiceSettings() {
        serviceSettings?.nextivaAnywhereLocationsList = ArrayList()

        serviceSettings?.let { serviceSettings ->
            mCompositeDisposable.add(
                    userRepository.getSingleServiceSettings(
                            serviceSettings.type,
                            serviceSettings.uri)
                            .subscribe(serviceSettingsGetResponseEventConsumer))
        }
    }

    fun saveServiceSettings(formCallSettings: ServiceSettings) {
        mCompositeDisposable.add(
                userRepository.putServiceSettings(formCallSettings)
                        .flatMap<RxEvents.ServiceSettingsGetResponseEvent> {
                            serviceSettings?.let { serviceSettings ->
                                userRepository.getSingleServiceSettings(serviceSettings.type, serviceSettings.uri)
                                        .single(RxEvents.ServiceSettingsGetResponseEvent(false, null))
                            }
                        }.subscribe(saveServiceSettingsConsumer))
    }

    fun getLocationListItems(): ArrayList<NextivaAnywhereLocation> {
        return serviceSettings?.nextivaAnywhereLocationsList ?: ArrayList()
    }

    fun setCallSettingsListener(listener: SetCallSettingsListener?, context: Context) {
        try {
            setCallSettingsListener = listener

        } catch (e: ClassCastException) {
            throw UnsupportedOperationException(context.javaClass.simpleName + " must implement SetCallSettingsListener.")
        }
    }

    fun onFormUpdated() {
        setCallSettingsListener?.onFormUpdated()
    }

    @VisibleForTesting
    fun getServiceSettingsGetResponseEventConsumer(): Consumer<RxEvents.ServiceSettingsGetResponseEvent> {
        return serviceSettingsGetResponseEventConsumer
    }

    @VisibleForTesting
    fun getSaveSettingsConsumer(): Consumer<RxEvents.ServiceSettingsGetResponseEvent> {
        return saveServiceSettingsConsumer
    }

    @VisibleForTesting
    fun getServiceSettingsGetResponseEventLiveData(): LiveData<Boolean> {
        return serviceSettingsGetResponseEventLiveData
    }

    @VisibleForTesting
    fun getServiceSettingsSaveResponseEventLiveData(): LiveData<Boolean> {
        return serviceSettingsSaveResponseEventLiveData
    }
}