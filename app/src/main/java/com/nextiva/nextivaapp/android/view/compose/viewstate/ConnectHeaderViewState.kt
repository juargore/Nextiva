package com.nextiva.nextivaapp.android.view.compose.viewstate
data class ConnectHeaderViewState (
    val onCloseButtonClick: ()-> Unit,
    val shouldShowRoundedCornerShape: Boolean = false
)