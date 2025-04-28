package com.nextiva.nextivaapp.android.view.compose
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeV6
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectHeaderViewState
@Composable
fun ConnectHeaderView(modifier: Modifier = Modifier, viewState: ConnectHeaderViewState) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (viewState.shouldShowRoundedCornerShape) {
                    Modifier.clip(
                        RoundedCornerShape(
                            topStart = dimensionResource(id = R.dimen.general_padding_medium),
                            topEnd = dimensionResource(
                                id = R.dimen.general_padding_medium
                            )
                        )
                    )
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(color = colorResource(id = R.color.connectGrey01))
            .height(dimensionResource(id = R.dimen.general_view_xxlarge)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End,
    ) {
        Text(
            modifier = Modifier
                .padding(end = dimensionResource(id = R.dimen.general_view_xmedium))
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = viewState.onCloseButtonClick
                ),
            textAlign = TextAlign.End,
            text = stringResource(id = R.string.fa_times),
            fontSize = dimensionResource(R.dimen.bottom_sheet_close_icon).value.sp,
            fontFamily = FontAwesomeV6,
            color = colorResource(id = R.color.connectGrey10)
        )
    }
}
@Preview
@Composable
fun ConnectHeaderViewPreview() {
    ConnectHeaderView(viewState = ConnectHeaderViewState(onCloseButtonClick = {}))
}