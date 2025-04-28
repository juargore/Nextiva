package com.nextiva.nextivaapp.android.util

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import java.nio.charset.Charset
import java.security.MessageDigest

// https://bumptech.github.io/glide/doc/transformations.html
class GlideTwoMbBitmapTransformation(val avatarManager: AvatarManager, val fileSize: Int): BitmapTransformation() {
    private val id: String = "package com.nextiva.nextivaapp.android.util.GlideTwoMbBitmapTransformation"
    private val idBytes: ByteArray = id.toByteArray(Charset.forName("UTF-8"))

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return avatarManager.scaleBitmapForMMS(toTransform, fileSize)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is GlideTwoMbBitmapTransformation
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(idBytes)
    }
}