package com.nextiva.nextivaapp.android.core.notifications.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.api.Holiday
import com.nextiva.nextivaapp.android.core.notifications.models.HolidaySelection
import com.nextiva.nextivaapp.android.features.rooms.view.components.ScreenTitleBarView
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyButton
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyH6
import com.nextiva.nextivaapp.android.util.extensions.contentDescription

@Composable
fun ObservedHolidayScreenView(
    selectedHolidayList: MutableList<HolidaySelection>?,
    holidayList: ArrayList<Holiday>,
    onBackButton: () -> Unit,
    observedHolidayAddListener: (MutableList<HolidaySelection>?) -> Unit
) {
    val selectedHolidays =
        remember { selectedHolidayList }
    var selectAllChecked by remember { mutableStateOf(false) }
    val title = stringResource(id = R.string.notification_schedules_observed_holiday_toolbar_title)
    val descText = stringResource(id = R.string.notification_schedules_observed_holiday_description)
    val titleStyle = TypographyH6
    val descStyle = TypographyBody1
    val greyColor = colorResource(id = R.color.connectGrey10)
    var isButtonEnabled by remember(selectedHolidays) { mutableStateOf(selectedHolidays?.any { it.isSelected } ?: false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.connectWhite))
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(bottom = dimensionResource(id = R.dimen.general_padding_medium))
        ) {
            ScreenTitleBarView(
                title = title,
                onBackButton = onBackButton,
                titleStyle = titleStyle
            )

            Text(
                modifier = Modifier.padding(
                    top = dimensionResource(id = R.dimen.general_padding_xlarge),
                    start = dimensionResource(id = R.dimen.general_padding_medium)
                ),
                text = descText,
                style = descStyle,
                color = greyColor
            )

            Spacer(Modifier.height(dimensionResource(id = R.dimen.general_padding_large)))

            Text(
                modifier = Modifier
                    .contentDescription(stringResource(id = R.string.create_schedule_observed_holidays_select_all_accessibility_id))
                    .padding(
                        start = dimensionResource(id = R.dimen.general_padding_medium),
                        end = dimensionResource(id = R.dimen.general_padding_medium)
                    )
                    .align(Alignment.End)
                    .clickable {

                        if (!selectAllChecked) {
                            selectedHolidays?.clear()
                            selectedHolidays?.addAll(holidayList.map { HolidaySelection(it, true) })
                            isButtonEnabled = true
                        }
                        selectAllChecked = !selectAllChecked
                    },
                text = stringResource(id = R.string.notification_schedules_observed_holiday_select_all),
                style = TypographyBody1Heavy,
                color = colorResource(id = R.color.connectPrimaryBlue)
            )

            Spacer(Modifier.height(dimensionResource(id = R.dimen.general_padding_large)))

            LaunchedEffect(selectedHolidays) {
                // Do nothing. This effect is here just to recompose the composable whenever the selectedHolidays changes.
            }

            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(
                        start = dimensionResource(id = R.dimen.general_padding_medium),
                        end = dimensionResource(id = R.dimen.general_padding_medium)
                    ),
            ) {
                items(holidayList) { holidayItem ->
                    var holidaySelection =
                        selectedHolidays?.firstOrNull { it.holiday.name == holidayItem.name }

                    holidayItem.name?.let { holidayName ->
                        HolidayItemView(
                            title = holidayName,
                            onItemChecked = { checked ->
                                holidaySelection = selectedHolidays?.firstOrNull(){it.holiday.name == holidayItem.name}
                                if (holidaySelection != null) {
                                    holidaySelection?.isSelected = checked
                                    if (!checked) {
                                        selectAllChecked = false
                                    }
                                } else if (checked) {
                                    selectedHolidays?.add(HolidaySelection(holidayItem, true))
                                }
                                isButtonEnabled = selectedHolidays?.any { it.isSelected } == true
                            },
                            isItemChecked = selectAllChecked || holidaySelection?.isSelected ?: false,
                            selectedHolidays = selectedHolidays,
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.general_padding_xmedium)))
                    }
                }
            }

            Button(
                modifier = Modifier
                    .contentDescription(stringResource(id = R.string.create_schedule_custom_specific_holiday_button_add, isButtonEnabled.toString().replaceFirstChar { it.uppercase() }))
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.general_view_xxxxxlarge))
                    .padding(dimensionResource(id = R.dimen.general_padding_medium)),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.connectPrimaryBlue)),
                shape = RoundedCornerShape(dimensionResource(id = androidx.cardview.R.dimen.cardview_default_radius)),
                onClick = {
                    observedHolidayAddListener.invoke(selectedHolidays)
                },
                enabled = isButtonEnabled
            ) {
                Text(
                    modifier = Modifier.contentDescription(stringResource(id = R.string.notification_schedules_observed_holiday_add)),
                    text = stringResource(id = R.string.notification_schedules_observed_holiday_add),
                    color = colorResource(id = R.color.connectWhite),
                    style = TypographyButton
                )
            }
        }
    }
}