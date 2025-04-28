package com.nextiva.nextivaapp.android.models

import android.net.Uri
import com.nextiva.nextivaapp.android.BasePowerMockTest
import com.nextiva.nextivaapp.android.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(Uri::class)
class AvatarInfoTest : BasePowerMockTest() {

    private lateinit var avatarInfo: AvatarInfo
    private lateinit var photoData: ByteArray
    private lateinit var displayName: String
    private var iconResId: Int = 0
    private var size: Int = AvatarInfo.SIZE_LARGE

    override fun setup() {
        super.setup()

        photoData = byteArrayOf(1)
        displayName = "Displaying Name"
        iconResId = R.drawable.ic_phone
        size = AvatarInfo.SIZE_LARGE

        avatarInfo = AvatarInfo.Builder()
                .setPhotoData(photoData)
                .setDisplayName(displayName)
                .setIconResId(iconResId)
                .setSize(size)
                .build()
    }

    @Test
    fun getAvatarBytes_returnsCorrectValue() {
        assertEquals(photoData, avatarInfo.photoData)
    }

    @Test
    fun setAvatarBytes_setsCorrectValue() {
        assertEquals(photoData, avatarInfo.photoData)

        val avatarBytes2 = byteArrayOf(4)
        avatarInfo.photoData = avatarBytes2

        assertEquals(avatarBytes2, avatarInfo.photoData)
    }

    @Test
    fun getDisplayName_returnsCorrectValue() {
        assertEquals(displayName, avatarInfo.displayName)
    }

    @Test
    fun setDisplayName_setsCorrectValue() {
        assertEquals(displayName, avatarInfo.displayName)

        avatarInfo.displayName = "New Display"

        assertEquals("New Display", avatarInfo.displayName)
    }

    @Test
    fun getIconResId_returnsCorrectValue() {
        assertEquals(iconResId, avatarInfo.iconResId)
    }

    @Test
    fun setIconResId_setsCorrectValue() {
        assertEquals(iconResId, avatarInfo.iconResId)

        avatarInfo.iconResId = R.drawable.ic_add

        assertEquals(R.drawable.ic_add, avatarInfo.iconResId)
    }

    @Test
    fun getSize_returnsCorrectValue() {
        assertEquals(size, avatarInfo.size)
    }

    @Test
    fun setSize_setsCorrectValue() {
        assertEquals(size, avatarInfo.size)

        avatarInfo.size = AvatarInfo.SIZE_LARGE

        assertEquals(AvatarInfo.SIZE_LARGE, avatarInfo.size)
    }

    @Test
    fun builder_setAvatarBytes_returnsBuilder() {
        val builder = AvatarInfo.Builder()

        assertEquals(builder, builder.setPhotoData(photoData))
    }

    @Test
    fun builder_setDisplayName_returnsBuilder() {
        val builder = AvatarInfo.Builder()

        assertEquals(builder, builder.setDisplayName(displayName))
    }

    @Test
    fun builder_setIconResId_returnsBuilder() {
        val builder = AvatarInfo.Builder()

        assertEquals(builder, builder.setIconResId(iconResId))
    }

    @Test
    fun builder_setSize_returnsBuilder() {
        val builder = AvatarInfo.Builder()

        assertEquals(builder, builder.setSize(size))
    }

    @Test
    fun builder_build_returnsAvatarInfo() {
        val avatarInfo = AvatarInfo.Builder()
                .setPhotoData(photoData)
                .setDisplayName(displayName)
                .setIconResId(iconResId)
                .setSize(size)
                .build()

        assertEquals(photoData, avatarInfo.photoData)
        assertEquals(displayName, avatarInfo.displayName)
        assertEquals(iconResId, avatarInfo.iconResId)
        assertEquals(size, avatarInfo.size)
    }

    @Test
    fun builder_build_returnsDefaultValues() {
        val avatarInfo = AvatarInfo.Builder().build()

        assertEquals(0, avatarInfo.iconResId)
        assertEquals(AvatarInfo.SIZE_SMALL, avatarInfo.size)
    }
}
