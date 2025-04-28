package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemMessagesBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.SmsParticipant
import com.nextiva.nextivaapp.android.util.ApplicationUtil
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.MessageUtil
import com.nextiva.nextivaapp.android.util.StringUtil
import com.nextiva.nextivaapp.android.util.extensions.equalsIgnoringEmpty
import com.nextiva.nextivaapp.android.view.AvatarView

internal class MessagesListViewholder private constructor(
    itemView: View,
    private val context: Context,
    private val masterListListener: MasterListListener,
    private val mAvatarManager: AvatarManager?,
    private val mSettingsManager: SettingsManager?,
    private val calendarManager: CalendarManager?,
    private val mSessionManager: SessionManager?,
    private val nextivaMediaPlayer: NextivaMediaPlayer?,
    private val dbManager: DbManager?
) : BaseViewHolder<MessageListItem>(itemView, context, masterListListener), View.OnClickListener {

    constructor(itemView: View, context: Context, masterListListener: MasterListListener) : this(
        itemView,
        context,
        masterListListener,
        null, null, null, null, null, null
    )

    private val mMasterItemView: View = itemView

    lateinit var mAvatarView: AvatarView
    lateinit var mTitleTextView: TextView
    lateinit var mSubtitleTextView: TextView
    lateinit var mTimeTextView: TextView
    lateinit var mUnreadMessageCount: TextView

    init {
        bindViews(itemView)
        itemView.setOnClickListener(this)
    }

    companion object {
        private const val AVATAR_VIEW_WIDTH_DP = 48
        private const val PARTICIPANT_TEXTVIEW_MARGINS_DP = 12
        private const val LIST_ITEM_PADDING_DP = 32
        fun create(
            parent: ViewGroup,
            context: Context,
            masterListListener: MasterListListener,
            mAvatarManager: AvatarManager,
            mSettingsManager: SettingsManager,
            calendarManager: CalendarManager,
            mSessionManager: SessionManager,
            nextivaMediaPlayer: NextivaMediaPlayer,
            dbManager: DbManager
        ): MessagesListViewholder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_messages, parent, false)
            return MessagesListViewholder(
                itemView,
                context,
                masterListListener,
                mAvatarManager,
                mSettingsManager,
                calendarManager,
                mSessionManager,
                nextivaMediaPlayer,
                dbManager
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bind(listItem: MessageListItem) {
        removeItemViewFromParent()

        mListItem = listItem
        if (mListItem != null) {

            mSubtitleTextView.text = when {
                !mListItem.data.preview.isNullOrEmpty() -> mListItem.data.preview
                !mListItem.data.body.isNullOrEmpty() -> mListItem.data.body
                mListItem.data.attachments?.firstOrNull() != null -> mListItem.data.attachments?.firstOrNull()?.getBodyText(mContext) ?: ""
                else -> ""
            }

            /*if (mListItem.data.dbSmsMessage != null && mListItem.data.messageState != null && !TextUtils.isEmpty(mListItem.data.messageState!!.readStatus)) {
                if (mListItem?.data?.messageState?.readStatus == Enums.Messages.ReadStatus.UNREAD && !mListItem?.data?.dbSmsMessage?.isSender!!) {
                    mTitleTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                } else {
                    mTitleTextView.setTypeface(Typeface.DEFAULT)
                }
            }*/

            updateUnreadMessageCount()

            if (mListItem.smsMessage.sent != null) {
                mListItem.smsMessage.sent?.let {
                    val formatterManager = FormatterManager.getInstance()
                    mTimeTextView.text = calendarManager?.let { it1 ->
                        formatterManager.format_humanReadableForMainListItems(
                            mContext,
                            it1,
                            it)
                    }
                }
            } else {
                mTimeTextView.text = ""
            }

        var participantNumbers: List<String>? = mListItem?.data?.groupValue?.split(",")?.map { it.trim() }
        val phoneNumber = CallUtil.getCountryCode() + mSessionManager?.userDetails?.telephoneNumber

        participantNumbers = participantNumbers?.filter { it != phoneNumber }
        val dbParticipantsList: List<SmsParticipant>?
        val avatarInfo = AvatarInfo.Builder()
                .build()
        val displayMetrics = DisplayMetrics()
        (mContext as Activity).windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)


        val r = mContext.getResources()

        val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                (AVATAR_VIEW_WIDTH_DP +
                        PARTICIPANT_TEXTVIEW_MARGINS_DP +
                        LIST_ITEM_PADDING_DP).toFloat(),
                r.displayMetrics
        )

        val width = displayMetrics.widthPixels - px.toInt()

            when ((participantNumbers?.size ?: 0) + (mListItem.data.teams?.size ?: 0)) {

            // add o case
            Enums.SMSMessages.ConversationTypes.SELF_MESSAGE_PARTICIPANT_COUNT -> {
                mTitleTextView.text = StringUtil.getGroupChatParticipantsString(getUiNameList(mListItem.smsMessage.sender),
                    mListItem.data.teams,
                    width,
                    mTitleTextView.paint,
                    mContext)

                if (mListItem.smsMessage.sender?.isNotEmpty() == true) {
                    if (!TextUtils.isEmpty(mListItem.smsMessage.sender?.get(0)?.name)) {
                        avatarInfo.displayName = mListItem.smsMessage.sender?.get(0)?.name

                    } else {
                        avatarInfo.iconResId = R.drawable.ic_person_new
                    }
                }
                mAvatarView.setAvatar(avatarInfo, false)
            }

            Enums.SMSMessages.ConversationTypes.MESSAGE_CONVERSATION_SINGLE_PARTICIPANT_COUNT -> {
                dbParticipantsList = MessageUtil.getDisplayNameStringList(participantNumbers, mListItem.smsMessage)
                if (dbParticipantsList != null) {
                    mTitleTextView.text = getUiNameList(dbParticipantsList)?.firstOrNull() ?: mListItem.smsMessage.teams?.firstOrNull()?.teamName

                    if (mListItem.photoData != null) {
                        avatarInfo.photoData = mListItem.photoData

                    } else if (dbParticipantsList.isNotEmpty()) {
                        when {
                            !TextUtils.isEmpty(dbParticipantsList[0].name) -> avatarInfo.displayName = dbParticipantsList[0].name
                            !TextUtils.isEmpty(dbParticipantsList[0].phoneNumber) -> {
                                val dbDisplayName = dbManager?.getUiNameFromPhoneNumber(dbParticipantsList[0].phoneNumber?.let { CallUtil.getStrippedPhoneNumber(it) })

                                if (!TextUtils.isEmpty(dbDisplayName)) {
                                    avatarInfo.displayName = dbDisplayName
                                } else {
                                    avatarInfo.iconResId = R.drawable.ic_person_new
                                }
                            }
                            else -> avatarInfo.iconResId = R.drawable.ic_person_new
                        }
                    }

                    if (dbParticipantsList.isNotEmpty()) {
                        dbManager?.getContactFromPhoneNumberInThread(
                                dbParticipantsList.first().phoneNumber?.let { CallUtil.getStrippedPhoneNumber(it) })?.value?.let { contact ->
                            avatarInfo.presence = dbManager.getPresenceInThread(contact.jid)
                        }
                    }
                }

                mAvatarView.setAvatar(avatarInfo, false)
            }
            else -> {
                dbParticipantsList = MessageUtil.getDisplayNameStringList(participantNumbers, mListItem.smsMessage)

                avatarInfo.iconResId = R.drawable.avatar_group
                mAvatarView.setAvatar(avatarInfo, false)
                mTitleTextView.text = StringUtil.getGroupChatParticipantsString(getUiNameList(dbParticipantsList),
                    mListItem.data.teams,
                    width,
                    mTitleTextView.paint,
                    mContext)
            }
        }
        }

        mUnreadMessageCount.visibility = View.GONE
        setContentDescriptions()
    }

    private fun bindViews(view: View) {
        val binding = ListItemMessagesBinding.bind(view)

        mAvatarView = binding.listItemMessagesAvatarView
        mTitleTextView = binding.listItemMessageTitleTextView
        mSubtitleTextView = binding.listItemMessageSubTitleTextView
        mTimeTextView = binding.listItemMessageTimeTextView
        mUnreadMessageCount = binding.listItemUnreadMessageCountTextView
    }

    private fun updateUnreadMessageCount() {
        if (mListItem.unReadCount > 0) {
            mTimeTextView.setTextColor(mContext.resources.getColor(R.color.nextivaPrimaryBlue))

        } else {
            mTimeTextView.setTextColor(mContext.resources.getColor(R.color.smsMessageListItemSubtitle))
        }
    }

    private fun getUiNameList(dbParticipantsList: List<SmsParticipant>?): ArrayList<String> {
        val uiNamesList = arrayListOf<String>()

        dbParticipantsList?.forEach { participant ->
            val isParticipantInTeam = mListItem.smsMessage.teams?.any { team ->
                participant.teamUuids?.any { teamUuid ->
                    teamUuid.equalsIgnoringEmpty(team.teamId)
                } == true
            } == true

            if (!isParticipantInTeam) {
                val isCurrentUser = mSessionManager?.currentUser?.userUuid?.equalsIgnoringEmpty(participant.userUUID) == true ||
                        participant.phoneNumber?.let { phoneNumber ->
                            CallUtil.getStrippedPhoneNumber(phoneNumber).equalsIgnoringEmpty(
                                CallUtil.getStrippedPhoneNumber(mSessionManager?.userDetails?.telephoneNumber.orEmpty())
                            )
                        } == true

                if (!isCurrentUser) {
                    val uiName = participant.uiName?.takeIf { it.isNotEmpty() }
                        ?: participant.phoneNumber?.let { CallUtil.phoneNumberFormatNumberDefaultCountry(it) }

                    uiName?.let { uiNamesList.add(it) }
                }
            }
        }

        return uiNamesList
    }

private fun getAvatarName(mDisplayName: String?): String? {
    return if (!TextUtils.isEmpty(mDisplayName)) {
        mDisplayName
    } else {
        ""
    }
}

private fun setImageButtonTint(active: Boolean): ColorStateList {
    return ColorStateList.valueOf(
            if (active) {
                if (mSettingsManager?.let { ApplicationUtil.isNightModeEnabled(mContext, it) } == true) {
                    ContextCompat.getColor(mContext, R.color.white)
                } else {
                    ContextCompat.getColor(mContext, R.color.nextivaPrimaryBlue)
                }

            } else {
                ContextCompat.getColor(mContext, R.color.grey)
            })
}


private fun setContentDescriptions() {

    mTitleTextView.contentDescription = mContext.getString(R.string.message_list_item_name_content_description, mTitleTextView.text)
    mSubtitleTextView.contentDescription = mContext.getString(R.string.message_list_item_number_content_description, mTitleTextView.text)
    mAvatarView.contentDescription = mContext.getString(R.string.message_list_item_avatar_content_description, mTitleTextView.text)

}

// --------------------------------------------------------------------------------------------
// View.OnClickListener Methods
// --------------------------------------------------------------------------------------------
override fun onClick(v: View) {
    if (mListItem != null && mMasterListListener != null) {
        when (v.id) {

            else -> {
                mMasterListListener.onSmsConversationItemClicked(mListItem)
            }
        }
    }
}

}


// --------------------------------------------------------------------------------------------
