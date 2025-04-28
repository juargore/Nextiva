/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.core.notifications.view.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.util.extensions.contentDescription

/**
 * Created by Thaddeus Dannar on 4/4/23.
 */
@Composable
fun SimpleListItem(text: String = "", onClick: () -> Unit = {}, isSelected: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = rememberRipple(color = MaterialTheme.colors.primary),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .background(colorResource(id = if (isSelected) R.color.connectGrey01 else R.color.connectWhite)),
    ) {

        Text(
            text = text,
            style = TypographyBody1,
            color = colorResource(id = R.color.connectSecondaryDarkBlue),
            modifier = Modifier.contentDescription(text)
                .padding(
                    top = dimensionResource(R.dimen.general_padding_medium),
                    start = dimensionResource(R.dimen.general_padding_small),
                    bottom = dimensionResource(R.dimen.general_padding_medium)
                )
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SimpleListItemPreviewSelected() {
    SimpleListItem("Text", {}, true)
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SimpleListItemPreview() {
    SimpleListItem("Text", {})
}