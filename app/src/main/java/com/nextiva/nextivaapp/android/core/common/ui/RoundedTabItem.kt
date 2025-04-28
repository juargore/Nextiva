package com.nextiva.nextivaapp.android.core.common.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.models.CallTabItem
import kotlin.math.pow

@Composable
fun RoundedTabItem(
    tab: CallTabItem,
    index: Int,
    state: Int,
    maxBadgeCharLimit: Int = 1,
    onClick: (Int) -> Unit,
) {

    val shape = RoundedCornerShape(
        topStart = dimensionResource(R.dimen.general_view_small), topEnd = dimensionResource(
            R.dimen.general_view_small
        )
    )

    Tab(
        modifier = Modifier
            .padding(top = 3.dp, start = 0.5.dp, end = 0.5.dp)
            .fillMaxWidth(0f)
            .shadow(
                shape = shape,
                elevation = if (state == index) 6.dp else 1.dp,
                clip = true
            )
            .clip(shape = shape)
            .background(
                color =
                if (state == index)
                    colorResource(id = R.color.connectWhite)
                else
                    colorResource(id = R.color.connectGrey01),
                shape = shape
            ),
        selected = state == index,
        onClick = {

            onClick(index)
        },
        selectedContentColor = colorResource(id = R.color.connectSecondaryBlue),
        unselectedContentColor = colorResource(id = R.color.connectSecondaryDarkBlue)
    ) {
        Row(
            modifier = Modifier.height(dimensionResource(id = R.dimen.general_view_xxlarge)),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            tab.CallTabIcon?.let {
                Icon(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.general_view_medium))
                        .weight(1f, fill = false),
                    painter = painterResource(id = it),
                    contentDescription = tab.CallTabContentDescription,
                    tint = colorResource(id = R.color.connectGrey09)
                )
            }

            Text(
                text = tab.CallTabTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(R.font.lato_heavy)),
                    fontWeight = FontWeight(800),
                    letterSpacing = 0.sp,

                    ),
                modifier = Modifier
                    .weight(4f, fill = false)
            )

            tab.CallTabBadgeNumber?.let {
                if(it > 0) {
                    BadgedBox(
                        modifier = Modifier
                            .weight(2.5f, fill = false),
                        badge = {
                            Badge(
                                modifier = Modifier.offset(x = (-16).dp, y = 10.dp),
                                backgroundColor = colorResource(id = R.color.connectPrimaryRed)
                            ) {
                                Text(
                                    getBadgeText(it, maxBadgeCharLimit),
                                    color = colorResource(id = R.color.connectWhite),
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                )
                            }
                        }) {

                        Box(
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.general_view_xmedium))
                                .background(Color.Transparent)
                        )

                    }
                }
            }

        }
    }
}

private fun getBadgeText(badgeNumber: Int, maxBadgeCharLimit: Int): String {
    return if (maxBadgeCharLimit < 2 || badgeNumber.toString().length < maxBadgeCharLimit) {
        badgeNumber.toString()
    } else {
        maxBadgeNumber(maxBadgeCharLimit).toString() + "+"
    }
}

private fun maxBadgeNumber(maxCharacterCount: Int): Int {
    val powerBase = 10.0
    return powerBase.pow(maxCharacterCount.toDouble() - 1).toInt() - 1
}


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, fontScale = 4f)
@Composable
fun RoundTabItemMax2Preview() {
    val callTabItem = CallTabItem(
        CallTabId = 0,
        ConnectCallsFilterType = 0,
        CallTabTitle = "Test",
        CallTabIcon = R.drawable.ic_call_missed_grey,
        CallTabContentDescription = "Test tab",
        CallTabBadgeNumber = 1
    )

    RoundedTabItem(callTabItem, 1, 2, 2) {}
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, fontScale = 4f)
@Composable
fun RoundTabItemMax3Preview() {
    val callTabItem = CallTabItem(
        CallTabId = 0,
        ConnectCallsFilterType = 0,
        CallTabTitle = "Test",
        CallTabIcon = R.drawable.ic_call_missed_grey,
        CallTabContentDescription = "Test tab",
        CallTabBadgeNumber = 546
    )

    RoundedTabItem(callTabItem, 1, 2, 3) {}
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, widthDp = 100)
@Preview(showBackground = true, fontScale = 4f, widthDp = 150)
@Composable
fun RoundTabItemMax4Preview() {
    val callTabItem = CallTabItem(
        CallTabId = 0,
        ConnectCallsFilterType = 0,
        CallTabTitle = "Test",
        CallTabIcon = R.drawable.ic_call_missed_grey,
        CallTabContentDescription = "Test tab",
        CallTabBadgeNumber = 546
    )

    RoundedTabItem(callTabItem, 1, 2, 4) {}
}

@Preview(showBackground = true, fontScale = 2f, widthDp = 100)
@Composable
fun RoundTabItemLongTitleShortWidthPreview() {
    val callTabItem = CallTabItem(
        CallTabId = 0,
        ConnectCallsFilterType = 0,
        CallTabTitle = "Test Long",
        CallTabIcon = R.drawable.ic_call_missed_grey,
        CallTabContentDescription = "Test tab",
        CallTabBadgeNumber = 54600
    )

    RoundedTabItem(callTabItem, 1, 2, 4) {}
}