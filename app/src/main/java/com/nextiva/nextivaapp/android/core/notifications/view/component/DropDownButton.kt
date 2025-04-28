/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.core.notifications.view.component

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeSolid
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption2Bold
import com.nextiva.nextivaapp.android.util.extensions.contentDescription

/**
 * Created by Thaddeus Dannar on 3/28/23.
 */

@Composable
fun LabeledDropDownButton(
    modifier: Modifier = Modifier,
    labelText: String = "",
    buttonText: String = "",
    defaultText: String = "",
    onClick: () -> Unit = {},
    enabled: Boolean = true,
) {

    Column {
        Text(
            modifier= Modifier.contentDescription(labelText),
            text = labelText,
            style = TypographyCaption2Bold,
            color = colorResource(R.color.connectGrey10)
        )

        DropDownButton(
            modifier = modifier,
            buttonText = buttonText,
            defaultText = defaultText,
            onClick = onClick,
            enabled = enabled
        )
    }

}


@Composable
fun DropDownButton(
    modifier: Modifier = Modifier,
    buttonText: String = "",
    defaultText: String = "",
    onClick: () -> Unit = {},
    enabled: Boolean = true,
) {
    OutlinedButton(
        modifier = modifier.contentDescription(buttonText),
        shape = RoundedCornerShape(dimensionResource(id = androidx.cardview.R.dimen.cardview_default_radius)),
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = R.color.connectWhite)
        ),
        border = BorderStroke(dimensionResource(id = R.dimen.boarder_stroke_outline_dropdown_button), colorResource(id = R.color.connectPrimaryGrey))

    ) {
        Row {
            Text(
                text = buttonText.ifEmpty { defaultText },
                style = TypographyBody1,
                color = if(buttonText.isEmpty()) colorResource(id = R.color.connectGrey08) else colorResource(id = R.color.connectSecondaryDarkBlue),
                textAlign = TextAlign.End,
                modifier = Modifier.contentDescription(buttonText.ifEmpty { defaultText }).
                wrapContentWidth()
            )
            Spacer(modifier = Modifier.weight(1.0f))
            Text(
                text = stringResource(R.string.fa_caret_down_solid),
                modifier = Modifier.align(CenterVertically),
                style = TextStyle(
                    color = colorResource(R.color.connectGrey09),
                    fontFamily = FontAwesomeSolid,
                    fontWeight = FontWeight.W400
                ),
                textAlign = TextAlign.Center
            )

        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DropDownButtonPreview() {
    DropDownButton(
        buttonText = "Button"
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LabeledDropDownButtonPreview() {
    LabeledDropDownButton(
        labelText = "Label",
        buttonText = "Button"
    )
}