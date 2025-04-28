package com.nextiva.nextivaapp.android.core.common.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.ConversationViewModel
import com.nextiva.nextivaapp.android.features.ui.dpToSp
import com.nextiva.nextivaapp.android.features.ui.drawVerticalScrollbar
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.FontLato
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption2
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import com.nextiva.nextivaapp.android.util.extensions.orZero
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageTextField(activity: AppCompatActivity, messageTextFieldInterface: MessageTextFieldInterface) {
    var text by remember { mutableStateOf(TextFieldValue(text = "", selection = TextRange.Zero)) }
    val sending: Boolean by messageTextFieldInterface.isSending.observeAsState(false)
    val editText: String? by messageTextFieldInterface.editMessage.observeAsState()
    val setDraft: String? by messageTextFieldInterface.setDraftMessage.observeAsState()
    val errorMessages: List<String>? by messageTextFieldInterface.errorMessages.observeAsState()
    val selectedAttachments: List<AttachmentInfo>? by messageTextFieldInterface.selectedAttachments.observeAsState()
    val sendingPhoneNumbers: ArrayList<ConversationViewModel.SendingPhoneNumber>? by messageTextFieldInterface.sendingPhoneNumbers.observeAsState()
    val conversationDetails: SmsConversationDetails? by messageTextFieldInterface.conversationDetails.observeAsState()
    val sendingViaBanner: SendingViaBanner? by messageTextFieldInterface.sendingViaBanner.observeAsState()

    val stateVertical = rememberScrollState()
    val context = LocalContext.current

    val minHeight = dimensionResource(R.dimen.general_view_xxlarge).value
    LaunchedEffect(text) {
        if (stateVertical.value != 0 || stateVertical.maxValue < minHeight) {
            stateVertical.scrollTo(stateVertical.maxValue)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uriList ->
        messageTextFieldInterface.addAttachments(uriList)
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        it?.let { bitmap ->
            storeBitmap(context, bitmap)?.let { uri ->
                messageTextFieldInterface.addAttachments(listOf(uri))
            }
        }
    }

    fun takePictureAction() : (() -> Unit)? {
        if (messageTextFieldInterface.menuItems().contains(AttachmentMenuItems.TAKE_PICTURE)) {
            return { cameraLauncher.launch(null) }
        }
        return null
    }
    fun choosePhotoAction() : (() -> Unit)? {
        if (messageTextFieldInterface.menuItems().contains(AttachmentMenuItems.CHOOSE_PHOTO)) {
            return { galleryLauncher.launch(arrayOf("image/*")) }
        }
        if (messageTextFieldInterface.menuItems().contains(AttachmentMenuItems.CHOOSE_PHOTO_OR_VIDEO)) {
            return { galleryLauncher.launch(arrayOf("image/*", "video/*")) }
        }
        return null
    }
    fun attachFileAction(): (() -> Unit)? {
        if (messageTextFieldInterface.menuItems().contains(AttachmentMenuItems.ATTACH_FILE)) {
            return { galleryLauncher.launch(arrayOf("*/*")) }
        }

        return null
    }

    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(colorResource(R.color.connectWhite))
    ) {

        sendingViaBanner?.text?.nullIfEmpty()?.let {
            Row(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(colorResource(R.color.connectGrey03))
                .padding(
                    horizontal = dimensionResource(R.dimen.general_padding_medium),
                    vertical = dimensionResource(R.dimen.general_padding_small)
                )) {

                Text(modifier = Modifier.padding(dimensionResource(R.dimen.general_padding_xsmall))
                    .weight(1f)
                    .width(0.dp),
                    text = it,
                    color = colorResource(R.color.connectSecondaryDarkBlue),
                    style = TypographyCaption1)

                if (sendingViaBanner?.showClose != false) {
                    Text(
                        text = stringResource(R.string.fa_times),
                        color = colorResource(R.color.connectGrey09),
                        fontFamily = FontAwesome,
                        fontSize = dimensionResource(R.dimen.material_text_title).value.sp,
                        fontWeight = FontWeight.Light,

                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable(onClick = {
                                messageTextFieldInterface.onSendingViaBannerClosed()
                            })
                    )
                }
            }
        }

        EditTextIndicator(
            messageTextFieldInterface = messageTextFieldInterface,
            editText = editText,
            onClose = { messageTextFieldInterface.onEditMessageCancel() })

        Divider(
            color = colorResource(R.color.connectGrey03),
            modifier = Modifier.fillMaxWidth(),
            thickness = dimensionResource(R.dimen.hairline_medium),
        )

        Row(modifier = Modifier
            .padding(
                top = if (selectedAttachments?.isNotEmpty() == true) dimensionResource(R.dimen.general_padding_medium) else 0.dp
            )
            .horizontalScroll(rememberScrollState())) {
            selectedAttachments?.forEachIndexed { _, attachmentInfo ->
                if (attachmentInfo.isImageError) {
                    AttachmentOverlayError(onDelete = {
                        messageTextFieldInterface.removeAttachment(attachmentInfo)
                    })
                } else if (attachmentInfo.displayAsIcon) {
                    AttachmentThumbnailIcon(faIconId = attachmentInfo.faIcon, onDelete = {
                        messageTextFieldInterface.removeAttachment(attachmentInfo)
                    })
                } else {
                    AttachmentThumbnail(attachmentInfo = attachmentInfo, onDelete = {
                        messageTextFieldInterface.removeAttachment(attachmentInfo)
                    })
                }
            }
        }

        errorMessages?.forEach { errorMessage ->
            Text(
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.general_padding_xxsmall),
                    start = dimensionResource(R.dimen.general_padding_small)),
                text = errorMessage,
                color = colorResource(R.color.connectSecondaryRed),
                style = TypographyCaption1)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {

            LaunchedEffect(editText, setDraft) {
                if (TextUtils.isEmpty(editText)) {
                    text = TextFieldValue(text = "", selection = TextRange.Zero)
                } else {
                    editText?.let {
                        text = TextFieldValue(text = it, selection = TextRange(it.length))
                        focusRequester.requestFocus()
                    }
                }
                if (TextUtils.isEmpty(setDraft)) {
                    text = TextFieldValue(text = "", selection = TextRange.Zero)
                } else {
                    setDraft?.let {
                        text = TextFieldValue(text = it, selection = TextRange(it.length))
                        focusRequester.requestFocus()
                    }
                }
            }

            sendingPhoneNumbers?.removeIf { !it.enabled }
            val hasMultipleSendingNumbers = !sendingPhoneNumbers.isNullOrEmpty() && (sendingPhoneNumbers?.size ?: 0) > 1
            val isOurTeamSelected = conversationDetails?.isOurTeamSelected() == true
            val totalTeamsInConversation = conversationDetails?.getTotalTeamsInConversation().orZero()
            val totalParticipantsInConversation = conversationDetails?.getParticipantsList()?.size.orZero()
            val hasOneParticipantWithMultipleNumbers = conversationDetails?.getParticipantsList()?.size == 1 &&
                    conversationDetails?.getParticipantsList()?.firstOrNull()?.representingTeam == null &&
                    (conversationDetails?.getParticipantsList()?.firstOrNull()?.contact?.allPhoneNumbersSorted?.filter { CallUtil.isValidSMSNumber(it.strippedNumber) }?.size ?: 0) > 1

            fun allowSend(): Boolean {
                val isSelectedNumberEnabled = sendingPhoneNumbers?.firstOrNull { it.isSelected }?.enabled != false
                val hasErrors = errorMessages?.isNotEmpty() == true
                val hasAttachments = selectedAttachments?.isNotEmpty() ?: false
                val hasText = text.text.trim().isNotEmpty()
                return (hasText || hasAttachments) && !hasErrors && (isSelectedNumberEnabled || isOurTeamSelected)
            }

            // Rules from N1 to show/hide headset icon
            // if sending to myTeam + other teams -> show
            // if sending to myTeam (and no other team or participant) -> show
            // if sending to myTeam + other participants (saved or unsaved) -> show
            // any other condition -> hide

            var showHeadset = false

            if (hasMultipleSendingNumbers || hasOneParticipantWithMultipleNumbers) {
                when {
                    totalTeamsInConversation == 1 && totalParticipantsInConversation == 0 -> {
                        showHeadset = false
                    }
                    totalTeamsInConversation > 1 || totalParticipantsInConversation > 0 -> {
                        showHeadset = true
                    }
                }
            }

            if (showHeadset) {
                Box(
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(R.dimen.general_padding_xmedium),
                            bottom = dimensionResource(R.dimen.general_padding_xmedium)
                        )
                        .size(dimensionResource(R.dimen.general_view_large))
                        .clip(CircleShape)
                        .background(colorResource(R.color.transparent))
                        .clickable { messageTextFieldInterface.onSelectPhoneNumberClicked() },
                    contentAlignment = Alignment.Center) {

                    Image(
                        painter = painterResource(id = R.drawable.phone_select),
                        colorFilter = ColorFilter.tint(colorResource(id = R.color.connectGrey09)),
                        contentDescription = stringResource(id = R.string.chat_conversation_select_sending_phone_view_content_description)
                    )
                }
            }

            TextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .drawVerticalScrollbar(stateVertical)
                    .verticalScroll(stateVertical)
                    .weight(1f),
                textStyle = TextStyle(
                    color = colorResource(R.color.connectSecondaryDarkBlue),
                    fontSize = dimensionResource(R.dimen.material_text_subhead).value.sp,
                    fontFamily = FontLato,
                ),
                value = text,
                placeholder = {
                    Text(text = stringResource(if (sending) R.string.progress_sending else R.string.chat_conversation_message_hint)) },
                onValueChange = { newText ->
                    text = newText
                    messageTextFieldInterface.onValueChanged(text.text)
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = colorResource(R.color.connectSecondaryDarkBlue),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    placeholderColor = colorResource(R.color.connectGrey08),
                    textColor = colorResource(R.color.connectSecondaryDarkBlue)
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Box(
                modifier = Modifier
                    .padding(
                        end = dimensionResource(R.dimen.general_padding_xmedium),
                        bottom = dimensionResource(R.dimen.general_padding_xmedium)
                    )
                    .size(dimensionResource(R.dimen.general_view_large))
                    .clip(CircleShape)
                    .background(colorResource(R.color.transparent))
                    .clickable {
                        messageTextFieldInterface.getPermission(activity,
                            listOf(Manifest.permission.CAMERA),
                            granted = {
                                BottomSheetAttachmentMenuDialog(
                                    takePictureAction = takePictureAction(),
                                    choosePhotoAction = choosePhotoAction(),
                                    attachFileAction = attachFileAction(),
                                    choosePhotoString = messageTextFieldInterface.choosePhotoMenuItem(
                                        context
                                    )
                                ).show(
                                    activity.supportFragmentManager,
                                    null
                                )
                            },
                            denied = {
                                Toast
                                    .makeText(
                                        context,
                                        context.getString(R.string.permission_required_title),
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            })
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.fa_paperclip),
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.connectGrey09),
                    fontFamily = FontAwesome,
                )
            }

            Box(
                modifier = Modifier
                    .padding(
                        end = dimensionResource(R.dimen.general_padding_medium),
                        bottom = dimensionResource(R.dimen.general_padding_xmedium)
                    )
                    .size(dimensionResource(R.dimen.general_view_large))
                    .clip(CircleShape)
                    .background(
                        colorResource(
                            if (allowSend()) R.color.connectPrimaryBlue else R.color.connectGrey03
                        )
                    )
                    .clickable(
                        onClick = {
                            if (text.text.isNotBlank() || selectedAttachments?.isNotEmpty() == true) {
                                if (allowSend()) {
                                    messageTextFieldInterface.onSend(text.text)
                                    text = TextFieldValue(text = "", selection = TextRange.Zero)
                                } else {
                                    messageTextFieldInterface.onAlertError()
                                }
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.fa_paper_plane),
                    textAlign = TextAlign.Center,
                    color = colorResource(if (allowSend()) R.color.connectWhite else R.color.connectGrey09),
                    fontFamily = FontAwesome,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = dimensionResource(R.dimen.general_padding_xxsmall))
                )
            }
        }

        Divider(
            color = colorResource(R.color.connectGrey03),
            modifier = Modifier.fillMaxWidth(),
            thickness = dimensionResource(R.dimen.hairline_small),
        )
    }
}

@Composable
fun EditTextIndicator(messageTextFieldInterface: MessageTextFieldInterface, editText: String?, onClose: () -> Unit) {
    editText?.let {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.connectSecondaryGrey))
        ) {
            Text(
                text = messageTextFieldInterface.editMessageFormatted(
                    title = stringResource(R.string.room_conversation_edit_message),
                    text = editText
                ),
                color = colorResource(R.color.connectGrey10),
                style = TypographyCaption1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.general_padding_medium),
                        end = dimensionResource(R.dimen.general_padding_small),
                        top = dimensionResource(R.dimen.general_padding_small),
                        bottom = dimensionResource(R.dimen.general_padding_small)
                    )
                    .weight(1f)
            )
            Text(
                text = stringResource(R.string.fa_times),
                color = colorResource(R.color.connectGrey09),
                fontFamily = FontAwesome,
                fontSize = dimensionResource(R.dimen.material_text_title).value.sp,
                fontWeight = FontWeight.Light,

                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.general_padding_small),
                        end = dimensionResource(R.dimen.general_padding_medium)
                    )
                    .align(Alignment.CenterVertically)
                    .clickable(onClick = { onClose() })
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AttachmentThumbnail(attachmentInfo: AttachmentInfo, onDelete: () -> Unit) {
    Box(modifier = Modifier.padding(start = dimensionResource(R.dimen.general_horizontal_margin_small))) {
        GlideImage(
            modifier = Modifier
                .size(dimensionResource(R.dimen.general_view_xxxxlarge))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.connect_message_bubble_square_corner)))
                .border(
                    width = dimensionResource(R.dimen.hairline_small),
                    color = colorResource(R.color.connectPrimaryGrey),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.connect_message_bubble_square_corner))
                ),
            contentScale = ContentScale.Crop,
            model = attachmentInfo.uri,
            contentDescription = ""
        )

        AttachmentThumbnailCloseButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onDelete = onDelete
        )

        attachmentInfo.videoLength?.let { videoLength ->
            Row(modifier = Modifier
                .size(
                    dimensionResource(R.dimen.general_view_xxxxlarge),
                    dimensionResource(R.dimen.general_view_medium)
                )
                .clip(
                    RoundedCornerShape(
                        bottomStart = dimensionResource(R.dimen.connect_message_bubble_square_corner),
                        bottomEnd = dimensionResource(R.dimen.connect_message_bubble_square_corner)
                    )
                )
                .align(Alignment.BottomCenter)
                .background(colorResource(R.color.connectSecondaryDarkBlue_50))
            ) {
                Text(
                    text = stringResource(R.string.fa_video),
                    color = colorResource(R.color.connectWhite),
                    fontFamily = FontAwesome,
                    fontSize = dimensionResource(R.dimen.material_text_body1).value.sp,
                    fontWeight = FontWeight.W900,

                    modifier = Modifier
                        .padding(start = dimensionResource(R.dimen.general_padding_small))
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1.0f))
                Text(
                    text = videoLength,
                    color = colorResource(R.color.connectWhite),
                    style = TypographyCaption2,

                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.general_padding_small))
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun AttachmentThumbnailCloseButton(modifier: Modifier, onDelete: () -> Unit) {
    Box(
        modifier = modifier
            .size(dimensionResource(R.dimen.general_view_xmedium))
            .padding(
                top = dimensionResource(R.dimen.general_padding_xsmall),
                end = dimensionResource(R.dimen.general_padding_xsmall)
            )
            .clickable(onClick = { onDelete() })
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.general_view_small))
                .padding(dimensionResource(R.dimen.hairline_small))
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(colorResource(R.color.connectWhite))
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(R.string.fa_times_circle),
            color = colorResource(R.color.connectGrey09),
            fontFamily = FontAwesome,
            fontSize = dpToSp(dimensionResource(R.dimen.general_view_small).value.dp),
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AttachmentOverlayError(onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = dimensionResource(R.dimen.general_horizontal_margin_small))
            .clickable(onClick = { onDelete() })) {

        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.general_view_xxxxlarge))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.connect_message_bubble_square_corner)))
                .background(colorResource(R.color.dangerSecondaryLight))
                .border(
                    width = dimensionResource(R.dimen.hairline_small),
                    color = colorResource(R.color.connectSecondaryRed),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.connect_message_bubble_square_corner))
                )
        )

        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.general_view_large))
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.general_view_large))
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(colorResource(R.color.connectSecondaryRed))
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.fa_exclamation),
                color = colorResource(R.color.dangerSecondaryLight),
                fontFamily = FontAwesome,
                fontSize = dimensionResource(R.dimen.material_text_subhead).value.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        AttachmentThumbnailCloseButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onDelete = onDelete
        )
    }
}

@Composable
fun AttachmentThumbnailIcon(faIconId: Int, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = dimensionResource(R.dimen.general_horizontal_margin_small))
            .clickable(onClick = { onDelete() })) {

        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.general_view_xxxxlarge))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.connect_message_bubble_square_corner)))
                .background(colorResource(R.color.connectWhite))
                .border(
                    width = dimensionResource(R.dimen.hairline_small),
                    color = colorResource(R.color.connectPrimaryGrey),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.connect_message_bubble_square_corner))
                )
        )

        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.general_view_xlarge))
                .align(Alignment.Center)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(faIconId),
                color = colorResource(R.color.connectGrey09),
                fontFamily = FontAwesome,
                fontSize = dimensionResource(R.dimen.material_text_display1).value.sp,
                fontWeight = FontWeight.W300,
                textAlign = TextAlign.Center
            )
        }

        AttachmentThumbnailCloseButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onDelete = onDelete
        )
    }
}

@Preview
@Composable
fun MessageTextFieldPreview() {
    MessageTextField(
        activity = AppCompatActivity(),
        messageTextFieldInterface = object : MessageTextFieldInterface {

            override val isSending: MutableLiveData<Boolean> = MutableLiveData(false)
            override val editMessage: MutableLiveData<String?> = MutableLiveData(null)
            override val setDraftMessage: MutableLiveData<String?> = MutableLiveData(null)
            override val errorMessages: MutableLiveData<List<String>?> = MutableLiveData(null)
            override val selectedAttachments: MutableLiveData<List<AttachmentInfo>?> = MutableLiveData(null)
            override val sendingViaBanner: MutableLiveData<SendingViaBanner?> = MutableLiveData(null)
            override val sendingPhoneNumbers: MutableLiveData<java.util.ArrayList<ConversationViewModel.SendingPhoneNumber>?> =
                MutableLiveData(
                    arrayListOf(
                        ConversationViewModel.SendingPhoneNumber(
                            phoneNumber = "123467890",
                            team = null,
                            enabled = true,
                            isSelected = false
                        ),
                        ConversationViewModel.SendingPhoneNumber(
                            phoneNumber = "1432567890",
                            team = null,
                            enabled = true,
                            isSelected = false
                        )
                    )
                )
            override val conversationDetails: MutableLiveData<SmsConversationDetails?> = MutableLiveData(null)

            override fun onEditMessageCancel() {}

            override fun onSend(text: String?) {}

            override fun onSendingViaBannerClosed() {}

            override fun onValueChanged(text: String) {}

            override fun addAttachments(attachments: List<Uri>) {}

            override fun removeAttachment(info: AttachmentInfo) {}

            override fun isImageError(uri: Uri, byteSize: Long) = false

            override fun hasThumbnail(uri: Uri) = false

            override fun menuItems(): List<AttachmentMenuItems> = emptyList()

            override fun isExcludedType(uri: Uri) = false

            override fun onAlertError() { }
        })
}

private fun storeBitmap(context: Context, bitmap: Bitmap): Uri? {
    try {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(Date())
        val fileName = "camera-$timeStamp.png"
        val fileOutputStream: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
        return Uri.fromFile(context.getFileStreamPath(fileName))
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
    }
    return null
}
