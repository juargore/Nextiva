package com.nextiva.nextivaapp.android.features.rooms.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1

@Composable
fun RoundButton(buttonActiveTitle: String,
                buttonInactiveTitle: String,
                buttonActiveIcon: String,
                buttonInactiveIcon: String,
                isActive: Boolean,
                clicked: () -> Unit) {
    var active by remember { mutableStateOf(isActive) }

    Column(
        modifier = Modifier
            .width(dimensionResource(R.dimen.general_view_xxxxxlarge)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.general_padding_xsmall),
                    end = dimensionResource(R.dimen.general_padding_xsmall),
                    bottom = dimensionResource(R.dimen.general_padding_xsmall)
                )
                .width(dimensionResource(R.dimen.general_view_xxlarge))
                .height(dimensionResource(R.dimen.general_view_xxlarge))
                .clip(CircleShape)
                .background(colorResource(if (active) R.color.roomButtonActive else R.color.connectGrey01))
                .clickable(
                    onClick = {
                        active = !active
                        clicked()
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (active) buttonActiveIcon else buttonInactiveIcon,
                textAlign = TextAlign.Center,
                color = colorResource(if (active) R.color.connectWhite else R.color.connectGrey09),
                fontFamily = FontAwesome,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                fontSize = dimensionResource(id = R.dimen.material_text_title).value.sp,
            )
        }

        Text(
            text = if (active) buttonActiveTitle else buttonInactiveTitle,
            style = TypographyCaption1,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.connectGrey10),
            modifier = Modifier.padding(end = dimensionResource(R.dimen.general_padding_xxsmall))
        )
    }

}

@Preview(showSystemUi = true)
@Composable
fun RoundButtonPreview() {
    Row {
        Box(modifier = Modifier
            .width(dimensionResource(R.dimen.general_view_xxxxxxlarge))
            .height(dimensionResource(R.dimen.general_view_xxxxxxlarge))) {
            RoundButton(
                buttonActiveTitle = stringResource(R.string.connect_room_details_favorite_button),
                buttonInactiveTitle = stringResource(R.string.connect_room_details_favorite_button),
                buttonActiveIcon = stringResource(R.string.fa_star),
                buttonInactiveIcon = stringResource(R.string.fa_star),
                isActive = false
            ) { }
        }
        Box(modifier = Modifier
            .width(dimensionResource(R.dimen.general_view_xxxxxxlarge))
            .height(dimensionResource(R.dimen.general_view_xxxxxxlarge))) {
            RoundButton(
                buttonActiveTitle = stringResource(R.string.connect_room_details_mute_button),
                buttonInactiveTitle = stringResource(R.string.connect_room_details_mute_button),
                buttonActiveIcon = stringResource(R.string.fa_bell),
                buttonInactiveIcon = stringResource(R.string.fa_bell_slash),
                isActive = false
            ) { }
        }
        Box(modifier = Modifier
            .width(dimensionResource(R.dimen.general_view_xxxxxxlarge))
            .height(dimensionResource(R.dimen.general_view_xxxxxxlarge))) {
            RoundButton(
                buttonActiveTitle = stringResource(R.string.connect_room_details_hide_button),
                buttonInactiveTitle = stringResource(R.string.connect_room_details_hide_button),
                buttonActiveIcon = stringResource(R.string.fa_eye),
                buttonInactiveIcon = stringResource(R.string.fa_eye_slash),
                isActive = false
            ) { }
        }
    }
}