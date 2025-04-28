package com.nextiva.nextivaapp.android.core.notifications.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.api.Holiday
import com.nextiva.nextivaapp.android.core.notifications.api.HolidayType
import com.nextiva.nextivaapp.android.core.notifications.api.HourOfWeek
import com.nextiva.nextivaapp.android.core.notifications.api.Interval
import com.nextiva.nextivaapp.android.core.notifications.api.Repeat
import com.nextiva.nextivaapp.android.core.notifications.utils.Utils
import com.nextiva.nextivaapp.android.features.rooms.view.components.ScreenTitleBarView
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyButton
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyH6
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.extensions.contentDescription
import com.nextiva.nextivaapp.android.view.compose.NextivaDateSelectView
import com.nextiva.nextivaapp.android.view.compose.NextivaTextFieldView
import com.nextiva.nextivaapp.android.view.compose.NextivaTimeSelectView
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class SpecifiedDatesActivity : BaseActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SpecifiedDatesActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(ComposeView(this).apply {
            setContent {
                SpecifiedDatesView {
                    val intent = Intent()
                    intent.putExtra(CreateScheduleActivity.INTENT_EXTRA_SPECIFIED_DATE, GsonUtil.getJSON(it))
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        })
    }
}

@Composable
fun SpecifiedDatesView(holidayCallback: (Holiday) -> Unit) {
    val activity = (LocalContext.current as? Activity)
    val scrollState = rememberScrollState()
    val formatterManager = FormatterManager.getInstance()
    val context = LocalContext.current

    var isAllDayEventChecked by remember { mutableStateOf(true) }
    var isRepeatForeverChecked by remember { mutableStateOf(true) }
    var startTime by remember { mutableStateOf(LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0))) }
    var endTime by remember { mutableStateOf(LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0))) }
    var selectedDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var occurrences by remember { mutableStateOf<Int?>(null)}

    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(id = R.color.connectGrey01))) {

        Column {
            ScreenTitleBarView(
                title = stringResource(id = R.string.specified_dates_title),
                onBackButton = { activity?.finish() },
                titleStyle = TypographyH6)

            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Text(
                    modifier = Modifier
                        .padding(
                            top = dimensionResource(id = R.dimen.general_padding_xlarge),
                            start = dimensionResource(id = R.dimen.general_padding_medium),
                            end = dimensionResource(id = R.dimen.general_padding_medium)
                        ),
                    text = stringResource(id = R.string.specified_dates_message),
                    style = TypographyBody1,
                    color = colorResource(id = R.color.connectGrey10)
                )

                NextivaDateSelectView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    title = stringResource(id = R.string.specified_dates_text_title)) { selectedDate = it }

                Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically) {

                    Checkbox(
                        modifier = Modifier
                            .semantics {
                                stateDescription = isAllDayEventChecked
                                    .toString()
                                    .replaceFirstChar { it.uppercase() }
                            }
                            .contentDescription(
                                stringResource(
                                    id = R.string.create_schedule_holidays_specific_custom_date_check_marks_accessibility_id,
                                    stringResource(R.string.specified_dates_all_day_event)
                                )
                            ),
                        checked = isAllDayEventChecked,
                        onCheckedChange = { isAllDayEventChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorResource(R.color.connectPrimaryBlue),
                            uncheckedColor = colorResource(R.color.connectGrey09),
                            checkmarkColor = colorResource(R.color.connectWhite)
                        ),
                        enabled = true
                    )

                    Text(
                        modifier = Modifier.contentDescription(stringResource(id = R.string.specified_dates_all_day_event)),
                        text = stringResource(id = R.string.specified_dates_all_day_event),
                        style = TypographyBody1,
                        color = colorResource(R.color.connectSecondaryDarkBlue)
                    )
                }

                if (!isAllDayEventChecked) {
                    Row(
                        modifier = Modifier
                            .padding(start = 64.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically) {

                        NextivaTimeSelectView(
                            modifier = Modifier
                                .weight(0.5F)
                                .padding(end = 8.dp),
                            title = stringResource(id = R.string.notification_create_schedule_start_time_label),
                            accessibilityId = stringResource(id = R.string.create_schedule_specified_custom_dates_start_time_accessibility_id),
                            defaultTime = startTime) { startTime = it }

                        NextivaTimeSelectView(
                            modifier = Modifier
                                .weight(0.5F)
                                .padding(start = 8.dp),
                            title = stringResource(id = R.string.notification_create_schedule_end_time_label),
                            accessibilityId = stringResource(id = R.string.create_schedule_specified_custom_dates_end_time_accessibility_id),
                            defaultTime = endTime) { endTime = it }
                    }
                }

                Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically) {

                    Checkbox(
                        modifier = Modifier
                            .semantics {
                                stateDescription = isRepeatForeverChecked
                                    .toString()
                                    .replaceFirstChar { it.uppercase() }
                            }
                            .contentDescription(
                                stringResource(
                                    id = R.string.create_schedule_holidays_specific_custom_date_check_marks_accessibility_id,
                                    stringResource(R.string.specified_dates_repeat)
                                )
                            ),
                        checked = isRepeatForeverChecked,
                        onCheckedChange = { isRepeatForeverChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorResource(R.color.connectPrimaryBlue),
                            uncheckedColor = colorResource(R.color.connectGrey09),
                            checkmarkColor = colorResource(R.color.connectWhite)
                        ),
                        enabled = true
                    )

                    Text(
                        modifier = Modifier.contentDescription(stringResource(id = R.string.specified_dates_repeat)),
                        text = stringResource(id = R.string.specified_dates_repeat),
                        style = TypographyBody1,
                        color = colorResource(R.color.connectSecondaryDarkBlue)
                    )
                }

                if (!isRepeatForeverChecked) {
                    Row(
                        modifier = Modifier
                            .padding(start = 64.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NextivaTextFieldView(title = stringResource(id = R.string.specified_dates_end_after),
                            hint = stringResource(id = R.string.specified_dates_occurrences),
                            isDigitsOnly = true,
                            onValueChanged = {
                                if (it.isDigitsOnly()) {
                                    it.toIntOrNull()?.let { input ->
                                        occurrences = input
                                    }
                                }
                            })
                    }
                }
            }

            Spacer(modifier = Modifier
                .weight(1F))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        stateDescription = if (selectedDate == null) "False" else "True"
                    }
                    .contentDescription(contentDescription = stringResource(id = R.string.general_add))
                    .height(dimensionResource(id = R.dimen.general_view_xxxxxlarge))
                    .padding(dimensionResource(id = R.dimen.general_padding_medium)),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = if (selectedDate == null) R.color.connectGrey03 else R.color.connectPrimaryBlue)),
                shape = RoundedCornerShape(dimensionResource(id = androidx.cardview.R.dimen.cardview_default_radius)),
                onClick = {
                    selectedDate?.let { selectedDate ->
                        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        var name = FormatterManager.getInstance().dateFormatter_MMMdd.format(
                            selectedDate
                        )

                        if (!isRepeatForeverChecked) {
                            occurrences?.let { occurrences ->
                                if (occurrences > 1) {
                                    name = "${name}, ${selectedDate.year}-${selectedDate.plusYears(occurrences.toLong() - 1).year.toString().drop(2)}"

                                } else if (occurrences == 0) {
                                    name = "${name}, ${selectedDate.year}"
                                }
                            }
                        }

                        if (!isAllDayEventChecked) {
                            startTime?.let { startTime ->
                                endTime?.let { endTime ->
                                    name = ("$name (" +
                                            "${formatterManager.formatHHmm(context, startTime.atZone(ZoneId.systemDefault()).toInstant()).replace(" ", "")}-" +
                                            "${formatterManager.formatHHmm(context, endTime.atZone(ZoneId.systemDefault()).toInstant()).replace(" ", "")})")
                                        .replace("AM", "a")
                                        .replace("PM", "p")
                                }
                            }
                        }

                        holidayCallback(
                            Holiday(
                                null,
                                null,
                                HolidayType.SPECIFIC,
                                null,
                                name,
                                null,
                                Interval(isAllDayEventChecked,
                                    if (isAllDayEventChecked) null else HourOfWeek(
                                        Utils.formatScheduleTimeTo24hr(formatterManager.formatHHmm(context, startTime.atZone(ZoneId.systemDefault()).toInstant())) ?: "",
                                        Utils.formatScheduleTimeTo24hr(formatterManager.formatHHmm(context, endTime.atZone(ZoneId.systemDefault()).toInstant())) ?: "")),
                                Repeat(isRepeatForeverChecked, occurrences),
                                format.format(selectedDate)
                            )
                        )
                    }
                }
            ) {
                Text(modifier = Modifier.contentDescription(stringResource(id = R.string.general_add)),
                    text = stringResource(id = R.string.general_add),
                    color = colorResource(id = if (selectedDate == null) R.color.connectPrimaryGrey else R.color.connectWhite),
                    style = TypographyButton
                )
            }
        }
    }
}