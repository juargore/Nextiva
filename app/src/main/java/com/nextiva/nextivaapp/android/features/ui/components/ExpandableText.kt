/*
 * Copyright (c) 2024 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontLato
import com.nextiva.nextivaapp.android.util.extensions.orZero

@Composable
fun ExpandableText(text: String, maxLines: Int = 3) {

    var expandedState by remember { mutableStateOf(false) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var isTextOverflowing by remember { mutableStateOf(false) }

    LaunchedEffect(textLayoutResult) {
        isTextOverflowing = textLayoutResult?.lineCount.orZero() > maxLines
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.general_padding_xmedium))
    ) {
        Text(
            text = stringResource(R.string.voicemail_list_transcription_title),
            color = colorResource(R.color.connectSecondaryDarkBlue),
            fontSize = dimensionResource(R.dimen.material_text_button).value.sp,
            fontWeight = FontWeight.W800,
            fontFamily = FontLato
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = text.trimStart(),
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.general_padding_xmedium))
                    .height(dimensionResource(R.dimen.hairline_xlarge)),
                onTextLayout = { textLayoutResult = it },
                color = Color.Transparent,
                fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
            )
            Text(
                text = text.trimStart(),
                maxLines = if (expandedState) Int.MAX_VALUE else maxLines,
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.general_padding_xmedium)),
                overflow = TextOverflow.Ellipsis,
                color = colorResource(R.color.connectGrey10),
                fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
            )
        }

        if (isTextOverflowing) {
            val showMore = stringResource(R.string.voicemail_list_show_more)
            val showLess = stringResource(R.string.voicemail_list_show_less)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = if (expandedState) showLess else showMore,
                    modifier = Modifier
                        .padding(vertical = dimensionResource(R.dimen.general_padding_xsmall))
                        .clickable { expandedState = !expandedState },
                    textAlign = TextAlign.End,
                    color = colorResource(R.color.connectPrimaryBlue),
                    fontSize = dimensionResource(R.dimen.material_text_button).value.sp,
                    fontWeight = FontWeight.W800,
                    fontFamily = FontLato
                )
            }
        }
    }
}
