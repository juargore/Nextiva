package com.nextiva.nextivaapp.android.features.messaging.viewmodel

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.diffcallbacks.ConnectPagedDiffCallback
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.messaging.helpers.SmsTitleHelper
import com.nextiva.nextivaapp.android.features.messaging.view.MessageListViewHolderCompose
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import javax.inject.Inject

class PagedMessagingListAdapter @Inject constructor(
    private val context: Context,
    private val masterListListener: MasterListListener,
    private val mSessionManager: SessionManager,
    private val mAvatarManager: AvatarManager,
    private val dbManager: DbManager,
    diffCallback: ConnectPagedDiffCallback = ConnectPagedDiffCallback()
) : PagingDataAdapter<BaseListItem, RecyclerView.ViewHolder>(diffCallback) {

    private val connectMessageItemType = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_connect_messages_compose, parent, false)
        return MessageListViewHolderCompose(itemView, context, masterListListener, mSessionManager, mAvatarManager, SmsTitleHelper(dbManager, mSessionManager))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listItem = getItem(position)
        if (holder is MessageListViewHolderCompose && listItem is MessageListItem) holder.bind(listItem)
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is MessageListItem -> connectMessageItemType
        else -> -1
    }

}
