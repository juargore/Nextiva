package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeV6
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyH5
import com.nextiva.nextivaapp.android.features.ui.theme.withTextColor

@Composable
fun ConnectNoResultsFoundView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.connectWhite)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .padding(vertical = dimensionResource(id = R.dimen.general_padding_small))
                .background(
                    shape = CircleShape,
                    color = colorResource(id = R.color.connectPrimaryLightBlue)
                )
                .requiredSize(dimensionResource(id = R.dimen.general_view_xxxxxxxxxxxlarge).value.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.general_padding_xsmall)),
                text = stringResource(id = R.string.fa_search),
                textAlign = TextAlign.Center,
                fontSize = dimensionResource(R.dimen.material_text_display2).value.sp,
                fontWeight = FontWeight.W400,
                color = colorResource(id = R.color.connectGrey08),
                fontFamily = FontAwesomeV6
            )

        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.general_view_small)))
        Text(
            modifier = Modifier.padding(),
            text = stringResource(id = R.string.connect_rooms_search_no_results_title),
            style = TypographyH5.withTextColor(color = colorResource(id = R.color.connectSecondaryDarkBlue))
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.general_view_small)))

        Text(
            modifier = Modifier.padding(),
            text = stringResource(id = R.string.connect_contact_selection_no_result_try_dialing_text),
            style = TypographyBody2.withTextColor(color = colorResource(id = R.color.connectGrey10))
        )
    }
}

@Composable
@Preview
private fun ConnectNoResultsFoundPreview() {
    ConnectNoResultsFoundView()
}