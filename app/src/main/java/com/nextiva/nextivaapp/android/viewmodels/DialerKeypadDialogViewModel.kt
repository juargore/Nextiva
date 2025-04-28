package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DialerKeypadDialogViewModel @Inject constructor(application: Application) : BaseViewModel(application) {
    val widthMetric = (application.resources.displayMetrics.widthPixels * 0.85).toInt()
    val heightMetric = (application.resources.displayMetrics.heightPixels * 0.75).toInt()
}