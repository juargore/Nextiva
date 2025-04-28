package com.nextiva.nextivaapp.android.view.compose
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeV6
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyButton
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.withTextColor
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectCallTransferSelectionViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectCallerInfoViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectHeaderViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectTextButtonViewState

@Composable
fun ConnectCallTransferTypeSelectionView(viewState: ConnectCallTransferSelectionViewState) {
    Box(modifier = Modifier.fillMaxSize()) {
        viewState.connectHeaderViewState?.let {
            ConnectHeaderView(viewState = it)
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            ConnectCallerInformationView(viewState = viewState.callerInfoViewState)

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.general_view_small)))

            TransferSelectionQuestionsView(viewState)

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.general_view_xmedium)))

            TransferSelectionOptionsView(viewState)
        }
        viewState.cancelTxtBtnViewState?.let {
            BottomCancelView(
                modifier = Modifier.align(Alignment.BottomCenter),
                viewState = it
            )
        }
    }
}

@Composable
fun TransferSelectionQuestionsView(viewState: ConnectCallTransferSelectionViewState) {
    Text(
        modifier = Modifier.clickable (onClick = { viewState.onChangeTransferSelected?.invoke() }),
        text = stringResource(id = R.string.general_change),
        style = TypographyButton.withTextColor(
            color =
            colorResource(id = R.color.connectSecondaryDarkBlue)
        ),
    )

    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.general_view_large)))

    Text(
        text = stringResource(id = R.string.active_call_transfer_type_selection_question),
        style = TypographyBody2Heavy.withTextColor(
            color = colorResource(
                id = R.color.connectSecondaryDarkBlue
            )
        )
    )
}

@Composable
fun BottomCancelView(modifier: Modifier = Modifier, viewState: ConnectTextButtonViewState) {
    Column(
        modifier = modifier
    ) {
        ConnectTextButton(viewState = viewState)
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.general_view_xlarge)))
    }
}

@Composable
fun TransferSelectionOptionsView(viewState: ConnectCallTransferSelectionViewState) {
    Row() {

        OptionsButtonView(
            R.string.fa_arrow_up_right,
            stringResource(id = R.string.call_transfer_type_option_blind),
            onOptionSelected = {
                viewState.onBlindOptionSelected?.invoke()
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        OptionsButtonView(
            fontAwesomeImage = R.string.fa_user_headset,
            buttonTitle = stringResource(
                id = R.string.call_transfer_type_option_warm
            ),
            onOptionSelected = {
                viewState.onWarmOptionSelected?.invoke()
            }
        )

    }
}

@Composable
fun OptionsButtonView(fontAwesomeImage: Int, buttonTitle: String, onOptionSelected: ()-> Unit) {

    val defaultBackgroundColor = colorResource(id = R.color.connectGrey01)
    val pressedBackgroundColor = colorResource(id = R.color.connectSecondaryLightBlue)

    val defaultBorderColor = colorResource(id = R.color.connectGrey01)
    val pressedBorderColor = colorResource(id = R.color.connectPrimaryBlue)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    var isSelected by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(if (isPressed || isSelected) pressedBackgroundColor else defaultBackgroundColor)
    val borderColor by animateColorAsState(if (isPressed || isSelected) pressedBorderColor else defaultBorderColor)

    val onClick = {
        isSelected = !isSelected
        if (isSelected){
        onOptionSelected.invoke()
        }
    }

    Box(
        modifier = Modifier
            .height(dimensionResource(id = R.dimen.general_view_xxxxxxxxlarge))
            .width(dimensionResource(id = R.dimen.general_view_xxxxxxxxlarge))
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .then(
                if (isPressed || isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(fontAwesomeImage),
                fontFamily = FontAwesomeV6,
                color = colorResource(id = R.color.connectPrimaryBlue),
                fontSize = dimensionResource(
                    R.dimen.text_connect_empty_title_font_size
                ).value.sp,
            )
            Spacer(
                modifier = Modifier.height(
                    dimensionResource(
                        id = R.dimen.general_view_xxsmall
                    )
                )
            )
            Text(
                text = buttonTitle,
                style = TypographyCaption1Heavy.withTextColor(
                    color = colorResource(id = R.color.connectSecondaryDarkBlue)
                )
            )
        }
    }
}

@Preview
@Composable
fun ConnectCallTransferTypeSelectionViewPreview() {
    ConnectCallTransferTypeSelectionView(
        ConnectCallTransferSelectionViewState(
            callerInfoViewState = ConnectCallerInfoViewState(
                callerDisplayName = "Anjan N",
                callerPhoneNumber = "555-555-5555"
            ),
            cancelTxtBtnViewState = ConnectTextButtonViewState(
                text = "Cancel",
                textColor = R.color.connectSecondaryDarkBlue,
                onButtonClicked = {}
            ),
            connectHeaderViewState = ConnectHeaderViewState(onCloseButtonClick = {})
        )
    )
}