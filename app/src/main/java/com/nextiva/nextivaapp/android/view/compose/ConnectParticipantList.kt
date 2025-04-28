package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.withTextColor
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectParticipantsListViewState

@Composable
fun ConnectParticipantsList(
    modifier: Modifier = Modifier,
    viewState: ConnectParticipantsListViewState,
) {
    Box(modifier.fillMaxSize()) {

        Column {

            ConnectHeaderView(viewState = viewState.headerViewState)

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.general_view_xmedium)))

            Column(
                Modifier
                    .nestedScroll(rememberNestedScrollInteropConnection())
                    .padding(
                        start = dimensionResource(id = R.dimen.general_view_small),
                        end = dimensionResource(id = R.dimen.general_view_small),
                        top = dimensionResource(id = R.dimen.general_view_xmedium),
                        bottom = dimensionResource(id = R.dimen.general_view_xmedium)
                    )
            ) {
                Text(
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.general_view_small)),
                    text = stringResource(id = R.string.active_call_participants),
                    style = TypographyBody1Heavy.withTextColor(color = colorResource(id = R.color.connectSecondaryDarkBlue)),
                )
                Surface(
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .background(color = colorResource(id = R.color.connectWhite))
                            .fillMaxWidth()
                            .nestedScroll(rememberNestedScrollInteropConnection()),
                        verticalArrangement = Arrangement.spacedBy(
                            dimensionResource(id = R.dimen.general_view_small)
                        )
                    ) {
                        items(
                            count = viewState.participantsList.size,
                            key = { index -> index }
                        ) { item ->
                            val listItem = viewState.participantsList[item]
                            ConnectContactListItemView(viewState = listItem)
                        }
                    }
                }
            }
        }

        BottomCancelView(
            modifier = Modifier.align(Alignment.BottomCenter),
            viewState = viewState.closeButtonViewState
        )
    }
}