package com.nextiva.nextivaapp.android.models

import androidx.compose.ui.state.ToggleableState

data class ConnectEditModeViewState(
    val itemCountDescription: String,
    var selectAllCheckState: ToggleableState? = ToggleableState.Off,
    var shouldShowBulkUpdateActionIcons: Boolean = false,
    var shouldShowBulkDeleteActionIcons: Boolean = false,
    val onSelectAllCheckedChanged: ((ToggleableState) -> Unit)? = null,
    val onMarkUnReadIconClicked: (() -> Unit)? = null,
    val onMarkReadIconClicked: (() -> Unit)? = null,
    val onDeleteIconClicked: (() -> Unit)? = null,
    val onDoneClicked: (() -> Unit)? = null
)
