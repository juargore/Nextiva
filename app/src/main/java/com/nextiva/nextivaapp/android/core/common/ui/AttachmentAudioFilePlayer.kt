package com.nextiva.nextivaapp.android.core.common.ui

import androidx.lifecycle.MutableLiveData

class AttachmentAudioFilePlayer(
    var activeItemId: String = "",
    var activeItemFilename: String = "",
    var activeItemUrl: String = "",
    var duration: Int = 0,
    var isPlaying: MutableLiveData<Boolean> = MutableLiveData(false),
    var progress: MutableLiveData<Int> = MutableLiveData(0),
    var formattedDuration: MutableLiveData<String?> = MutableLiveData(null),
    var isSpeakerEnabled: MutableMap<String, MutableLiveData<Boolean>> = mutableMapOf(),
) {
    fun formattedDuration(seconds: Int): String {
        val displayMinutes = (seconds / 60).toString().padStart(2, '0')
        val displaySeconds = (seconds - (seconds / 60 * 60)).toString().padStart(2, '0')
        return if (seconds == 0) "" else "${displayMinutes}:${displaySeconds}"
    }

    fun updateDuration(milliseconds: Long) {
        duration = (milliseconds / 1000).toInt()
        formattedDuration.value = formattedDuration(duration)
    }

    fun updateProgress(milliseconds: Int) {
        val fraction = milliseconds.toDouble() / 1000 / duration.toDouble()
        progress.value = (fraction * 100).toInt()
        if (fraction < 0) {
            isPlaying.value = false
        }
    }

    fun updateSpeakerEnabled(attachmentId: String, enabled: Boolean) {
        speakerEnabledLiveData(attachmentId).value = enabled
    }

    fun speakerEnabledLiveData(attachmentId: String): MutableLiveData<Boolean> {
        if (isSpeakerEnabled[attachmentId] == null) {
            isSpeakerEnabled[attachmentId] = MutableLiveData(false)
        }
        return isSpeakerEnabled[attachmentId]!!
    }
}
