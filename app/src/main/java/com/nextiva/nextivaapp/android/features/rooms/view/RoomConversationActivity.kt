package com.nextiva.nextivaapp.android.features.rooms.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.ConnectContactDetailsActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentReceivedView
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentSentView
import com.nextiva.nextivaapp.android.core.common.ui.MessageTextField
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.model.RoomMessageListItem
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageFailedView
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageReceivedView
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageSentView
import com.nextiva.nextivaapp.android.features.rooms.view.components.ScreenTitleBarView
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.ChatMessageListViewModel
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1Heavy
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetDeleteConfirmation
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream
import javax.inject.Inject

@AndroidEntryPoint
class RoomConversationActivity : BaseActivity() {
    companion object {
        private const val PARAMS_ROOM_ID = "PARAMS_ROOM_ID"
        private const val PARAMS_PARTICIPANTS = "PARAMS_PARTICIPANTS"
        private const val PARAMS_MEMBER_UUIDS = "PARAMS_MEMBER_UUIDS"
        private const val PARAMS_TITLE_COUNT = "PARAMS_TITLE_COUNT"

        fun newIntent(context: Context, roomId: String, title: String): Intent {
            val intent = Intent(context, RoomConversationActivity::class.java)
            intent.putExtra(PARAMS_ROOM_ID, roomId)
            intent.putExtra(PARAMS_PARTICIPANTS, title)
            return intent
        }

        fun newIntent(context: Context, roomId: String, title: String, titleCount: String, members: Array<String>?): Intent {
            val intent = newIntent(context, roomId, title)
            members?.let {
                intent.putExtra(PARAMS_MEMBER_UUIDS, members)
            }
            intent.putExtra(PARAMS_TITLE_COUNT, titleCount)
            return intent
        }
    }

    @Inject
    lateinit var mPermissionManager: PermissionManager

    @Inject
    lateinit var mCallManager: CallManager

    private lateinit var viewModel: ChatMessageListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatMessageListViewModel::class.java]

        intent.extras?.getString(PARAMS_ROOM_ID)?.let { roomId ->
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.loadRoom(roomId)
            }
        }
        intent.extras?.getStringArray(PARAMS_MEMBER_UUIDS)?.let { members ->
            viewModel.newRoomMemberList = members.toList()
        }

        val title = intent.extras?.getString(PARAMS_PARTICIPANTS) ?: ""
        val titleCount = intent.extras?.getString(PARAMS_TITLE_COUNT) ?: ""

        setContentView(ComposeView(this).apply {
            setContent {
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                val singleContact: NextivaContact? by viewModel.singleContact.observeAsState()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    Column {
                        ScreenTitleBarView(
                            title = title,
                            titleCount = titleCount,
                            onBackButton = { this@RoomConversationActivity.finish() },
                            onTitleClicked = {
                                viewModel.dbRoom.value?.let {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        handleTitleClicked(it)
                                    }
                                }
                            },
                            onIconClicked = if (singleContact != null) viewModel.phoneIconAction(this@RoomConversationActivity) else null)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(weight = 1.0f, fill = true)
                        ) {
                            Messages(
                                deleted = {
                                    viewModel.deleteMessageDatabase()
                                    scope.launch {
                                        val snackbarResult = snackbarHostState.showSnackbar(
                                            message = resources.getString(R.string.room_conversation_delete_toast),
                                            actionLabel = resources.getString(R.string.room_conversation_undo_toast)
                                        )
                                        when (snackbarResult) {
                                            SnackbarResult.Dismissed -> { viewModel.deleteMessageNetwork() }
                                            SnackbarResult.ActionPerformed -> {
                                                viewModel.undoDeleteMessage()
                                                snackbarHostState.showSnackbar(
                                                    message = resources.getString(R.string.room_conversation_message_delete_undone)
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        Spacer(Modifier.height(dimensionResource(R.dimen.general_padding_small)))

                        MessageTextField(
                            activity = this@RoomConversationActivity,
                            messageTextFieldInterface = viewModel
                        )
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .padding(
                                horizontal = dimensionResource(id = R.dimen.general_padding_medium),
                                vertical = dimensionResource(id = R.dimen.general_padding_xxxxxlarge)
                            )
                            .align(Alignment.BottomCenter)
                            .wrapContentSize(),
                        snackbar = { snackbarData: SnackbarData ->
                            Card(
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.message_bubble_rounded_corner)),
                                backgroundColor = colorResource(id = R.color.connectSecondaryDarkBlue),
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.general_padding_medium))
                                    .wrapContentSize()
                            ) {
                                Row(
                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.general_padding_medium))
                                ) {
                                    Text(modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.general_padding_small)),
                                        text = snackbarData.message,
                                        style = TypographyBody1,
                                        color = colorResource(id = R.color.connectWhite)
                                    )

                                    snackbarData.actionLabel?.let {
                                        Text(text = it,
                                            style = TypographyBody2,
                                            color = colorResource(id = R.color.connectSecondaryBrightBlue),
                                            modifier = Modifier
                                                .padding(start = dimensionResource(id = R.dimen.general_padding_large))
                                                .clickable {
                                                    snackbarData.performAction()
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        })
    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        if (TextUtils.equals(intent!!.action, Intent.ACTION_VIEW) &&
            !TextUtils.isEmpty(intent.dataString) &&
            intent.dataString!!.startsWith("tel:")
        ) {
            val participantInfo = ParticipantInfo(numberToCall = CallUtil.getStrippedPhoneNumber(intent.dataString!!))

            mCallManager.makeCall(
                this,
                Enums.Analytics.ScreenName.CONNECT_ROOMS_CHAT,
                participantInfo,
                compositeDisposable
            )
        } else {
            super.startActivity(intent, options)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseAudio()
    }

    private fun attachmentDeleteAction(item: RoomMessageListItem, index: Int, deleted: () -> Unit) : (() -> Unit)? {
        return if (viewModel.isSent(item)) ({
            viewModel.markForDeletion(index)
            BottomSheetDeleteConfirmation.newInstance(
                title = resources.getString(R.string.room_conversation_delete_message),
                subtitle = resources.getString(R.string.room_conversation_are_you_sure),
                deleteAction = { deleted() },
                cancelAction = { viewModel.unmarkForDeletion() }
            ).show(supportFragmentManager, null)
        }) else null
    }

    private fun handleTitleClicked(room: DbRoom) {
        if (room.isChat()) {
            val oneToOneContact = viewModel.singleContact.value
            if (oneToOneContact != null) {
                startActivity(ConnectContactDetailsActivity.newIntent(this, oneToOneContact))
            } else {
                BottomSheetChatDetailsFragment.newInstance(room.roomId).show(
                    supportFragmentManager,
                    null
                )
            }
        } else {
            BottomSheetRoomDetailsFragment.newInstance(room.roomId, true).show(
                supportFragmentManager,
                null
            )
        }
    }

    @Composable
    fun Messages(deleted: () -> Unit) {
        val allMessages by viewModel.messages.observeAsState(listOf())
        val messageIndexMarkedForDeletion by viewModel.messageIndexMarkedForDeletion.observeAsState()
        val scrollState = rememberLazyListState()
        val context = LocalContext.current

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            state = scrollState,
            reverseLayout = true
        ) {
            itemsIndexed(
                items = allMessages,
                key = { index, item -> "$index:${viewModel.presenceMap[item.message.senderId]?.value?.state ?: 0}" }
            ) { index, item ->
                if (!viewModel.hasSent(item)) {
                    MessageFailedView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                end = dimensionResource(R.dimen.general_padding_medium),
                                bottom = dimensionResource(R.dimen.general_padding_small)
                            ),
                        onClicked = { viewModel.retrySend(item) })
                }

                Attachments(
                    item = item,
                    index = index,
                    onClicked = { fullLink, fileName ->
                        context.startActivity(AttachmentDetailsActivity.newIntent(context, fileName, fullLink))
                    },
                    onLongClicked = { fullLink, fileName, contentType, drawable ->
                        val textToDisplayBottomSheet = context.getString(
                            if (viewModel.isTheFileAnImageOrVideo(fileName)) {
                                R.string.chat_details_download_image
                            } else {
                                R.string.chat_details_download_file
                            }
                        )
                        BottomSheetMessageMenuDialog(
                            editAction = null,
                            deleteAction = attachmentDeleteAction(item, index, deleted),
                            cancelAction = { },
                            downloadAction = {
                                mSessionManager.sessionId?.let {
                                    viewModel.downloadImageOrFile(it, contentType, drawable, fullLink) { attachment ->
                                        validateStoragePermissions(context, attachment, fileName)
                                    }
                                }
                            },
                            downloadTextString = textToDisplayBottomSheet
                        ).show(supportFragmentManager, null)
                    }
                )

                TextMessage(
                    item = item,
                    index = index,
                    deleteIndex = messageIndexMarkedForDeletion,
                    deleted = deleted)

                viewModel.separatorTimestamp(item, index)?.let {
                    Separator(title = it)
                }
            }
        }
    }

    @Composable
    private fun TextMessage(item: RoomMessageListItem, index: Int, deleteIndex: Int?, deleted: () -> Unit) {
        if (!TextUtils.isEmpty(item.message.text)) {
            if (viewModel.isSent(item)) {
                MessageSentView(
                    message = viewModel.formattedTextMessage(item),
                    displayTime = viewModel.sentDisplayTime(item, index),
                    bubbleType = viewModel.bubbleType(index),
                    markedForDeletion = index == deleteIndex,
                    onClicked = {
                        viewModel.markForDeletion(index)
                        BottomSheetMessageMenuDialog(
                            editAction = { viewModel.markForEdit(index) },
                            deleteAction = {
                                BottomSheetDeleteConfirmation.newInstance(
                                    title = resources.getString(R.string.room_conversation_delete_message),
                                    subtitle = resources.getString(R.string.room_conversation_are_you_sure),
                                    deleteAction = { deleted() },
                                    cancelAction = { viewModel.unmarkForDeletion() }
                                ).show(supportFragmentManager, null)
                            },
                            cancelAction = {
                                viewModel.unmarkForDeletion()
                            },
                            downloadAction = null
                        ).show(
                            supportFragmentManager,
                            null
                        )
                    }
                )
            } else {
                MessageReceivedView(
                    message = viewModel.formattedTextMessage(item),
                    avatarInfo = viewModel.avatarInfo(item, index),
                    displayTime = viewModel.displayTime(item),
                    bubbleType = viewModel.bubbleType(index)
                )
            }
        }
    }

    @Composable
    private fun Attachments(
        item: RoomMessageListItem,
        index: Int,
        onClicked: (String, String) -> Unit,
        onLongClicked: (String, String, String, Drawable?) -> Unit
    ) {
        val context = LocalContext.current
        if (viewModel.isSent(item)) {
            Column {
                item.message.attachments?.forEachIndexed { attachmentIndex, attachment ->
                    mSessionManager.sessionId?.let {
                        AttachmentSentView(
                            id = attachment.id,
                            thumbnail = attachment.thumbnailReference.link,
                            filename = attachment.filename,
                            contentType = attachment.contentType,
                            sessionId = it,
                            corpAcctNumber = mSessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                            displayTime = viewModel.attachmentSentDisplayTime(item, index, attachmentIndex),
                            bubbleType = viewModel.attachmentBubbleType(index, attachmentIndex),
                            audioPlayer = viewModel.attachmentAudioFilePlayer,
                            onClicked = {
                                if (viewModel.isAllowedImageType(attachment.filename)) {
                                    onClicked(attachment.reference.link, attachment.filename)
                                }
                            },
                            onLongClicked = { drawable ->
                                if (viewModel.isAllowedFileType(attachment.filename)) {
                                    viewModel.getImageFullSizeIfNeeded(context, drawable, attachment.contentType, attachment.reference.link) {
                                        onLongClicked(attachment.reference.link, attachment.filename, attachment.contentType, it)
                                    }
                                }
                            },
                            onAudioProgressDragged = { progress ->
                                viewModel.audioProgressDragged(progress)
                            },
                            onPlayClicked = {
                                viewModel.playAudioAttachment(item.message.id, attachment)
                            },
                            onSpeakerClicked = {
                                viewModel.toggleSpeaker(it)
                            }
                        )
                    }
                }
            }
        } else {
            Column {
                item.message.attachments?.forEachIndexed { attachmentIndex, attachment ->
                    mSessionManager.sessionId?.let {
                        AttachmentReceivedView(
                            id = attachment.id,
                            thumbnail = attachment.thumbnailReference.link,
                            filename = attachment.filename,
                            contentType = attachment.contentType,
                            sessionId = it,
                            corpAccountNumber = mSessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                            avatarInfo = viewModel.avatarInfo(item, index),
                            displayName = viewModel.attachmentAvatarName(item, index, attachmentIndex),
                            displayTime = viewModel.displayTime(item),
                            bubbleType = viewModel.attachmentBubbleType(index, attachmentIndex),
                            audioPlayer = viewModel.attachmentAudioFilePlayer,
                            onClicked = {
                                if (viewModel.isAllowedImageType(attachment.filename)) {
                                    onClicked(attachment.reference.link, attachment.filename)
                                }
                            },
                            onLongClicked = { drawable ->
                                if (viewModel.isAllowedFileType(attachment.filename)) {
                                    viewModel.getImageFullSizeIfNeeded(context, drawable, attachment.contentType, attachment.reference.link) {
                                        onLongClicked(attachment.reference.link, attachment.filename, attachment.contentType, it)
                                    }
                                }
                            },
                            onAudioProgressDragged = { progress ->
                                viewModel.audioProgressDragged(progress)
                            },
                            onPlayClicked = {
                                viewModel.playAudioAttachment(item.message.id, attachment)
                            },
                            onSpeakerClicked = {
                                viewModel.toggleSpeaker(it)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun Separator(title: String) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.general_padding_medium),
                    top = dimensionResource(R.dimen.general_padding_large),
                    end = dimensionResource(R.dimen.general_padding_medium),
                    bottom = dimensionResource(R.dimen.general_padding_small)
                )
        ) {
            Text(
                text = title,
                color = colorResource(R.color.connectGrey10),
                style = TypographyCaption1Heavy,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.general_padding_xsmall))
            )
            Divider(
                color = colorResource(R.color.connectSecondaryGrey),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.hairline_small))
            )
        }
    }

    private fun validateStoragePermissions(context: Context, file: ByteArray?, filename: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveDataToStorage(context, file, filename)
        } else {
            mPermissionManager.requestStorageToDownloadPermission(
                context as Activity,
                Enums.Analytics.ScreenName.APP_PREFERENCES,
                { saveDataToStorage(context, file, filename) },
                { showSimpleToast(context, R.string.permission_required_title) }
            )
        }
    }

    private fun saveDataToStorage(context: Context, file: ByteArray?, filename: String) {
        if (file == null) {
            showSimpleToast(context, R.string.chat_details_attachment_invalid)
            return
        }
        val fos: OutputStream? = viewModel.saveMediaToStorage(context, filename)
        fos?.use {
            it.write(file)
            (context as Activity).runOnUiThread {
                showCustomToastWhenFinished(context, filename)
            }
        }
    }

    private fun showSimpleToast(context: Context, message: Int) {
        Toast.makeText(context, context.getString(message), Toast.LENGTH_LONG).show()
    }

    @SuppressLint("InflateParams")
    private fun showCustomToastWhenFinished(context: Context, fileName: String) {
        val textToDisplayToast = context.getString(
            if (viewModel.isTheFileAnImageOrVideo(fileName)) {
                R.string.chat_details_image_saved
            } else {
                R.string.chat_details_file_saved
            }
        )
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_toast, null)
        val textView = view.findViewById<TextView>(R.id.custom_toast_message)
        textView.text = textToDisplayToast
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }
}
