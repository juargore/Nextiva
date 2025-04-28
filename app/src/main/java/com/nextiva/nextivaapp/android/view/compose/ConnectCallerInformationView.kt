package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyButton
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyH5
import com.nextiva.nextivaapp.android.util.extensions.contentDescription
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectCallerInfoViewState


@Composable
fun ConnectCallerInformationView(
    modifier: Modifier = Modifier,
    viewState: ConnectCallerInfoViewState?
) {

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        viewState?.avatarViewState?.avatarBitMap?.let {
            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.general_view_xxxxxxxxxlarge)),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = stringResource(id = R.string.active_call_caller_avatar_content_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.general_view_xxxxxxxxxlarge))
                        .clip(CircleShape)
                )
                viewState.conferenceParticipantCount?.let { participantCount ->
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.general_view_xxlarge)) // Adjust the size as needed
                            .background(
                                color = colorResource(id = R.color.avatarConferenceCountBoxColor),
                                shape = CircleShape
                            )
                            .border(
                                BorderStroke(
                                    dimensionResource(id = R.dimen.general_padding_xxsmall),
                                    color = colorResource(id = R.color.connectWhite)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+$participantCount",
                            color = colorResource(id = R.color.connectSecondaryDarkBlue),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W800,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Text(
            modifier = Modifier
                .contentDescription(viewState?.callerDisplayName ?: "")
                .padding(top = dimensionResource(id = R.dimen.general_vertical_margin_large)),
            color = colorResource(id = R.color.connectSecondaryDarkBlue),
            text = viewState?.callerDisplayName ?: "",
            textAlign = TextAlign.Center,
            style = TypographyH5
        )

        Text(
            modifier = Modifier
                .contentDescription(viewState?.callerPhoneNumber ?: "")
                .padding(top = dimensionResource(id = R.dimen.general_padding_small)),
            text = viewState?.callerPhoneNumber ?: "",
            color = colorResource(
                id = if (viewState?.callerPhoneNumber?.isConferenceCall() == true) {
                    R.color.nextivaPrimaryBlue
                } else {
                    R.color.connectGrey10
                }
            ),
            style = if (viewState?.callerPhoneNumber?.isConferenceCall() == true) {
                TypographyButton
            } else {
                TypographyBody2
            }
        )
        if (viewState?.shouldShowTimer == true) {
            Text(
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.general_padding_small)),
                text = viewState.callTimer ?: "",
                color = colorResource(id = R.color.connectPrimaryGreen),
                style = TypographyCaption1
            )

        }

    }
}

@Composable
private fun String.isConferenceCall() =
    this.contains(stringResource(id = R.string.active_call_participants))