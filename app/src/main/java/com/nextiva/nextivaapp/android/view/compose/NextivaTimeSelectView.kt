package com.nextiva.nextivaapp.android.view.compose

import android.app.TimePickerDialog
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
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.util.extensions.contentDescription
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

@Composable
fun NextivaTimeSelectView(modifier: Modifier = Modifier, title: String, accessibilityId:String, defaultTime: LocalDateTime, onTimeSelected: (LocalDateTime) -> Unit) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(defaultTime) }
    val formatterManager = FormatterManager.getInstance()

    Column(modifier) {
        Text(
            modifier = Modifier.contentDescription(title.uppercase())
            .padding(bottom = 4.dp),
            text = title.uppercase(),
            style = TypographyOverlineHeavy,
            color = colorResource(id = R.color.connectGrey10)
        )

        Row(modifier = Modifier.contentDescription(accessibilityId)
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.connectPrimaryGrey),
                shape = RoundedCornerShape(size = 4.dp)
            )
            .padding(12.dp)
            .fillMaxWidth()
            .clickable {
                val dialog = TimePickerDialog(context, { _, hour, minute ->
                    selectedDate = selectedDate
                        .withHour(hour)
                        .withMinute(minute)
                    onTimeSelected(selectedDate)
                }, selectedDate.hour, selectedDate.minute, false)
                dialog.show()
            }) {

            Text(modifier = Modifier.weight(1f),
                text = formatterManager.formatHHmm(context, selectedDate.atZone(ZoneId.systemDefault()).toInstant())
                    .replace(":", " : ")
                    .replace("AM", "am")
                    .replace("PM", "pm"),
                style = TypographyBody1,
                color = colorResource(R.color.connectSecondaryDarkBlue))

            Text(text = stringResource(R.string.fa_clock),
                style = TextStyle(
                    color = colorResource(R.color.connectGrey08),
                    fontSize = 16.sp,
                    fontFamily = FontAwesome,
                    fontWeight = FontWeight.Normal,
                )
            )
        }
    }
}