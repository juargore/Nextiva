package com.nextiva.nextivaapp.android.features.messaging.view

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.MessageBaseViewHolder
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentSentView
import com.nextiva.nextivaapp.android.databinding.ListItemConnectChatMessageSentBinding
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.AudioAttachmentInterface
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageFailedView
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageSentView

internal class ConversationSentViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener?) :
    MessageBaseViewHolder<SmsMessageListItem>(itemView, context, masterListListener) {

    private var composeView: ComposeView? = null

    private lateinit var audioAttachmentInterface: AudioAttachmentInterface

    constructor(
        parent: ViewGroup,
        context: Context,
        masterListListener: MasterListListener?,
        audioAttachmentInterface: AudioAttachmentInterface
    ) : this(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_connect_chat_message_sent, parent, false),
        context,
        masterListListener
    ) {
        this.audioAttachmentInterface = audioAttachmentInterface
    }

    init {
        bindViews(itemView)
    }

    override fun bind(listItem: SmsMessageListItem) {
        removeItemViewFromParent()
        val isEmptyText = TextUtils.isEmpty(listItem.data.body)
        val textBubbleTime = if (listItem.showHumanReadableTime) listItem.humanReadableTime else null
        val attachmentBubbleTime = if (listItem.showHumanReadableTime && isEmptyText) listItem.humanReadableTime else null
        composeView?.setContent {
            Column {
                if (!isEmptyText) {
                    MessageSentView(
                        message = listItem.data.body?.trim() ?: "",
                        displayTime = textBubbleTime,
                        bubbleType = convertBubbleType(true, listItem),
                        onDeleteOption = { audioAttachmentInterface.onDeleteMessage(listItem) }
                    )
                }
                listItem.data.attachments?.forEachIndexed { attachmentIndex, attachment ->
                    AttachmentSentView(
                        id = "${attachment.id ?: 0}",
                        thumbnail = attachment.thumbnailLink ?: attachment.link ?: "",
                        filename = attachment.fileName ?: "",
                        contentType = attachment.contentType ?: "",
                        sessionId = audioAttachmentInterface.getSessionId(),
                        corpAcctNumber = audioAttachmentInterface.getCorpAcctNumber(),
                        displayTime = attachmentBubbleTime,
                        bubbleType = convertBubbleType(false, listItem),
                        audioPlayer = audioAttachmentInterface.getAttachmentAudioFilePlayer(),
                        audioDuration = (attachment.fileDuration ?: 0).toInt(),
                        onClicked = {
                            audioAttachmentInterface.onClicked(attachment.fileName, attachment.link)
                        },
                        onLongClicked = { drawable ->
                            audioAttachmentInterface.onLongClicked(drawable, attachment.fileName, attachment.link, listItem)
                        },
                        onAudioProgressDragged = { progress ->
                            audioAttachmentInterface.audioProgressDragged(progress)
                        },
                        onPlayClicked = {
                            audioAttachmentInterface.playAudioAttachment(
                                id = "${attachment.id ?: 0}",
                                filename = attachment.fileName ?: "",
                                url = attachment.link ?: ""
                            )
                        },
                        onSpeakerClicked = {
                            audioAttachmentInterface.toggleSpeaker(it)
                        }
                    )
                }
                if (listItem.data.sentStatus == Enums.Chats.SentStatus.FAILED) {
                    MessageFailedView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                end = dimensionResource(R.dimen.general_padding_medium),
                                bottom = dimensionResource(R.dimen.general_padding_small)
                            ),
                        onClicked = {
                            audioAttachmentInterface.onResendSmsMessage(listItem.data)
                        })
                }
            }
        }
    }

    fun bindViews(view: View) {
        val binding = ListItemConnectChatMessageSentBinding.bind(view)
        composeView = binding.composeView
    }
}