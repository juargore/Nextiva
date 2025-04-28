package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.ColorStateList
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.LiveDataDatabaseObserver
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemChatMessageSentBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbAttachment
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.MessageUtil
import com.nextiva.nextivaapp.android.util.extensions.makeLinkable
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class SmsMessageSentViewHolder private constructor(
        itemView: View,
        context: Context,
        masterListListener: MasterListListener?
) : SmsMessageViewHolder(itemView, context, masterListListener), LiveDataDatabaseObserver {

    private val mMasterItemView: View
    private var containsImage = false

    private var audioFileAttachment: File? = null
    private var imageFileAttachment: ByteArray? = null
    private var attachmentLink: String = ""

    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private var activeVoiceMailMessage: LiveData<String>? = null
    private var currentPlayingProgress: LiveData<Int>? = null
    private var voiceMailPaused: LiveData<String>? = null

    private val currentPlayingProgressChangedObserver = Observer<Int> { progress ->
        if (nextivaMediaPlayer.getCurrentActiveAudioFileMessageId() == mListItem.data.messageState?.messageId) {
            if (progress >= 0) {
                val currentPosition: Int = progress / 1000
                mAudioAttachmentSeekBar.progress = currentPosition

                if (nextivaMediaPlayer.isPlaying()) {
                    setPlayButtonIcon(false)
                } else {
                    resetPlayState()
                }

            } else if (!nextivaMediaPlayer.isPlaying()) {
                resetPlayState()
            }
        }
    }

    private val activeAudioFileMessageIdChangedObserver = Observer<String> { messageId ->
        mListItem?.data?.messageState?.let { messageState ->
            if (messageId != Constants.TEMP_ATTACHMENT_MESSAGE_ID && messageState.messageId != messageId) {
                resetPlayState()
            }
        }
    }

    private fun resetPlayState() {
        mAudioAttachmentSeekBar.animate()
                .alpha(1.0f)
                .setDuration(0.5.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        mAudioAttachmentSeekBar.progress = 0
                    }
                })

        setPlayButtonIcon(true)
    }

    private val currentPlayingAudioPausedObserver = Observer<String> { messageId ->
        mListItem?.data?.messageState?.let { messageState ->
            if (messageState.messageId == messageId) {
                setPlayButtonIcon(true)
            }
        }
    }

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener?) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_message_sent, parent, false),
            context,
            masterListListener)


    override fun bind(listItem: SmsMessageListItem) {
        removeItemViewFromParent()
        containsImage = false
        mListItem = listItem
        setContentDescriptions()

        if (nextivaMediaPlayer.getCurrentActiveAudioFileMessageId() == listItem.data.messageState?.messageId) {

            if (nextivaMediaPlayer.isPlaying()) {
                setPlayButtonIcon(false)

            } else {
                nextivaMediaPlayer.getCurrentPlayingProgress()?.let { progress ->
                    mAudioAttachmentSeekBar.progress = progress.div(1000)
                }
            }

        } else {
            resetPlayState()
        }
        if (!nextivaMediaPlayer.getCurrentActiveSpeakerMessageId().isNullOrEmpty() && nextivaMediaPlayer.getCurrentActiveSpeakerMessageId().equals(listItem.data.messageState?.messageId)) {
            mAudioSpeakerButton.imageTintList = setAudioSpeakerButtonTint(nextivaMediaPlayer.isSpeakerPhoneEnabled())
        } else {
            mAudioSpeakerButton.imageTintList = setAudioSpeakerButtonTint(false)
        }
        if (mListItem == null) {
            return
        }

        if (mFailedMessageIcon != null && mFailedMessageTextView != null && mRetryMessageTextView != null) {
            if (mListItem.data.sentStatus == Enums.SMSMessages.SentStatus.FAILED) {
                mFailedMessageIcon?.visibility = View.VISIBLE
                mFailedMessageTextView?.visibility = View.VISIBLE
                mRetryMessageTextView?.visibility = View.VISIBLE
            } else {
                mFailedMessageIcon?.visibility = View.GONE
                mFailedMessageTextView?.visibility = View.GONE
                mRetryMessageTextView?.visibility = View.GONE
            }
        }

        val attachments = mListItem.data.attachments

        if (attachments?.isNotEmpty() == true) {
            containsImage = true
            mMessageImageView.visibility = View.VISIBLE

            attachments.first().let { attachment ->
                if (MessageUtil.isFileTypeSupported(attachment.contentType ?: "")) {
                    mFileNotSupportedTextView.visibility = View.GONE
                    attachment.fileName?.let {
                        if (MessageUtil.isFileExtensionSMSSupportedAudioType(it)) {
                            if (attachment.fileDuration == null) {
                                attachment.contentData?.let { contentData ->
                                    setupAudioFile(listItem.data.messageState?.messageId ?: "",
                                            attachment.contentType ?: "",
                                            contentData,
                                            attachment.link ?: "")
                                            .subscribe(object : DisposableSingleObserver<Long>() {
                                                override fun onSuccess(duration: Long) {
                                                    updateAudioDuration(duration)
                                                }

                                                override fun onError(e: Throwable) {
                                                    audioAttachmentProgressBar.visibility = View.GONE
                                                    FirebaseCrashlytics.getInstance().recordException(e)
                                                }
                                            })
                                }
                            } else {
                                attachment.fileDuration?.let {
                                    updateAudioDuration(it)
                                }
                            }
                            updateAudioAttachmentUI(it)
                        } else {
                            updateImageAttachmentUI()
                        }
                    }
                    if (attachment.contentData != null) {
                        attachment.contentData?.let {
                            mMessageImageView.hideProgress()
                            loadImage(attachment)
                        }

                    } else {
                        mMessageImageView.imageView?.setImageDrawable(ContextCompat.getDrawable(mContext, attachment.getFileSupportedPlaceholderDrawableId()))
                        mMessageImageView.showProgress()
                        mDbManager.saveContentDataFromLink(attachment.link, attachment.thumbnailLink, attachment.contentType)
                                .subscribe()
                    }

                } else {
                    mFileNotSupportedTextView.visibility = View.VISIBLE
                    mMessageImageView.imageView?.setImageDrawable(
                            ContextCompat.getDrawable(
                                    mContext,
                                    attachment.getFileUnsupportedPlaceholderDrawableId()
                            )
                    )
                }

                addObservers()

                mAudioAttachmentSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        nextivaMediaPlayer.setProgress(progress, fromUser)
                    }
                })

                mAudioSpeakerButton.setOnClickListener {
                    nextivaMediaPlayer.setCurrentActiveSpeakerMessageId(listItem.data.messageState?.messageId.toString())
                    nextivaMediaPlayer.toggleSpeakerPhone(mContext)
                    mAudioSpeakerButton.imageTintList = setAudioSpeakerButtonTint(nextivaMediaPlayer.isSpeakerPhoneEnabled())
                }

                mAudioAttachmentPlayButton.setOnClickListener {
                    nextivaMediaPlayer.setViewHolderPlayButtonClickedLiveData(true)
                    val audioFile = getAudioFile()
                    if (audioFile != null) {
                        initialiseAudioPlayer(audioFile)

                    } else if (attachment.contentData != null) {
                        attachment.contentData?.let { contentData ->
                            setupAudioFile(listItem.data.messageState?.messageId ?: "",
                                    attachment.contentType ?: "",
                                    contentData,
                                    attachment.link ?: "")
                                    .doOnError { e ->
                                        audioAttachmentProgressBar.visibility = View.GONE
                                        FirebaseCrashlytics.getInstance().recordException(e)
                                    }
                                    .subscribe(object : DisposableSingleObserver<Long>() {
                                        override fun onSuccess(duration: Long) {
                                            getAudioFile()?.let { initialiseAudioPlayer(it) }
                                        }

                                        override fun onError(e: Throwable) {
                                            audioAttachmentProgressBar.visibility = View.GONE
                                            FirebaseCrashlytics.getInstance().recordException(e)
                                        }
                                    })
                        }
                    } else {
                        attachment.link?.let { link ->
                            attachment.contentType?.let { contentType ->
                                listItem.data.messageState?.messageId?.let { messageId ->
                                    downloadAudioAttachment(link, contentType, messageId)
                                }
                            }
                        }
                    }
                }


            }

        } else {
            mMessageImageView.visibility = View.GONE
            mFileNotSupportedTextView.visibility = View.GONE
            mAudioAttachmentConstraint.visibility = View.GONE
        }

        if (!mListItem.data.body.isNullOrEmpty() && !TextUtils.equals(mListItem.data.body, mContext.getString(R.string.chat_details_shared_an_image))) {
            mMessageTextView.visibility = View.VISIBLE
            mMessageTextView.text = mListItem.data.body
            mMessageTextView.makeLinkable()

        } else {
            mMessageTextView.visibility = View.GONE
        }
        mMessageTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.nextivaPrimaryBlue))
        var topSpacePadding = 0

        when (mListItem.bubbleType) {
            Enums.Chats.MessageBubbleTypes.START -> {
                topSpacePadding = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, mContext.resources.displayMetrics) + 0.5f).toInt()
            }
            Enums.Chats.MessageBubbleTypes.MIDDLE,
            Enums.Chats.MessageBubbleTypes.END -> {

            }
        }
        mSentLayout?.setPadding(mSentLayout!!.paddingLeft,
                topSpacePadding,
                mSentLayout!!.paddingRight,
                mSentLayout!!.paddingBottom)
        mMessageContainer.setBackgroundResource(if (isNightModeEnabled(mContext, mSettingsManager)) R.drawable.shape_message_sent else R.drawable.shape_message_sent)

        mUINameTextView?.visibility = View.GONE

        mDatetimeTextView.text = mListItem.humanReadableDatetime
        mDatetimeTextView.visibility = View.GONE

    }

    fun bindViews(view: View) {
        val binding = ListItemChatMessageSentBinding.bind(view)

        mSentLayout = binding.listItemChatMessageSentLayout
        mMessageTextView = binding.listItemChatMessageTextView
        mMessageContainer = binding.listItemMessageContainer
        mMessageImageView = binding.listItemChatMessageImageView
        mDatetimeTextView = binding.listItemChatMessageDatetimeTextView
        mUINameTextView = binding.listItemChatMessageUserTextView
        mFailedMessageTextView = binding.listItemChatMessageFailedTextView
        mRetryMessageTextView = binding.listItemChatMessageFailedRetry
        mFailedMessageIcon = binding.listItemChatMessageFailedIcon
        mFileNotSupportedTextView = binding.listItemChatMessageFileNotSupported
        mFileNameTextView = binding.listItemChatMessageFileName

        mAudioAttachmentConstraint = binding.listItemLayoutAudioAttachment.audioAttachmentConstraint
        mAudioAttachmentFileName = binding.listItemLayoutAudioAttachment.audioFileNameText
        mAudioAttachmentPlayButton = binding.listItemLayoutAudioAttachment.audioAttachmentPlayButton
        mAudioSpeakerButton = binding.listItemLayoutAudioAttachment.audioAttachmentSpeakerButton
        mAudioAttachmentSeekBar = binding.listItemLayoutAudioAttachment.audioAttachmentSeekBar
        mAudioAttachmentDurationText = binding.listItemLayoutAudioAttachment.audioAttachmentDurationTimeText
        audioAttachmentProgressBar = binding.listItemLayoutAudioAttachment.audioAttachmentProgressBar

        mMessageImageView.setOnLongClickListener {
            showBottomMenuToDownloadFile(imageFileAttachment, null, attachmentLink)
            true
        }

        mAudioAttachmentConstraint.setOnLongClickListener {
            audioFileAttachment = getAudioFile()
            showBottomMenuToDownloadFile(null, audioFileAttachment, attachmentLink)
            true
        }
    }

    private fun updateAudioDuration(duration: Long?) {
        if (duration != null) {
            mAudioAttachmentSeekBar.max = duration.toInt()
            mAudioAttachmentDurationText.visibility = View.VISIBLE
            mAudioAttachmentDurationText.text = String.format("%02d:%02d",
                    TimeUnit.SECONDS.toMinutes(duration),
                    TimeUnit.SECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(duration)))

        } else {
            mAudioAttachmentDurationText.visibility = View.GONE
        }
    }

    private fun setupAudioFile(messageId: String, contentType: String, contentData: ByteArray, link: String): Single<Long> {
        return MessageUtil.createAudioCacheFile(mContext, messageId, contentType, contentData)
                .subscribeOn(schedulerProvider.io())
                .map { file ->
                    attachmentLink = link
                    MessageUtil.getAudioFileDuration(file.path, mContext).div(Constants.ONE_SECOND_IN_MILLIS.toInt()) }
                .flatMap { duration -> dbManager.saveFileDuration(link, duration) }
                .observeOn(schedulerProvider.ui())
    }

    private fun downloadAudioAttachment(link: String, contentType: String, messageId: String) {
        audioAttachmentProgressBar.visibility = View.VISIBLE
        dbManager.saveContentDataFromLinkWithReturn(link, contentType)
                .flatMap { contentData -> MessageUtil.createAudioCacheFile(mContext, messageId, contentType, contentData) }
                .map { file ->
                    schedulerProvider.ui().scheduleDirect { initialiseAudioPlayer(file) }
                    MessageUtil.getAudioFileDuration(file.path, mContext).div(Constants.ONE_SECOND_IN_MILLIS.toInt())
                }
                .flatMap { duration -> dbManager.saveFileDuration(link, duration) }
                .observeOn(schedulerProvider.ui())
                .subscribe(object : DisposableSingleObserver<Long>() {
                    override fun onSuccess(duration: Long) {
                        updateAudioDuration(duration)
                        audioAttachmentProgressBar.visibility = View.GONE
                    }

                    override fun onError(e: Throwable) {
                        audioAttachmentProgressBar.visibility = View.GONE
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                })
    }


    private fun initialiseAudioPlayer(audioFile: File) {
        if (mAudioAttachmentSeekBar.max != 0) {
            if (nextivaMediaPlayer.getCurrentActiveAudioFileMessageId() != mListItem.data.messageState?.messageId) {
                nextivaMediaPlayer.finishPlayingAudioFile()
                mListItem.data.messageState?.messageId?.let {
                    nextivaMediaPlayer.setCurrentActiveAudioFileMessageId(
                        it
                    )
                }
            }

            if (nextivaMediaPlayer.isPlaying()) {
                nextivaMediaPlayer.pausePlaying()

            } else {
                if (mAudioAttachmentSeekBar.progress == mAudioAttachmentSeekBar.max) {
                    resetPlayState()

                } else {
                    nextivaMediaPlayer.playAudioFile(mContext, audioFile)
                }

                setPlayButtonIcon(false)
            }
        } else {
            Toast.makeText(mContext, R.string.corrupted_file_playing_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun addObservers() {
        removeObservers()
        activeVoiceMailMessage = nextivaMediaPlayer.getActiveVoicemailMessageIdChangedLiveData()
                .apply { observeForever(activeAudioFileMessageIdChangedObserver) }
        currentPlayingProgress = nextivaMediaPlayer.getCurrentPlayingProgressChangedLiveData()
                .apply { observeForever(currentPlayingProgressChangedObserver) }
        voiceMailPaused = nextivaMediaPlayer.getCurrentPlayingVoicemailPausedLiveData()
                .apply { observeForever(currentPlayingAudioPausedObserver) }
    }

    override fun removeObservers() {
        activeVoiceMailMessage?.removeObserver(activeAudioFileMessageIdChangedObserver)
        currentPlayingProgress?.removeObserver(currentPlayingProgressChangedObserver)
        voiceMailPaused?.removeObserver(currentPlayingAudioPausedObserver)
    }

    private fun getAudioFile(): File? {
        var file = mListItem.data.attachments?.get(0)?.fileName?.split(".")?.get(0)?.let { it1 -> nextivaMediaPlayer.getAudioFileFromCache(mContext, it1) }

        if (file == null) {
            file = mListItem.data.messageState?.messageId?.let { it1 -> nextivaMediaPlayer.getAudioFileFromCache(mContext, it1) }
        }

        return file
    }

    private fun setContentDescriptions() {
        mMasterItemView.contentDescription = mContext.getString(R.string.chat_message_list_item_sent_content_description)
        mMessageTextView.contentDescription = mContext.getString(R.string.chat_message_list_item_message_content_description)
    }

    private fun setAudioSpeakerButtonTint(active: Boolean): ColorStateList {
        return ColorStateList.valueOf(
                if (active) {
                    ContextCompat.getColor(mContext, R.color.nextivaPrimaryBlue)

                } else {
                    ContextCompat.getColor(mContext, R.color.coolGray_50)
                })
    }

    private fun loadImage(attachment: DbAttachment) {
        mMessageImageView.imageView?.let { imageView ->
            attachment.contentData?.let { contentData ->
                attachmentLink = attachment.link ?: ""

                var baseRequestOptions = RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)

                if (MessageUtil.isFileTypeSupported(attachment.contentType ?: "")) {
                    mMessageImageView.imageView?.layoutParams?.width = 450
                    mMessageImageView.imageView?.layoutParams?.height = 450

                    when {
                        attachment.contentType?.contentEquals(Enums.Attachment.AttachmentContentType.IMAGE_GIF) == true -> {
                            imageFileAttachment = contentData
                            attachment.link?.let { link ->
                                baseRequestOptions = baseRequestOptions.signature(ObjectKey(link))
                            }

                            Glide.with(mContext)
                                    .asGif()
                                    .load(contentData)
                                    .priority(Priority.IMMEDIATE)
                                    .listener(glideGifListener)
                                    .apply(baseRequestOptions.transform(RoundedCorners(20)))
                                    .into(imageView)
                        }
                        attachment.contentType?.contains(Enums.Attachment.ContentMajorType.AUDIO) == true -> {
                            Glide.with(mContext)
                                    .load(ContextCompat.getDrawable(mContext, R.drawable.ic_soundfile))
                                    .apply(baseRequestOptions)
                                    .into(imageView)
                        }
                        else -> {
                            val cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, mContext.resources.displayMetrics).toInt()
                            imageFileAttachment = contentData
                            attachment.link?.let { link ->
                                baseRequestOptions = baseRequestOptions.signature(ObjectKey(link))
                            }

                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(contentData)
                                    .priority(Priority.IMMEDIATE)
                                    .listener(glideBitmapListener)
                                    .apply(baseRequestOptions.transform(RoundedCorners(cornerRadius)))
                                    .into(imageView)
                        }
                    }
                } else {
                    mFileNotSupportedTextView.visibility = View.VISIBLE
                    Glide.with(mContext)
                            .load(ContextCompat.getDrawable(mContext, R.drawable.ic_photo))
                            .apply(baseRequestOptions)
                            .into(imageView)
                }
            }
        }
    }

    private fun updateImageAttachmentUI() {
        if (mAudioAttachmentConstraint.visibility == View.VISIBLE) {
            mAudioAttachmentConstraint.visibility = View.GONE
        }
        mMessageImageView.visibility = View.VISIBLE
        mFileNameTextView.visibility = View.GONE

    }

    private fun updateAudioAttachmentUI(fileName: String) {
        if (mMessageImageView.visibility == View.VISIBLE) {
            mMessageImageView.visibility = View.GONE
        }
        mFileNameTextView.visibility = View.GONE
        mAudioAttachmentConstraint.visibility = View.VISIBLE
        mAudioAttachmentFileName.visibility = View.VISIBLE
        mAudioAttachmentFileName.text = fileName
    }

    private fun setPlayButtonIcon(showPlayIcon: Boolean) {
        val currentDrawable = if (mAudioAttachmentPlayButton.tag == null) { R.drawable.ic_play } else { mAudioAttachmentPlayButton.tag }

        if (showPlayIcon && currentDrawable != R.drawable.ic_play) {
            mAudioAttachmentPlayButton.tag = R.drawable.ic_play
            mAudioAttachmentPlayButton.setImageDrawable(mContext.let { ContextCompat.getDrawable(it, R.drawable.ic_play) })

        } else if (!showPlayIcon && currentDrawable != R.drawable.ic_pause_circle) {
            mAudioAttachmentPlayButton.tag = R.drawable.ic_pause_circle
            mAudioAttachmentPlayButton.setImageDrawable(mContext.let { ContextCompat.getDrawable(it, R.drawable.ic_pause_circle) })
        }
    }

    init {
        mMasterItemView = itemView
    }

}