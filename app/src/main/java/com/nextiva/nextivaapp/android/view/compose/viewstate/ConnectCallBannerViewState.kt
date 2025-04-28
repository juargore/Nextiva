package com.nextiva.nextivaapp.android.view.compose.viewstate

data class ConnectCallBannerViewState(
    val callerDisplayName: String? = null,
    val callerPhoneNumber: String? = null,
    val avatarViewState: ConnectAvatarViewState? = ConnectAvatarViewState(),
    val onResumeButtonClicked: (() -> Unit)? = null
)