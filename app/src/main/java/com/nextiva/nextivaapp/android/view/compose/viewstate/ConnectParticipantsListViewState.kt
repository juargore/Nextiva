package com.nextiva.nextivaapp.android.view.compose.viewstate

data class ConnectParticipantsListViewState(
    val participantsList: List<ConnectContactListItemViewState>,
    val headerViewState: ConnectHeaderViewState,
    val closeButtonViewState: ConnectTextButtonViewState
)
