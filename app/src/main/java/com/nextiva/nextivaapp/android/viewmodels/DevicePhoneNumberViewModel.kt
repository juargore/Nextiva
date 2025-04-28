package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DevicePhoneNumberViewModel @Inject constructor(
    application: Application,
    private val settingsManager: SettingsManager
) : AndroidViewModel(application) {

    fun setPhoneNumber(phoneNumber: String) {
        settingsManager.phoneNumber = phoneNumber
    }

    fun getPhoneNumber(): String? {
        return settingsManager.phoneNumber
    }
}