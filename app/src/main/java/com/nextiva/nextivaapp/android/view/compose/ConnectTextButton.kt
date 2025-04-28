package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeV6
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyButton
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectButtonType
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectTextButtonViewState

@Composable
fun ConnectTextButton(modifier: Modifier = Modifier, viewState: ConnectTextButtonViewState) {

    if (viewState.buttonType == ConnectButtonType.PRIMARY_GHOST) {
        Text(
            modifier = modifier
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = viewState.onButtonClicked
                )
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = viewState.text,
            style = TypographyButton,
            color = colorResource(id = viewState.textColor)
        )
    } else {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (viewState.isButtonEnabled) 1f else 0.3f),
            enabled = viewState.isButtonEnabled,
            onClick = viewState.onButtonClicked,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = getButtonColors(buttonType = viewState.buttonType),
                disabledBackgroundColor = getButtonColors(buttonType = viewState.buttonType)
            ),
            shape = RoundedCornerShape(4.dp), // Set the rounded corners here
        ) {

            viewState.leadingIcon?.let {
                Text(
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.general_padding_xsmall)),
                    text = stringResource(viewState.leadingIcon),
                    textAlign = TextAlign.Center,
                    fontSize = dimensionResource(R.dimen.material_text_subhead).value.sp,
                    fontWeight = FontWeight.W400,
                    color = colorResource(id = viewState.textColor),
                    fontFamily = FontAwesomeV6,
                )
            }

            Text(
                modifier = modifier
                    .padding(dimensionResource(id = R.dimen.general_padding_xsmall)),
                textAlign = TextAlign.Center,
                text = viewState.text,
                style = TypographyButton,
                color = colorResource(id = viewState.textColor)
            )
        }

    }
}

@Composable
private fun getButtonColors(buttonType: ConnectButtonType?) =
    if (buttonType == ConnectButtonType.PRIMARY) {
        colorResource(id = R.color.connectPrimaryBlue)
    } else if (buttonType == ConnectButtonType.SECONDARY) {
        colorResource(id = R.color.connectGrey03)
    } else {
        colorResource(id = R.color.transparent)
    }

@Preview
@Composable
private fun ConnectTextButtonPreview() {
    ConnectTextButton(viewState = ConnectTextButtonViewState(
        text = "Text button",
        isButtonEnabled = false,
        buttonType = ConnectButtonType.PRIMARY,
        onButtonClicked = {}
    ))
}
