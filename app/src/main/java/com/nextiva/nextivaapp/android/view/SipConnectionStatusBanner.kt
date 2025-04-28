package com.nextiva.nextivaapp.android.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeV6
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2HeavySpan
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2Span
import com.nextiva.nextivaapp.android.features.ui.theme.withTextColor
import com.nextiva.pjsip.pjsip_lib.sipservice.SipConnectionStatus

@Composable
fun SipConnectionStatusBanner(status: SipConnectionStatus, onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(colorResource(id = if (status == SipConnectionStatus.POOR) R.color.surfaceWarning else R.color.surfaceError))
        .padding(
            top = dimensionResource(id = R.dimen.general_padding_medium),
            bottom = dimensionResource(id = R.dimen.general_padding_medium),
            start = dimensionResource(id = R.dimen.general_padding_medium),
            end = dimensionResource(id = R.dimen.general_padding_medium)
        )) {
        Text(
            text = stringResource(R.string.fa_exclamation_circle),
            textAlign = TextAlign.Center,
            fontSize = dimensionResource(R.dimen.material_text_title1).value.sp,
            fontWeight = FontWeight.W400,
            color = colorResource(id = R.color.connectSecondaryRed),
            fontFamily = FontAwesomeV6
        )

        Text(
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.general_padding_small))
                .weight(1f),
            text = buildAnnotatedString {
                withStyle(TypographyBody2HeavySpan.withTextColor(color = colorResource(
                    id = R.color.connectSecondaryRed
                ))) {
                    append(
                        if (status == SipConnectionStatus.POOR) stringResource(id = R.string.active_call_poor_connection_banner_title) else stringResource(
                            id = R.string.active_call_lost_connection_banner_title
                        )
                    )
                }

                withStyle(TypographyBody2Span.withTextColor(color = colorResource(id = R.color.connectSecondaryRed))) {
                    append(" ")
                    append(if (status == SipConnectionStatus.POOR) stringResource(id = R.string.active_call_poor_connection_banner_message) else stringResource(
                        id = R.string.active_call_lost_connection_banner_message
                    ))
                }
            }
        )

        Text(
            modifier = Modifier.clickable {
                onClick()
            },
            text = stringResource(R.string.fa_times),
            textAlign = TextAlign.Center,
            fontSize = dimensionResource(R.dimen.material_text_title1).value.sp,
            fontWeight = FontWeight.W400,
            color = colorResource(id = R.color.avatarConnectOfflinePresenceStroke),
            fontFamily = FontAwesomeV6
        )
    }
}

@Preview
@Composable
fun SipConnectionStatusBannerPreview() {
    SipConnectionStatusBanner(status = SipConnectionStatus.LOST) {

    }
}