package com.nextiva.nextivaapp.android.features.rooms.view.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.common.ui.ToolbarButton
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeSolid
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyLargeHeavy
import com.nextiva.nextivaapp.android.util.extensions.contentDescription

@Composable
fun ScreenTitleBarView(title: String, titleCount: String = "", onBackButton: (() -> Unit)? = null, onTitleClicked: (() -> Unit)? = null, onIconClicked: (() -> Unit)? = null) {
    ScreenTitleBarView(title = title, titleCount = titleCount, onBackButton = onBackButton, onTitleClicked = onTitleClicked, onIconClicked = onIconClicked, titleStyle = TypographyBody2)
}

@Composable
fun ScreenTitleBarLargeTextView(title: String, titleCount: String = "", onBackButton: (() -> Unit)? = null, onTitleClicked: (() -> Unit)? = null, onIconClicked: (() -> Unit)? = null) {
    ScreenTitleBarView(title = title, titleCount = titleCount, onBackButton = onBackButton, onTitleClicked = onTitleClicked, onIconClicked = onIconClicked, titleStyle = TypographyLargeHeavy)
}

@Composable
fun ScreenTitleBarView(title: String, titleCount: String = "", onBackButton: (() -> Unit)? = null, onTitleClicked: (() -> Unit)? = null, onIconClicked: (() -> Unit)? = null, titleStyle: TextStyle) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.connectGrey01)),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            ToolbarButton(
                iconId = R.string.fa_arrow_left,
                colorId = R.color.connectGrey10,
                fontFamily = FontAwesome
            ) { onBackButton?.let { it() } }

            Text(
                text = title,
                color = colorResource(R.color.connectSecondaryDarkBlue),
                style = titleStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .contentDescription(title)
                    .weight(1f)
                    .padding(
                        start = dimensionResource(R.dimen.general_padding_small),
                        top = dimensionResource(R.dimen.general_padding_xmedium),
                        bottom = dimensionResource(R.dimen.general_padding_xmedium))
                    .clickable { onTitleClicked?.let { it() }
                }
            )

            Text(
                text = titleCount,
                color = colorResource(R.color.connectSecondaryDarkBlue),
                style = titleStyle,
                maxLines = 1,
                modifier = Modifier
                    .contentDescription(title)
                    .weight(0.2f)
                    .padding(
                        start = dimensionResource(R.dimen.general_padding_small),
                        top = dimensionResource(R.dimen.general_padding_xmedium),
                        bottom = dimensionResource(R.dimen.general_padding_xmedium))
                    .clickable { onTitleClicked?.let { it() }
                    }
            )

            onIconClicked?.let { clickedAction ->
                ToolbarButton(
                    iconId = R.string.fa_phone_alt,
                    colorId = R.color.connectPrimaryBlue,
                    fontFamily = FontAwesomeSolid
                ) { clickedAction() }
            }
        }
    }
}

@Preview(
    showSystemUi = true
)
@Composable
fun ScreenTitleBarViewDefaultPreview() {
    ScreenTitleBarView(
        title = "Ashlynn Dias, Adeline Johnson-Markson & Shirley Fuller",
        titleCount = "+3",
        onBackButton = null,
        onTitleClicked = null,
        onIconClicked = { }
    )
}

@Preview(
    showSystemUi = true
)
@Preview(
    showSystemUi = true, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScreenTitleBarLargeDefaultPreview() {
    ScreenTitleBarLargeTextView(
        title = "Screen Title",
        onBackButton = null,
        onTitleClicked = null
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScreenTitleBarViewTitleStylePreview() {
    ScreenTitleBarView(
        title = "Ashlynn Dias, Adeline Johnson-Markson",
        onBackButton = null,
        onTitleClicked = null,
        titleStyle = TypographyBody1Heavy
    )
}