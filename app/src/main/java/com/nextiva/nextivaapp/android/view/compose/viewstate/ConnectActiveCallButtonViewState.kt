package com.nextiva.nextivaapp.android.view.compose.viewstate

import androidx.annotation.DrawableRes
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums.FontAwesomeIconType

data class ConnectActiveCallButtonViewState(
    val fontAwesomeIcons: FontAwesomeIcons? = null,
    val drawableIcons: DrawableIcons? = null,
    val buttonTitle: String? = "",
    val buttonState: ButtonStateEnum? = null,
    val onButtonClicked: (() -> Unit)? = null,
    val contentDescription: String? = null,
    val popupMenu: PopupDetails = PopupDetails()
)

data class FontAwesomeIcons(
    val normalIcon: Int,
    val activatedIcon: Int,
    val iconType: Int = FontAwesomeIconType.REGULAR
)

data class PopupDetails(
    val popUpItems: List<MenuItem>? = null,
    val shouldShowSelection: Boolean = false,
    val onMenuDismissed: (() -> Unit)? = null
)

data class DrawableIcons(
    @DrawableRes val normalIcon: Int,
    @DrawableRes val activatedIcon: Int,
    val iconSize: Int = R.dimen.connect_dialer_voicemail_icon_size
)

enum class ButtonStateEnum {
    NORMAL,
    ACTIVATED,
    DISABLED
}