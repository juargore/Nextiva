package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectActiveCallViewState

@Composable
fun ConnectWarmTransferView(viewState: ConnectActiveCallViewState) {

    Box(modifier = Modifier.fillMaxSize()) {
        viewState.headerViewState?.let {
            ConnectHeaderView(viewState = it)
        }

        Column(
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.general_view_small),
                    end = dimensionResource(id = R.dimen.general_view_small)
                )
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            ConnectCallerInformationView(viewState = viewState.activeCallerInfo)

            ActiveCallPartialActionsView(modifier = Modifier, viewState)

        }

        bottomTransfersButtonView(
            modifier = Modifier.align(Alignment.BottomCenter),
            viewState = viewState
        )
    }
}

@Composable
fun ActiveCallPartialActionsView(
    modifier: Modifier = Modifier,
    connectActiveCallViewState: ConnectActiveCallViewState
) {

    Column(
        modifier
            .padding(horizontal = dimensionResource(id = R.dimen.general_view_xsmall))
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
    }
}

@Composable
fun bottomTransfersButtonView(
    modifier: Modifier = Modifier,
    viewState: ConnectActiveCallViewState
) {
    Column(
        modifier = modifier.padding(
            start = dimensionResource(id = R.dimen.general_view_small),
            end = dimensionResource(id = R.dimen.general_view_small),
            bottom = dimensionResource(id = R.dimen.general_view_small)
        )
    ) {
        viewState.completeTransferButton?.let { completeTransferButtonViewState ->
            ConnectTextButton(viewState = completeTransferButtonViewState)
        }

        viewState.cancelTransferButton?.let { cancelTransferButtonViewState ->
            ConnectTextButton(viewState = cancelTransferButtonViewState)
        }
    }
}