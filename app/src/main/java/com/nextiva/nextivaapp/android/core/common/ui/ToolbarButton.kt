package com.nextiva.nextivaapp.android.core.common.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeSolid

@Composable
fun ToolbarButton(
    modifier: Modifier = Modifier,
    iconId: Int,
    colorId: Int,
    fontFamily: FontFamily,
    onClick: () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }
    val backgroundColor = if (isPressed) colorResource(id = R.color.connectGrey03) else Color.Transparent

    Box(modifier = modifier
        .width(dimensionResource(id = R.dimen.toolbar_button_size) +
                dimensionResource(R.dimen.general_padding_small) +
                dimensionResource(R.dimen.general_padding_medium))
        .height(dimensionResource(id = R.dimen.toolbar_button_size))
        .padding(
            start = dimensionResource(R.dimen.general_padding_small),
            end = dimensionResource(R.dimen.general_padding_medium)
        )
        .background(backgroundColor, CircleShape)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    try {
                        isPressed = true
                        awaitRelease()
                    } finally {
                        isPressed = false
                        onClick()
                    }
                },
            )
        }
    ) {
        Text(
            text = stringResource(iconId),
            color = colorResource(colorId),
            fontFamily = fontFamily,
            fontSize = dimensionResource(R.dimen.general_view_medium).value.sp,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ToolbarButtonPreview() {
    Row {
        ToolbarButton(
            iconId = R.string.fa_arrow_left,
            colorId = R.color.connectGrey10,
            fontFamily = FontAwesome
        ) {}
        ToolbarButton(
            iconId = R.string.fa_phone_alt,
            colorId = R.color.connectPrimaryBlue,
            fontFamily = FontAwesomeSolid
        ) {}
    }
}
