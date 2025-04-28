package com.nextiva.nextivaapp.android.features.rooms.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1

@Composable
fun SearchView() {
    var text by rememberSaveable { mutableStateOf("") }

    BasicTextField(modifier = Modifier
        .background(
            colorResource(R.color.connectWhite),
            MaterialTheme.shapes.small,
        )
        .fillMaxWidth(),
        value = text,
        onValueChange = {
            text = it
        },
        singleLine = true,
        cursorBrush = SolidColor(colorResource(R.color.connectGrey10)),
        textStyle = TypographyBody1,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .height(dimensionResource(id = R.dimen.general_view_xlarge))
                    .border(
                        width = dimensionResource(id = R.dimen.hairline_small),
                        color = colorResource(id = R.color.connectGrey09),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.general_view_xxsmall))
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.general_padding_xmedium)),
                    text = stringResource(R.string.fa_search),
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.connectGrey10),
                    fontFamily = FontAwesome,
                )

                Box(Modifier.weight(1f)) {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.connect_bottom_sheet_room_search_hint),
                            color = colorResource(R.color.connectGrey10),
                            style = TypographyBody1
                        )
                    }

                    innerTextField()
                }
            }
        }
    )
}

@Preview
@Composable
fun SearchViewDefaultPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SearchView()
    }
}