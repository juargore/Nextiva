package com.nextiva.nextivaapp.android.features.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem
import com.nextiva.nextivaapp.android.features.messaging.helpers.SMSType
import com.nextiva.nextivaapp.android.features.messaging.helpers.SmsTitleInfo
import com.nextiva.nextivaapp.android.features.ui.theme.FontLato
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.view.compose.ConnectAvatarView
import org.threeten.bp.Instant

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageListView(
    mListItem: MessageListItem,
    smsTitleInfo: SmsTitleInfo,
    avatarInfo: AvatarInfo,
    avatarManager: AvatarManager,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    var showCheckBox by remember { mutableStateOf(false) }
    var checkBoxIsChecked by remember { mutableStateOf(false) }
    var totalUnreadMessages = ""
    val showDraftIcon = mListItem.draftMessage != null
    val titleTextColor: Int

    val mContext = LocalContext.current
    val title = smsTitleInfo.smsTitleName
    val sender = mListItem.data.sender?.firstOrNull()
    val firstName = sender?.uiFirstName ?: ""
    val lastName = sender?.uiLastName ?: ""

    val timeToDisplay = mListItem.smsMessage.sent?.let {
        val formatterManager = FormatterManager.getInstance()
        formatterManager.format_humanReadableForListItems(mContext, it.toEpochMilli())
    } ?: run { "" }

    val userName = if (firstName.isNotEmpty()) "$firstName $lastName" else null

    val name = when {
        mListItem.smsMessage.isSender.orFalse() -> stringResource(R.string.message_list_item_message_you) + ":"
        userName == null -> {
            if (smsTitleInfo.type == SMSType.OneToOne)
                ""
            else
                "${sender?.phoneNumber.orEmpty()}:"
        }
        else -> "$userName:"
    }

    mListItem.isChecked?.let {
        showCheckBox = true
        checkBoxIsChecked = it
    } ?: run {
        showCheckBox = false
    }

    if (mListItem.unReadCount > 0) {
        titleTextColor = R.color.nextivaPrimaryBlue
        totalUnreadMessages = if (mListItem.unReadCount > 1) {
            if (mListItem.unReadCount < 100) {
                mListItem.unReadCount.toString()
            } else {
                mContext.getString(R.string.message_list_item_more_than_ninety_nine)
            }
        } else ""
    } else {
        titleTextColor = R.color.connectSecondaryDarkBlue
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

    Column(
        modifier = cardBackground
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                        .height(dimensionResource(R.dimen.avatar_connect_call_size).value.dp)
                        .padding(end = dimensionResource(R.dimen.general_horizontal_margin_xxsmall).value.dp)
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
                    dimensionResource(R.dimen.avatar_connect_size_sms).value.dp
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dimensionResource(R.dimen.general_horizontal_margin_xmedium).value.dp)
            ) {
                Column(
                    modifier = Modifier.weight(0.75f),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.hairline_large))
                ) {
                    Text(
                        text = title,
                        color = colorResource(titleTextColor),
                        fontSize = dimensionResource(R.dimen.material_text_body1).value.sp,
                        fontWeight = FontWeight.W800,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        fontFamily = FontLato
                    )
                    Text(
                        text = getSubtitle(mListItem, name),
                        maxLines = 2
                    )
                }

                Column(
                    modifier = Modifier.weight(0.25f),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.hairline_medium))
                ) {
                    Text(
                        text = timeToDisplay,
                        color = colorResource(R.color.connectSecondaryDarkBlue),
                        fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
                        textAlign = TextAlign.End
                    )
                    Row {
                        if (showDraftIcon) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = colorResource(R.color.draftMessageColor),
                                        shape = RoundedCornerShape(dimensionResource(R.dimen.general_view_xxsmall).value.dp)
                                    )
                                    .padding(
                                        horizontal = dimensionResource(R.dimen.general_horizontal_margin_xxsmall),
                                        vertical = dimensionResource(R.dimen.hairline_large)
                                    )
                            ) {
                                Text(
                                    text = stringResource(R.string.general_draft).uppercase(),
                                    color = colorResource(R.color.connectSecondaryDarkBlue),
                                    fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.general_horizontal_margin_xsmall)))

                        if (totalUnreadMessages.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = colorResource(R.color.nextivaPrimaryBlue),
                                        shape = RoundedCornerShape(dimensionResource(R.dimen.general_view_xxsmall).value.dp)
                                    )
                                    .padding(
                                        horizontal = dimensionResource(R.dimen.general_horizontal_margin_xxsmall),
                                        vertical = dimensionResource(R.dimen.hairline_large)
                                    )
                            ) {
                                Text(
                                    text = totalUnreadMessages,
                                    color = Color.White,
                                    fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        Divider(color = colorResource(R.color.connectGrey03))
    }
}

@Composable
fun getSubtitle(mListItem: MessageListItem, name: String): AnnotatedString {
    return buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
                color = colorResource(R.color.connectGrey10)
            )
        ) {
            append(name)
        }

        var body = mListItem.data.body?.ifEmpty { stringResource(R.string.Connect_sms_media_message) }

        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Normal,
                fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
                color = colorResource(R.color.connectGrey10)
            )
        ) {
            if ((mListItem.smsMessage.sent ?: Instant.MIN) < (mListItem.draftMessage?.sent ?: Instant.MIN)) {
                body = mListItem.draftMessage?.body.orEmpty()
            }
            if (name.isEmpty()) {
                append("$body")
            } else {
                append(" $body")
            }
        }
    }
}
