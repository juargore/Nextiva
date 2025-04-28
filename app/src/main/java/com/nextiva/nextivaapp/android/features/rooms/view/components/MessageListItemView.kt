package com.nextiva.nextivaapp.android.features.rooms.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1Heavy
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.view.AvatarView

@Composable
fun MessageListItemView(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    timestamp: String,
    avatarInfo: AvatarInfo,
    unreadCount: Int
) {
    Column(modifier = modifier
        .background(colorResource(id = R.color.connectWhite))
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(id = R.dimen.general_padding_medium),
                    vertical = dimensionResource(id = R.dimen.general_padding_xmedium)
                )
                .fillMaxWidth()
        ) {
            AndroidView(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.avatar_connect_size_sms))
                    .width(dimensionResource(R.dimen.avatar_connect_size_sms)),
                factory = { context ->
                    AvatarView(context).apply {
                        setAvatar(avatarInfo, true)
                    }
                }
            )

            Column(
                modifier = Modifier
                    .padding(start = dimensionResource(id = R.dimen.general_padding_medium))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colorResource(if (unreadCount > 0) R.color.connectPrimaryBlue else R.color.connectSecondaryDarkBlue),
                        style = TypographyBody2Heavy,
                        modifier = Modifier
                            .weight(0.9f)
                    )

                    Spacer(Modifier.weight(0.1f))

                    Text(
                        text = timestamp,
                        maxLines = 1,
                        color = colorResource(R.color.connectSecondaryDarkBlue),
                        style = TypographyCaption1,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                Row (
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.general_padding_xsmall))
                ) {
                    Text(
                        text = subtitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = colorResource(R.color.connectGrey10),
                        style = TypographyCaption1,
                        modifier = Modifier
                            .weight(0.9f)
                    )

                    Spacer(Modifier.weight(0.1f))

                    if (unreadCount > 0) {
                        UnreadMessageCountView(
                            count = unreadCount,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }

        Divider(
            color = colorResource(R.color.connectGrey03),
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.hairline_small))
        )
    }
}

@Composable
fun UnreadMessageCountView(count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(dimensionResource(id = R.dimen.general_view_xmedium))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    colorResource(R.color.connectPrimaryBlue),
                    shape = RoundedCornerShape(4.dp)
                )
        )

        Text(
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(id = R.dimen.general_padding_small)
                )
                .align(Alignment.Center),
            text = "$count",
            color = colorResource(R.color.connectWhite),
            style = TypographyCaption1Heavy,
        )
    }
}

@Preview
@Composable
fun MessageListItemViewDefaultPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.connectWhite))
    ) {

        MessageListItemView(
            title = "Warren Wong",
            subtitle = "Hi! Going to be running a couple of minutes late.",
            timestamp = "1:45 pm",
            avatarInfo = AvatarInfo.Builder().build(),
            unreadCount = 2
        )

        MessageListItemView(
            title = "Albert Nichols, Alexis Nava, Madison Wong",
            subtitle = "Warren Wong: Have you guys been able to go through and clean up/organize your files?",
            timestamp = "Yesterday",
            avatarInfo = AvatarInfo.Builder().build(),
            unreadCount = 7
        )

        MessageListItemView(
            title = "Shirley Fuller",
            subtitle = "You: Thank you Shirley for being amazing! I know it has been a busy busy week and just trying to get back into longer test message",
            timestamp = "Monday",
            avatarInfo = AvatarInfo.Builder().build(),
            unreadCount = 0
        )

    }
}