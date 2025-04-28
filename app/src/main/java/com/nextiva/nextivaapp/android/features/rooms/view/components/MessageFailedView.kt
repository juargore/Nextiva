package com.nextiva.nextivaapp.android.features.rooms.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.nextiva.nextivaapp.android.features.ui.theme.FontLato
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1Heavy

@Composable
fun MessageFailedView(modifier: Modifier, onClicked: (() -> Unit)? = null) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.tab_badge_text_size))
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(colorResource(R.color.connectSecondaryRed))
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "!",
                color = colorResource(R.color.connectWhite),
                fontFamily = FontLato,
                fontWeight = FontWeight.W800,
                fontSize = dimensionResource(R.dimen.avatar_initials_text_size_xsmall).value.sp,
                textAlign = TextAlign.Center
            )
        }

        Text(
            modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.general_padding_xsmall),
                    end = dimensionResource(R.dimen.general_padding_xsmall)),
            text = stringResource(R.string.room_conversation_unable_to_send),
            textAlign = TextAlign.Center,
            color = colorResource(R.color.connectSecondaryRed),
            style = TypographyCaption1,
        )

        Text(
            modifier = Modifier.clickable { onClicked?.let { it() } },
            text = stringResource(R.string.room_conversation_retry),
            textAlign = TextAlign.Center,
            color = colorResource(R.color.connectPrimaryBlue),
            style = TypographyCaption1Heavy,
        )
    }
}

@Preview
@Composable
fun MessageFailedViewDefaultPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        MessageFailedView(
            modifier = Modifier
                .padding(end = dimensionResource(id = R.dimen.general_padding_medium))
        ) {

        }
    }
}