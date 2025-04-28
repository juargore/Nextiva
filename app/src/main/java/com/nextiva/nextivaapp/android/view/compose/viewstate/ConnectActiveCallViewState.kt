package com.nextiva.nextivaapp.android.view.compose.viewstate

import com.nextiva.pjsip.pjsip_lib.sipservice.SipConnectionStatus

data class ConnectActiveCallViewState(
    val activeCallerInfo: ConnectCallerInfoViewState? = null,
    val headerViewState: ConnectHeaderViewState? = null,
    val callBannerInfo: ConnectCallBannerViewState? = null,
    val holdButton: ConnectActiveCallButtonViewState,
    val muteButton: ConnectActiveCallButtonViewState,
    val keyPadButton: ConnectActiveCallButtonViewState,
    val speakerButton: ConnectActiveCallButtonViewState,
    val addCallButton: ConnectActiveCallButtonViewState,
    val moreButton: ConnectActiveCallButtonViewState,
    val completeTransferButton: ConnectTextButtonViewState? = null,
    val cancelTransferButton: ConnectTextButtonViewState? = null,
    val sipConnectionStatus: SipConnectionStatus,
    var wasSipConnectionBannerDismissed: Boolean = false,
    val onEndCallButtonClicked: (() -> Unit)? = null,
    val onActiveCallerInfoClick: (() -> Unit),
)