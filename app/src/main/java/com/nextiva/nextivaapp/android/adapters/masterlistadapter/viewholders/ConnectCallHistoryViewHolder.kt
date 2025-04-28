package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.View
import androidx.compose.material.DismissDirection
import androidx.compose.ui.platform.ComposeView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectCallHistoryListItem
import com.nextiva.nextivaapp.android.databinding.ListItemConnectCallHistoryBinding
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.features.calls.CallsMasterListListener
import com.nextiva.nextivaapp.android.features.ui.components.ConnectCallHistoryListItemView
import com.nextiva.nextivaapp.android.features.ui.components.SwipeableItem
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import javax.inject.Inject

internal class ConnectCallHistoryViewHolder @Inject constructor(
    itemView: View,
    private val context: Context,
    private val masterListListener: MasterListListener,
    private var mSettingsManager: SettingsManager,
    private var mAvatarManager: AvatarManager
) : BaseViewHolder<ConnectCallHistoryListItem>(itemView, context, masterListListener) {

    private val masterItemView: View

    private val formatterManager = FormatterManager.getInstance()

    private lateinit var composeView: ComposeView


    init {
        bindViews(itemView)
        masterItemView = itemView
    }

    override fun bind(listItem: ConnectCallHistoryListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        val avatarInfo = AvatarInfo.Builder()
            .setDisplayName(listItem.callEntry.humanReadableName)
            .setPhotoData(mListItem.callEntry.avatar)
            .setPresence(DbPresence(mListItem.callEntry.jid,
                mListItem.callEntry.presenceState,
                mListItem.callEntry.presencePriority,
                mListItem.callEntry.statusText,
                mListItem.callEntry.presenceType))
            .setFontAwesomeIconResId(R.string.fa_user)
            .isConnect(true)
            .build()

        val time = listItem.callEntry.callInstant?.let {
            formatterManager.format_humanReadableForListItems(
                mContext,
                it.minusSeconds(listItem.callEntry.callDuration.toLong()).toEpochMilli())
        }
        val enableSwipeActions = mSettingsManager.isSwipeActionsEnabled && mListItem.isChecked == null
        composeView?.setContent {
            SwipeableItem(
                isRead = mListItem.callEntry.isRead,
                enableSwiping = enableSwipeActions,
                content = {
                    ConnectCallHistoryListItemView(
                        mListItem= mListItem,
                        avatarInfo= avatarInfo,
                        time = time,
                        avatarManager = mAvatarManager,
                        onClick= {
                            if (mListItem != null){
                                (mMasterListListener as? CallsMasterListListener)?.onConnectCallHistoryListItemClicked(mListItem, absoluteAdapterPosition)
                            }
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
                                (mMasterListListener as? CallsMasterListListener)?.onCallHistorySwipedItemMarkAsReadOrUnread(mListItem)
                            }
                        }
                        DismissDirection.EndToStart -> {
                            if (mListItem != null){
                                (mMasterListListener as? CallsMasterListListener)?.onCallHistorySwipedItemDelete(mListItem)
                            }
                        }
                    }
                },
                forceReset = mListItem.forceChangeState
            )
        }
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectCallHistoryBinding.bind(view)

        composeView = binding.composeView
    }
}