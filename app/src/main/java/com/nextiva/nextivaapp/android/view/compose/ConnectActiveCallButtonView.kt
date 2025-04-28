package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeDuoToneV6
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeSolidV6
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeV6
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1Heavy
import com.nextiva.nextivaapp.android.view.compose.viewstate.ButtonStateEnum
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectActiveCallButtonViewState

@Composable
fun ConnectActiveCallButtonView(viewState: ConnectActiveCallButtonViewState) {

    val shouldShowPopup = remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.TopCenter) {
        if (shouldShowPopup.value) {
            viewState.popupMenu.popUpItems?.let { items ->
                ConnectOptionsMenu(
                    shouldShowSelection = viewState.popupMenu.shouldShowSelection,
                    menuItems = items,
                    onMenuDismissListener = {
                        shouldShowPopup.value = false
                        viewState.popupMenu.onMenuDismissed?.invoke()
                    },
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.general_padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            viewState.drawableIcons?.let {
                TextButton(
                    onClick = {
                        viewState.onButtonClicked?.invoke()
                        if (!viewState.popupMenu.popUpItems.isNullOrEmpty()) {
                            shouldShowPopup.value = true
                        }
                    },
                    enabled = viewState.buttonState != ButtonStateEnum.DISABLED,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(dimensionResource(id = R.dimen.general_view_xxxxlarge))
                        .semantics {
                            contentDescription =
                                viewState.contentDescription ?: viewState.buttonTitle ?: ""
                        }
                        .background(colorResource(id = if (viewState.buttonState == ButtonStateEnum.ACTIVATED) R.color.connectGrey03 else if (viewState.buttonState == ButtonStateEnum.NORMAL) R.color.connectWhite else R.color.transparent)),
                ) {
                    Icon(
                        painter = painterResource(id = if (viewState.buttonState == ButtonStateEnum.ACTIVATED) viewState.drawableIcons.activatedIcon else viewState.drawableIcons.normalIcon),
                        modifier = Modifier.size(dimensionResource(viewState.drawableIcons.iconSize)),
                        tint = if (viewState.buttonState == ButtonStateEnum.DISABLED) {
                            colorResource(id = R.color.connectSecondaryGrey)
                        } else {
                            Color.Unspecified
                        },
                        contentDescription = viewState.contentDescription
                    )
                }
            }


            viewState.fontAwesomeIcons?.let {
                TextButton(
                    onClick = {
                        viewState.onButtonClicked?.invoke()
                        if (!viewState.popupMenu.popUpItems.isNullOrEmpty()) {
                            shouldShowPopup.value = true
                        }
                    },
                    enabled = viewState.buttonState != ButtonStateEnum.DISABLED,
                    modifier = Modifier
                        .clip(CircleShape)
                        .semantics {
                            contentDescription =
                                viewState.contentDescription ?: viewState.buttonTitle ?: ""
                        }
                        .size(dimensionResource(id = R.dimen.general_view_xxxxlarge))
                        .background(colorResource(id = if (viewState.buttonState == ButtonStateEnum.ACTIVATED) R.color.connectGrey03 else if (viewState.buttonState == ButtonStateEnum.NORMAL) R.color.connectWhite else R.color.transparent))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(color = colorResource(id = R.color.connectGrey08)),
                        ) {
                        },
                ) {
                    Text(
                        text = stringResource(if (viewState.buttonState == ButtonStateEnum.ACTIVATED) viewState.fontAwesomeIcons.activatedIcon else viewState.fontAwesomeIcons.normalIcon),
                        textAlign = TextAlign.Center,
                        fontSize = dimensionResource(R.dimen.connect_dialer_voicemail_icon_size).value.sp,
                        fontWeight = FontWeight.W400,
                        color = colorResource(id = if (viewState.buttonState == ButtonStateEnum.DISABLED) R.color.connectSecondaryGrey else R.color.connectGrey09),
                        fontFamily = if (viewState.fontAwesomeIcons.iconType == Enums.FontAwesomeIconType.SOLID) {
                            FontAwesomeSolidV6
                        } else if (viewState.fontAwesomeIcons.iconType == Enums.FontAwesomeIconType.DUOTONE) {
                            FontAwesomeDuoToneV6
                        } else {
                            FontAwesomeV6
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.general_vertical_margin_small)))
            Text(
                modifier = Modifier
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                        }
                    ),
                text = viewState.buttonTitle ?: "",
                color = colorResource(id = R.color.connectSecondaryDarkBlue),
                style = TypographyCaption1Heavy
            )
        }
    }
}

@Preview
@Composable
fun ConnectActiveCallButtonViewPreview() {
}