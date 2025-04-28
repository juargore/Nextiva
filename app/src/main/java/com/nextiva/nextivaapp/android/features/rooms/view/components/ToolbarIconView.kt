/*
 * Copyright (c) 2021 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.rooms.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome

@Composable
fun ToolbarIconView(
    modifier: Modifier = Modifier,
    iconString: String,
    onButtonClicked: () -> Unit
) {
    val fontSizeSp = with(LocalDensity.current) {
        dimensionResource(id = R.dimen.material_text_headline).toSp()
    }
    Box(
        modifier = modifier
            .padding(dimensionResource(R.dimen.general_padding_small))
            .size(dimensionResource(R.dimen.general_view_xxlarge))
            .clip(CircleShape)
            .background(colorResource(R.color.transparent))
            .clickable { onButtonClicked() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iconString,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.connectWhite),
            fontSize = fontSizeSp,
            fontFamily = FontAwesome,
            fontWeight = FontWeight.Normal
        )
    }
}
