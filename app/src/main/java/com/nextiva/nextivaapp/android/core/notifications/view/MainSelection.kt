package com.nextiva.nextivaapp.android.core.notifications.view

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.models.Schedule
import com.nextiva.nextivaapp.android.features.rooms.view.components.ScreenTitleBarView
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.Typography
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyForm
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyH6
import com.nextiva.nextivaapp.android.features.ui.theme.TypographySubtitle1
import com.nextiva.nextivaapp.android.util.extensions.contentDescription
import kotlinx.coroutines.channels.Channel

@Composable
fun MainSelection(
    selectSchedule: () -> Unit,
    onCreateScheduleClick: () -> Unit,
    onEditScheduleClick: () -> Unit,
    removeSelectedSchedule: () -> Unit,
    selectedSchedule: Schedule?,
    shouldShowSnackBar: MutableState<Boolean>
) {

    val allDayIndex = 0
    val selectScheduleIndex = 1

    val items = listOf(
        stringResource(id = R.string.notification_preferences_all_day),
        stringResource(id = R.string.notification_preferences_select)
    )

    val selectedValue = remember { mutableStateOf(items.first()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val message = stringResource(id = R.string.notification_create_schedule_save_success)

    if (shouldShowSnackBar.value) {
        val channel = remember { Channel<Int>(Channel.CONFLATED) }
        LaunchedEffect(channel) {
            val snackBarResult = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = ""
            )
            when (snackBarResult) {
                SnackbarResult.Dismissed -> {}
                SnackbarResult.ActionPerformed -> {
                    snackbarHostState.showSnackbar(
                        message = ""
                    )
                }
            }
        }
    }

    val isSelectedItem: (String) -> Boolean = { rowIndex ->
        if (rowIndex == items[selectScheduleIndex]) {
            selectedSchedule != null
        } else {
            selectedSchedule == null
        }
    }
    val onChangeState: (String) -> Unit = {
        selectedValue.value = it
        if (selectedValue.value == items[selectScheduleIndex]) {
            selectSchedule()
        } else {
            removeSelectedSchedule()
        }
    }
    val activity = (LocalContext.current as? Activity)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.connectGrey01))
    ) {
        Column {
            ScreenTitleBarView(
                title = stringResource(id = R.string.main_nav_notifications),
                onBackButton = { activity?.finish() },
                titleStyle = TypographyH6
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.connectWhite))
            ) {
                item {
                    Text(
                        modifier = Modifier
                            .contentDescription(stringResource(id = R.string.notifications_sub_header_view_accessibility_id))
                            .padding(
                                top = dimensionResource(id = R.dimen.general_padding_xlarge),
                                start = dimensionResource(id = R.dimen.general_padding_medium)
                            ),
                        text = stringResource(id = R.string.notification_preferences_allow),
                        style = TypographySubtitle1,
                        color = colorResource(id = R.color.connectSecondaryDarkBlue)
                    )

                    Text(
                        modifier = Modifier
                            .contentDescription(stringResource(id = R.string.notifications_schedules_sub_header_accessibility_id))
                            .padding(
                                start = dimensionResource(id = R.dimen.general_padding_medium),
                                end = dimensionResource(id = R.dimen.general_padding_medium),
                                top = dimensionResource(id = R.dimen.general_padding_medium),
                                bottom = dimensionResource(id = R.dimen.general_padding_large)
                            ),
                        text = stringResource(id = R.string.notification_preferences_description),
                        style = TypographyBody1,
                        color = colorResource(id = R.color.connectGrey10)
                    )

                items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .semantics {
                                stateDescription = isSelectedItem(item).toString()
                            }
                            .selectable(
                                selected = isSelectedItem(item),
                                onClick = { onChangeState(item) },
                                role = Role.RadioButton
                            )
                            .padding(
                                start = dimensionResource(id = R.dimen.general_padding_medium),
                                end = dimensionResource(id = R.dimen.general_padding_medium),
                                top = dimensionResource(id = R.dimen.general_padding_xmedium),
                                bottom = dimensionResource(id = R.dimen.general_padding_xmedium)
                            )
                    ) {
                        RadioButton(
                            modifier = Modifier
                                .semantics {
                                    stateDescription = isSelectedItem(item).toString()
                                }
                                .contentDescription(item),
                            selected = isSelectedItem(item),
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colorResource(id = R.color.connectPrimaryBlue),
                                unselectedColor = colorResource(id = R.color.connectGrey09),
                                disabledColor = colorResource(id = R.color.connectGrey09)
                            )
                        )
                        Text(
                            modifier = Modifier
                                .semantics {
                                    stateDescription = isSelectedItem(item).toString()
                                }
                                .contentDescription( "%s %s".format(item, if (isSelectedItem(item)) "true" else "false"))
                                .padding(
                                    start = dimensionResource(id = R.dimen.general_padding_medium)
                                )
                                .fillMaxWidth(),
                            text = item,
                            style = TypographyBody1,
                            color = colorResource(id = R.color.connectSecondaryDarkBlue)
                        )
                    }
                }
                selectedSchedule?.let { schedule ->
                    Text(
                        modifier = Modifier
                            .contentDescription(stringResource(id = R.string.notifications_selected_schedule_accessibility_id))
                            .padding(
                                start = dimensionResource(id = R.dimen.general_padding_xxxxlarge),
                                end = dimensionResource(id = R.dimen.general_padding_medium),
                                top = dimensionResource(id = R.dimen.general_padding_xmedium),
                                bottom = dimensionResource(id = R.dimen.general_padding_xmedium)
                            )
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.notification_schedules_selected_schedules).uppercase(),
                        style = TypographyForm,
                        color = colorResource(R.color.connectGrey10)
                    )

                        ScheduleChipGroup(
                            schedules = arrayListOf(schedule),
                            modifier = Modifier
                                .padding(
                                    start = dimensionResource(id = R.dimen.general_padding_xxxxlarge),
                                    end = dimensionResource(id = R.dimen.general_padding_medium),
                                    top = dimensionResource(id = R.dimen.general_padding_xmedium),
                                    bottom = dimensionResource(id = R.dimen.general_padding_xmedium)
                                )
                        ) {
                            removeSelectedSchedule()
                            onChangeState(items[allDayIndex])
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { selectSchedule() }
                                .padding(
                                    start = dimensionResource(id = R.dimen.general_padding_xxlarge),
                                    end = dimensionResource(id = R.dimen.general_padding_medium),
                                    top = dimensionResource(id = R.dimen.general_padding_xmedium),
                                    bottom = dimensionResource(id = R.dimen.general_padding_xmedium)
                                )) {
                            Text(
                                modifier = Modifier
                                    .padding(
                                        start = dimensionResource(id = R.dimen.general_padding_medium)
                                    ),
                                text = stringResource(R.string.fa_exchange),
                                style = TextStyle(
                                    color = colorResource(R.color.connectGrey09),
                                    fontSize = dimensionResource(R.dimen.material_text_subhead).value.sp,
                                    fontFamily = FontAwesome,
                                    fontWeight = FontWeight.Normal
                                )
                            )

                            Text(
                                modifier = Modifier
                                    .contentDescription(stringResource(id = R.string.notification_schedule_change_selected_schedule_accessibility_id))
                                    .padding(
                                        start = dimensionResource(id = R.dimen.general_padding_small)
                                    )
                                    .fillMaxWidth(),
                                text = stringResource(id = R.string.notification_schedules_change_selected),
                                style = TypographyBody1Heavy,
                                color = colorResource(id = R.color.connectPrimaryBlue)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onEditScheduleClick() }
                                .padding(
                                    start = dimensionResource(id = R.dimen.general_padding_xxlarge),
                                    end = dimensionResource(id = R.dimen.general_padding_medium),
                                    top = dimensionResource(id = R.dimen.general_padding_xmedium),
                                    bottom = dimensionResource(id = R.dimen.general_padding_xmedium)
                                )) {

                            Text(
                                modifier = Modifier
                                    .padding(
                                        start = dimensionResource(id = R.dimen.general_padding_medium)
                                    ),
                                text = stringResource(R.string.fa_edit),
                                style = TextStyle(
                                    color = colorResource(R.color.connectGrey09),
                                    fontSize = dimensionResource(R.dimen.material_text_subhead).value.sp,
                                    fontFamily = FontAwesome,
                                    fontWeight = FontWeight.Normal
                                )
                            )

                            Text(
                                modifier = Modifier
                                    .contentDescription(stringResource(id = R.string.notification_schedule_edit_selected_schedule_accessibility_id))
                                    .padding(
                                        start = dimensionResource(id = R.dimen.general_padding_small)
                                    )
                                    .fillMaxWidth(),
                                text = stringResource(id = R.string.notification_schedules_edit_selected),
                                style = TypographyBody1Heavy,
                                color = colorResource(id = R.color.connectPrimaryBlue)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .padding(top = dimensionResource(id = R.dimen.general_padding_large))
                            .fillMaxWidth()
                            .height(dimensionResource(R.dimen.general_padding_small))
                            .background(colorResource(id = R.color.connectGrey01))
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(
                                start = dimensionResource(id = R.dimen.general_padding_medium),
                                end = dimensionResource(id = R.dimen.general_padding_medium),
                                top = dimensionResource(id = R.dimen.general_padding_xmedium),
                                bottom = dimensionResource(id = R.dimen.general_padding_xmedium)
                            )
                            .clickable(onClick = onCreateScheduleClick)
                    ) {
                        Text(
                            text = stringResource(R.string.fa_plus),
                            color = colorResource(R.color.connectPrimaryBlue),
                            fontFamily = FontAwesome,
                            fontSize = dimensionResource(R.dimen.material_text_subhead).value.sp,
                            fontWeight = FontWeight.W400,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            modifier = Modifier
                                .contentDescription(stringResource(id = R.string.notifications_schedule_create_schedule_accessibility_id))
                                .padding(
                                    start = dimensionResource(id = R.dimen.general_padding_medium)
                                ),
                            text = stringResource(id = R.string.notification_preferences_create),
                            style = TypographyBody1Heavy,
                            color = colorResource(id = R.color.connectPrimaryBlue)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .wrapContentWidth()
        ) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.general_padding_medium),
                    ),
                snackbar = { snackbarData: SnackbarData ->
                    Card(
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.message_bubble_rounded_corner)),
                        backgroundColor = colorResource(id = R.color.connectSecondaryDarkBlue),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.general_padding_medium))
                                .wrapContentWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_check_circle),
                                contentDescription = "Snackbar Icon",
                                tint = colorResource(id = R.color.connectPrimaryGreen),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(end = dimensionResource(id = R.dimen.general_padding_small))
                            )
                            Text(
                                modifier = Modifier
                                    .weight(1f,false)
                                    .align(Alignment.CenterVertically),
                                text = snackbarData.message,
                                style = Typography.body2,
                                color = colorResource(id = R.color.connectWhite),
                            )
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_cancel_transparent),
                                contentDescription = "Snackbar Icon",
                                tint = colorResource(id = R.color.connectGrey09),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(
                                        start = dimensionResource(id = R.dimen.general_padding_small),
                                        end = dimensionResource(id = R.dimen.general_padding_small)
                                    )
                                    .clickable { snackbarHostState.currentSnackbarData?.dismiss() }
                            )
                        }
                    }

                }
            )
        }
    }
}

@Composable
fun ScheduleChipGroup(
    schedules: ArrayList<Schedule>,
    modifier: Modifier,
    onChipDeleted: (String) -> Unit
) {
    Column(modifier = modifier) {
        FlowRow(
            crossAxisSpacing = dimensionResource(id = R.dimen.general_padding_xmedium),
            mainAxisSpacing = dimensionResource(id = R.dimen.general_padding_xmedium)
        ) {
            schedules.forEach { schedule ->
                schedule.name?.let { name ->
                    schedule.scheduleId?.let { scheduleId ->
                        Chip(
                            name = name,
                            referenceId = scheduleId,
                            onChipDeleted = onChipDeleted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Chip(name: String, referenceId: String, onChipDeleted: (String) -> Unit) {
    val activity = (LocalContext.current as? Activity)
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(color = colorResource(id = R.color.connectGrey02))
            .padding(
                start = dimensionResource(id = R.dimen.general_padding_medium),
                end = dimensionResource(id = R.dimen.general_padding_medium),
                top = dimensionResource(id = R.dimen.general_padding_small),
                bottom = dimensionResource(id = R.dimen.general_padding_small)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .contentDescription(
                    activity?.getString(R.string.notification_schedule_name_chip_accessibility_id)
                        ?: ""
                )
                .padding(end = dimensionResource(id = R.dimen.general_padding_medium))
                .weight(1f, false),
            text = name,
            style = TypographyBody1,
            color = colorResource(id = R.color.connectSecondaryDarkBlue),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            modifier = Modifier
                .contentDescription(
                    activity?.getString(R.string.notification_schedule_name_chip_close_icon_accessibility_id)
                        ?: ""
                )
                .clickable { onChipDeleted(referenceId) },
            text = stringResource(R.string.fa_times_circle),
            style = TextStyle(
                color = colorResource(id = R.color.connectGrey09),
                fontSize = dimensionResource(R.dimen.material_text_subhead).value.sp,
                fontFamily = FontAwesome,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun MainSelectionPreview() {
    MainSelection(
        selectSchedule = {},
        onCreateScheduleClick = {},
        onEditScheduleClick = {},
        removeSelectedSchedule = {},
        null,
        mutableStateOf(false)
    )
}