package com.nextiva.nextivaapp.android.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LicenseAgreementViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val intentManager: IntentManager
) : ViewModel() {


}