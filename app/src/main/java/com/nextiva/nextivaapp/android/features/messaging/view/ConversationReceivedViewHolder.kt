package com.nextiva.nextivaapp.android.features.messaging.view

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.ComposeView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.MessageBaseViewHolder
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentReceivedView
import com.nextiva.nextivaapp.android.databinding.ListItemConnectChatMessageReceivedBinding
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.AudioAttachmentInterface
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageReceivedView
import com.nextiva.nextivaapp.android.models.AvatarInfo

internal class ConversationReceivedViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener?) :
        MessageBaseViewHolder<SmsMessageListItem>(itemView, context, masterListListener) {

    private var participantNumbers: List<String>? = null

    private var composeView: ComposeView? = null
    private lateinit var audioAttachmentInterface: AudioAttachmentInterface

    constructor(
        parent: ViewGroup,
        context: Context,
        masterListListener: MasterListListener?,
        audioAttachmentInterface: AudioAttachmentInterface,
    ) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_connect_chat_message_received, parent, false),
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
        mListItem = listItem
        val time = listItem.humanReadableTime ?: ""
        val showAvatar = listItem.showHumanReadableTime
        val avatarInfo = if (showAvatar) avatarInfo() else null
        composeView?.setContent {
            Column {
                if (!listItem.data.body.isNullOrBlank()) {
                    MessageReceivedView(
                        message = listItem.data.body?.trim() ?: "",
                        avatarInfo = avatarInfo,
                        displayTime = time,
                        bubbleType = convertBubbleType(true, listItem),
                        onDeleteOption = { audioAttachmentInterface.onDeleteMessage(listItem) }
                    )
                }
                listItem.data.attachments?.forEachIndexed { attachmentIndex, attachment ->
                    AttachmentReceivedView(
                        id = "${attachment.id ?: 0}",
                        thumbnail = attachment.thumbnailLink ?: attachment.link ?: "",
                        filename = attachment.fileName ?: "",
                        contentType = attachment.contentType ?: "",
                        sessionId = audioAttachmentInterface.getSessionId(),
                        corpAccountNumber = audioAttachmentInterface.getCorpAcctNumber(),
                        avatarInfo = avatarInfo(),
                        displayName = if (listItem.data.body.isNullOrEmpty()) audioAttachmentInterface.senderUiName(mListItem) else null,
                        displayTime = time,
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
            }
        }
    }

    fun bindViews(view: View) {
        val binding = ListItemConnectChatMessageReceivedBinding.bind(view)
        composeView = binding.composeView
    }

    private fun avatarInfo(): AvatarInfo {
        val senderUiName = audioAttachmentInterface.senderUiName(mListItem)
        val avatarInfo = AvatarInfo.Builder()
            .setFontAwesomeIconResId(R.string.fa_user)
            .setPresence(mListItem.presence)
            .isConnect(true)
            .build()
        avatarInfo.photoData = mListItem.data.photoData

        when (participantNumbers?.size) {

            // add o case
            Enums.SMSMessages.ConversationTypes.SELF_MESSAGE_PARTICIPANT_COUNT -> {

                if (!TextUtils.isEmpty(mListItem.data.sender?.get(0)?.name)) {
                    avatarInfo.displayName = mListItem.data.sender?.get(0)?.name
                }

            }

            Enums.SMSMessages.ConversationTypes.MESSAGE_CONVERSATION_SINGLE_PARTICIPANT_COUNT -> {
                if (!TextUtils.isEmpty(senderUiName)) {
                    avatarInfo.displayName = senderUiName
                }

            }
            else -> {
                mListItem.data.sender?.let { sender ->
                    if (!TextUtils.isEmpty(sender.firstOrNull()?.name)) {
                        avatarInfo.displayName = sender.firstOrNull()?.name

                    } else if (!TextUtils.isEmpty(senderUiName)) {
                        avatarInfo.displayName = senderUiName
                    }
                }

            }
        }
        return avatarInfo
    }

}