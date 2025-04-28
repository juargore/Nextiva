package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums.FontAwesomeIconType
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeSolidV6
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeV6
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2
import com.nextiva.nextivaapp.android.view.compose.viewstate.MenuItem

@Composable
fun ConnectOptionsMenu(
    menuItems: List<MenuItem>,
    onMenuDismissListener: () -> Unit,
    shouldShowSelection: Boolean
) {
    DropdownMenu(
        expanded = true,
        modifier = Modifier.background(color = colorResource(id = R.color.audioDevicePopupBackground)),
        onDismissRequest = onMenuDismissListener
    ) {
        menuItems.forEachIndexed { index, menuItem ->
            DropdownMenuItem(
                onClick = {
                    menuItem.onItemClicked.invoke(index)
                    onMenuDismissListener.invoke()
                },
            ) {
                if (shouldShowSelection) {
                    MenuRowWithSelections(menuItem = menuItem)
                } else {
                    MenuRowWithOutSelections(menuItem = menuItem)
                }
            }
        }
    }
}

@Composable
private fun getMenuItemColor(menuItem: MenuItem): Color {
    return if (menuItem.isEnabled) colorResource(R.color.connectGrey09) else colorResource(
        id = R.color.connectPrimaryGrey
    )
}

@Composable
private fun MenuRowWithOutSelections(modifier: Modifier = Modifier, menuItem: MenuItem) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier,
            fontSize = dimensionResource(id = R.dimen.material_text_title1).value.sp,
            text = stringResource(id = menuItem.icon),
            fontWeight = FontWeight.W400,
            color = getMenuItemColor(menuItem = menuItem),
            fontFamily = if (menuItem.fontAwesomeIconType == FontAwesomeIconType.SOLID) {
                FontAwesomeSolidV6
            } else {
                FontAwesomeV6
            }
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.general_padding_medium)))
        Text(
            menuItem.name,
            modifier = Modifier
                .weight(1f),
            style = TypographyBody2,
            color = if (menuItem.isEnabled) colorResource(id = R.color.connectSecondaryDarkBlue)
            else colorResource(
                id = R.color.connectPrimaryGrey
            )
        )
    }
}

@Composable
private fun MenuRowWithSelections(modifier: Modifier = Modifier, menuItem: MenuItem) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (menuItem.isSelected) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                text = stringResource(R.string.fa_check),
                fontSize = dimensionResource(id = R.dimen.material_text_button).value.sp,
                fontWeight = FontWeight.W400,
                color = getMenuItemColor(menuItem = menuItem),
                fontFamily = FontAwesomeV6
            )
        } else {
            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.general_padding_medium)))
        }
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.general_padding_xxmedium)))
        Text(
            menuItem.name,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            style = TypographyBody2,
            color = colorResource(id = R.color.connectSecondaryDarkBlue)
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.general_padding_medium)))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = stringResource(id = menuItem.icon),
            fontSize = dimensionResource(id = R.dimen.material_text_title1).value.sp,
            fontWeight = FontWeight.W400,
            color = getMenuItemColor(menuItem = menuItem),
            fontFamily = FontAwesomeV6
        )
    }
}


@Composable
@Preview
fun MenuRowWithOutSelectionPreview(){
    MenuRowWithOutSelections(
        modifier = Modifier,
        menuItem = MenuItem(
            name = stringResource(id = R.string.active_call_transfer),
            icon = R.string.fa_phone_arrow_right,
            isEnabled = true,
            fontAwesomeIconType = FontAwesomeIconType.SOLID,
            isSelected = false,
            onItemClicked = {

            }
        )
    )
}