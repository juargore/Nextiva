/*
 * Copyright (c) 2024 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.models.AvatarInfo

@Composable
fun ConnectAvatarView(
    modifier: Modifier = Modifier,
    avatarInfo: AvatarInfo,
    avatarManager: AvatarManager
) {
    val avatarBitmap = remember { avatarManager.getBitmap(avatarInfo) }

    Box(
        modifier = modifier
            .background(Color.Transparent)
    ) {
        avatarBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .padding(dimensionResource(R.dimen.hairline_large).value.dp)
                    .align(Alignment.Center)
            )
        }

        if (avatarInfo.presence?.state != null && avatarInfo.presence?.state != -1) {
            PresenceIndicator(
                modifier = Modifier.align(Alignment.BottomEnd),
                avatarInfo = avatarInfo
            )
        }
    }
}

@Composable
private fun PresenceIndicator(
    modifier: Modifier,
    avatarInfo: AvatarInfo
) {
    Box(
        modifier = modifier
            .size(15.dp)
            .background(
                color = colorResource(R.color.connectWhite),
                shape = CircleShape
            )
            .clip(CircleShape)
            .padding(dimensionResource(R.dimen.general_padding_xxsmall).value.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = colorResource(getPresenceColor(avatarInfo.presence)),
                    shape = CircleShape
                )
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when (avatarInfo.presence?.state) {
                Enums.Contacts.PresenceStates.OFFLINE,
                Enums.Contacts.PresenceStates.CONNECT_OFFLINE -> {
                    PresenceRing(colorResource(R.color.avatarConnectOfflinePresenceStroke))
                }
                else -> {
                    getPresenceIcon(avatarInfo.presence)?.let {
                        Image(
                            painter = painterResource(it),
                            contentDescription = null,
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.general_horizontal_margin_xxsmall).value.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresenceRing(color: Color) {
    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {
            drawCircle(
                color = color,
                style = Stroke(width = 12f)
            )
        }
    )
}


private fun getPresenceColor(presence: DbPresence?): Int {
    return when (presence?.state) {
        Enums.Contacts.PresenceStates.AVAILABLE -> R.color.avatarOnlinePresence
        Enums.Contacts.PresenceStates.CONNECT_ACTIVE,
        Enums.Contacts.PresenceStates.CONNECT_ONLINE -> R.color.connectPrimaryGreen
        Enums.Contacts.PresenceStates.AWAY -> R.color.avatarAwayPresence
        Enums.Contacts.PresenceStates.CONNECT_BUSY,
        Enums.Contacts.PresenceStates.CONNECT_DND,
        Enums.Contacts.PresenceStates.CONNECT_OUT_OF_OFFICE -> R.color.connectPrimaryRed
        Enums.Contacts.PresenceStates.BUSY -> R.color.avatarBusyPresence
        Enums.Contacts.PresenceStates.CONNECT_AUTOMATIC -> R.color.connectSecondaryGrey
        Enums.Contacts.PresenceStates.CONNECT_AWAY,
        Enums.Contacts.PresenceStates.CONNECT_BE_RIGHT_BACK -> R.color.connectPrimaryYellow
        Enums.Contacts.PresenceStates.PENDING -> R.color.avatarPendingPresence
        else -> R.color.avatarConnectOfflinePresenceFill
    }
}

private fun getPresenceIcon(presence: DbPresence?): Int? {
    return when (presence?.state) {
        Enums.Contacts.PresenceStates.AVAILABLE,
        Enums.Contacts.PresenceStates.CONNECT_ACTIVE,
        Enums.Contacts.PresenceStates.CONNECT_ONLINE -> R.drawable.ic_online
        Enums.Contacts.PresenceStates.AWAY,
        Enums.Contacts.PresenceStates.CONNECT_AWAY -> R.drawable.ic_clock
        Enums.Contacts.PresenceStates.BUSY -> R.drawable.ic_busy
        Enums.Contacts.PresenceStates.CONNECT_DND -> R.drawable.ic_dnd
        Enums.Contacts.PresenceStates.OFFLINE,
        Enums.Contacts.PresenceStates.CONNECT_OFFLINE -> R.drawable.ic_offline
        Enums.Contacts.PresenceStates.PENDING -> R.drawable.ic_invite
        else -> null
    }
}
