package com.nextiva.nextivaapp.android.features.rooms.view.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CustomizedGlideImage(
    modifier: Modifier = Modifier,
    data: Any,
    contentType: String,
    placeHolderDrawable: Drawable? = null,
    contentDescription: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    sessionId: String,
    corpAcctNumber: String?,
    zoomEnabled: Boolean = false,
    loadFailed: (() -> Unit)? = null,
    loadCompleted: (Drawable) -> Unit = { }
) {
    if (contentType == Enums.Attachment.AttachmentContentType.IMAGE_GIF) {
        GlideImageGif(
            modifier = modifier,
            data = data,
            sessionId = sessionId,
            corpAcctNumber = corpAcctNumber.toString(),
            placeHolderDrawable = AppCompatResources.getDrawable(
                LocalContext.current,
                R.drawable.placeholder_padded_gif
            ),
            contentDescription = contentDescription,
            contentScale = contentScale,
            zoomEnabled = zoomEnabled,
            loadFailed = loadFailed,
            loadCompleted = loadCompleted
        )
    } else {
        GlideImageDefault(
            modifier = modifier,
            data = data,
            sessionId = sessionId,
            corpAcctNumber = corpAcctNumber.toString(),
            placeHolderDrawable = placeHolderDrawable,
            contentDescription = contentDescription,
            contentScale = contentScale,
            zoomEnabled = zoomEnabled,
            loadFailed = loadFailed,
            loadCompleted = loadCompleted
        )
    }
}

@Composable
fun GlideImageDefault(
    modifier: Modifier = Modifier,
    data: Any,
    sessionId: String,
    corpAcctNumber: String,
    glideModifier: (RequestBuilder<Drawable>) -> RequestBuilder<Drawable> = { it },
    placeHolderDrawable: Drawable? = null,
    contentDescription: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    zoomEnabled: Boolean = false,
    loadFailed: (() -> Unit)? = null,
    loadCompleted: (Drawable) -> Unit = { }
) {

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var imgSize by remember { mutableStateOf(IntSize(0, 0)) }

    BoxWithConstraints(modifier = modifier) {
        val state = remember(placeHolderDrawable) {
            mutableStateOf<Drawable?>(null)
        }
        val context = LocalContext.current
        val headers = LazyHeaders.Builder()
            .addHeader("x-api-key", sessionId)
            .addHeader("nextiva-context-corpAcctNumber", corpAcctNumber)
            .build()

        DisposableEffect(data, modifier, glideModifier, placeHolderDrawable) {
            val glide = Glide.with(context)
            var builder = when (data) {
                is String -> {
                    if (!TextUtils.isEmpty(data)) {
                        if (data.startsWith("file")) {
                            glide.load(data)  // load from local file (file://)
                        } else {
                            glide.load(GlideUrl(data, headers)) // load from server (https://)
                        }
                    } else {
                        glide.load(placeHolderDrawable)
                    }
                }
                else -> {
                    glide.load(data)
                }
            }
            builder = builder.placeholder(placeHolderDrawable)
            builder = glideModifier(builder).dontAnimate()
            val request = builder.into(object: CustomTarget<Drawable>(
                constraints.maxWidth,
                constraints.maxHeight
            ) {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    state.value = resource
                    loadCompleted(resource)
                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    if (placeholder != null) {
                        state.value = placeholder
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    if (placeholder != null) {
                        state.value = placeholder
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    loadFailed?.let { it() }
                }
            }).request!!
            onDispose {
                request.clear()
            }
        }
        state.value?.let { currentDrawable ->
            val painter = rememberDrawablePainter(currentDrawable)
            Image(
                contentDescription = contentDescription,
                painter = painter,
                contentScale = contentScale,
                modifier = modifier.then(other = painter.intrinsicSize.let { intrinsicSize ->
                    if (contentScale != ContentScale.Fit && contentScale != ContentScale.Crop)
                        Modifier.aspectRatio(intrinsicSize.width / intrinsicSize.height)
                    else
                        Modifier.aspectRatio(1f)
                })
                    .onSizeChanged { imgSize = it }
                    .pointerInput(Unit) {
                        if(zoomEnabled) {
                            detectTapGestures(
                                onDoubleTap = {
                                    scale = if (scale > 1) { 1f } else { 2f }
                                    offset = Offset(0f, 0f)
                                }
                            )
                        }
                    }
                    .pointerInput(Unit) {
                        if(zoomEnabled) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale *= zoom
                                scale = scale.coerceIn(1f, 3f)
                                val deltaX = with(imgSize.width) { ((this * scale) - this) / 2 }
                                val deltaY = with(imgSize.height) { ((this * scale) - this) / 2 }
                                offset = if (scale == 1f) Offset(0f, 0f) else Offset(
                                    (offset.x + pan.x).coerceIn(-deltaX, deltaX),
                                    (offset.y + pan.y).coerceIn(-deltaY, deltaY)
                                )
                            }
                        }
                    }
                    .graphicsLayer(
                        scaleX = scale, scaleY = scale,
                        translationX = offset.x, translationY = offset.y
                    )
            )
        }
    }
}

@Composable
fun GlideImageGif(
    modifier: Modifier = Modifier,
    data: Any,
    sessionId: String,
    corpAcctNumber: String,
    glideModifier: (RequestBuilder<GifDrawable>) -> RequestBuilder<GifDrawable> = { it },
    placeHolderDrawable: Drawable? = null,
    contentDescription: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    zoomEnabled: Boolean = false,
    loadFailed: (() -> Unit)? = null,
    loadCompleted: (Drawable) -> Unit = { }
) {

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var imgSize by remember { mutableStateOf(IntSize(0, 0)) }

    BoxWithConstraints(modifier = modifier) {
        val state = remember(placeHolderDrawable) {
            mutableStateOf<GifDrawable?>(null)
        }
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        DisposableEffect(data, modifier, glideModifier, placeHolderDrawable) {
            val headers = LazyHeaders.Builder()
                .addHeader("x-api-key", sessionId)
                .addHeader("nextiva-context-corpAcctNumber", corpAcctNumber).build()
            var builder = if (data is String && data.startsWith("file")) {
                // load from local file (file://)
                Glide.with(context).asGif().load(data).placeholder(placeHolderDrawable)
            } else {
                // load from server (https://)
                Glide.with(context).asGif().load(GlideUrl(data as String, headers)).placeholder(placeHolderDrawable)
            }
            builder = glideModifier(builder)
            val request = builder.into(object: CustomTarget<GifDrawable>(
                constraints.maxWidth,
                constraints.maxHeight
            ) {
                override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable>?) {
                    state.value = resource
                    loadCompleted(resource)
                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    if (placeholder != null) {
                        coroutineScope.launch {
                            loadGifPlaceholder(context, state)
                        }
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    if (placeholder != null) {
                        coroutineScope.launch {
                            loadGifPlaceholder(context, state)
                        }
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    loadFailed?.let { it() }
                }
            }).request!!
            onDispose {
                request.clear()
            }
        }
        val currentBitmap = state.value
        if (currentBitmap != null) {
            val painter = rememberDrawablePainter(drawable = currentBitmap)
            Image(
                contentDescription = contentDescription,
                painter = painter,
                contentScale = contentScale,
                modifier = modifier.then(other = painter.intrinsicSize.let { intrinsicSize ->
                    Modifier.aspectRatio(intrinsicSize.width / intrinsicSize.height)
                        .onSizeChanged { imgSize = it }
                        .pointerInput(Unit) {
                            if(zoomEnabled) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        scale = if (scale > 1) { 1f } else { 2f }
                                        offset = Offset(0f, 0f)
                                    }
                                )
                            }
                        }
                        .pointerInput(Unit) {
                            if(zoomEnabled) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale *= zoom
                                    scale = scale.coerceIn(1f, 3f)
                                    val deltaX = with(imgSize.width) { ((this * scale) - this) / 2 }
                                    val deltaY = with(imgSize.height) { ((this * scale) - this) / 2 }
                                    offset = if (scale == 1f) Offset(0f, 0f) else Offset(
                                        (offset.x + pan.x).coerceIn(-deltaX, deltaX),
                                        (offset.y + pan.y).coerceIn(-deltaY, deltaY)
                                    )
                                }
                            }
                        }
                        .graphicsLayer(
                            scaleX = scale, scaleY = scale,
                            translationX = offset.x, translationY = offset.y
                        )
                })
            )
        }
    }
}

private suspend fun loadGifPlaceholder(context: Context, state: MutableState<GifDrawable?>) {
    val drawable = withContext(Dispatchers.IO) {
        Glide.with(context)
            .load(R.drawable.placeholder_padded_gif)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .submit()
            .get() as GifDrawable
    }
    state.value = drawable
}
