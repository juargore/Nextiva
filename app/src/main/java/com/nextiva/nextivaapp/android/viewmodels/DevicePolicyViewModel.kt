/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.managers.NextivaSharedPreferencesManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DevicePolicyViewModel @Inject constructor(nextivaApplication: Application, val sharedPreferencesManager: NextivaSharedPreferencesManager) : BaseViewModel(nextivaApplication) {
    var isCallDecline: MutableLiveData<Boolean> = MutableLiveData()

    fun loadDevicePolicies()
    {
        isCallDecline()
    }

    private fun isCallDecline() {
        isCallDecline.value = sharedPreferencesManager.getBoolean(
                SharedPreferencesManager.USER_SETTINGS_DEVICES_POLICIES_CALL_DECLINE_ENABLED,
                false
            )
    }
}