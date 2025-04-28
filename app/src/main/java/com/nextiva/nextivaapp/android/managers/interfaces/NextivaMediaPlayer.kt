package com.nextiva.nextivaapp.android.managers.interfaces

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import java.io.File

interface NextivaMediaPlayer {
    fun createAudioFile(context: Context, content: String?, fileType: String?, messageUuid: String)

    fun getAudioFileFromCache(context: Context, messageUuid: String): File?

    fun getAudioFileFromCacheByName(context: Context, audioName: String): File?

    fun deleteAudioFile(context: Context, messageId: String)

    fun deleteAudioFiles(context: Context, messageIds: ArrayList<String>)

    fun finishPlayingAudioFile()

    fun playVoicemailFromFile(context: Context, voicemailFile: File)

    fun playVoicemail(context: Context, messageDetailsPath: String, isRead: Boolean)

    fun playVoicemail(context: Context, messageDetailsPath: String, callId: String, isRead: Boolean)

    fun playAudioFile(context: Context, audioFile: File)

    fun pausePlaying()

    fun setProgress(progress: Int, fromUser: Boolean)

    fun getCurrentPlayingProgress(): Int

    fun getCurrentActiveAudioFileMessageId(): String?

    fun setCurrentActiveAudioFileMessageId(messageId: String)

    fun isPlaying(): Boolean

    fun isSpeakerPhoneEnabled(): Boolean

    fun toggleSpeakerPhone(context: Context)

    fun getActiveVoicemailMessageIdChangedLiveData(): LiveData<String>

    fun getCurrentPlayingProgressChangedLiveData(): LiveData<Int>

    fun getCurrentPlayingVoicemailPausedLiveData(): LiveData<String>

    fun getFetchingVoicemailDetailsStartedLiveData(): LiveData<Int>

    fun getFetchingVoicemailDetailsFinishedLiveData(): LiveData<Boolean>

    fun getFetchingVoicemailFailedNoInternetLiveData(): LiveData<Void>

    fun playSmsAudioFileFromURI(context: Context, uri: Uri)

    fun getCurrentActiveSpeakerMessageId(): String?

    fun setCurrentActiveSpeakerMessageId(messageId: String)

    fun getIsPlayerCurrentlyPaused(): Boolean

    fun getViewHolderPlayButtonClickedLiveData(): LiveData<Boolean>

    fun setViewHolderPlayButtonClickedLiveData(isPlayButtonClicked: Boolean)

    fun reset()

    fun isInternetAvailableToPlayMedia(): Boolean
}