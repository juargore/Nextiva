package com.nextiva.nextivaapp.android.features.messaging.viewmodel

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.diffcallbacks.ConnectPagedDiffCallback
import com.nextiva.nextivaapp.android.features.messaging.view.ConversationHeaderViewHolder
import com.nextiva.nextivaapp.android.features.messaging.view.ConversationReceivedViewHolder
import com.nextiva.nextivaapp.android.features.messaging.view.ConversationSentViewHolder

class PagedConversationListAdapter(
    private val context: Context,
    private val masterListListener: MasterListListener,
    private val audioAttachmentInterface: AudioAttachmentInterface,
    private val diffCallback: ConnectPagedDiffCallback = ConnectPagedDiffCallback(),
) : PagingDataAdapter<BaseListItem, RecyclerView.ViewHolder>(diffCallback) {

    private var focusedView: View? = null
    private val messageHeaderListItem = 1
    private val messageSentListItem = 2
    private val messageReceivedListItem = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            messageHeaderListItem -> ConversationHeaderViewHolder(
                parent,
                context,
                masterListListener
            )
            messageSentListItem -> ConversationSentViewHolder(
                parent,
                context,
                masterListListener,
                audioAttachmentInterface
            )
            messageReceivedListItem -> ConversationReceivedViewHolder(
                parent,
                context,
                masterListListener,
                audioAttachmentInterface
            )
            else -> ConversationReceivedViewHolder(
                parent,
                context,
                masterListListener,
                audioAttachmentInterface
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listItem = getItem(position)
        when {
            holder is ConversationHeaderViewHolder && listItem is MessageHeaderListItem -> holder.bind(listItem)
            holder is ConversationSentViewHolder && listItem is SmsMessageListItem -> holder.bind(listItem)
            holder is ConversationReceivedViewHolder && listItem is SmsMessageListItem -> holder.bind(listItem)
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is MessageHeaderListItem -> messageHeaderListItem
        is SmsMessageListItem ->
            if ((getItem(position) as SmsMessageListItem).data.isSender == true)
                messageSentListItem
            else
                messageReceivedListItem

        else -> -1
    }

}
