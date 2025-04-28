package com.nextiva.nextivaapp.android.core.notifications.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.models.HolidaySelection
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.util.extensions.contentDescription

@Composable
fun HolidayItemView(
    title: String,
    onItemChecked: (Boolean) -> Unit,
    isItemChecked: Boolean,
    selectedHolidays: List<HolidaySelection>?
) {
    var isChecked by remember { mutableStateOf(isItemChecked) }
    selectedHolidays?.firstOrNull { it.holiday.name == title }?.let {
        isChecked = it.isSelected
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {}
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isChecked = !isChecked
                        onItemChecked(isChecked)
                    }
            ) {
                Text(
                    modifier = Modifier
                        .contentDescription(title),
                    text = title,
                    style = TypographyBody1,
                    color = colorResource(R.color.connectSecondaryDarkBlue),
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .padding(3.dp)
                        .semantics {
                            stateDescription = isChecked.toString().replaceFirstChar { it.uppercase() }
                        }
                        .contentDescription(
                            stringResource(
                                id = R.string.create_schedule_holidays_check_marks_accessibility_id,
                                title, isChecked.toString().replaceFirstChar { it.uppercase() }
                            )
                        )
                ) {
                    Image(
                        painter = painterResource(
                            id = if (isChecked) R.drawable.ic_custom_checked else R.drawable.ic_custom_unchecked
                        ),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}
