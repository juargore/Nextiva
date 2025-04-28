package com.nextiva.nextivaapp.android.features.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectCallHistoryListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.features.ui.theme.FontLato
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.util.extensions.extractFirstNumber
import com.nextiva.nextivaapp.android.util.extensions.formatPhoneNumber
import com.nextiva.nextivaapp.android.view.compose.ConnectAvatarView
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

@Composable
fun ConnectCallHistoryListItemView(
    mListItem: ConnectCallHistoryListItem,
    avatarInfo: AvatarInfo,
    time: String?,
    avatarManager: AvatarManager,
    onClick: () -> Unit,
) {
    val mContext = LocalContext.current
    var showCheckBox by remember { mutableStateOf(false) }
    var checkBoxIsChecked by remember { mutableStateOf(false) }

    mListItem.isChecked?.let {
        showCheckBox = true
        checkBoxIsChecked = it
    } ?: run {
        showCheckBox = false
    }

    var iconColor = mContext.getColor(R.color.connectGrey09)
    var iconString = R.string.fa_phone_alt
    var enumIconType = Enums.FontAwesomeIconType.REGULAR
    when (mListItem.callEntry.callType) {
        Enums.Calls.CallTypes.PLACED -> {
            iconString = R.string.fa_custom_outbound_call
            enumIconType = Enums.FontAwesomeIconType.CUSTOM
        }

        Enums.Calls.CallTypes.MISSED -> {
            iconColor = mContext.getColor(R.color.connectPrimaryRed)
        }
    }


    val cardBackground = if (!checkBoxIsChecked) {
        Modifier.background(color = colorResource(R.color.connectWhite))
    } else {
        Modifier
            .border(
                width = dimensionResource(R.dimen.hairline_small).value.dp,
                color = colorResource(R.color.connectSecondaryBrightBlue)
            )
            .background(color = colorResource(R.color.connectSecondaryLightBlue))
    }

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = cardBackground
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(vertical = dimensionResource(R.dimen.general_vertical_margin_medium))
            .padding(
                start = dimensionResource(R.dimen.general_padding_xmedium),
                end = dimensionResource(R.dimen.general_padding_medium)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showCheckBox) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.avatar_connect_call_size))
                    .padding(start = dimensionResource(R.dimen.general_padding_small))
            ) {
                Checkbox(
                    checked = checkBoxIsChecked,
                    onCheckedChange = { onClick() },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = colorResource(R.color.connectWhite),
                        checkedColor = colorResource(R.color.nextivaPrimaryBlue),
                        uncheckedColor = colorResource(R.color.connectGrey09)
                    )
                )
            }
        }

        ConnectAvatarView(
            avatarInfo = avatarInfo,
            avatarManager = avatarManager,
            modifier = Modifier.size(
                dimensionResource(R.dimen.avatar_connect_call_size).value.dp
            )
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = dimensionResource(R.dimen.general_horizontal_margin_xmedium),
                    end = dimensionResource(R.dimen.general_padding_medium)
                ),
        ) {
            val colorResource = colorResource(
                if (mListItem.isBlocked) {
                    R.color.connectGreyDisabled
                } else {
                    if (mListItem.callEntry.isRead) R.color.connectSecondaryDarkBlue else R.color.connectPrimaryBlue
                }
            )

            Text(
                text = mListItem.callEntry.humanReadableName ?: mContext.getString(R.string.general_unavailable),
                color = colorResource,
                fontSize = dimensionResource(R.dimen.material_text_body1).value.sp,
                fontWeight = FontWeight.W800,
                textAlign = TextAlign.Start,
                maxLines = 1,
                fontFamily = FontLato,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.general_padding_xsmall))
            ) {

                AndroidView(modifier = Modifier.size(dimensionResource(R.dimen.general_view_small)),
                    factory = { context ->
                        FontTextView(context).apply {
                            setIcon(iconString, enumIconType)
                            setTextColor(iconColor)
                        }
                    })

                Text(
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.general_padding_xxsmall)),
                    text = mListItem.callEntry.phoneNumber?.extractFirstNumber()
                        ?.formatPhoneNumber().orEmpty(),
                    color = colorResource(R.color.connectGrey10),
                    fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
                    maxLines = 1,
                    fontFamily = FontLato,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Column(
            modifier = Modifier,
            horizontalAlignment = AbsoluteAlignment.Right
        ) {
            Text(
                text = time ?: "",
                modifier = Modifier.padding(
                    end = dimensionResource(R.dimen.general_padding_small),
                    bottom = dimensionResource(R.dimen.general_padding_xsmall)
                ),
                fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
                color = colorResource(R.color.connectGrey10),
                fontFamily = FontLato,
            )

            if (mListItem.isBlocked) {
                Box(
                    modifier = Modifier
                        .background(
                            color = colorResource(R.color.surfaceError),
                            shape = RoundedCornerShape(dimensionResource(R.dimen.general_view_xxsmall).value.dp)
                        )
                        .padding(
                            horizontal = dimensionResource(R.dimen.general_horizontal_margin_xxsmall),
                            vertical = dimensionResource(R.dimen.hairline_large)
                        )
                ) {
                    Text(
                        text = mContext.getString(R.string.connect_contact_details_blocked_label),
                        color = colorResource(R.color.connectSecondaryRed),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
