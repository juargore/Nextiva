package com.nextiva.nextivaapp.android.view.compose.viewstate

import com.nextiva.nextivaapp.android.constants.Enums.FontAwesomeIconType

data class ConnectOptionsMenuViewState(
    val menuItems: List<MenuItem>,
)
data class MenuItem(
    val name: String,
    val icon: Int,
    val isEnabled: Boolean = true,
    val fontAwesomeIconType: Int = FontAwesomeIconType.REGULAR,
    val isSelected: Boolean = false,
    val onItemClicked: (Int) -> Unit,
)