package com.nextiva.nextivaapp.android.core.common.ui

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.common.FileUtil
import com.nextiva.nextivaapp.android.features.rooms.view.components.CustomizedGlideImage
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageBubbleDirection
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageBubbleType
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption2
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.view.AvatarView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AttachmentReceivedView(
    id: String,
    thumbnail: String,
    filename: String,
    contentType: String,
    sessionId: String,
    corpAccountNumber: String,
    avatarInfo: AvatarInfo?,
    displayName: String?,
    displayTime: String,
    bubbleType: MessageBubbleType,
    audioPlayer: AttachmentAudioFilePlayer,
    audioDuration: Int = 0,
    onClicked: (() -> Unit)? = null,
    onLongClicked: ((Drawable?) -> Unit)? = null,
    onAudioProgressDragged: ((Int) -> Unit)? = null,
    onPlayClicked: (() -> Unit)? = null,
    onSpeakerClicked: ((String) -> Unit)? = null
) {

    val MIN_HEIGHT = 50
    val MAX_HEIGHT = 250
    var drawable: Drawable? = null
    val maxHeight: MutableState<Int> = remember { mutableStateOf(MIN_HEIGHT) }
    val isImage = FileUtil.hasExtension(filename, FileUtil.ALLOWED_FILE_IMAGE_TYPES)
    val hasThumbnail = FileUtil.hasExtension(filename, FileUtil.THUMBNAIL_FILE_TYPES) && isImage
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.general_padding_small),
                top = dimensionResource(if (displayName == null) R.dimen.general_padding_xsmall else R.dimen.general_padding_medium)
            )
    ) {
        AndroidView(
            modifier = Modifier
                .height(dimensionResource(R.dimen.avatar_connect_size_sms))
                .width(dimensionResource(R.dimen.avatar_connect_size_sms))
                .alpha(if (displayName == null) 0f else 1f),
            factory = { context ->
                AvatarView(context).apply {
                    avatarInfo?.let {
                        setAvatar(it, true)
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            displayName?.let { name ->
                Row(modifier = Modifier.padding(bottom = dimensionResource(R.dimen.general_padding_small))) {
                    Text(
                        text = name,
                        color = colorResource(R.color.connectGrey09),
                        style = TypographyCaption1Heavy,
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.general_padding_small),
                            top = dimensionResource(R.dimen.general_padding_xxsmall)
                        )
                    )
                    Text(
                        text = displayTime,
                        color = colorResource(R.color.connectGrey09),
                        style = TypographyCaption2,
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.general_padding_small),
                            top = dimensionResource(R.dimen.general_padding_xsmall)
                        )
                    )
                }
            }

            if (hasThumbnail) {
                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(R.dimen.general_padding_small),
                            end = dimensionResource(R.dimen.general_padding_xxlarge)
                        )
                        .heightIn(MIN_HEIGHT.dp, maxHeight.value.dp)
                        .clip(bubbleType.shape(MessageBubbleDirection.RECEIVED))
                        .combinedClickable(
                            onClick = { onClicked?.invoke() },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongClicked?.invoke(drawable)
                            }
                        )
                ) {
                    CustomizedGlideImage(
                        modifier = Modifier
                            .align(Alignment.TopStart),
                        contentScale = ContentScale.FillWidth,
                        placeHolderDrawable = AppCompatResources.getDrawable(
                            context,
                            R.drawable.placeholder_padded
                        ),
                        sessionId = sessionId,
                        corpAcctNumber = corpAccountNumber,
                        data = thumbnail,
                        contentType = contentType
                    ) {
                        maxHeight.value = MAX_HEIGHT
                        drawable = it
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(R.dimen.general_padding_small),
                            end = dimensionResource(R.dimen.general_padding_xxlarge)
                        )
                        .shadow(
                            elevation = dimensionResource(R.dimen.general_elevation),
                            shape = bubbleType.shape(MessageBubbleDirection.RECEIVED),
                            clip = true,
                            ambientColor = colorResource(id = R.color.connectSecondaryDarkBlue_50),
                            spotColor = colorResource(id = R.color.connectSecondaryDarkBlue_50)
                        )
                        .combinedClickable(
                            onClick = { onClicked?.invoke() },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongClicked?.invoke(drawable)
                            }
                        )
                ) {
                    AttachmentFileView(
                        id = id,
                        faIconId = FileUtil.faIconId(filename),
                        faFontFamily = FileUtil.faIconFontFamily(filename),
                        filename = filename,
                        borderShape = bubbleType.shape(MessageBubbleDirection.RECEIVED),
                        audioPlayer = audioPlayer,
                        audioLength = audioPlayer.formattedDuration(audioDuration),
                        progressDragged = { progress -> onAudioProgressDragged?.invoke(progress) },
                        playButtonClicked = { onPlayClicked?.invoke() },
                        speakerButtonClicked = { onSpeakerClicked?.invoke(id) })
                }
            }
        }
    }
}


@Preview(
    showSystemUi = true
)
@Composable
fun AttachmentReceivedViewDefaultPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        AttachmentReceivedView(
            id = "",
            thumbnail = "https://nextiva.nextos.com/public/attachment/v3/attachments/thumbnail/63697a25c6856b05fd05d273",
            filename = "testfile.jpeg",
            contentType = "image/jpeg",
            avatarInfo = AvatarInfo.Builder().setDisplayName("Group Chat").build(),
            displayName = "Peter Seymour",
            displayTime = "1:40 am",
            bubbleType = MessageBubbleType.NONE,
            audioPlayer = AttachmentAudioFilePlayer(),
            sessionId = "sessionId",
            corpAccountNumber = "000000"
        )
    }
}
