/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.sip.tone

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.sip.tone.data.ToneItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.annotation.Nullable
import javax.inject.Inject

@HiltViewModel
class ToneViewModel @Inject constructor() : ViewModel() {
    var toneType: Int = Enums.Sip.CallTones.ToneTypes.RING
    var selectedTone: ToneItem? = null
    var toneList: MutableList<ToneItem> = mutableListOf()

    @Inject
    lateinit var notificationManager: NotificationManager

    fun selectTone(@Nullable context: Context?, toneUri: Uri?) {
        if (context != null && toneList.isEmpty()) {
            toneList = getTones(context, toneUri)
        } else {
            toneList.forEach { it.isSelected = it.uri == toneUri }
        }
    }

    private fun getTones(context: Context, selectedUri: Uri?): MutableList<ToneItem> {
        val ringtoneManager: RingtoneManager = RingtoneManager(context).apply {
            setType(if(toneType == Enums.Sip.CallTones.ToneTypes.RING)RingtoneManager.TYPE_RINGTONE else RingtoneManager.TYPE_NOTIFICATION)
        }

        ringtoneManager.cursor.moveToFirst()
        val tones = mutableStateListOf<ToneItem>()
        val cursor = ringtoneManager.cursor

        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val ringtoneUri = ringtoneManager.getRingtoneUri(cursor.position)
            val ringtone = ringtoneManager.getRingtone(cursor.position)
            val isSelected = ringtoneUri == selectedUri

            if(isSelected)
            {
                selectedTone = ToneItem(title, ringtoneUri, ringtone, true)
            }

            tones.add(ToneItem(title, ringtoneUri, ringtone, isSelected))
        }

        cursor.close()
        return tones
    }

    fun getTitle(context: Context): String
    {
        return if(toneType == Enums.Sip.CallTones.ToneTypes.NOTIFICATION) context.getString(R.string.app_preference_sms) else context.getString(R.string.app_preference_ringtone)
    }

    fun updateChannelTone(toneUri: Uri?)
    {
        if(toneType == Enums.Sip.CallTones.ToneTypes.NOTIFICATION) {
            notificationManager.replaceNotificationChannelForNewSound(Enums.Notification.ChannelIDs.SMS, toneUri)
        }
    }


}