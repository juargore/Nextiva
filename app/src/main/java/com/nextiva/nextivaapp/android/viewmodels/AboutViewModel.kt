package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(application: Application) : BaseViewModel(application)