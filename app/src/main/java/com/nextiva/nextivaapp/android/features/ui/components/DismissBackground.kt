package com.nextiva.nextivaapp.android.features.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DismissDirection
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.util.extensions.toPositive

@Composable
fun DismissBackground(
        offset: Int,
        isRead: Boolean,
        direction: DismissDirection?,
        onDelete: () -> Unit,
        onUpdateAsReadOrUnread: () -> Unit
) {
    val color = when (direction) {
        DismissDirection.StartToEnd -> colorResource(id = R.color.connectSecondaryLightBlue)
        DismissDirection.EndToStart -> colorResource(id = R.color.connectPrimaryRed)
        null -> Color.Transparent
    }
    val envelopeIcon = if (isRead) R.string.fa_envelope else R.string.fa_envelope_open

    Box(modifier = Modifier.fillMaxSize()) {
        if (direction == DismissDirection.StartToEnd) {
            SwipeableButton(
                    modifier = Modifier
                            .width(offset.dp)
                            .background(color)
                            .align(Alignment.CenterStart),
                    offset = offset,
                    icon = envelopeIcon,
                    color = R.color.connectPrimaryBlue,
                    direction = DismissDirection.StartToEnd,
                    onDelete = onUpdateAsReadOrUnread,
                    onUpdateAsReadOrUnread = onDelete
            )
        }

        if (direction == DismissDirection.EndToStart) {
            SwipeableButton(
                    modifier = Modifier
                            .width((offset.toPositive().dp))
                            .background(color)
                            .align(Alignment.CenterEnd),
                    offset = offset,
                    icon = R.string.fa_trash_alt,
                    color = R.color.connectWhite,
                    direction = DismissDirection.EndToStart,
                    onDelete = onUpdateAsReadOrUnread,
                    onUpdateAsReadOrUnread = onDelete
            )
        }
    }
}

@Composable
fun SwipeableButton(
        modifier: Modifier,
        offset: Int,
        icon: Int,
        color: Int,
        direction: DismissDirection,
        onDelete: () -> Unit,
        onUpdateAsReadOrUnread: () -> Unit
) {
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val thresholdToEnableShortSwipe = 220

    val horizontalPadding = if (screenWidth <= 1080) dimensionResource(id = R.dimen.general_padding_xmedium) else 0.dp

    Box(
        modifier = modifier.clickable {
            if (direction == DismissDirection.StartToEnd) {
                onDelete()
            } else {
                onUpdateAsReadOrUnread()
            }
        }.then(
                Modifier
                        .fillMaxHeight()
                        .padding(
                            vertical = dimensionResource(id = R.dimen.general_padding_xmedium),
                            horizontal = horizontalPadding
                        )
        ),
        contentAlignment = if (direction == DismissDirection.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        val padding = calculatePadding(direction, offset, thresholdToEnableShortSwipe)

        Text(
            modifier = padding,
            text = stringResource(icon),
            color = colorResource(color),
            fontFamily = FontAwesome,
            fontSize = dimensionResource(R.dimen.material_text_title).value.sp,
            fontWeight = FontWeight.W300,
            textAlign = TextAlign.Center
        )
    }
}

@SuppressLint("ModifierFactoryExtensionFunction")
@Composable
fun calculatePadding(direction: DismissDirection, offset: Int, threshold: Int): Modifier {
    // increase paddings dynamically to keep them in the center all the time
    return if (direction == DismissDirection.StartToEnd) {
        if (offset <= threshold) {
            Modifier.padding(start = (offset * 0.09).dp)
        } else {
            Modifier.padding(start = (offset * 0.2).dp)
        }
    } else {
        if (offset.toPositive() <= threshold) {
            Modifier.padding(end = (offset.toPositive() * 0.09).dp)
        } else {
            Modifier.padding(end = (offset.toPositive() * 0.2).dp)
        }
    }
}
