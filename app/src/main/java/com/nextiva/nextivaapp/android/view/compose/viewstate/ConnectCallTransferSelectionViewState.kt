package com.nextiva.nextivaapp.android.view.compose.viewstate
data class ConnectCallTransferSelectionViewState(
    val connectHeaderViewState: ConnectHeaderViewState? = null,
    val callerInfoViewState: ConnectCallerInfoViewState? = null,
    val cancelTxtBtnViewState: ConnectTextButtonViewState? = null,
    val onBlindOptionSelected: (() -> Unit)? = null,
    val onWarmOptionSelected: (() -> Unit)? = null,
    val onChangeTransferSelected: (() -> Unit)? = null
)