package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemVoicemailBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.ApplicationUtil
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.view.AvatarView
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.observers.DisposableSingleObserver
import org.threeten.bp.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VoicemailViewHolder private constructor(
    itemView: View,
    @ApplicationContext context: Context,
    masterListListener: MasterListListener
) : BaseViewHolder<VoicemailListItem>(itemView, context, masterListListener), View.OnClickListener {
    private val mMasterItemView: View

    lateinit var avatarView: AvatarView
    lateinit var titleTextView: TextView
    lateinit var subtitleTextView: TextView
    lateinit var timeTextView: TextView
    lateinit var expandedLayout: ConstraintLayout
    lateinit var playButton: ImageButton
    lateinit var seekBar: SeekBar
    lateinit var durationTimeTextView: TextView
    lateinit var speakerButton: ImageButton
    lateinit var phoneButton: ImageButton
    lateinit var readButton: ImageButton
    lateinit var contactButton: ImageButton
    lateinit var deleteButton: ImageButton
    lateinit var transcriptionLayout: ConstraintLayout
    lateinit var transcription: TextView
    lateinit var showMore: TextView
    lateinit var thumbsUp: ImageButton
    lateinit var thumbsDown: ImageButton
    lateinit var rateThisServiceTextView: TextView

    @Inject
    lateinit var mAvatarManager: AvatarManager

    @Inject
    lateinit var mSettingsManager: SettingsManager

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var nextivaMediaPlayer: NextivaMediaPlayer

    @Inject
    lateinit var dbManager: DbManager

    private val currentPlayingProgressChangedObserver = Observer<Int> { progress ->
        if (nextivaMediaPlayer.getCurrentActiveAudioFileMessageId() == mListItem?.voicemail?.messageId) {
            if (progress >= 0) {
                val currentPosition: Int = progress / 1000
                seekBar.progress = currentPosition

            } else if (!nextivaMediaPlayer.isPlaying()){
                resetPlayState()
            }
        }
    }

    private val currentPlayingVoicemailPausedObserver = Observer<String> { messageId ->
        playButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_play))
    }

    private val activeVoicemailMessageIdChangedObserver = Observer<String> { messageId ->
        mListItem?.voicemail?.let { voicemail ->
            if (voicemail.messageId != messageId) {
                collapseListItem()
                resetPlayState()
                nextivaMediaPlayer.finishPlayingAudioFile()

            } else {
                expandListItem()
            }
        }
    }

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_voicemail, parent, false),
        context,
        masterListListener
    )

    init {
        bindViews(itemView)
        mMasterItemView = itemView
        itemView.setOnClickListener(this)

        playButton.setOnClickListener(this)
        phoneButton.setOnClickListener(this)
        readButton.setOnClickListener(this)
        deleteButton.setOnClickListener(this)
        contactButton.setOnClickListener(this)
        speakerButton.setOnClickListener(this)
    }

    override fun bind(listItem: VoicemailListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        if (nextivaMediaPlayer.getCurrentActiveAudioFileMessageId() == listItem.voicemail.messageId) {
            expandListItem()

            if (nextivaMediaPlayer.isPlaying()) {
                playButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_pause_circle
                    )
                )

            } else {
                nextivaMediaPlayer.getCurrentPlayingProgress()?.let { progress ->
                    seekBar.progress = progress.div(1000)
                }
            }

        } else {
            resetPlayState()
            collapseListItem()
        }

        avatarView.setAvatar(
            AvatarInfo.Builder()
                .setPhotoData(listItem.data.avatar)
                .setPresence(
                    DbPresence(
                        mListItem.voicemail.jid,
                        mListItem.voicemail.presenceState,
                        mListItem.voicemail.presencePriority,
                        mListItem.voicemail.statusText,
                        mListItem.voicemail.presenceType
                    )
                )
                .setDisplayName(mListItem.voicemail.uiName)
                .build()
        )

        titleTextView.typeface = if (listItem.data.isRead == false) {
            Typeface.DEFAULT_BOLD
        } else {
            Typeface.DEFAULT
        }

        readButton.imageTintList = setImageButtonTint(listItem.voicemail.isRead != true)
        mListItem.nextivaContact = null

        phoneButton.isEnabled = !listItem.voicemail.address.isNullOrEmpty()
        phoneButton.imageTintList = setImageButtonTint(!listItem.voicemail.address.isNullOrEmpty())

        listItem.data.address?.let {
            mListItem.strippedNumber = CallUtil.getStrippedPhoneNumber(it)
            subtitleTextView.text =
                CallUtil.phoneNumberFormatNumberDefaultCountry(mListItem.strippedNumber)
        }

        if (!listItem.strippedNumber.isNullOrEmpty()) {
            dbManager.getContactFromPhoneNumber(listItem.strippedNumber)
                .subscribe(object : DisposableSingleObserver<DbResponse<NextivaContact>>() {
                    override fun onSuccess(t: DbResponse<NextivaContact>) {
                        if (t.value != null) {
                            mListItem.nextivaContact = t.value
                            contactButton.isEnabled = true
                            contactButton.imageTintList = setImageButtonTint(true)

                        } else {
                            setContactByUserId()
                        }
                    }

                    override fun onError(e: Throwable) {
                        setContactByUserId()
                    }
                })

        } else {
            setContactByUserId()
        }

        speakerButton.imageTintList = setImageButtonTint(nextivaMediaPlayer.isSpeakerPhoneEnabled())
        titleTextView.text = listItem.data.uiName ?: listItem.data.name
                ?: mContext.getString(R.string.general_unavailable)

        durationTimeTextView.text = String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(listItem.data.duration?.toLong() ?: 0L),
            TimeUnit.MILLISECONDS.toSeconds(listItem.data.duration?.toLong() ?: 0L) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            listItem.data.duration?.toLong()
                                ?: 0L
                        )
                    )
        )

        listItem.voicemail.duration?.div(Constants.ONE_SECOND_IN_MILLIS.toInt())?.let { duration ->
            seekBar.max = duration
        }

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                nextivaMediaPlayer.setProgress(progress, fromUser)
            }
        })

        listItem.data.time?.let { time ->
            val formatterManager = FormatterManager.getInstance()
            timeTextView.text = formatterManager.format_humanReadableForMainListItems(
                mContext,
                calendarManager,
                Instant.ofEpochMilli(time)
            )
        }

        setContentDescriptions()
    }

    private fun bindViews(view: View) {
        val binding = ListItemVoicemailBinding.bind(view)

        avatarView = binding.listItemVoicemailAvatarView
        titleTextView = binding.listItemVoicemailTitleTextView
        subtitleTextView = binding.listItemVoicemailSubTitleTextView
        timeTextView = binding.listItemVoicemailTimeView
        expandedLayout = binding.listItemVoicemailExpandedLayout
        playButton = binding.listItemVoicemailPlayButton
        seekBar = binding.listItemVoicemailSeekBar
        durationTimeTextView = binding.listItemVoicemailDurationTimeText
        speakerButton = binding.listItemVoicemailSpeakerButton
        phoneButton = binding.listItemVoicemailPhoneButton
        readButton = binding.listItemVoicemailReadButton
        contactButton = binding.listItemVoicemailContactButton
        deleteButton = binding.listItemVoicemailDeleteButton
        transcriptionLayout = binding.listItemVoicemailTranscriptionLayout
        transcription = binding.listItemVoicemailTranscription
        showMore = binding.listItemVoicemailShowMore
        thumbsUp = binding.listItemVoicemailThumbsUp
        thumbsDown = binding.listItemVoicemailThumbsDown
        rateThisServiceTextView = binding.listItemVoicemailRateThisService

    }

    private fun setContactByUserId() {
        if (mListItem.data.userId.isNullOrEmpty()) {
            contactButton.isEnabled = false
            contactButton.imageTintList = setImageButtonTint(false)

        } else mListItem.data.userId?.let { userId ->
            dbManager.getNextivaContactByUserId(userId)
                .onErrorReturn { NextivaContact("") }
                .subscribe(object : DisposableSingleObserver<NextivaContact>() {
                    override fun onSuccess(contact: NextivaContact) {
                        mListItem.nextivaContact = contact

                        contactButton.isEnabled = contact.dbId != null
                        contactButton.imageTintList = setImageButtonTint(contact.dbId != null)
                    }

                    override fun onError(e: Throwable) {
                        contactButton.isEnabled = false
                        contactButton.imageTintList = setImageButtonTint(false)
                    }
                })
        }
    }

    private fun setImageButtonTint(active: Boolean): ColorStateList {
        return ColorStateList.valueOf(
            if (active) {
                if (ApplicationUtil.isNightModeEnabled(mContext, mSettingsManager)) {
                    ContextCompat.getColor(mContext, R.color.white)
                } else {
                    ContextCompat.getColor(mContext, R.color.nextivaPrimaryBlue)
                }

            } else {
                ContextCompat.getColor(mContext, R.color.grey)
            }
        )
    }

    private fun setRatingImageButtonTint(active: Boolean): ColorStateList {
        return ColorStateList.valueOf(
            if (active) {
                ContextCompat.getColor(mContext, R.color.nextivaPrimaryBlue)

            } else {
                ContextCompat.getColor(mContext, R.color.grey)
            }
        )
    }

    private fun resetPlayState() {
        seekBar.animate()
            .alpha(1.0f)
            .setDuration(0.5.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    seekBar.progress = 0
                }
            })

        speakerButton.imageTintList = setImageButtonTint(nextivaMediaPlayer.isSpeakerPhoneEnabled())
        playButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_play))
    }

    private fun expandListItem() {
        if (expandedLayout.visibility == View.GONE) {
            expandedLayout.visibility = View.VISIBLE
            expandedLayout.alpha = 0.0f
            speakerButton.imageTintList =
                setImageButtonTint(nextivaMediaPlayer.isSpeakerPhoneEnabled())

            expandedLayout.animate()
                .translationY(0f)
                .setDuration(0.5.toLong())
                .alpha(1.0f)
                .setListener(null)

            if (!mListItem.data.transcription.isNullOrEmpty()) {
                transcriptionLayout.visibility = View.VISIBLE
                transcriptionLayout.alpha = 0.0f

                transcriptionLayout.animate()
                    .translationY(0f)
                    .setDuration(0.5.toLong())
                    .alpha(1.0f)
                    .setListener(null)

                val transcriptionText = "\"${mListItem.voicemail.transcription}\""

                transcription.maxLines = 3
                transcription.text = transcriptionText
                transcription.post {
                    if (transcription.lineCount > 3) {
                        showMore.visibility = View.VISIBLE
                        showMore.setOnClickListener {
                            if (transcription.maxLines == 3) {
                                transcription.maxLines = Integer.MAX_VALUE
                                showMore.text =
                                    mContext.getString(R.string.voicemail_list_show_less)

                            } else {
                                transcription.maxLines = 3
                                showMore.text =
                                    mContext.getString(R.string.voicemail_list_show_more)
                            }
                        }
                    } else {
                        showMore.visibility = View.GONE
                    }
                }

                if (mListItem.voicemail.rating != null && (mListItem.voicemail.rating == Enums.VoicemailRating.POSITIVE || mListItem.voicemail.rating == Enums.VoicemailRating.NEGATIVE)) {
                    rateThisServiceTextView.visibility = View.GONE
                    thumbsUp.visibility = View.GONE
                    thumbsDown.visibility = View.GONE
                }


                thumbsUp.setOnClickListener {
                    thumbsUp.imageTintList =
                        setRatingImageButtonTint(true)
                    mMasterListListener?.onPositiveRatingItemClicked(mListItem)
                    dbManager.updateVoicemailRating(
                        Enums.VoicemailRating.POSITIVE,
                        mListItem.voicemail.messageId
                    )
                    setCustomToast()
                }

                thumbsDown.setOnClickListener {
                    thumbsDown.imageTintList = setRatingImageButtonTint(true)
                    mMasterListListener?.onNegativeRatingItemClicked(mListItem)
                    dbManager.updateVoicemailRating(
                        Enums.VoicemailRating.NEGATIVE,
                        mListItem.voicemail.messageId
                    )
                    setCustomToast()
                }
            } else {
                transcriptionLayout.visibility = View.GONE
            }
        }
    }

    private fun setCustomToast() {
        val vi =
            mContext.applicationContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = vi.inflate(R.layout.rating_toast, null)
        val toast = Toast(mContext.applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.setView(v)
        toast.show()

    }

    private fun collapseListItem() {
        if (expandedLayout.visibility == View.VISIBLE) {
            seekBar.progress = 0
            playButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_play))

            expandedLayout.animate()
                .translationY(0f)
                .alpha(0.0f)
                .setDuration(0.5.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        expandedLayout.visibility = View.GONE
                    }
                })

            if (!mListItem.data.transcription.isNullOrEmpty()) {
                transcriptionLayout.animate()
                    .translationY(0f)
                    .alpha(0.0f)
                    .setDuration(0.5.toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            showMore.text = mContext.getString(R.string.voicemail_list_show_more)
                            transcriptionLayout.visibility = View.GONE
                        }
                    })
            } else {
                transcriptionLayout.visibility = View.GONE
            }
        }
    }

    fun observerLiveData() {
        nextivaMediaPlayer.getActiveVoicemailMessageIdChangedLiveData()
            .observeForever(activeVoicemailMessageIdChangedObserver)
        nextivaMediaPlayer.getCurrentPlayingProgressChangedLiveData()
            .observeForever(currentPlayingProgressChangedObserver)
        nextivaMediaPlayer.getCurrentPlayingVoicemailPausedLiveData()
            .observeForever(currentPlayingVoicemailPausedObserver)
    }

    fun removeObservers() {
        nextivaMediaPlayer.getActiveVoicemailMessageIdChangedLiveData()
            .removeObserver(activeVoicemailMessageIdChangedObserver)
        nextivaMediaPlayer.getCurrentPlayingProgressChangedLiveData()
            .removeObserver(currentPlayingProgressChangedObserver)
        nextivaMediaPlayer.getCurrentPlayingVoicemailPausedLiveData()
            .removeObserver(currentPlayingVoicemailPausedObserver)
    }

    private fun setContentDescriptions() {
        mMasterItemView.contentDescription = mContext.getString(
            R.string.voicemail_list_item_content_description,
            if (mListItem.voicemail.isRead == true) {
                "Read"
            } else {
                "Unread"
            }, titleTextView.text
        )
        titleTextView.contentDescription = mContext.getString(
            R.string.voicemail_list_item_name_content_description,
            titleTextView.text
        )
        subtitleTextView.contentDescription = mContext.getString(
            R.string.voicemail_list_item_number_content_description,
            titleTextView.text
        )
        avatarView.contentDescription = mContext.getString(
            R.string.voicemail_list_item_avatar_content_description,
            titleTextView.text
        )
        playButton.contentDescription = mContext.getString(
            R.string.voicemail_list_item_play_button_content_description,
            titleTextView.text
        )
        seekBar.contentDescription = mContext.getString(
            R.string.voicemail_list_item_seek_bar_content_description,
            titleTextView.text
        )
        durationTimeTextView.contentDescription = mContext.getString(
            R.string.voicemail_list_item_duration_content_description,
            titleTextView.text
        )
        speakerButton.contentDescription = mContext.getString(
            R.string.voicemail_list_item_speaker_button_content_description,
            titleTextView.text,
            if (nextivaMediaPlayer.isSpeakerPhoneEnabled()) {
                "On"
            } else {
                "Off"
            }
        )
        phoneButton.contentDescription = mContext.getString(
            R.string.voicemail_list_item_call_button_content_description,
            titleTextView.text
        )
        readButton.contentDescription = mContext.getString(
            R.string.voicemail_list_item_read_button_content_description,
            titleTextView.text,
            if (mListItem.voicemail.isRead == true) {
                "Unread"
            } else {
                "Read"
            }
        )
        contactButton.contentDescription = mContext.getString(
            R.string.voicemail_list_item_contact_button_content_description,
            titleTextView.text
        )
        deleteButton.contentDescription = mContext.getString(
            R.string.voicemail_list_item_delete_button_content_description,
            titleTextView.text
        )
    }

    // --------------------------------------------------------------------------------------------
    // View.OnClickListener Methods
    // --------------------------------------------------------------------------------------------
    override fun onClick(v: View) {
        if (mListItem != null && mMasterListListener != null) {
            when (v.id) {
                playButton.id -> {
                    if (nextivaMediaPlayer.isPlaying()) {
                        nextivaMediaPlayer.pausePlaying()

                    } else {
                        if (seekBar.max != 0 && seekBar.progress == seekBar.max) {
                            resetPlayState()

                        } else {
                            mListItem.data.messageId?.let { messageDetailsPath ->
                                nextivaMediaPlayer.playVoicemail(
                                    mContext,
                                    messageDetailsPath,
                                    mListItem.voicemail.isRead == true
                                )
                                playButton.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        mContext,
                                        R.drawable.ic_pause_circle
                                    )
                                )
                            }
                        }
                    }
                }
                phoneButton.id -> {
                    resetPlayState()
                    nextivaMediaPlayer.finishPlayingAudioFile()
                    mMasterListListener.onVoicemailCallButtonClicked(mListItem)
                }
                readButton.id -> {
                    mMasterListListener.onVoicemailReadButtonClicked(mListItem)
                }
                deleteButton.id -> {
                    mMasterListListener.onVoicemailDeleteButtonClicked(mListItem)
                }
                contactButton.id -> {
                    mMasterListListener.onVoicemailContactButtonClicked(mListItem)
                }
                speakerButton.id -> {
                    nextivaMediaPlayer.toggleSpeakerPhone(mContext)
                    speakerButton.imageTintList =
                        setImageButtonTint(nextivaMediaPlayer.isSpeakerPhoneEnabled())
                    setContentDescriptions()
                }
                else -> {
                    if (expandedLayout.visibility == View.VISIBLE) {
                        nextivaMediaPlayer.setCurrentActiveAudioFileMessageId("")

                    } else {
                        mListItem.voicemail.messageId?.let {
                            nextivaMediaPlayer.setCurrentActiveAudioFileMessageId(it)
                        }
                    }
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------------
}