package com.nextiva.nextivaapp.android.viewmodels.setcallsettings

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.listeners.SetCallSettingsListener
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.models.ServiceSettings
import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.functions.Consumer
import javax.inject.Inject

@HiltViewModel
class SetCallSettingsSimultaneousRingViewModel @Inject constructor(application: Application, var userRepository: UserRepository) : BaseViewModel(application) {

    var serviceSettings: ServiceSettings? = null
    var setCallSettingsListener: SetCallSettingsListener? = null

    var serviceSettingsEventLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private val serviceSettingsGetResponseEventConsumer = Consumer<RxEvents.ServiceSettingsGetResponseEvent> { event ->
        if (event.isSuccessful && event.serviceSettings != null) {
            serviceSettings = event.serviceSettings

            val data = Intent()
            data.putExtra(Constants.EXTRA_SERVICE_SETTINGS, serviceSettings)

            setCallSettingsListener?.onCallSettingsRetrieved(data)
        }

        serviceSettingsEventLiveData.value = event.isSuccessful
    }
    private val serviceSettingsPutResponseEventConsumer = Consumer<RxEvents.ServiceSettingsPutResponseEvent> { event ->
        if (event.isSuccessful && event.serviceSettings != null) {
            serviceSettings?.setActive(event.serviceSettings.active)
            serviceSettings?.setDontRingWhileOnCall(event.serviceSettings.dontRingWhileOnCall)
            serviceSettings?.simultaneousRingLocationsList = event.serviceSettings.simultaneousRingLocationsList

            val data = Intent()
            data.putExtra(Constants.EXTRA_SERVICE_SETTINGS, serviceSettings)

            setCallSettingsListener?.onCallSettingsSaved(data)

        }

        serviceSettingsEventLiveData.value = event.isSuccessful
    }

    fun saveServiceSettings(formCallSettings: ServiceSettings) {
        mCompositeDisposable.add(
                userRepository.putServiceSettings(formCallSettings)
                        .subscribe(serviceSettingsPutResponseEventConsumer))
    }

    fun getSingleServiceSettings() {
        serviceSettings?.simultaneousRingLocationsList = ArrayList()

        serviceSettings?.let { serviceSettings ->
            mCompositeDisposable.add(
                    userRepository.getSingleServiceSettings(
                            serviceSettings.type,
                            serviceSettings.uri)
                            .subscribe(serviceSettingsGetResponseEventConsumer))
        }
    }

    fun getLocationListItems(): ArrayList<SimultaneousRingLocation> {
        return serviceSettings?.simultaneousRingLocationsList ?: ArrayList()
    }

    fun setCallSettingsListener(listener: SetCallSettingsListener?, context: Context) {
        try {
            setCallSettingsListener = listener

        } catch (e: ClassCastException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            throw UnsupportedOperationException(context.javaClass.simpleName + " must implement SetCallSettingsListener.")
        }
    }

    fun onFormUpdated() {
        setCallSettingsListener?.onFormUpdated()
    }

    fun onCallSettingsRetrieved() {
        val data = Intent()
        data.putExtra(Constants.EXTRA_SERVICE_SETTINGS, serviceSettings)
        setCallSettingsListener?.onCallSettingsRetrieved(data)
    }

    @VisibleForTesting
    fun getServiceSettingsGetResponseEventConsumer(): Consumer<RxEvents.ServiceSettingsGetResponseEvent> {
        return serviceSettingsGetResponseEventConsumer
    }

    @VisibleForTesting
    fun getServiceSettingsPutResponseEventConsumer(): Consumer<RxEvents.ServiceSettingsPutResponseEvent> {
        return serviceSettingsPutResponseEventConsumer
    }
}