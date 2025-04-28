package com.nextiva.nextivaapp.android.features.messaging.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.BaseViewHolder
import com.nextiva.nextivaapp.android.databinding.ListItemMessagesBinding
import com.nextiva.nextivaapp.android.features.messaging.MessagingMasterListListener
import com.nextiva.nextivaapp.android.features.messaging.helpers.SMSType
import com.nextiva.nextivaapp.android.features.messaging.helpers.SmsTitleHelper
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.view.AvatarView
import org.threeten.bp.Instant
import javax.inject.Inject


internal class MessageListViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener) : BaseViewHolder<MessageListItem>(itemView, context, masterListListener),
        View.OnClickListener, View.OnLongClickListener {
    private val mMasterItemView: View

    lateinit var parentLayout: ConstraintLayout
    lateinit var mAvatarView: AvatarView
    lateinit var mTitleTextView: TextView
    lateinit var mSubtitleTextView: TextView
    lateinit var mTimeTextView: TextView
    lateinit var mUnreadMessageCount: TextView
    lateinit var mDraftMessagePencil: TextView
    lateinit var checkBox: CheckBox

    private var ourNumber: String = ""
    private var ourUuid: String = ""
    private var titleNotUsableArea = 0

    @Inject
    lateinit var mSessionManager: SessionManager
    @Inject
    lateinit var nextivaMediaPlayer: NextivaMediaPlayer
    @Inject
    lateinit var smsTitleHelper: SmsTitleHelper

    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_connect_messages, parent, false),
        context,
        masterListListener)

    init {
        bindViews(itemView)
        mMasterItemView = itemView

        ourNumber = mSessionManager.userDetails?.telephoneNumber?.let { CallUtil.getCountryCode() + it } ?: ""
        ourUuid = mSessionManager.currentUser.userUuid ?: ""

        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)

        getTitleNonUsableArea()
    }

    private fun getTitleNonUsableArea() {
        val r = mContext.resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            (AVATAR_VIEW_WIDTH_DP +
                    PARTICIPANT_TEXTVIEW_MARGINS_DP +
                    LIST_ITEM_PADDING_DP).toFloat(),
            r.displayMetrics
        )
        val displayMetrics = DisplayMetrics()
        (mContext as Activity).windowManager
            .defaultDisplay
            .getMetrics(displayMetrics)
        titleNotUsableArea = displayMetrics.widthPixels - px.toInt()
    }

    @SuppressLint("SetTextI18n")
    override fun bind(listItem: MessageListItem) {
        removeItemViewFromParent()

        mListItem = listItem
        if (mListItem != null) {

            updateUnreadMessageCount()
            showOrHideDraftIndicator()

            if (mListItem.smsMessage.sent != null) {
                mListItem.smsMessage.sent?.let {
                    val formatterManager = FormatterManager.getInstance()
                    mTimeTextView.text = formatterManager.format_humanReadableForListItems(
                        mContext,
                        it.toEpochMilli())
                }
            } else {
                mTimeTextView.text = ""
            }

            val timeTextWidth = Rect().let {
                mTimeTextView.paint.getTextBounds(
                    mTimeTextView.text.toString(),
                    0,
                    mTimeTextView.text.length,
                    it
                )
                it.width()
            }

            val smsTitleInfo = smsTitleHelper.getSMSConversationParticipantInfo(
                conversationDetails = SmsConversationDetails(mListItem.smsMessage, ourNumber, ourUuid),
                width = titleNotUsableArea - timeTextWidth,
                paint = mTitleTextView.paint,
                context = mContext,
            ).let { smsTitleInfo ->
                mTitleTextView.text = smsTitleInfo.smsTitleName
                mAvatarView.setAvatar(smsTitleInfo.avatarInfo, true)
                smsTitleInfo
            }

            val sender = mListItem.data.sender?.firstOrNull()
            val firstName = sender?.uiFirstName ?: ""
            val lastName = sender?.uiLastName ?: ""

            val userName = if (firstName.isNotEmpty()) "$firstName $lastName" else null

            // get user name if necessary
            val name = when {
                mListItem.smsMessage.isSender.orFalse() -> mContext.getString(R.string.message_list_item_message_you) + ":"
                userName == mTitleTextView.text.toString() ||
                        userName == null -> {
                    if (smsTitleInfo.type == SMSType.OneToOne)
                        ""
                    else
                        "${sender?.phoneNumber.orEmpty()}:"
                }
                else -> "$userName:"
            }

            // bold the user's name if exists
            val ssb = SpannableStringBuilder(name)
            val boldSpan = StyleSpan(Typeface.BOLD)
            ssb.setSpan(boldSpan, 0, name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // get body (normal typeface)
            var body = mListItem.data.body?.ifEmpty { mContext.getString(R.string.Connect_sms_media_message) }

            // get draft body if exists (normal typeface)
            if ((mListItem.smsMessage.sent ?: Instant.MIN) < (mListItem.draftMessage?.sent ?: Instant.MIN)) {
                body = mListItem.draftMessage?.body.orEmpty()
            }

            mSubtitleTextView.text = if (name.isEmpty()) {
                ssb.append("$body")
            } else {
                ssb.append(" $body")
            }
        }

        setupCheckbox(mListItem)
        setContentDescriptions()
    }

    private fun bindViews(view: View) {
        val binding = ListItemMessagesBinding.bind(view)

        parentLayout = binding.listItemMessageMasterLayout
        mAvatarView = binding.listItemMessagesAvatarView
        mTitleTextView = binding.listItemMessageTitleTextView
        mSubtitleTextView = binding.listItemMessageSubTitleTextView
        mTimeTextView = binding.listItemMessageTimeTextView
        mUnreadMessageCount = binding.listItemUnreadMessageCountTextView
        mDraftMessagePencil = binding.listItemDraftMessageTextView
        checkBox = binding.listItemMessageCheckbox
    }

    private fun showOrHideDraftIndicator() {
        if (mListItem.draftMessage != null) {
            mDraftMessagePencil.visibility = View.VISIBLE
        } else {
            mDraftMessagePencil.visibility = View.GONE
        }
    }

    private fun setupCheckbox(mListItem: MessageListItem) {
        checkBox.setOnCheckedChangeListener(null)
        checkBox.setOnClickListener {
            if (this.mListItem != null) {
                (mMasterListListener as? MessagingMasterListListener)?.onSmsConversationItemClicked(this.mListItem)
            }
        }

        val params = mAvatarView.layoutParams as ViewGroup.MarginLayoutParams
        mListItem.isChecked?.let {
            checkBox.visibility = View.VISIBLE
            checkBox.isChecked = it
            params.marginStart = mContext.resources.getDimensionPixelOffset(R.dimen.general_padding_xxxxxlarge)
            if (it) {
                parentLayout.setBackgroundResource(R.drawable.background_bulk_action_message)
            } else {
                parentLayout.background = null
            }
        } ?: run {
            checkBox.visibility = View.GONE
            params.marginStart = mContext.resources.getDimensionPixelOffset(R.dimen.general_padding_xmedium)
            parentLayout.background = null
        }.also {
            mAvatarView.layoutParams = params
        }
    }

    private fun updateUnreadMessageCount() {
        if (mListItem.unReadCount > 0) {
            mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.nextivaPrimaryBlue))
            if (mListItem.unReadCount > 1) {
                mUnreadMessageCount.visibility = View.VISIBLE
                mUnreadMessageCount.text = if (mListItem.unReadCount < 100) {
                    mListItem.unReadCount.toString()
                } else {
                    mContext.getString(R.string.message_list_item_more_than_ninety_nine)
                }
            } else {
                mUnreadMessageCount.visibility = View.GONE
                mUnreadMessageCount.text = ""
            }
        } else {
            mUnreadMessageCount.visibility = View.GONE
            mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.connectSecondaryDarkBlue))
        }
    }

//    private fun getAvatarName(mDisplayName: String?): String? {
//        return if (!TextUtils.isEmpty(mDisplayName)) {
//            mDisplayName
//        } else {
//            ""
//        }
//    }

//    private fun setImageButtonTint(active: Boolean): ColorStateList {
//        return ColorStateList.valueOf(
//            if (active) {
//                if (ApplicationUtil.isNightModeEnabled(mContext, mSettingsManager)) {
//                    ContextCompat.getColor(mContext, R.color.white)
//                } else {
//                    ContextCompat.getColor(mContext, R.color.nextivaPrimaryBlue)
//                }
//
//            } else {
//                ContextCompat.getColor(mContext, R.color.grey)
//            })
//    }


    private fun setContentDescriptions() {
        mTitleTextView.contentDescription = mContext.getString(R.string.message_list_item_name_content_description, mTitleTextView.text)
        mSubtitleTextView.contentDescription = mContext.getString(R.string.message_list_item_number_content_description, mTitleTextView.text)
        mAvatarView.contentDescription = mContext.getString(R.string.message_list_item_avatar_content_description, mTitleTextView.text)
    }

    // --------------------------------------------------------------------------------------------
// View.OnClickListener Methods
// --------------------------------------------------------------------------------------------
    override fun onClick(v: View) {
        if (mListItem != null) {
            (mMasterListListener as? MessagingMasterListListener)?.onSmsConversationItemClicked(mListItem)
        }
    }

    override fun onLongClick(view: View?): Boolean {
        if (mMasterListListener != null && mListItem != null) {
            (mMasterListListener as MessagingMasterListListener).onSmsConversationLongItemClicked(mListItem)
        }

        return true
    }

    companion object {
        private const val AVATAR_VIEW_WIDTH_DP = 56
        private const val PARTICIPANT_TEXTVIEW_MARGINS_DP = 16
        private const val LIST_ITEM_PADDING_DP = 32
    }
}


// --------------------------------------------------------------------------------------------
