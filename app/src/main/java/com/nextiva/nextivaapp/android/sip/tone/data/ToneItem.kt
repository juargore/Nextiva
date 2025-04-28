/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.sip.tone.data

import android.media.Ringtone
import android.net.Uri

/**
 * Created by Thaddeus Dannar on 12/11/23.
 */
data class ToneItem(val title: String, val uri: Uri, val ringtone: Ringtone? = null, var isSelected: Boolean = false)
