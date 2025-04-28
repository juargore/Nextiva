package com.nextiva.nextivaapp.android.models
import android.graphics.drawable.Drawable
import com.nextiva.nextivaapp.android.constants.Enums.AudioDevices.AudioDevice

data class AudioDeviceInfo(
    val textResId: Int,
    val icon: Drawable?,
    val description: String,
    val deviceName: String,
    val audioDevice: AudioDevice
)