package com.nextiva.nextivaapp.android.features.rooms.view.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.LinkifyText
import com.nextiva.nextivaapp.android.features.ui.theme.Typography
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1Heavy
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.view.AvatarView
import org.threeten.bp.Instant
import java.util.Locale

@Composable
fun MessageReceivedView(
    message: String,
    avatarInfo: AvatarInfo?,
    displayTime: String,
    bubbleType: MessageBubbleType,
    onDeleteOption: (() -> Unit)? = null
) {

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    var expanded by remember { mutableStateOf(false) }
    val copiedToClipboard = stringResource(id = R.string.general_copied_to_clipboard)
    var markedToCopy by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.general_padding_small),
                top = dimensionResource(R.dimen.general_padding_xsmall)
            )
    ) {
        AndroidView(
            modifier = Modifier
                .height(dimensionResource(R.dimen.avatar_connect_size_sms))
                .width(dimensionResource(R.dimen.avatar_connect_size_sms))
                .alpha(if (avatarInfo == null) 0f else 1f),
            factory = { context ->
                AvatarView(context).apply {
                    avatarInfo?.let {
                        setAvatar(it, true)
                    }
                }
            },
            update = { avatarView ->
                avatarInfo?.let { avatarView.setAvatar(it, true) }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            avatarInfo?.displayName?.let { name ->
                Row(modifier = Modifier
                    .padding(bottom = dimensionResource(R.dimen.general_padding_xsmall))
                    .height(24.dp)) {
                    Text(
                        text = name,
                        color = colorResource(R.color.connectGrey09),
                        style = TypographyCaption1Heavy,
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.general_padding_small),
                            top = dimensionResource(R.dimen.general_padding_xxsmall)
                        ).align(Alignment.CenterVertically)
                    )
                    Text(
                        text = displayTime,
                        color = colorResource(R.color.connectGrey09),
                        style = TypographyCaption1,
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.general_padding_small),
                            top = dimensionResource(R.dimen.general_padding_xsmall)
                        ).align(Alignment.CenterVertically)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.general_padding_small),
                        end = dimensionResource(R.dimen.general_padding_xxlarge)
                    )
                    .clip(bubbleType.shape(MessageBubbleDirection.RECEIVED))
                    .background(
                        colorResource(
                            if (markedToCopy)
                                R.color.connectMarkToCopyWhite
                            else
                                R.color.connectGrey01
                        )
                    )
                    .pointerInput(true) {
                        detectTapGestures(
                            onLongPress = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                markedToCopy = true
                                pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                                expanded = true
                            }
                        )
                    }
            ) {
                LinkifyText(
                    text = message,
                    color = colorResource(R.color.connectSecondaryDarkBlue),
                    linkColor = colorResource(R.color.connectSecondaryDarkBlue),
                    style = Typography.body2,
                    modifier = Modifier.padding(dimensionResource(R.dimen.general_padding_xmedium)),
                )
                FloatingContextMenu(
                    pressOffset = pressOffset,
                    expanded = expanded,
                    menuData = listOf(
                        MenuData(
                            name = stringResource(id = R.string.general_copy),
                            icon = stringResource(id = R.string.fa_copy),
                            color = colorResource(id = R.color.connectSecondaryDarkBlue),
                            onClick = {
                                markedToCopy = false
                                expanded = false
                                val clipboardManager =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clipData: ClipData = ClipData.newPlainText("text", message)
                                clipboardManager.setPrimaryClip(clipData)
                                Toast.makeText(context, copiedToClipboard, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        ),
                        MenuData(
                            name = stringResource(id = R.string.general_delete),
                            icon = stringResource(id = R.string.fa_trash_alt),
                            color = colorResource(id = R.color.connectSecondaryRed),
                            onClick = {
                                expanded = false
                                markedToCopy = false
                                onDeleteOption?.invoke()
                            }
                        )
                    ),
                    onDismissRequest = {
                        markedToCopy = false
                        expanded = it
                    })
            }
        }
    }
}

class MessageReceivedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {

    var text by mutableStateOf<String>("")
    var avatarInfo by mutableStateOf<AvatarInfo?>(null)
    var date by mutableStateOf<Instant>(Instant.now())
    var bubbleType by mutableStateOf(MessageBubbleType.NONE)

    @Composable
    override fun Content() {
        val displayTime = FormatterManager.getInstance().format_humanReadableSmsTime(
            LocalContext.current,
            date
        ).lowercase(Locale.getDefault())

        MessageReceivedView(text, avatarInfo, displayTime, bubbleType)
    }
}

@Preview(
    showSystemUi = true
)
@Composable
fun MessageReceivedViewDefaultPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        MessageReceivedView(
            "Lorem ipsum A.",
            AvatarInfo.Builder().setDisplayName("Peter Seymour").build(),
            "1:40 am",
            MessageBubbleType.NONE
        )
        MessageReceivedView(
            "Lorem ipsum dolor sit amet, consectetr elit adipiscing.",
            AvatarInfo.Builder().setDisplayName("Group Chat").build(),
            "1:40 am",
            MessageBubbleType.TOP
        )
        MessageReceivedView(
            "Lorem ipsum dolor.",
            null,
            "1:40 am",
            MessageBubbleType.MIDDLE
        )
        MessageReceivedView(
            "Lorem ipsum dolor sit amet, consectetr adipiscing elit.",
            null,
            "1:40 am",
            MessageBubbleType.BOTTOM
        )
    }
}