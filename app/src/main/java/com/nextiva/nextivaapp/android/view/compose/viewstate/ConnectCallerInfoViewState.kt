package com.nextiva.nextivaapp.android.view.compose.viewstate

import android.graphics.Bitmap
import com.nextiva.nextivaapp.android.models.AvatarInfo

data class ConnectCallerInfoViewState(
    val callerDisplayName: String? = null,
    val callerPhoneNumber: String? = null,
    val avatarViewState: ConnectAvatarViewState? = ConnectAvatarViewState(),
    val callTimer: String? = null,
    val shouldShowTimer: Boolean? = false,
    val conferenceParticipantCount: String? = null
)

data class ConnectAvatarViewState(
    val avatarBitMap: Bitmap? = null,
    val avatarInfo: AvatarInfo? = null
)