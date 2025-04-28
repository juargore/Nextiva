/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.core.notifications.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.view.component.BottomSheetDaysOfTheWeekList
import com.nextiva.nextivaapp.android.core.notifications.view.component.BottomSheetEmpty
import com.nextiva.nextivaapp.android.core.notifications.view.component.BottomSheetMonthList
import com.nextiva.nextivaapp.android.core.notifications.viewmodel.CreateScheduleViewModel
import com.nextiva.nextivaapp.android.util.GsonUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

@AndroidEntryPoint
class CustomDateActivity : BaseActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CustomDateActivity::class.java)
        }
    }

    private lateinit var viewModel: CreateScheduleViewModel

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CreateScheduleViewModel::class.java]

        setContentView(ComposeView(this).apply {
            setContent {
                val onBackButton =
                    {
                        setResult(RESULT_OK, intent)
                        this@CustomDateActivity.finish()
                    }

                val onAddHoliday = {
                    if(viewModel.customOccurrence.value != null &&
                            viewModel.customDay.value != null &&
                            viewModel.customMonth.value != null) {
                        val intent = Intent()
                        intent.putExtra(
                            CreateScheduleActivity.INTENT_EXTRA_CUSTOM_DATE,
                            GsonUtil.getJSON(viewModel.createCustomHolidayEntry())
                        )
                        setResult(RESULT_OK, intent)
                        this@CustomDateActivity.finish()
                    }
                }

                CustomDateActivityLayout(
                    onBackButton = onBackButton,
                    onAddHoliday = onAddHoliday,
                    context = context,
                    viewModel = viewModel,
                    window = window
                )
            }
        })
    }


}

@ExperimentalMaterialApi
@Composable
fun CustomDateActivityLayout(
    onBackButton: () -> Unit,
    onAddHoliday: () -> Unit,
    context: Context,
    viewModel: CreateScheduleViewModel,
    window: Window,
) {

    val coroutineScope = rememberCoroutineScope()

    val isAllDayState = viewModel.isAllDay.observeAsState(
        initial = true
    )
    val isEventRepeatsForeverState = viewModel.isEventRepeatsForever.observeAsState(
        initial = true
    )
    val occurrenceState = viewModel.customOccurrence.observeAsState(
        initial = ""
    )
    val dayState = viewModel.customDay.observeAsState(
        initial = ""
    )
    val monthState = viewModel.customMonth.observeAsState(
        initial = ""
    )

    val occurrencesState = viewModel.occurrences.observeAsState(
        initial = ""
    )

    val startTimeState = viewModel.customStartTime.observeAsState(
        initial = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0))
    )

    val endTimeState = viewModel.customEndTime.observeAsState(
        initial = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0))
    )



    var showOccurrence by remember { mutableStateOf(false) }
    var showDay by remember { mutableStateOf(false) }
    var showMonth by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val animationSpec = remember {
        Animatable(0f)
            .run {
                TweenSpec<Float>(durationMillis = 300)
            }
    }
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        animationSpec = animationSpec
    )

    val occurrenceOnClick = {
        showOccurrence = true
        showDay = false
        showMonth = false
        coroutineScope.launch {
            focusManager.clearFocus(true)
            modalBottomSheetState.show()
        }
        Unit
    }
    val dayOnClick = {
        showOccurrence = false
        showDay = true
        showMonth = false
        coroutineScope.launch {
            focusManager.clearFocus(true)
            modalBottomSheetState.show()
        }
        Unit
    }
    val monthOnClick = {
        showOccurrence = false
        showDay = false
        showMonth = true
        coroutineScope.launch {
            focusManager.clearFocus(true)
            modalBottomSheetState.show()
        }
        Unit
    }

    val allDayEventOnCheckChange: (Boolean) -> Unit = {
        viewModel.isAllDay.value = it
        if(!it)
        {
            viewModel.customStartTime.value = startTimeState.value
            viewModel.customEndTime.value = endTimeState.value
        }
    }

    val eventRepeatForeverOnCheckChange: (Boolean) -> Unit = {
        viewModel.isEventRepeatsForever.value = it
        if (!it)
            viewModel.occurrences.value = ""
    }

    val occurrencesOnChange: (String) -> Unit = {
        viewModel.occurrences.value = it.filter { it.isDigit() }
    }

    val startTimeOnChange: (LocalDateTime) -> Unit = {
        viewModel.customStartTime.value = it
    }

    val endTimeOnChange: (LocalDateTime) -> Unit = {
        viewModel.customEndTime.value = it
    }

    val addOnClick = {onAddHoliday()}

    ModalBottomSheetLayout(
        modifier = Modifier
            .navigationBarsPadding()
            .captionBarPadding()
            .imePadding()
            .background(when (modalBottomSheetState.targetValue) {
                ModalBottomSheetValue.Expanded,
                ModalBottomSheetValue.HalfExpanded,
                    -> colorResource(R.color.blackOverlay40)
                else -> colorResource(R.color.connectGrey01)
            })
            .windowInsetsPadding(WindowInsets.statusBars),
        sheetState = modalBottomSheetState,
        sheetElevation = 8.dp,
        scrimColor = colorResource(id = R.color.blackOverlay40),
        sheetShape = RoundedCornerShape(
            topStart = dimensionResource(id = R.dimen.bottom_sheet_rounded_corner),
            topEnd = dimensionResource(id = R.dimen.bottom_sheet_rounded_corner)
        ),
        sheetContent = {

            val onBackButton: () -> Unit = {
                coroutineScope.launch {
                    modalBottomSheetState.hide()
                }
            }

            if (showMonth) {
                BottomSheetMonthList(
                    onBackButton = onBackButton,
                    customMonth = viewModel.customMonth
                )
            } else if (showDay) {
                BottomSheetDaysOfTheWeekList(
                    onBackButton = onBackButton,
                    customDay = viewModel.customDay
                )
            } else if (showOccurrence) {
                WorkScheduleCustomDate(onBackButton = onBackButton, viewModel.customOccurrence)
            } else {
                BottomSheetEmpty(stringResource(id = R.string.work_schedule_custom_dates_schedule), stringResource(R.string.fa_clock), {}, {})
            }
        }) {
        CustomDateLayout(
            onBackButton = onBackButton,
            occurrenceOnClick = occurrenceOnClick,
            occurrenceState = occurrenceState,
            dayOnClick = dayOnClick,
            dayState = dayState,
            monthOnClick = monthOnClick,
            monthState = monthState,
            isAllDayState = isAllDayState,
            allDayEventOnCheckChange = allDayEventOnCheckChange,
            isEventRepeatsForeverState = isEventRepeatsForeverState,
            eventRepeatForeverOnCheckChange = eventRepeatForeverOnCheckChange,
            startTimeState = startTimeState,
            startTimeOnChange = startTimeOnChange,
            endTimeState = endTimeState,
            endTimeOnChange = endTimeOnChange,
            occurrencesState = occurrencesState,
            occurrencesOnChange = occurrencesOnChange,
            addButtonOnClick = addOnClick
        )
    }
}



/*
@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun CustomDatePreview() {
    var window = (LocalContext.current as CustomDateActivity).window
    var viewModel = CreateScheduleViewModel()
    CustomDateLayout(onBackButton = {}, context = LocalContext.current, window = window, viewModel = viewModel)
}*/
