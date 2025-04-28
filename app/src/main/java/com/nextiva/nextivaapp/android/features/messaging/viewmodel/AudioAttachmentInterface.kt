package com.nextiva.nextivaapp.android.features.messaging.viewmodel

import android.graphics.drawable.Drawable
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentAudioFilePlayer
import com.nextiva.nextivaapp.android.models.SmsMessage

interface AudioAttachmentInterface {
    fun playAudioAttachment(id: String, filename: String, url: String)
    fun audioProgressDragged(progress: Int)
    fun toggleSpeaker(attachmentId: String)
    fun getAttachmentAudioFilePlayer(): AttachmentAudioFilePlayer
    fun getSessionId(): String
    fun getCorpAcctNumber(): String
    fun senderUiName(listItem: SmsMessageListItem): String
    fun onClicked(filename: String?, url: String?)
    fun onResendSmsMessage(message: SmsMessage)
    fun onLongClicked(drawable: Drawable?, filename: String?, url: String?, message: SmsMessageListItem)
    fun onDeleteMessage(message: SmsMessageListItem)
}
