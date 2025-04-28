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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.LinkifyText
import com.nextiva.nextivaapp.android.features.ui.theme.Typography
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.util.extensions.isNull
import org.threeten.bp.Instant
import java.util.Locale

@Composable
fun MessageSentView(message: String,
                    displayTime: String?,
                    bubbleType: MessageBubbleType,
                    markedForDeletion: Boolean = false,
                    onClicked: (() -> Unit)? = null,
                    onDeleteOption: (() -> Unit)? = null
) {

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    var expanded by remember { mutableStateOf(false) }
    val copiedToClipboard = stringResource(id = R.string.general_copied_to_clipboard)
    var markedToCopy by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        displayTime?.let { time ->
            Text(
                text = time,
                color = colorResource(R.color.connectGrey09),
                style = TypographyCaption1,
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.general_padding_small),
                    end = dimensionResource(R.dimen.general_padding_medium),
                    top = dimensionResource(R.dimen.general_padding_small)
                ).height(24.dp)
            )
        }

        Box(
            modifier = Modifier
                .padding(
                    dimensionResource(R.dimen.general_padding_xxxxxxxlarge),
                    if (displayTime == null) 0.dp else dimensionResource(R.dimen.general_padding_xsmall),
                    dimensionResource(R.dimen.general_padding_medium),
                    dimensionResource(R.dimen.general_padding_xsmall)
                )
                .clip(bubbleType.shape(MessageBubbleDirection.SENT))
                .background(
                    colorResource(
                            if (markedForDeletion)
                                R.color.connectPrimaryRed
                            else if (markedToCopy)
                                R.color.connectMarkToCopyBlue
                            else
                                R.color.connectPrimaryBlue
                    )
                )
                .pointerInput(true) {
                    detectTapGestures(
                        onLongPress = {
                            markedToCopy = true
                            if (onClicked.isNull()) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                                expanded = true
                            } else {
                                onClicked?.invoke()
                            }
                        }
                    )
                }
        ) {
            LinkifyText(
                text = message,
                color = colorResource(R.color.connectWhite),
                linkColor = colorResource(R.color.connectWhite),
                style = Typography.body2,
                modifier = Modifier.padding(dimensionResource(R.dimen.general_padding_xmedium))
            )
            FloatingContextMenu(
                pressOffset = pressOffset,
                expanded = expanded,
                menuData = listOf(
                    /**** Copy Menu *****/
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
                            Toast.makeText(context, copiedToClipboard, Toast.LENGTH_SHORT).show()
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

class MessageSentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {

    var text by mutableStateOf<String>("")
    var date by mutableStateOf<Instant?>(null)
    var bubbleType by mutableStateOf(MessageBubbleType.NONE)

    @Composable
    override fun Content() {
        var displayTime: String? = null
        date?.let { date ->
            displayTime = FormatterManager.getInstance().format_humanReadableSmsTime(
                LocalContext.current,
                date
            ).lowercase(Locale.getDefault())
        }
        MessageSentView(text, displayTime, bubbleType)
    }
}

@Preview(
    showSystemUi = true
)
@Composable
fun MessageSentViewDefaultPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        MessageSentView(
            "Lorem ipsum.",
            "1:40 am",
            MessageBubbleType.NONE
        )
        MessageSentView(
            "Lorem ipsum dolor sit amet, consectetr adipiscing elit.",
            "1:40 am",
            MessageBubbleType.TOP
        )
        MessageSentView(
            "Lorem ipsum dolor.",
            "1:40 am",
            MessageBubbleType.MIDDLE
        )
        MessageSentView(
            "Lorem ipsum dolor sit amet, consectetr adipiscing elit.",
            "1:40 am",
            MessageBubbleType.BOTTOM
        )
    }
}