/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.core.notifications.view

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.view.component.BottomSheetList
import com.nextiva.nextivaapp.android.core.notifications.view.component.LabeledDropDownButton
import com.nextiva.nextivaapp.android.core.notifications.view.component.SimpleCheckbox
import com.nextiva.nextivaapp.android.core.notifications.viewstate.CustomDateScheduleStartEndTimeViewState
import com.nextiva.nextivaapp.android.features.rooms.view.components.ScreenTitleBarLargeTextView
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyButton
import com.nextiva.nextivaapp.android.util.extensions.contentDescription
import com.nextiva.nextivaapp.android.view.compose.NextivaTextFieldView
import com.nextiva.nextivaapp.android.view.compose.NextivaTimeSelectView
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

/**
 * Created by Thaddeus Dannar on 4/5/23.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomDateLayout(
    onBackButton: () -> Unit,
    occurrenceOnClick: () -> Unit,
    occurrenceState: State<String>,
    dayOnClick: () -> Unit,
    dayState: State<String>,
    monthOnClick: () -> Unit,
    monthState: State<String>,
    isAllDayState: State<Boolean>,
    allDayEventOnCheckChange: (Boolean) -> Unit = {},
    isEventRepeatsForeverState: State<Boolean>,
    eventRepeatForeverOnCheckChange: (Boolean) -> Unit = {},
    startTimeState: State<LocalDateTime>,
    startTimeOnChange: (LocalDateTime) -> Unit = {},
    endTimeState: State<LocalDateTime>,
    endTimeOnChange: (LocalDateTime) -> Unit = {},
    occurrencesState: State<String>,
    occurrencesOnChange: (String) -> Unit = {},
    addButtonOnClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.connectWhite))

    ) {
        ScreenTitleBarLargeTextView(
            title = stringResource(R.string.title_activity_custom_date),
            onBackButton = onBackButton
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.general_padding_medium))

        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {


                Text(
                    text = stringResource(R.string.work_schedule_custom_dates_enter_date_message),
                    color = colorResource(R.color.connectGrey10),
                    style = TypographyBody1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.general_padding_medium))
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.general_padding_small),
                        Alignment.Top
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
                {
                    LabeledDropDownButton(
                        labelText = stringResource(id = R.string.work_schedule_custom_dates_occurrence),
                        buttonText = occurrenceState.value,
                        defaultText = stringResource(id = R.string.general_select),
                        onClick = occurrenceOnClick,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    LabeledDropDownButton(
                        labelText = stringResource(id = R.string.work_schedule_custom_dates_day),
                        buttonText = dayState.value,
                        defaultText = stringResource(id = R.string.general_select),
                        onClick = dayOnClick,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    LabeledDropDownButton(
                        labelText = stringResource(id = R.string.work_schedule_custom_dates_month),
                        buttonText = monthState.value,
                        defaultText = stringResource(id = R.string.general_select),
                        onClick = monthOnClick,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    SimpleCheckbox(
                        text = stringResource(id = R.string.work_schedule_custom_dates_this_is_an_all_day_event),
                        textStyle = TypographyBody1,
                        checked = isAllDayState.value,
                        onCheckChanged = allDayEventOnCheckChange

                    )

                    if (!isAllDayState.value) {

                        Row(
                            modifier = Modifier
                                .padding(
                                    start = dimensionResource(R.dimen.general_padding_medium),
                                    end = dimensionResource(R.dimen.general_padding_medium),
                                    top = dimensionResource(R.dimen.general_padding_small),
                                    bottom = dimensionResource(R.dimen.general_padding_small)
                                )
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            NextivaTimeSelectView(
                                modifier = Modifier
                                    .weight(0.5F)
                                    .padding(end = dimensionResource(R.dimen.general_padding_small)),
                                title = stringResource(id = R.string.notification_create_schedule_start_time_label),
                                defaultTime = startTimeState.value,
                                accessibilityId = stringResource(id = R.string.create_schedule_specified_custom_dates_start_time_accessibility_id)
                            ) { startTimeOnChange(it) }
                            NextivaTimeSelectView(
                                modifier = Modifier
                                    .weight(0.5F)
                                    .padding(start = dimensionResource(R.dimen.general_padding_small)),
                                title = stringResource(id = R.string.notification_create_schedule_end_time_label),
                                defaultTime = endTimeState.value,
                                accessibilityId = stringResource(id = R.string.create_schedule_specified_custom_dates_end_time_accessibility_id)
                            ) { endTimeOnChange(it) }
                        }
                    }


                    SimpleCheckbox(
                        text = stringResource(id = R.string.work_schedule_custom_dates_this_event_repeats_forever),
                        checked = isEventRepeatsForeverState.value,
                        onCheckChanged = eventRepeatForeverOnCheckChange
                    )

                    if (!isEventRepeatsForeverState.value) {
                        val focusManager = LocalFocusManager.current

                        val bringIntoViewRequester = remember { BringIntoViewRequester() }
                        val coroutineScope = rememberCoroutineScope()

                        NextivaTextFieldView(
                            title = stringResource(id = R.string.work_schedule_custom_dates_end_after),
                            value = occurrencesState.value,
                            onValueChanged = occurrencesOnChange,
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            hint = stringResource(id = R.string.work_schedule_custom_dates_occurrences),
                            columnModifier = Modifier
                                .bringIntoViewRequester(bringIntoViewRequester)
                                .onFocusEvent {
                                    if (it.isFocused) {
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                }
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.connectWhite))
            ) {

                val isEnabled =
                    (monthState.value.isNotEmpty() && dayState.value.isNotEmpty() && occurrenceState.value.isNotEmpty() && (isEventRepeatsForeverState.value || occurrencesState.value.isNotEmpty()))

                Button(
                    modifier = Modifier
                        .contentDescription(stringResource(id = R.string.notification_schedules_observed_holiday_add))
                        .semantics { stateDescription = isEnabled.toString().replaceFirstChar { it.uppercase() } }
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.general_view_xxxxxlarge))
                        .padding(dimensionResource(id = R.dimen.general_padding_medium)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.connectPrimaryBlue)),
                    shape = RoundedCornerShape(dimensionResource(id = androidx.cardview.R.dimen.cardview_default_radius)),
                    onClick = addButtonOnClick,
                    enabled = isEnabled

                ) {
                    Text(
                        modifier = Modifier.contentDescription(stringResource(id = R.string.notification_schedules_observed_holiday_add)),
                        text = stringResource(id = R.string.notification_schedules_observed_holiday_add),
                        color = colorResource(id = if (!isEnabled) R.color.connectPrimaryGrey else R.color.connectWhite),
                        style = TypographyButton
                    )
                }

            }
        }
    }

}

@Composable
fun WorkScheduleCustomDate(
    onBackButton: () -> Unit,
    occurrenceMutableLiveData: MutableLiveData<String> = MutableLiveData("Second"),
) {
    BottomSheetList(
        stringArrayResource(id = R.array.work_schedule_custom_dates).toList(),
        onBackButton = onBackButton,
        valueField = occurrenceMutableLiveData
    )
}


@Preview(showBackground = true)
@Composable
fun CustomDateLayoutPreview() {
    val isAllDay: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val isEventRepeatsForever: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val occurrence: MutableLiveData<String> = MutableLiveData<String>()
    val day: MutableLiveData<String> = MutableLiveData<String>()
    val month: MutableLiveData<String> = MutableLiveData<String>()
    val occurrences: MutableLiveData<String> = MutableLiveData<String>()
    val startTime: MutableLiveData<LocalDateTime> = MutableLiveData<LocalDateTime>()
    val endTime: MutableLiveData<LocalDateTime> = MutableLiveData<LocalDateTime>()
    val isAllDayState = isAllDay.observeAsState(
        initial = false
    )
    val isEventRepeatsForeverState = isEventRepeatsForever.observeAsState(
        initial = true
    )
    val occurrenceState = occurrence.observeAsState(
        ""
    )
    val dayState = day.observeAsState(
        ""
    )
    val monthState = month.observeAsState(
        ""
    )
    val occurrencesState = occurrences.observeAsState(
        ""
    )
    val startTimeState = startTime.observeAsState(
        initial = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0))
    )

    val endTimeState = endTime.observeAsState(
        initial = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0))
    )

    val addButtonOnClick = {

    }

    CustomDateLayout(
        onBackButton = {},
        occurrenceOnClick = {},
        occurrenceState = occurrenceState,
        dayOnClick = {},
        dayState = dayState,
        monthOnClick = {},
        monthState = monthState,
        eventRepeatForeverOnCheckChange = {},
        isAllDayState = isAllDayState,
        startTimeState = startTimeState,
        endTimeState = endTimeState,
        isEventRepeatsForeverState = isEventRepeatsForeverState,
        occurrencesState = occurrencesState,
        addButtonOnClick = addButtonOnClick
    )
}

fun loadCheckBoxContainer(
    customHoursScheduleViewState: CustomDateScheduleStartEndTimeViewState,
    parentView: ConstraintLayout,
    context: Context,
) {
    parentView.addView(TimeScheduleView(context).apply {
        viewState = customHoursScheduleViewState
    })

}


@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, fontScale = 4f)
@Composable
fun WorkScheduleCustomDatePreview() {
    WorkScheduleCustomDate({})
}