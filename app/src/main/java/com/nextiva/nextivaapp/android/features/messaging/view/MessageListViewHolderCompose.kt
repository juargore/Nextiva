package com.nextiva.nextivaapp.android.features.messaging.view

import android.content.Context
import android.text.TextPaint
import android.view.View
import androidx.compose.material.DismissDirection
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.BaseViewHolder
import com.nextiva.nextivaapp.android.databinding.ListItemConnectMessagesComposeBinding
import com.nextiva.nextivaapp.android.features.messaging.MessagingMasterListListener
import com.nextiva.nextivaapp.android.features.messaging.helpers.SmsTitleHelper
import com.nextiva.nextivaapp.android.features.messaging.helpers.SmsTitleInfo
import com.nextiva.nextivaapp.android.features.ui.components.MessageListView
import com.nextiva.nextivaapp.android.features.ui.components.SwipeableItem
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.util.CallUtil
import javax.inject.Inject

internal class MessageListViewHolderCompose @Inject constructor(
    itemView: View,
    private val context: Context,
    private val masterListListener: MasterListListener?,
    private val mSessionManager: SessionManager,
    private val mAvatarManager: AvatarManager,
    private val smsTitleHelper: SmsTitleHelper
) : BaseViewHolder<MessageListItem>(itemView, context, masterListListener) {

    private val masterItemView: View
    private lateinit var composeView: ComposeView
    private var ourUuid = mSessionManager.currentUser.userUuid ?: ""
    private var ourNumber = mSessionManager.userDetails?.telephoneNumber?.let { CallUtil.getCountryCode() + it } ?: ""

    init {
        bindViews(itemView)
        masterItemView = itemView
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectMessagesComposeBinding.bind(view)
        composeView = binding.composeView
    }

    override fun bind(listItem: MessageListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        composeView?.setContent {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp
            val enableSwipeActions = listItem.isSwipeActionEnabled && mListItem.isChecked == null
            val smsTitleInfo: SmsTitleInfo = smsTitleHelper.getSMSConversationParticipantInfo(
                conversationDetails = SmsConversationDetails(mListItem.smsMessage, ourNumber, ourUuid),
                width = (screenWidth / 2),
                paint = TextPaint(),
                context = mContext,
            )

            SwipeableItem(
                enableSwiping = enableSwipeActions,
                isRead = mListItem.unReadCount <= 0,
                content = {
                    MessageListView(
                        mListItem = mListItem,
                        smsTitleInfo = smsTitleInfo,
                        avatarManager = mAvatarManager,
                        avatarInfo = smsTitleInfo.avatarInfo,
                        onClick = { onSmsConversationItemClicked(mListItem) },
                        onLongClick = { onSmsConversationLongItemClicked(mListItem) }
                    )
                },
                onShortSwipe = { onShortSwipe(mListItem) },
                onCompleteSwipe = { swipeDirection ->
                    when (swipeDirection) {
                        DismissDirection.StartToEnd -> onSwipedSmsConversationItemMarkAsReadOrUnread(mListItem)
                        DismissDirection.EndToStart -> onSwipedSmsConversationItemDelete(mListItem)
                    }
                }
            )
        }
    }

    private fun onSmsConversationItemClicked(listItem: MessageListItem) {
        (mMasterListListener as? MessagingMasterListListener)?.onSmsConversationItemClicked(listItem)
    }

    private fun onSmsConversationLongItemClicked(listItem: MessageListItem) {
        (mMasterListListener as? MessagingMasterListListener)?.onSmsConversationLongItemClicked(listItem)
    }

    private fun onShortSwipe(listItem: MessageListItem) {
        (mMasterListListener as? MessagingMasterListListener)?.onShortSwipe(listItem)
    }

    private fun onSwipedSmsConversationItemDelete(listItem: MessageListItem) {
        (mMasterListListener as? MessagingMasterListListener)?.onSwipedSmsConversationItemDelete(listItem)
    }

    private fun onSwipedSmsConversationItemMarkAsReadOrUnread(listItem: MessageListItem) {
        (mMasterListListener as? MessagingMasterListListener)?.onSwipedSmsConversationItemMarkAsReadOrUnread(listItem)
    }
}
