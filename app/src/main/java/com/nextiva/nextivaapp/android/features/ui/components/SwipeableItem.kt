package com.nextiva.nextivaapp.android.features.ui.components

import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.nextiva.nextivaapp.android.util.extensions.toPositive
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableItem(
        enableSwiping: Boolean,
        isRead: Boolean,
        content: @Composable () -> Unit,
        onCompleteSwipe: (DismissDirection) -> Unit,
        onShortSwipe: (DismissDirection) -> Unit,
        forceReset: Boolean? = false
) {
    // enable swiping mode only if user is not in Edit Mode
    if (enableSwiping) {
        val minAndMaxOffsetToCallShortSwipe = IntRange(100, 240)
        val delayToAllowValidateShortSwipe = 140L
        val thresholdToEnableShortSwipe = 200
        val density = LocalDensity.current
        val dismissThreshold = 0.5f // until slide reaches this threshold the action will be executed
        val currentFraction = remember { mutableFloatStateOf(0f) }
        val dismissState = rememberDismissState(
                confirmStateChange = {
                    if (it == DismissValue.DismissedToStart) {
                        if (currentFraction.floatValue >= dismissThreshold && currentFraction.floatValue < 1.0f) {
                            onCompleteSwipe(DismissDirection.EndToStart)
                        }
                    }
                    false
                }
        )

        val offset = dismissState.offset.value.roundToInt()
        val dismissDirection = dismissState.dismissDirection

        SwipeToDismiss(
                state = dismissState,
                background = {
                    currentFraction.floatValue = dismissState.progress.fraction
                    DismissBackground(
                            offset = offset,
                            isRead = isRead,
                            direction = dismissDirection,
                            onDelete = { onCompleteSwipe(DismissDirection.EndToStart) } ,
                            onUpdateAsReadOrUnread = { onCompleteSwipe(DismissDirection.StartToEnd) }
                    )
                },
                dismissThresholds = { FractionalThreshold(dismissThreshold) },
                dismissContent = { content() },
                modifier = Modifier
        )

        ShortSwipeEffect(
                offset = offset,
                minAndMaxOffsetToCallShortSwipe = minAndMaxOffsetToCallShortSwipe,
                delayToAllowValidateShortSwipe = delayToAllowValidateShortSwipe,
                density = density,
                onShortSwipe = onShortSwipe,
                dismissState = dismissState
        )

        ResetEffect(
                offset = offset,
                thresholdToEnableShortSwipe = thresholdToEnableShortSwipe,
                dismissState = dismissState
        )

        ForceResetEffect(
            forceReset = forceReset,
            offset = offset,
            dismissState = dismissState
        )
    } else {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShortSwipeEffect(
        offset: Int,
        minAndMaxOffsetToCallShortSwipe: IntRange,
        delayToAllowValidateShortSwipe: Long,
        density: Density,
        onShortSwipe: (DismissDirection) -> Unit,
        dismissState: DismissState
) {
    LaunchedEffect(offset) {
        var lastWidth = 0.dp
        while (true) {
            val currentWidth = with(density) { offset.toDp() }
            if (currentWidth == lastWidth) {
                if (offset.toPositive() in minAndMaxOffsetToCallShortSwipe) {
                    onShortSwipe(dismissState.dismissDirection ?: DismissDirection.StartToEnd)
                }
                break
            }
            lastWidth = currentWidth
            delay(delayToAllowValidateShortSwipe)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ResetEffect(
        offset: Int,
        thresholdToEnableShortSwipe: Int,
        dismissState: DismissState
) {
    LaunchedEffect(offset) {
        if (offset.toPositive() > thresholdToEnableShortSwipe) {
            dismissState.reset()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ForceResetEffect(
    forceReset: Boolean?,
    offset: Int,
    dismissState: DismissState
){
    forceReset?.let {
        LaunchedEffect(forceReset) {
            if(forceReset && offset != 0){
                dismissState.reset()
            }
        }
    }
}

