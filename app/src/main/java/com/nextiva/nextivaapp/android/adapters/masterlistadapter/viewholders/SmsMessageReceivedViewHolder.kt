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
import com.nextiva.nextivaapp.android.databinding.ListItemChatMessageReceivedBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbAttachment
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.MessageUtil
import com.nextiva.nextivaapp.android.util.extensions.makeLinkable
import io.reactivex.observers.DisposableSingleObserver
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class SmsMessageReceivedViewHolder private constructor(
        private val mMasterItemView: View,
        context: Context,
        masterListListener: MasterListListener?
) : SmsMessageViewHolder(mMasterItemView, context, masterListListener), LiveDataDatabaseObserver {

    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var schedulerProvider: SchedulerProvider
    @Inject
    lateinit var sessionManager: SessionManager

    private var participantNumbers: List<String>? = null

    private var containsImage = false

    private var audioFileAttachment: File? = null
    private var imageFileAttachment: ByteArray? = null
    private var attachmentLink: String = ""

    private var activeVoiceMailMessage: LiveData<String>? = null
    private var currentPlayingProgress: LiveData<Int>? = null
    private var voiceMailPaused: LiveData<String>? = null
    private val unknownString = context.getString(R.string.general_unknown)

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

    private val activeAudioMessageIdChangedObserver = Observer<String> { messageId ->
        mListItem?.data?.messageState?.let { messageState ->
            if (messageId != Constants.TEMP_ATTACHMENT_MESSAGE_ID && messageState.messageId != messageId) {
                resetPlayState()
            }
        }
    }

    private val currentPlayingAudioPausedObserver = Observer<String> { messageId ->
        mListItem?.data?.messageState?.let { messageState ->
            if (messageState.messageId == messageId) {
                setPlayButtonIcon(true)
            }
        }
    }

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener?) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_message_received, parent, false),
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

        participantNumbers = mListItem?.data?.groupValue?.split(",")?.map { it.trim() }
        val phoneNumber = CallUtil.getCountryCode() + sessionManager.userDetails?.telephoneNumber

        participantNumbers = participantNumbers?.filter { it != phoneNumber }

        var senderUiName = ""

        mListItem.data.sender?.get(0)?.uiName?.let { uiName ->
            senderUiName = uiName
        }

        if (senderUiName.isEmpty()) {
            mListItem.data.sender?.get(0)?.phoneNumber?.let { senderPhoneNumber ->
                senderUiName = dbManager.getConnectUiNameFromPhoneNumber(CallUtil.getStrippedPhoneNumber(senderPhoneNumber)) ?:
                        CallUtil.phoneNumberFormatNumberDefaultCountry(senderPhoneNumber)
            }
        }

        if (mListItem.bubbleType == Enums.Chats.MessageBubbleTypes.START ||
                mListItem.bubbleType == Enums.Chats.MessageBubbleTypes.SINGLE) {
            mAvatarView?.visibility = View.VISIBLE
            displayAvatarView(senderUiName, mListItem.data.sender?.get(0)?.phoneNumber)

            mUINameTextView?.text = senderUiName
            mUINameTextView?.visibility = View.VISIBLE

        } else {
            mAvatarView?.visibility = View.GONE
        }

        setspeakerButtonState(mListItem.data.messageState?.messageId)


        val attachments = mListItem.data.attachments

        if (attachments?.isNotEmpty() == true) {
            containsImage = true
            mMessageImageView.visibility = View.VISIBLE

            attachments.first().let { attachment ->
                if (!attachment.contentType.isNullOrEmpty() && MessageUtil.isFileTypeSupported(attachment.contentType
                                ?: "")) {
                    mFileNotSupportedTextView.visibility = View.GONE
                    attachmentLink = attachment.link ?: ""
                    attachment.fileName?.let {
                        if (MessageUtil.isFileExtensionSMSSupportedAudioType(it)) {
                            updateAudioAttachmentUI(it)
                        } else {
                            updateImageAttachmentUI()
                        }
                    }
                    attachment.contentType?.let { it ->
                        if (MessageUtil.isImageFile(it)) {
                            attachment.contentData?.let {
                                mMessageImageView.hideProgress()
                                loadImage(attachment)

                            } ?: run {
                                mMessageImageView.imageView?.let { imageView ->
                                    Glide.with(mContext)
                                            .load(ContextCompat.getDrawable(mContext, attachment.getFileSupportedPlaceholderDrawableId()))
                                            .into(imageView)
                                }

                                mMessageImageView.showProgress()
                                mDbManager.saveContentDataFromLink(attachment.link, attachment.thumbnailLink, attachment.contentType)
                                        .subscribe()
                            }

                        } else {
                            updateAudioDuration(attachment.fileDuration)
                            setspeakerButtonState(mListItem.data.messageState?.messageId)
                        }
                    }

                } else {
                    mAudioAttachmentConstraint.visibility = View.GONE
                    mFileNotSupportedTextView.visibility = View.VISIBLE

                    mMessageImageView.imageView?.let { imageView ->
                        Glide.with(mContext)
                                .load(ContextCompat.getDrawable(mContext, attachment.getFileUnsupportedPlaceholderDrawableId()))
                                .into(imageView)
                    }

                    attachment.fileName?.let {
                        if (attachment.contentType?.contains(Enums.Attachment.ContentMajorType.AUDIO) == true || attachment.contentType?.contains(Enums.Attachment.ContentMajorType.VIDEO) == true) {
                            mFileNameTextView.text = attachment.fileName
                            mFileNameTextView.visibility = View.VISIBLE
                        }
                    }
                }

                mAudioAttachmentPlayButton.setOnClickListener {
                    nextivaMediaPlayer.setViewHolderPlayButtonClickedLiveData(true)
                    val audioFile = getAudioFile()
                    if (audioFile != null) {
                        initialiseAudioPlayer(audioFile)

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

            }

        } else {
            mFileNotSupportedTextView.visibility = View.GONE
            mMessageImageView.visibility = View.GONE
            mFileNameTextView.visibility = View.GONE
            mAudioAttachmentConstraint.visibility = View.GONE
        }

        if (!mListItem.data.body.isNullOrEmpty() && !TextUtils.equals(mListItem.data.body, mContext.getString(R.string.chat_details_shared_an_image))) {
            mMessageTextView.visibility = View.VISIBLE
            mMessageTextView.text = mListItem.data.body
            mMessageTextView.makeLinkable()

        } else {
            mMessageTextView.visibility = View.GONE
        }

        addObservers()

        //TODO: temporary fix for backend issue
        if (attachments?.isEmpty() == true && mListItem.data.body.isNullOrEmpty()) {
            mAudioAttachmentConstraint.visibility = View.GONE
            mFileNotSupportedTextView.visibility = View.VISIBLE
            mMessageImageView.visibility = View.VISIBLE
            mMessageImageView.imageView?.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_unsupported_file))
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
        mMessageContainer.setBackgroundResource(if (isNightModeEnabled(mContext, mSettingsManager)) R.drawable.shape_message_received else R.drawable.shape_message_received)
        mMessageTextView.elevation = 4.0f

        mDatetimeTextView.text = mListItem.humanReadableDatetime
        mDatetimeTextView.visibility = if (mListItem.showTimeSeparator) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }


    fun bindViews(view: View) {
        val binding = ListItemChatMessageReceivedBinding.bind(view)

        mAvatarView = binding.listItemChatMessageAvatarView
        mMessageTextView = binding.listItemChatMessageTextView
        mMessageContainer = binding.listItemMessageContainer
        mMessageImageView = binding.listItemChatMessageImageView
        mDatetimeTextView = binding.listItemChatMessageDatetimeTextView
        mUINameTextView = binding.listItemChatMessageUserTextView
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

    private fun setspeakerButtonState(messageId: String?) {
        if (!nextivaMediaPlayer.getCurrentActiveSpeakerMessageId().isNullOrEmpty() && nextivaMediaPlayer.getCurrentActiveSpeakerMessageId().equals(messageId)) {
            mAudioSpeakerButton.imageTintList = setAudioSpeakerButtonTint(nextivaMediaPlayer.isSpeakerPhoneEnabled())
        } else {
            mAudioSpeakerButton.imageTintList = setAudioSpeakerButtonTint(false)
        }
    }

    private fun getAudioFile(): File? {
        return mListItem.data.messageState?.messageId?.let { it1 -> nextivaMediaPlayer.getAudioFileFromCache(mContext, it1) }
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
        activeVoiceMailMessage = nextivaMediaPlayer.getActiveVoicemailMessageIdChangedLiveData().apply{ observeForever(activeAudioMessageIdChangedObserver) }
        currentPlayingProgress = nextivaMediaPlayer.getCurrentPlayingProgressChangedLiveData().apply { observeForever(currentPlayingProgressChangedObserver) }
        voiceMailPaused = nextivaMediaPlayer.getCurrentPlayingVoicemailPausedLiveData().apply{ observeForever(currentPlayingAudioPausedObserver) }
    }

    override fun removeObservers() {
        activeVoiceMailMessage?.removeObserver(activeAudioMessageIdChangedObserver)
        currentPlayingProgress?.removeObserver(currentPlayingProgressChangedObserver)
        voiceMailPaused?.removeObserver(currentPlayingAudioPausedObserver)
    }

    private fun setAudioSpeakerButtonTint(active: Boolean): ColorStateList {
        return ColorStateList.valueOf(
                if (active) {
                    ContextCompat.getColor(mContext, R.color.nextivaPrimaryBlue)

                } else {
                    ContextCompat.getColor(mContext, R.color.coolGray_50)
                })
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
        mAudioAttachmentConstraint.background = null
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

    private fun displayAvatarView(senderUiName: String?, senderPhoneNumber: String?) {

        val dbParticipantsList: List<SmsParticipant>?

        val avatarInfo = AvatarInfo.Builder()
                .build()

        avatarInfo.photoData = dbManager.getContactFromPhoneNumberInThread(senderPhoneNumber).value?.avatarInfo?.photoData

        when (participantNumbers?.size) {

            // add o case
            Enums.SMSMessages.ConversationTypes.SELF_MESSAGE_PARTICIPANT_COUNT -> {

                if (!TextUtils.isEmpty(senderUiName)) {
                    avatarInfo.displayName = senderUiName

                } else {
                    avatarInfo.iconResId = R.drawable.ic_person
                }
                mAvatarView?.setAvatar(avatarInfo)
            }

            Enums.SMSMessages.ConversationTypes.MESSAGE_CONVERSATION_SINGLE_PARTICIPANT_COUNT -> {
                dbParticipantsList = mListItem.data.getDisplayNameString(participantNumbers)
                if (!dbParticipantsList.isNullOrEmpty()) {
                    if (!TextUtils.isEmpty(senderUiName)) {
                        avatarInfo.displayName = senderUiName
                    } else {
                        avatarInfo.iconResId = R.drawable.ic_person
                    }
                }
                mAvatarView?.setAvatar(avatarInfo)
            }
            else -> {
                mListItem.data.sender?.let { sender ->
                    if (!TextUtils.isEmpty(sender.firstOrNull()?.name)) {
                        avatarInfo.displayName = sender.firstOrNull()?.name

                    } else if (!TextUtils.isEmpty(sender.firstOrNull()?.phoneNumber)) {
                        if (!TextUtils.isEmpty(senderUiName)) {
                            avatarInfo.displayName = senderUiName
                        } else {
                            avatarInfo.iconResId = R.drawable.ic_person
                        }
                    } else {
                        avatarInfo.iconResId = R.drawable.ic_person
                    }
                }

                mAvatarView?.setAvatar(avatarInfo)
                /*avatarInfo.iconResId = R.drawable.ic_people_outline
                mAvatarView?.setAvatar(mAvatarManager.getBitmap(avatarInfo))*/

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

    private fun setContentDescriptions() {
        mMasterItemView.contentDescription = mContext.getString(R.string.chat_message_list_item_received_content_description)
        mMessageTextView.contentDescription = mContext.getString(R.string.chat_message_list_item_message_content_description)
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
}