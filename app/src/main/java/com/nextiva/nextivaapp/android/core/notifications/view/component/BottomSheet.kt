/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.core.notifications.view.component

import android.content.res.Configuration
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyH5

/**
 * Created by Thaddeus Dannar on 3/30/23.
 */


@Composable
private fun BottomSheetTitle(type: String, onBackButton: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.connectGrey01))
    ) {

        Image(
            painter = painterResource(R.drawable.ic_bottom_sheet_pull_down),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(dimensionResource(id = R.dimen.general_padding_xsmall))
        )

        Row(
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.general_padding_medium),
                    end = dimensionResource(id = R.dimen.general_padding_xmedium),
                    top = dimensionResource(id = R.dimen.general_padding_small),
                    bottom = dimensionResource(id = R.dimen.general_padding_small)
                )
        ) {
            Text(
                text = type,
                style = TypographyH5,
                color = colorResource(R.color.connectSecondaryDarkBlue)
            )

            Spacer(modifier = Modifier.weight(1.0f))

            AndroidView(
                factory = {
                    val view = LayoutInflater.from(it).inflate(R.layout.view_toolbar_back_arrow, null, false)
                    view.setOnClickListener { onBackButton() }
                    view
                }
            )
        }
    }
}


@Composable
fun BottomSheetEmpty(
    type: String,
    typeIcon: String,
    onCreateClicked: () -> Unit,
    onBackButton: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.connectWhite))
    ) {
        BottomSheetTitle(type, onBackButton)
        Spacer(modifier = Modifier.weight(0.7f))

        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.general_view_xxxxxxxxxxxxlarge))
                .clip(CircleShape)
                .background(colorResource(id = R.color.connectPrimaryLightBlue))
                .align(Alignment.CenterHorizontally),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = typeIcon,
                style = TextStyle(
                    color = colorResource(R.color.connectGrey08),
                    fontSize = dimensionResource(R.dimen.general_view_xxlarge).value.sp,
                    fontFamily = FontAwesome,
                    fontWeight = FontWeight.Normal,
                )
            )
        }

        Text(
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.general_padding_large))
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.bottomsheet_no_type_to_show, type),
            color = colorResource(id = R.color.connectSecondaryDarkBlue),
            style = TypographyH5
        )

        Text(
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.general_padding_xmedium))
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.bottomsheet_type_empty_subtitle, type),
            color = colorResource(id = R.color.connectGrey10),
            style = TypographyBody1
        )

        Text(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.general_padding_large))
                .align(Alignment.CenterHorizontally)
                .clickable { onCreateClicked() },
            text = stringResource(id = R.string.bottomsheet_empty_subtitle, type),
            color = colorResource(id = R.color.connectPrimaryBlue),
            style = TypographyBody1Heavy
        )

        Spacer(modifier = Modifier.weight(0.7f))
    }
}


@Composable
fun BottomSheetList(
    list: List<String>,
    onBackButton: () -> Unit,
    valueField: MutableLiveData<String> = MutableLiveData("")
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.connectWhite))
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.connectGrey01))
        ) {

            Image(
                painter = painterResource(R.drawable.ic_bottom_sheet_pull_down),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(dimensionResource(id = R.dimen.general_padding_xsmall))
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            for (text in list) {
                item {
                    val itemClick = {
                        valueField.value = text
                        onBackButton()
                    }
                    SimpleListItem(text = text, onClick = itemClick, text == valueField.value.toString())
                }
            }
        }

    }
}

@Composable
fun BottomSheetMonthList(
    onBackButton: () -> Unit,
    customMonth: MutableLiveData<String> = MutableLiveData("")
) {
    BottomSheetList(
        stringArrayResource(id = R.array.general_months).toList(),
        onBackButton,
        customMonth
    )
}

@Composable
fun BottomSheetDaysOfTheWeekList(
    onBackButton: () -> Unit,
    customDay: MutableLiveData<String> = MutableLiveData("")
) {
        BottomSheetList(
        stringArrayResource(id = R.array.general_days_of_the_week).toList(),
        onBackButton,
        customDay
    )
}


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "small font", showBackground = true, group = "font scales", fontScale = 0.5f)
@Preview(name = "large font", showBackground = true, group = "font scales", fontScale = 1.5f)
@Composable
fun BottomSheetTitlePreview() {
    BottomSheetTitle("Schedule") {}
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "small font", showBackground = true, group = "font scales", fontScale = 0.5f)
@Preview(name = "large font", showBackground = true, group = "font scales", fontScale = 1.5f)
@Composable
fun BottomSheetEmptyPreview() {
    BottomSheetEmpty("Schedule", stringResource(R.string.fa_clock), {}, {})
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "small font", showBackground = true, group = "font scales", fontScale = 0.5f)
@Preview(name = "large font", showBackground = true, group = "font scales", fontScale = 1.5f)
@Composable
fun BottomSheetListPreview() {
    var list = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6")
    BottomSheetList(list, {})
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "small font", showBackground = true, group = "font scales", fontScale = 0.5f)
@Preview(name = "large font", showBackground = true, group = "font scales", fontScale = 1.5f)
@Composable
fun BottomSheetMonthListPreview() {
    val valueField: MutableLiveData<String> = MutableLiveData("May")
    BottomSheetMonthList({}, valueField)
}


@Preview(showBackground = true)
@Preview(name = "night mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "small font", showBackground = true, group = "font scales", fontScale = 0.5f)
@Preview(name = "large font", showBackground = true, group = "font scales", fontScale = 1.5f)
@Composable
fun BottomSheetDaysOfTheWeekListPreview() {
    val valueField: MutableLiveData<String> = MutableLiveData("Wednesday")
    BottomSheetDaysOfTheWeekList({}, valueField)
}