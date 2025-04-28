package com.nextiva.nextivaapp.android.features.rooms.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.dpToSp
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome

@Composable
fun FloatingContextMenu(
    pressOffset: DpOffset,
    expanded: Boolean,
    menuData: List<MenuData>,
    onDismissRequest: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .height(1.dp)
            .width(1.dp)
            .offset(x = pressOffset.x, y = pressOffset.y)
    ) {
        DropdownMenu(
            modifier = Modifier
                .background(colorResource(id = R.color.connectWhite)),
            expanded = expanded,
            onDismissRequest = { onDismissRequest(false) },
            properties = PopupProperties(clippingEnabled = false)
        ) {
            menuData.forEach { menuData ->
                DropdownMenuItem(onClick = { menuData.onClick.invoke() }) {
                    Row(modifier = Modifier.wrapContentSize()) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .align(alignment = Alignment.CenterVertically),
                            text = menuData.name,
                            color = menuData.color
                        )
                        Text(
                            text = menuData.icon,
                            fontFamily = FontAwesome,
                            fontSize = dpToSp(dimensionResource(R.dimen.general_view_small).value.dp),
                            fontWeight = FontWeight.Light,
                            modifier = Modifier
                                .padding(start = 48.dp)
                                .align(alignment = Alignment.CenterVertically),
                            color = menuData.color
                        )
                    }
                }
            }
        }
    }
}

data class MenuData(
    val name: String,
    val icon: String,
    val color: Color,
    val onClick: () -> Unit
)