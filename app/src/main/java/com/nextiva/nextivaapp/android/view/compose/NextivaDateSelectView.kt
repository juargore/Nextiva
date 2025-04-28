package com.nextiva.nextivaapp.android.view.compose

import android.app.DatePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyOverlineHeavy
import com.nextiva.nextivaapp.android.util.extensions.contentDescription
import com.nextiva.nextivaapp.android.util.extensions.dayWithSuffix
import org.threeten.bp.LocalDateTime
import java.util.Locale

@Composable
fun NextivaDateSelectView(modifier: Modifier = Modifier, title: String, onDateSelected: (LocalDateTime) -> Unit) {
    val context = LocalContext.current
    var selectedDate: LocalDateTime? by remember { mutableStateOf(null) }

    Column(modifier) {
        Text(
            modifier = Modifier.contentDescription(title.uppercase())
                .padding(bottom = 4.dp),
            text = title.uppercase(),
            style = TypographyOverlineHeavy,
            color = colorResource(id = R.color.connectGrey10)
        )

        Row(modifier = Modifier
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.connectPrimaryGrey),
                shape = RoundedCornerShape(size = 4.dp)
            )
            .padding(12.dp)
            .fillMaxWidth()
            .clickable {
                val initDate = selectedDate ?: LocalDateTime.now()

                val dialog = DatePickerDialog(context, { _, year, month, day ->
                    selectedDate = initDate.withYear(year)
                        .withDayOfMonth(day)
                        .withMonth(month.plus(1))

                    selectedDate?.let { onDateSelected(it) }

                }, initDate.year, initDate.month.ordinal, initDate.dayOfMonth)
                dialog.datePicker.minDate = System.currentTimeMillis()
                dialog.show()
            }) {

            selectedDate?.let { selectedDate ->
                Text(modifier = Modifier.weight(1f),
                    text = stringResource(R.string.set_do_not_disturb_bottom_sheet_duration_date,
                        selectedDate.month.getDisplayName(org.threeten.bp.format.TextStyle.SHORT, Locale.getDefault()),
                        selectedDate.dayOfMonth.dayWithSuffix(),
                        selectedDate.year.toString()),
                    style = TypographyBody1,
                    color = colorResource(R.color.connectSecondaryDarkBlue))

            } ?: kotlin.run {
                Text(modifier = Modifier
                    .contentDescription(stringResource(id = R.string.create_schedule_specified_dates_accessibility_id))
                    .weight(1f),
                    text = stringResource(R.string.specified_dates_text_hint),
                    style = TypographyBody1,
                    color = colorResource(R.color.connectGrey08))
            }

            Text(text = stringResource(R.string.fa_calendar),
                style = TextStyle(
                    color = colorResource(R.color.connectGrey09),
                    fontSize = 16.sp,
                    fontFamily = FontAwesome,
                    fontWeight = FontWeight.Normal,
                )
            )
        }
    }
}