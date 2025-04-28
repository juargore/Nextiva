package com.nextiva.nextivaapp.android.core.notifications.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1Heavy
import com.nextiva.nextivaapp.android.util.extensions.contentDescription

@Composable
fun ScheduleItemView(
    title: String,
    days: String,
    hours: String,
    isSelected: Boolean,
    clicked: () -> Unit) {

    Box(modifier = Modifier
        .fillMaxWidth()
        .background(colorResource(id = if (isSelected) R.color.connectGrey01 else R.color.connectWhite))
    ) {

        Column(modifier = Modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.general_padding_medium),
                vertical = dimensionResource(id = R.dimen.general_padding_medium)
            )
            .fillMaxWidth()
            .clickable {
                clicked()
            }
        ) {

            Text(
                modifier = Modifier
                    .contentDescription(title)
                    .padding(bottom = dimensionResource(id = R.dimen.general_padding_xsmall)),
                text = title,
                style = TypographyBody2Heavy,
                color = colorResource(R.color.connectSecondaryDarkBlue)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .padding(bottom = dimensionResource(id = R.dimen.general_padding_xxsmall)),
                    text = stringResource(id = R.string.notification_schedules_days_label),
                    style = TypographyCaption1Heavy,
                    color = colorResource(R.color.connectGrey10)
                )

                Text(
                    modifier = Modifier
                        .contentDescription(stringResource(R.string.notifications_schedules_schedule_days_accessibility_id, title))
                        .padding(start = dimensionResource(id = R.dimen.general_padding_xxsmall)),
                    text = days,
                    style = TypographyCaption1,
                    color = colorResource(R.color.connectGrey10)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = Modifier.align(Alignment.Top),
                    text = stringResource(id = R.string.notification_schedules_hours_label),
                    style = TypographyCaption1Heavy,
                    color = colorResource(R.color.connectGrey10)
                )

                Text(
                    modifier = Modifier
                        .contentDescription(stringResource(R.string.notifications_schedules_schedule_hours_accessibility_id, title))
                        .padding(
                            start = dimensionResource(id = R.dimen.general_padding_xxsmall),
                            top = dimensionResource(id = R.dimen.general_padding_xxsmall)
                        )
                        .align(Alignment.Top),
                    text = hours,
                    style = TypographyCaption1,
                    color = colorResource(R.color.connectGrey10)
                )
            }
        }

        if (isSelected) {
            val accessibilityText = stringResource(R.string.notifications_schedules_checkmark_selected_accessibility_id, title)
            Text(
                modifier = Modifier
                    .semantics { stateDescription = accessibilityText}
                    .contentDescription(stringResource(R.string.notifications_schedules_checkmark_accessibility_id, title))
                    .padding(end = dimensionResource(id = R.dimen.general_padding_xlarge))
                    .align(Alignment.CenterEnd),
                text = stringResource(R.string.fa_check),
                style = TextStyle(
                    color = colorResource(R.color.connectPrimaryBlue),
                    fontSize = dimensionResource(R.dimen.general_view_small).value.sp,
                    fontFamily = FontAwesome,
                    fontWeight = FontWeight.W400,
                ),
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = colorResource(R.color.connectGrey03),
            thickness = dimensionResource(R.dimen.hairline_small),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    Row {
        ScheduleItemView(title = "Pre-work hours", days = "Mon-Wed, Fri, Sat", hours = "9:00 am - 12:00 pm | 1:00 pm - 2 pm", isSelected = false) { }
    }
}