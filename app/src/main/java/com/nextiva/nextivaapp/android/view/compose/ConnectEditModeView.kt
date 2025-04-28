package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.material.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.Typography
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2
import com.nextiva.nextivaapp.android.models.ConnectEditModeViewState


@Composable
fun ConnectEditModeView(modifier: Modifier = Modifier, connectEditModeViewStateLiveDate: MutableLiveData<ConnectEditModeViewState>){
    val connectEditModeViewState by connectEditModeViewStateLiveDate.observeAsState()
    Column(
        modifier = modifier.background(colorResource(id = R.color.connectGrey01))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(id = R.dimen.general_horizontal_margin_medium).value.dp,
                    end = dimensionResource(id = R.dimen.general_horizontal_margin_medium).value.dp,
                    top = dimensionResource(id = R.dimen.general_horizontal_margin_large).value.dp,
                    bottom = dimensionResource(id = R.dimen.general_horizontal_margin_medium).value.dp
                )
                .background(colorResource(id = R.color.connectGrey01)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.requiredSize(dimensionResource(id = R.dimen.avatar_connect_call_size).value.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TriStateCheckbox(
                        state = connectEditModeViewState?.selectAllCheckState ?: ToggleableState.Off,
                        onClick = {
                            connectEditModeViewState?.onSelectAllCheckedChanged?.invoke(connectEditModeViewState?.selectAllCheckState ?: ToggleableState.Off)
                        },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = colorResource(R.color.connectWhite),
                            checkedColor = colorResource(R.color.nextivaPrimaryBlue),
                            uncheckedColor = colorResource(R.color.connectGrey09)
                        ),

                    )
                }
                
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.general_view_small).value.dp))

                Text(
                    style = Typography.body2,
                    color = colorResource(id = R.color.connectSecondaryDarkBlue),
                    text = connectEditModeViewState?.itemCountDescription ?: ""
                )
            }

            Text(
                modifier = Modifier
                    .clickable {
                        if (connectEditModeViewState?.shouldShowBulkUpdateActionIcons == true)
                            connectEditModeViewState?.onMarkReadIconClicked?.invoke()
                    }
                    .alpha(if (connectEditModeViewState?.shouldShowBulkUpdateActionIcons == true) 1f else 0f),
                text = stringResource(R.string.fa_envelope_open),
                textAlign = TextAlign.Center,
                fontSize = dimensionResource(R.dimen.connect_edit_mode_icon_size).value.sp,
                fontWeight = FontWeight.W400,
                color = colorResource(R.color.connectGrey10),
                fontFamily = FontAwesome,
            )

            Text(
                modifier = Modifier
                    .clickable {
                        if (connectEditModeViewState?.shouldShowBulkUpdateActionIcons == true)
                            connectEditModeViewState?.onMarkUnReadIconClicked?.invoke()
                    }
                    .alpha(if (connectEditModeViewState?.shouldShowBulkUpdateActionIcons == true) 1f else 0f),
                text = stringResource(R.string.fa_envelope),
                textAlign = TextAlign.Center,
                fontSize = dimensionResource(R.dimen.connect_edit_mode_icon_size).value.sp,
                fontWeight = FontWeight.W400,
                color = colorResource(R.color.connectGrey10),
                fontFamily = FontAwesome,
            )

            Text(
                modifier = Modifier
                    .clickable {
                        if (connectEditModeViewState?.shouldShowBulkDeleteActionIcons == true)
                            connectEditModeViewState?.onDeleteIconClicked?.invoke()
                    }
                    .alpha(if (connectEditModeViewState?.shouldShowBulkDeleteActionIcons == true) 1f else 0f),
                text = stringResource(R.string.fa_trash_alt),
                textAlign = TextAlign.Center,
                fontSize = dimensionResource(R.dimen.connect_edit_mode_icon_size).value.sp,
                fontWeight = FontWeight.W400,
                color = colorResource(R.color.connectPrimaryRed),
                fontFamily = FontAwesome,
            )

            Text(
                modifier = Modifier
                    .clickable { connectEditModeViewState?.onDoneClicked?.invoke() },
                text = stringResource(id = R.string.connect_edit_mode_section_done),
                color = colorResource(id = R.color.connectPrimaryBlue),
                style = TypographyBody2
            )
        }
    }

}

@Preview
@Composable
fun ConnectEditModeViewPreview() {
    ConnectEditModeView(
        connectEditModeViewStateLiveDate = MutableLiveData(
            ConnectEditModeViewState(
                itemCountDescription = "Showing 300"
            )
        )
    )
}