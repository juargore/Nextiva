/*
 * Copyright (c) 2021 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util.extensions

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.resource.gif.GifDrawable
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

fun Drawable.drawableToByteArray(): ByteArray {
    if (this is BitmapDrawable) {
        val outputStream = ByteArrayOutputStream()
        val format = Bitmap.CompressFormat.JPEG
        val bitmap = this.bitmap
        outputStream.reset()
        bitmap.compress(format, 100, outputStream)
        return outputStream.toByteArray()
    }
    return ByteArray(0)
}

fun GifDrawable?.gifDrawableToByteArray(): ByteArray {
    val buffer: ByteBuffer? = this?.buffer
    buffer?.let { byteBuffer ->
        val attachment = ByteArray(buffer.capacity())
        (byteBuffer.duplicate().clear() as ByteBuffer).get(attachment)
        return attachment
    }
    return ByteArray(0)
}
