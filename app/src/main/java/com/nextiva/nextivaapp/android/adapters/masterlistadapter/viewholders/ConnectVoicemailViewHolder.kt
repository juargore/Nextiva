package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.compose.material.DismissDirection
import androidx.compose.ui.platform.ComposeView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemConnectVoicemailBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.features.calls.CallsMasterListListener
import com.nextiva.nextivaapp.android.features.ui.components.ConnectVoicemailListItem
import com.nextiva.nextivaapp.android.features.ui.components.SwipeableItem
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.util.CallUtil
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class ConnectVoicemailViewHolder @Inject constructor(
    itemView: View,
    private val context: Context,
    private val masterListListener: MasterListListener,
    private val mAvatarManager: AvatarManager,
    private val mSettingsManager: SettingsManager,
    private val calendarManager: CalendarManager,
    private val nextivaMediaPlayer: NextivaMediaPlayer,
    private val dbManager: DbManager,
    private val sessionManager: SessionManager
) : BaseViewHolder<VoicemailListItem>(itemView, context, masterListListener) {

    private lateinit var composeView: ComposeView

    init {
        bindViews(itemView)
    }

    override fun bind(listItem: VoicemailListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        val avatarInfo = AvatarInfo.Builder()
            .setPhotoData(listItem.voicemail.avatar)
            .isConnect(true)
            .setPresence(
                DbPresence(
                    mListItem.voicemail.jid,
                    mListItem.voicemail.presenceState,
                    mListItem.voicemail.presencePriority,
                    mListItem.voicemail.statusText,
                    mListItem.voicemail.presenceType
                )
            )
            .setDisplayName(mListItem.voicemail.uiName ?: mListItem.voicemail.name)
            .setFontAwesomeIconResId(R.string.fa_user)
            .build()

        val enableSwipeActions = mSettingsManager.isSwipeActionsEnabled && mListItem.isChecked == null

        var time = ""
        listItem.voicemail.voicemailInstant?.let {
            val formatterManager = FormatterManager.getInstance()
            time = formatterManager.format_humanReadableForConnectMainListItems(
                mContext,
                calendarManager,
                it
            )
        }

        mListItem.nextivaContact = null

        listItem.voicemail.formattedPhoneNumber?.let {
            mListItem.strippedNumber = CallUtil.getStrippedPhoneNumber(it)
        }


        var isSmsEnabled = false
        var isValidSmsNumber = false
        if(sessionManager.isSmsEnabled){
            isSmsEnabled = true
            mListItem.strippedNumber?.let { strippedNumber ->
                isValidSmsNumber = CallUtil.isValidSMSNumber(strippedNumber)
            }
        }

        var startTime = ""
        mListItem.voicemail.voicemailInstant?.let {
            val formatterManager = FormatterManager.getInstance()
            startTime = mContext.getString(
                R.string.connect_call_details_start_time,
                formatterManager.getDateFormatter_hmma(mContext).format(it).lowercase()
            )
        }

        val duration = String.format(
            "%02d:%02d",
            TimeUnit.SECONDS.toMinutes(listItem.voicemail.duration?.toLong() ?: 0L),
            TimeUnit.SECONDS.toSeconds(listItem.voicemail.duration?.toLong() ?: 0L) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.SECONDS.toMinutes(
                            listItem.voicemail.duration?.toLong()
                                ?: 0L
                        )
                    )
        )

        val hasRating = mListItem.voicemail.rating.isNullOrEmpty() &&
                mListItem.voicemail.rating != Enums.VoicemailRating.POSITIVE &&
                mListItem.voicemail.rating != Enums.VoicemailRating.NEGATIVE


        composeView?.setContent {
            SwipeableItem(
                enableSwiping = enableSwipeActions,
                isRead = mListItem.voicemail.isRead ?: false,
                content = {
                    ConnectVoicemailListItem(
                        mContext = mContext,
                        mListItem = mListItem,
                        avatarInfo = avatarInfo,
                        avatarManager = mAvatarManager,
                        time = time,
                        startTime = startTime,
                        duration = duration,
                        isSmsEnabled = isSmsEnabled,
                        isValidSmsNumber = isValidSmsNumber,
                        hasRating = hasRating,
                        nextivaMediaPlayer = nextivaMediaPlayer,
                        dbManager = dbManager,
                        onClick = {
                            if (mListItem != null){
                                (mMasterListListener as? CallsMasterListListener)?.onVoicemailListItemClicked(mListItem, absoluteAdapterPosition)
                            }
                        },
                        readButton = {
                            mMasterListListener?.onVoicemailReadButtonClicked(mListItem)
                        },
                        deleteButton = {
                            mMasterListListener?.onVoicemailDeleteButtonClicked(mListItem)
                        },
                        contactButton = {
                            mMasterListListener?.onVoicemailContactButtonClicked(mListItem)
                        },
                        smsButton = {
                            mMasterListListener?.onVoicemailSmsButtonClicked(mListItem)
                        },
                        phoneButton = {
                            nextivaMediaPlayer.finishPlayingAudioFile()
                            mMasterListListener?.onVoicemailCallButtonClicked(mListItem)
                        },
                        thumbsUpButton = {
                            mMasterListListener?.onPositiveRatingItemClicked(mListItem)
                            dbManager.updateVoicemailRating(
                                Enums.VoicemailRating.POSITIVE,
                                mListItem.voicemail.messageId
                            )
                            setCustomToast()
                        },
                        thumbsDownButton = {
                            mMasterListListener?.onNegativeRatingItemClicked(mListItem)
                            dbManager.updateVoicemailRating(
                                Enums.VoicemailRating.NEGATIVE,
                                mListItem.voicemail.messageId
                            )
                            setCustomToast()
                        }
                    )

                },
                onShortSwipe = {
                    if (mListItem != null){
                        (mMasterListListener as? CallsMasterListListener)?.onShortSwipe(mListItem)
                    }
                },
                onCompleteSwipe = { swipeDirection ->
                    when (swipeDirection) {
                        DismissDirection.StartToEnd -> {
                            if (mListItem != null){
                                (mMasterListListener as? CallsMasterListListener)?.onVoicemailSwipedItemMarkAsReadOrUnread(mListItem)
                            }
                        }
                        DismissDirection.EndToStart -> {
                            if(mListItem != null){
                                (mMasterListListener as? CallsMasterListListener)?.onVoicemailSwipedDeleteItem(mListItem)
                            }
                        }
                    }
                },
                forceReset = mListItem.forceChangeState
            )
        }
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectVoicemailBinding.bind(view)
        composeView = binding.composeView
    }

    private fun setCustomToast() {
        val vi = mContext.applicationContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = vi.inflate(R.layout.rating_toast, null)
        val toast = Toast(mContext.applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = v
        toast.show()

    }
}