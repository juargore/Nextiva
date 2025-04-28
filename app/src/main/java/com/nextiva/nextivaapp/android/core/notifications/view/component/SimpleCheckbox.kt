/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.core.notifications.view.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.util.extensions.contentDescription

/**
 * Created by Thaddeus Dannar on 3/28/23.
 */
@Composable
fun SimpleCheckbox(
    checked: Boolean = false,
    onCheckChanged: (Boolean) -> Unit = {},
    enabled: Boolean = true,
    text: String = "",
    textColor: Color? = null,
    textStyle: TextStyle = TypographyBody1,
) {

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                //indication = rememberRipple(color = MaterialTheme.colors.primary), //This will likely be re-enabled when design realizes lack of visual interaction is a negative
                //interactionSource = remember { MutableInteractionSource() },
                onClick = { onCheckChanged(!checked) }
            )) {
        Checkbox(
            modifier = Modifier.semantics { stateDescription = checked.toString().replaceFirstChar { it.uppercase() } }
                .contentDescription(stringResource(id = R.string.create_schedule_holidays_specific_custom_date_check_marks_accessibility_id, text)),
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,

            colors = CheckboxDefaults.colors(
                checkmarkColor = colorResource(R.color.connectWhite),
                checkedColor = colorResource(R.color.nextivaPrimaryBlue),
                uncheckedColor = colorResource(R.color.connectGrey10)
            )
        )


        if (text.isNotEmpty())
            Text(
                text = text,
                style = textStyle,
                modifier = Modifier
                    .contentDescription(text)
                    .padding(start = 16.dp),
                color = textColor ?: colorResource(R.color.connectGrey10),
            )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, fontScale = 4f)
@Composable
fun SimpleCheckboxPreview() {
    SimpleCheckbox(text = "Label")
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, fontScale = 4f)
@Composable
fun SimpleCheckboxCheckedPreview() {
    SimpleCheckbox(text = "Label", checked = true)
}

