package com.nextiva.nextivaapp.android.view.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeSolidV6
import com.nextiva.nextivaapp.android.util.extensions.contentDescription
import com.nextiva.nextivaapp.android.view.SipConnectionStatusBanner
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectActiveCallViewState
import com.nextiva.pjsip.pjsip_lib.sipservice.SipConnectionStatus

@Composable
fun ConnectActiveCallView(connectActiveCallViewState: ConnectActiveCallViewState, onBack: () -> Unit) {
    Box {
        Column(modifier = Modifier.matchParentSize()) {
            if (connectActiveCallViewState.sipConnectionStatus != SipConnectionStatus.GOOD && !connectActiveCallViewState.wasSipConnectionBannerDismissed) {
                SipConnectionStatusBanner(status = connectActiveCallViewState.sipConnectionStatus) {
                    connectActiveCallViewState.wasSipConnectionBannerDismissed = true
                }
            }

            Text(
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.general_view_small),
                        start = dimensionResource(id = R.dimen.general_view_small)
                    )
                    .clickable { onBack() },
                text = stringResource(R.string.fa_arrow_left),
                style = TextStyle(
                    color = colorResource(R.color.connectGrey09),
                    fontSize = dimensionResource(R.dimen.material_text_headline).value.sp,
                    fontFamily = FontAwesome,
                    fontWeight = FontWeight.W400,
                ),
            )

            Box(
                modifier = Modifier.padding(
                    top = dimensionResource(id = R.dimen.general_padding_xxxlarge),
                    start = dimensionResource(id = R.dimen.general_view_small),
                    end = dimensionResource(id = R.dimen.general_view_small)
                )
            ) {
                connectActiveCallViewState.callBannerInfo?.let { ConnectCallBannerView(viewState = it) }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(
                            top = dimensionResource(id = R.dimen.general_view_xxxxxxxxxlarge),
                            start = dimensionResource(id = R.dimen.general_view_small),
                            end = dimensionResource(id = R.dimen.general_view_small),
                            bottom = dimensionResource(id = R.dimen.general_view_large)
                        )
                        .verticalScroll(rememberScrollState())
                ) {

                    ConnectCallerInformationView(
                        modifier = Modifier.clickable(onClick = connectActiveCallViewState.onActiveCallerInfoClick),
                        viewState = connectActiveCallViewState.activeCallerInfo
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    ActiveCallActionsView(modifier = Modifier, connectActiveCallViewState)

                    Spacer(modifier = Modifier.weight(1f))

                    ActiveCallEndButtonView(modifier = Modifier, connectActiveCallViewState)
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
private fun ActiveCallEndButtonView(
    modifier: Modifier = Modifier,
    connectActiveCallViewState: ConnectActiveCallViewState
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = { connectActiveCallViewState.onEndCallButtonClicked?.invoke() },
            modifier = Modifier
                .contentDescription(stringResource(id = R.string.active_call_end_call))
                .size(dimensionResource(id = R.dimen.general_view_xxxxlarge))
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(
                        color = colorResource(id = R.color.connectWhite),
                        radius = dimensionResource(id = R.dimen.general_view_xxxxlarge)
                    )
                ),
            shape = CircleShape,
            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.connectPrimaryRed),
                contentColor = colorResource(id = R.color.connectWhite),
            ),
            interactionSource = interactionSource,
        ) {
            Text(
                text = stringResource(R.string.fa_phone_hang_up),
                textAlign = TextAlign.Center,
                fontSize = dimensionResource(R.dimen.material_text_headline).value.sp,
                fontWeight = FontWeight.W400,
                color = colorResource(R.color.connectWhite),
                fontFamily = FontAwesomeSolidV6,
            )
        }
    }
}

@Composable
fun ActiveCallActionsView(
    modifier: Modifier = Modifier,
    connectActiveCallViewState: ConnectActiveCallViewState
) {

    Column(
        modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ConnectActiveCallButtonView(
                connectActiveCallViewState.holdButton
            )

            ConnectActiveCallButtonView(
                connectActiveCallViewState.muteButton
            )

            ConnectActiveCallButtonView(
                connectActiveCallViewState.keyPadButton
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ConnectActiveCallButtonView(
                connectActiveCallViewState.speakerButton
            )

            ConnectActiveCallButtonView(
                connectActiveCallViewState.addCallButton
            )

            ConnectActiveCallButtonView(
                connectActiveCallViewState.moreButton
            )
        }
    }
}

@Preview
@Composable
fun ConnectActiveCallViewPreview() {
    //ConnectActiveCallView()
}