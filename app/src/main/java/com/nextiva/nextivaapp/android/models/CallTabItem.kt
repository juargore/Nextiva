package com.nextiva.nextivaapp.android.models

import androidx.annotation.DrawableRes

data class CallTabItem(
    val CallTabId: Int,
    val ConnectCallsFilterType: Int,
    var CallTabTitle: String,
    @DrawableRes var CallTabIcon: Int?,
    var CallTabContentDescription: String,
    var CallTabBadgeNumber: Int?
)
