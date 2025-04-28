package com.nextiva.nextivaapp.android.core.notifications.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.flowlayout.FlowRow
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.models.HolidaySelection
import com.nextiva.nextivaapp.android.core.notifications.viewmodel.CreateScheduleViewModel
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyForm

@Composable
fun ScheduleHolidayView(viewModel: CreateScheduleViewModel, modifier: Modifier) {
    val observedHolidaySelectionList by viewModel.observedHolidaySelectionList.collectAsState()
    val holidaySelectionList by viewModel.holidays.collectAsState()

    if ((observedHolidaySelectionList.isNotEmpty() && observedHolidaySelectionList.any { it.isSelected }) || holidaySelectionList.isNotEmpty()) {
        Column(
            modifier = modifier.padding(
                start = dimensionResource(id = R.dimen.general_padding_medium),
                end = dimensionResource(id = R.dimen.general_padding_medium),
                top = dimensionResource(id = R.dimen.general_padding_large),
                bottom = dimensionResource(id = R.dimen.general_padding_medium)
            )
        ) {
            val holidayList = observedHolidaySelectionList.toMutableStateList()
            holidayList.addAll(holidaySelectionList)

            Text(
                text = stringResource(id = R.string.notification_create_schedule_label_scheduled_holidays).uppercase(),
                style = TypographyForm,
                color = colorResource(R.color.connectGrey10)
            )
            ScheduleHolidayChipGroup(
                holidayStateList = holidayList,
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.general_padding_medium),
                    )
            ) {
                viewModel.deleteHoliday(it)
            }
        }

    }
}

@Preview(showSystemUi = true)
@Composable
fun ScheduleHolidayViewPreview() {
}

@Composable
fun ScheduleHolidayChipGroup(
    holidayStateList: List<HolidaySelection>,
    modifier: Modifier,
    onChipDeleted: (String) -> Unit,
) {

    Column(modifier = modifier) {
        FlowRow(
            crossAxisSpacing = dimensionResource(id = R.dimen.general_padding_xmedium),
            mainAxisSpacing = dimensionResource(id = R.dimen.general_padding_xmedium)
        ) {
            holidayStateList.filter { it.isSelected }.forEach { holiday ->
                Chip(
                    name = holiday.holiday.name.toString(),
                    referenceId = holiday.holiday.toString(),
                    onChipDeleted = { deletedHoliday ->
                        onChipDeleted(deletedHoliday)
                    }
                )
            }
        }
    }
}