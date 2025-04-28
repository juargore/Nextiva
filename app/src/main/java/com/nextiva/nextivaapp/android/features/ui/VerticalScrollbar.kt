package com.nextiva.nextivaapp.android.features.ui

import android.view.ViewConfiguration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.nextiva.nextivaapp.android.R
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

fun Modifier.drawVerticalScrollbar(
    state: ScrollState,
    reverseScrolling: Boolean = false
): Modifier = drawScrollbar(state, reverseScrolling)

private fun Modifier.drawScrollbar(
    state: ScrollState,
    reverseScrolling: Boolean
): Modifier = drawScrollbar(
    reverseScrolling
) { reverseDirection, thickness, color, alpha ->
    val showScrollbar = state.maxValue > 0
    val canvasSize = size.height
    val totalSize = canvasSize + state.maxValue
    val thumbSize = canvasSize / totalSize * canvasSize
    val startOffset = state.value / totalSize * canvasSize
    val drawScrollbar = onDrawScrollbar(
        reverseDirection, showScrollbar,
        thickness, color, alpha, thumbSize, startOffset
    )
    onDrawWithContent {
        drawContent()
        drawScrollbar()
    }
}

private fun CacheDrawScope.onDrawScrollbar(
    reverseDirection: Boolean,
    showScrollbar: Boolean,
    thickness: Float,
    color: Color,
    alpha: () -> Float,
    thumbSize: Float,
    startOffset: Float
): DrawScope.() -> Unit {
    val topLeft =
        Offset(
            x = size.width - thickness,
            y = if (reverseDirection) size.height - startOffset - thumbSize else startOffset
        )
    val size = Size(thickness, thumbSize)

    return {
        if (showScrollbar) {
            drawRect(
                color = color,
                topLeft = topLeft,
                size = size,
                alpha = alpha()
            )
        }
    }
}

private fun Modifier.drawScrollbar(
    reverseScrolling: Boolean,
    onBuildDrawCache: CacheDrawScope.(
        reverseDirection: Boolean,
        thickness: Float,
        color: Color,
        alpha: () -> Float
    ) -> DrawResult
): Modifier = composed {
    val scrolled = remember {
        MutableSharedFlow<Unit>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    val nestedScrollConnection = remember(scrolled) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = consumed.y
                if (delta != 0f) scrolled.tryEmit(Unit)
                return Offset.Zero
            }
        }
    }

    val alpha = remember { Animatable(0f) }
    LaunchedEffect(scrolled, alpha) {
        scrolled.collectLatest {
            alpha.snapTo(1f)
            delay(ViewConfiguration.getScrollDefaultDelay().toLong())
            alpha.animateTo(0f, animationSpec = FadeOutAnimationSpec)
        }
    }

    val thickness = with(LocalDensity.current) { Thickness.toPx() }
    val color = colorResource(R.color.connectGrey03)
    Modifier
        .nestedScroll(nestedScrollConnection)
        .onSizeChanged { size ->
            scrolled.tryEmit(Unit) }
        .drawWithCache {
            onBuildDrawCache(reverseScrolling, thickness, color, alpha::value)
        }
}

private val Thickness = 4.dp
private val FadeOutAnimationSpec =
    tween<Float>(durationMillis = ViewConfiguration.getScrollBarFadeDuration())
