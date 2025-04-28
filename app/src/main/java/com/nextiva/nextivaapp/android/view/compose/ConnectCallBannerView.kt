package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeV6
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1
import com.nextiva.nextivaapp.android.features.ui.theme.withTextColor
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectCallBannerViewState

@Composable
fun ConnectCallBannerView(viewState: ConnectCallBannerViewState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.general_view_xsmall)),
                color = colorResource(id = R.color.connectSecondaryDarkBlue)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(
                )
        ) {
            Column(modifier = Modifier.padding(start = dimensionResource(id = R.dimen.general_padding_large))) {
                viewState.avatarViewState?.avatarInfo?.let {

                    ComposeAvatarView(
                        avatarInfo = it
                    )
                }
            }

            Column(
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.general_padding_xmedium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.general_padding_xsmall))
            ) {
                Text(
                    text = viewState.callerDisplayName ?: "",
                    style = TypographyBody2Heavy.withTextColor(
                        color = colorResource(
                            id = R.color.connectWhite
                        )
                    )
                )

                Text(
                    text = viewState.callerPhoneNumber ?: "",
                    style = TypographyCaption1.withTextColor(
                        color = colorResource(
                            id = R.color.connectWhite
                        )
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(
                    top = dimensionResource(id = R.dimen.general_padding_xsmall),
                    bottom = dimensionResource(id = R.dimen.general_padding_xsmall),
                    end = dimensionResource(id = R.dimen.general_padding_xmedium)
                )
        ) {
            Box(
                modifier = Modifier
                    .clickable { viewState.onResumeButtonClicked?.invoke() }
                    .padding(
                        top = dimensionResource(id = R.dimen.general_padding_small),
                        bottom = dimensionResource(id = R.dimen.general_padding_small),
                    )
                    .background(
                        color = colorResource(id = R.color.connectPrimaryGreen),
                        shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.general_view_xxxlarge))
                    )
                    .padding(dimensionResource(id = R.dimen.general_padding_xmedium))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.general_padding_xsmall)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.general_padding_xxmedium))
                ) {
                    Text(
                        text = stringResource(R.string.fa_circle_play),
                        textAlign = TextAlign.Center,
                        fontSize = dimensionResource(R.dimen.material_text_title1).value.sp,
                        fontWeight = FontWeight.W400,
                        color = colorResource(id = R.color.connectWhite),
                        fontFamily = FontAwesomeV6
                    )

                    Text(
                        text = stringResource(id = R.string.active_call_resume), style = TypographyBody2Heavy.withTextColor(
                            color = colorResource(
                                id = R.color.connectWhite
                            )
                        )
                    )
                }
            }

        }
    }
}


@Composable
@Preview
fun ConnectCallBannerViewPreview() {
    ConnectCallBannerView(
        ConnectCallBannerViewState(
            callerDisplayName = "Anjan N",
            callerPhoneNumber = "555-55-5555"
        )
    )
}