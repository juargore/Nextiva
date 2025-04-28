package com.nextiva.nextivaapp.android.managers

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.amr.AmrExtractor
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailDetails
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NextivaMediaPlayerManager @Inject constructor(
    val userRepository: UserRepository,
    val connectionStateManager: ConnectionStateManager,
    val dbManager: DbManager,
    val sessionManager: SessionManager,
    val platformRepository: PlatformRepository,
    val schedulerProvider: SchedulerProvider,
    val conversationRepository: ConversationRepository,
) : NextivaMediaPlayer {

    private var audioManager: AudioManager? = null
    private var exoPlayer: SimpleExoPlayer? = null

    private val audioSyncHandler = Handler(Looper.getMainLooper())
    private var audioSyncRunnable: Runnable? = null

    private var currentActiveAudioId: String = ""
    private var markReadMessageDetailsPath: String? = null
    private var callId: String? = null
    private var currentProgress = 0
    private var currentSpeakerEnabledMessageId: String? = null
    private var shouldResumePlaying = false
    private var isSpeakerPhoneOn = false
    private var isDownloading = false

    private var activeAudioMessageIdChangedLiveData: MutableLiveData<String> = MutableLiveData()
    private var currentPlayingProgressChangedLiveData: MutableLiveData<Int> = MutableLiveData()
    private var currentPlayingAudioPausedLiveData: MutableLiveData<String> = MutableLiveData()
    private var fetchingVoicemailDetailsStartedLiveData: MutableLiveData<Int> = MutableLiveData()
    private var fetchingVoicemailDetailsFinishedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var fetchingVoicemailFailedNoInternetLiveData: MutableLiveData<Void> = MutableLiveData()
    private var isViewHolderPlayButtonClickedLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()

    override fun getAudioFileFromCache(context: Context, messageUuid: String): File? {
        val dir = File(context.cacheDir, "")
        if (dir.exists()) {
            dir.listFiles()?.let { files ->
                for (file in files) {
                    if (file.name.startsWith(messageUuid)) {
                        return file
                    }
                }
            }
        }

        return null
    }

    override fun getAudioFileFromCacheByName(context: Context, audioName: String): File? {
        val dir = File(context.cacheDir, "")
        if (dir.exists()) {
            dir.listFiles()?.let { files ->
                for (file in files) {
                    if (file.name.equals(audioName)) {
                        return file
                    }
                }
            }
        }

        return null
    }

    override fun deleteAudioFile(context: Context, messageId: String) {
        finishPlayingAudioFile()

        val dir = File(context.cacheDir, "")
        if (dir.exists()) {
            dir.listFiles()?.let { files ->
                for (file in files) {
                    if (file.name.startsWith(messageId.split("/").last())) {
                        file.delete()
                    }
                }
            }
        }
    }

    override fun deleteAudioFiles(context: Context, messageIds: ArrayList<String>) {
        val dir = File(context.cacheDir, "")
        if (dir.exists()) {
            for(messageId in messageIds){
                dir.listFiles()?.let { files ->
                    for (file in files) {
                        if (file.name.startsWith(messageId.split("/").last())) {
                            file.delete()
                        }
                    }
                }
            }
        }
    }

    override fun createAudioFile(
        context: Context,
        content: String?,
        fileType: String?,
        messageUuid: String,
    ) {
        try {
            val outputDir: File = context.cacheDir
            val outputFile: File = File.createTempFile(
                messageUuid,
                "." + fileType?.lowercase(Locale.getDefault()),
                outputDir
            )

            val fos = FileOutputStream(outputFile)
            fos.write(Base64.decode(content, Base64.DEFAULT))
            fos.close()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    override fun playVoicemail(context: Context, messageDetailsPath: String, isRead: Boolean)
    {
        playVoicemail(context, messageDetailsPath, "", isRead)
    }

    override fun playVoicemail(context: Context, messageDetailsPath: String, callId: String,  isRead: Boolean) {
        if(isDownloading){
            return
        }
        val messageUuid = messageDetailsPath.split("/").last()

        if (!isRead) {
            // store messageDetails so it can be marked as Read when playback stops
            markReadMessageDetailsPath = messageDetailsPath
        }
        callId?.let {
            this.callId = callId
        }

        if (shouldResumePlaying) {
            syncProgressToMediaPlayer()
            exoPlayer?.play()

        } else {
            val voicemailFile = getAudioFileFromCache(context, messageUuid)

            if (voicemailFile != null) {
                syncProgressToMediaPlayer()
                playVoicemailFromFile(context, voicemailFile)

            } else if (connectionStateManager.isInternetConnected) {
                fetchingVoicemailDetailsStartedLiveData.value = R.string.progress_loading
                if (sessionManager.isNextivaConnectEnabled) {
                    isDownloading = true
                    platformRepository.getVoicemailDetails(messageUuid)
                        .observeOn(schedulerProvider.ui())
                        .subscribe(object : DisposableSingleObserver<VoicemailDetails>() {
                            override fun onSuccess(voicemailDetails: VoicemailDetails) {
                                handleVoicemailDetailsResponse(
                                    context,
                                    voicemailDetails.content,
                                    voicemailDetails.fileType,
                                    voicemailDetails.duration,
                                    messageUuid
                                )
                                isDownloading = false
                            }

                            override fun onError(e: Throwable) {
                                isDownloading = false
                                fetchingVoicemailDetailsFinishedLiveData.value = false
                                Toast.makeText(context, R.string.error_voicemail_not_found, Toast.LENGTH_SHORT).show()
                            }
                        })

                } else {
                    isDownloading = true
                    compositeDisposable.add(
                        userRepository.getVoicemailDetails(messageDetailsPath)
                            .subscribe { response ->
                                fetchingVoicemailDetailsFinishedLiveData.value =
                                    response.messageMediaContent != null

                                if (response.messageMediaContent?.content?.isNotEmpty() == true &&
                                    response.messageMediaContent?.description?.isNotEmpty() == true &&
                                    response.messageMediaContent?.mediaType == "WAV"
                                ) {

                                    createAudioFile(
                                        context,
                                        response.messageMediaContent?.content,
                                        response.messageMediaContent?.mediaType,
                                        messageUuid
                                    )
                                    getAudioFileFromCache(
                                        context,
                                        messageUuid
                                    )?.let { voicemailFile ->
                                        syncProgressToMediaPlayer()
                                        playVoicemailFromFile(context, voicemailFile)
                                    }
                                } else {
                                    finishPlayingAudioFile()
                                }
                                isDownloading = false
                            })
                }

            } else {
                fetchingVoicemailFailedNoInternetLiveData.value = null
                finishPlayingAudioFile()
            }
        }
    }

    private fun handleVoicemailDetailsResponse(
        context: Context,
        content: String?,
        mediaType: String?,
        duration: Int?,
        voicemailId: String,
    ) {
        fetchingVoicemailDetailsFinishedLiveData.value = content != null

        if (content?.isNotEmpty() == true && mediaType == "WAV") {
            createAudioFile(context, content, mediaType, voicemailId)
            getAudioFileFromCache(context, voicemailId)?.let { voicemailFile ->
                syncProgressToMediaPlayer()
                playVoicemailFromFile(context, voicemailFile)
            }

            duration?.let {
                dbManager.updateVoicemailDuration(duration, voicemailId)
            }

        } else {
            Toast.makeText(context, R.string.error_voicemail_not_found, Toast.LENGTH_LONG).show()
            finishPlayingAudioFile()
        }
    }

    override fun playAudioFile(context: Context, audioFile: File) {
        if (shouldResumePlaying) {
            syncProgressToMediaPlayer()
            exoPlayer?.play()

        } else {
            syncProgressToMediaPlayer()
            playVoicemailFromFile(context, audioFile)

        }
    }

    override fun playVoicemailFromFile(context: Context, voicemailFile: File) {
        try {
            val fileInputStream = FileInputStream(voicemailFile)

            if (audioManager == null) {
                audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            }

            audioManager?.let { audioManager ->
                audioManager.isSpeakerphoneOn = isSpeakerPhoneOn
                audioManager.mode = AudioManager.MODE_IN_CALL
            }

            val extractorsFactory: DefaultExtractorsFactory =
                DefaultExtractorsFactory().setAmrExtractorFlags(AmrExtractor.FLAG_ENABLE_CONSTANT_BITRATE_SEEKING)
            exoPlayer = SimpleExoPlayer.Builder(context)
                .setMediaSourceFactory(DefaultMediaSourceFactory(context, extractorsFactory))
                .build()
            exoPlayer?.apply {
                this.setMediaItem(MediaItem.fromUri(Uri.fromFile(voicemailFile)))
                this.setAudioAttributes(
                    AudioAttributes.Builder().setUsage(C.USAGE_VOICE_COMMUNICATION).build(), false
                )
                this.prepare()

                this.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        super.onPlaybackStateChanged(state)
                        if (playbackState == Player.STATE_ENDED) {
                            finishPlayingAudioFile()
                        }
                    }
                })

                currentProgress?.let { currentProgress ->
                    this.seekTo(currentProgress.toLong())
                }

                this.play()
            }

            shouldResumePlaying = true
            fileInputStream.close()

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    override fun finishPlayingAudioFile() {
        shouldResumePlaying = false
        currentProgress = 0
        currentPlayingProgressChangedLiveData.value = -1
        removeProgressSyncToMediaPlayer()

        if (exoPlayer?.isPlaying == true) {
            exoPlayer?.stop()
        }

        exoPlayer?.release()
        exoPlayer = null

        audioManager?.let { audioManager ->
            audioManager.isSpeakerphoneOn = false
            audioManager.mode = AudioManager.MODE_NORMAL
        }
    }

    override fun pausePlaying() {
        if (exoPlayer?.isPlaying == true) {
            currentPlayingAudioPausedLiveData.value = currentActiveAudioId
            exoPlayer?.pause()
            removeProgressSyncToMediaPlayer()
        }
    }

    override fun setProgress(progress: Int, fromUser: Boolean) {
        currentProgress = progress * 1000

        exoPlayer?.let { exoPlayer ->
            if (shouldResumePlaying && fromUser) {
                exoPlayer.seekTo(currentProgress.toLong())
            }
        }
    }

    override fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying == true
    }

    override fun setCurrentActiveAudioFileMessageId(messageId: String) {
        currentActiveAudioId = messageId
        activeAudioMessageIdChangedLiveData.value = messageId
    }

    override fun getCurrentPlayingProgress(): Int {
        return currentProgress
    }

    override fun getCurrentActiveAudioFileMessageId(): String {
        return currentActiveAudioId
    }

    override fun isSpeakerPhoneEnabled(): Boolean {
        return isSpeakerPhoneOn
    }

    override fun toggleSpeakerPhone(context: Context) {
        if (audioManager == null) {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        }

        audioManager?.let { audioManager ->
            isSpeakerPhoneOn = !isSpeakerPhoneOn
            audioManager.isSpeakerphoneOn = isSpeakerPhoneOn
        }
    }

    private fun syncProgressToMediaPlayer() {
        audioSyncRunnable = object : Runnable {
            override fun run() {
                exoPlayer?.currentPosition?.toInt()?.let { currentPosition ->
                    currentPlayingProgressChangedLiveData.value = currentPosition
                }

                audioSyncHandler.postDelayed(this, 50)
            }
        }

        audioSyncRunnable?.let { voicemailSyncRunnable ->
            audioSyncHandler.post(voicemailSyncRunnable)
        }
    }

    private fun removeProgressSyncToMediaPlayer() {
        audioSyncRunnable?.let { voicemailSyncRunnable ->
            audioSyncHandler.removeCallbacks(voicemailSyncRunnable)
        }

        markReadMessageDetailsPath?.let {
            val messageUuid = it.split("/").last()

            if (sessionManager.isNextivaConnectEnabled) {
                conversationRepository.markVoicemailRead(messageUuid).subscribe()
                callId?.let { it1 -> conversationRepository.markCallRead(it1).subscribe() }

            } else {
                userRepository.markVoicemailRead(messageUuid, it).subscribe()
            }

            markReadMessageDetailsPath = null
        }
    }

    override fun getActiveVoicemailMessageIdChangedLiveData(): LiveData<String> {
        return activeAudioMessageIdChangedLiveData
    }

    override fun getCurrentPlayingProgressChangedLiveData(): LiveData<Int> {
        return currentPlayingProgressChangedLiveData
    }

    override fun getCurrentPlayingVoicemailPausedLiveData(): LiveData<String> {
        return currentPlayingAudioPausedLiveData
    }

    override fun getFetchingVoicemailDetailsStartedLiveData(): LiveData<Int> {
        return fetchingVoicemailDetailsStartedLiveData
    }

    override fun getFetchingVoicemailDetailsFinishedLiveData(): LiveData<Boolean> {
        return fetchingVoicemailDetailsFinishedLiveData
    }

    override fun getFetchingVoicemailFailedNoInternetLiveData(): LiveData<Void> {
        return fetchingVoicemailFailedNoInternetLiveData
    }

    override fun playSmsAudioFileFromURI(context: Context, uri: Uri) {
        try {
            if (shouldResumePlaying) {
                syncProgressToMediaPlayer()
                exoPlayer?.play()

            } else {
                syncProgressToMediaPlayer()

                if (audioManager == null) {
                    audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
                }
                audioManager?.let { audioManager ->
                    audioManager.isSpeakerphoneOn = isSpeakerPhoneOn
                    audioManager.mode = AudioManager.MODE_IN_CALL
                }
                val extractorsFactory: DefaultExtractorsFactory =
                    DefaultExtractorsFactory().setAmrExtractorFlags(AmrExtractor.FLAG_ENABLE_CONSTANT_BITRATE_SEEKING)
                exoPlayer = SimpleExoPlayer.Builder(context)
                    .setMediaSourceFactory(DefaultMediaSourceFactory(context, extractorsFactory))
                    .build()
                exoPlayer?.apply {
                    this.setMediaItem(MediaItem.fromUri(uri.toString()))
                    this.setAudioAttributes(
                        AudioAttributes.Builder().setUsage(C.USAGE_VOICE_COMMUNICATION).build(),
                        false
                    )
                    this.prepare()

                    this.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) {
                            super.onPlaybackStateChanged(state)
                            if (playbackState == Player.STATE_ENDED) {
                                finishPlayingAudioFile()
                            }
                        }
                    })

                    currentProgress?.let { currentProgress ->
                        this.seekTo(currentProgress.toLong())
                    }

                    this.play()
                }
            }
            shouldResumePlaying = true

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    override fun reset() {
        finishPlayingAudioFile()
        setCurrentActiveAudioFileMessageId("")
    }

    override fun getCurrentActiveSpeakerMessageId(): String? {
        return currentSpeakerEnabledMessageId
    }

    override fun setCurrentActiveSpeakerMessageId(messageId: String) {
        currentSpeakerEnabledMessageId = messageId
    }

    override fun getIsPlayerCurrentlyPaused(): Boolean {
        return shouldResumePlaying
    }

    override fun setViewHolderPlayButtonClickedLiveData(isPlayButtonClicked: Boolean) {
        isViewHolderPlayButtonClickedLiveData.value = isPlayButtonClicked
    }

    override fun getViewHolderPlayButtonClickedLiveData(): LiveData<Boolean> {
        return isViewHolderPlayButtonClickedLiveData
    }

    override fun isInternetAvailableToPlayMedia(): Boolean {
        return connectionStateManager.isInternetConnected
    }

}