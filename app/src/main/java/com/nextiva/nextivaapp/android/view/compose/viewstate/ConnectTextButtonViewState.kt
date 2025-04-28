package com.nextiva.nextivaapp.android.view.compose.viewstate

import androidx.annotation.ColorRes
import com.nextiva.nextivaapp.android.R

data class ConnectTextButtonViewState(
    val text: String,
    val buttonType: ConnectButtonType? = ConnectButtonType.PRIMARY_GHOST,
    @ColorRes val textColor: Int = R.color.connectSecondaryDarkBlue,
    val onButtonClicked: (() -> Unit),
    val leadingIcon: Int? = null,
    val isButtonEnabled: Boolean = true
)

enum class ConnectButtonType {
    PRIMARY,
    SECONDARY,
    PRIMARY_GHOST
}