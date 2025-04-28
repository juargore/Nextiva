package com.nextiva.nextivaapp.android.adapters.pagedlistadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatMessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectCallHistoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MeetingListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.BaseViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ChatHeaderViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ChatMessageReceivedViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ChatMessageSentViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectCallHistoryViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactHeaderViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactHeaderViewHolderCompose
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectMeetingsListViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectVoicemailViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.MessageHeaderViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.SmsMessageReceivedViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.SmsMessageSentViewHolder
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.diffcallbacks.ConnectPagedDiffCallback
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager


class PagedConnectListAdapter(private val context: Context,
                              private val masterListListener: MasterListListener,
                              private val calendarManager: CalendarManager,
                              private val dbManager: DbManager,
                              private val sessionManager: SessionManager,
                              private val avatarManager: AvatarManager,
                              private val settingsManager: SettingsManager,
                              private val nextivaMediaPlayer: NextivaMediaPlayer,
                              diffCallback: ConnectPagedDiffCallback = ConnectPagedDiffCallback()) :
    PagingDataAdapter<BaseListItem, RecyclerView.ViewHolder>(diffCallback) {
    private val connectContactHeaderItemType = 0
    private val connectContactItemType = 1
    private val callLogEntryItemType = 2
    private val voicemailItemType = 3
    private val messageHeaderListItem = 5
    private val messageSentListItem = 6
    private val messageReceivedListItem = 7
    private val chatHeaderListItem = 8
    private val chatMessageSentListItem = 9
    private val chatMessageReceivedListItem = 10
    private val connectMeetingItemType = 11

    private val vhSet = HashSet<BaseViewHolder<*>>()

    private val TAG = PagedConnectListAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            connectContactHeaderItemType -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_connect_messages_compose, parent, false)
                ConnectContactHeaderViewHolderCompose(itemView, context, masterListListener)
            }
            connectContactItemType -> ConnectContactViewHolder(parent, context, masterListListener, dbManager, sessionManager)
            voicemailItemType -> ConnectVoicemailViewHolder(LayoutInflater.from(parent.context).inflate(com.nextiva.nextivaapp.android.R.layout.list_item_connect_voicemail, parent, false),
                    context,
                    masterListListener,
                    avatarManager,
                    settingsManager,
                    calendarManager,
                    nextivaMediaPlayer,
                    dbManager,
                    sessionManager
                )
            messageHeaderListItem -> MessageHeaderViewHolder(parent, context, masterListListener)
            messageSentListItem -> SmsMessageSentViewHolder(parent, context, masterListListener)
            messageReceivedListItem -> SmsMessageReceivedViewHolder(parent, context, masterListListener)
            chatHeaderListItem -> ChatHeaderViewHolder(parent, context, masterListListener)
            chatMessageSentListItem -> ChatMessageSentViewHolder(parent, context, masterListListener)
            chatMessageReceivedListItem -> ChatMessageReceivedViewHolder(parent, context, masterListListener)
            connectMeetingItemType -> ConnectMeetingsListViewHolder(parent, context, masterListListener)
            else -> getConnectCallHistoryViewHolder(parent, viewType)
        }.apply {
            if(this is LiveDataDatabaseObserver && !vhSet.contains(this)){
                vhSet.add(this as BaseViewHolder<*>)
            }
        }
    }

    private fun getConnectCallHistoryViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.nextiva.nextivaapp.android.R.layout.list_item_connect_call_history, parent, false)
        return ConnectCallHistoryViewHolder(itemView, context, masterListListener, settingsManager, avatarManager)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listItem = getItem(position)
        when {
            holder is ConnectContactHeaderViewHolderCompose && listItem is ConnectContactHeaderListItem -> holder.bind(listItem)
            holder is ConnectContactViewHolder && listItem is ConnectContactListItem -> holder.bind(listItem)
            holder is ConnectCallHistoryViewHolder && listItem is ConnectCallHistoryListItem -> holder.bind(listItem)
            holder is ConnectVoicemailViewHolder && listItem is VoicemailListItem -> holder.bind(listItem)
            holder is MessageHeaderViewHolder && listItem is MessageHeaderListItem -> holder.bind(listItem)
            holder is SmsMessageSentViewHolder && listItem is SmsMessageListItem -> holder.bind(listItem)
            holder is SmsMessageReceivedViewHolder && listItem is SmsMessageListItem -> holder.bind(listItem)
            holder is ChatHeaderViewHolder && listItem is ChatHeaderListItem -> holder.bind(listItem)
            holder is ChatMessageSentViewHolder && listItem is ChatMessageListItem -> holder.bind(listItem)
            holder is ChatMessageReceivedViewHolder && listItem is ChatMessageListItem -> holder.bind(listItem)
            holder is ConnectMeetingsListViewHolder && listItem is MeetingListItem -> holder.bind(listItem)
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is ConnectContactListItem -> connectContactItemType
        is ConnectContactHeaderListItem -> connectContactHeaderItemType
        is ConnectCallHistoryListItem -> callLogEntryItemType
        is VoicemailListItem -> voicemailItemType
        is MessageHeaderListItem -> messageHeaderListItem
        is SmsMessageListItem -> if ((getItem(position) as SmsMessageListItem).data.isSender == true) messageSentListItem else messageReceivedListItem
        is ChatHeaderListItem -> chatHeaderListItem
        is ChatMessageListItem -> if ((getItem(position) as ChatMessageListItem).data.isSender) chatMessageSentListItem else chatMessageReceivedListItem
        is MeetingListItem -> connectMeetingItemType

        else -> -1
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is ConnectContactViewHolder -> holder.addObservers()
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is ConnectContactViewHolder -> holder.removeObservers()
        }
    }

    fun clean() {
        vhSet.forEach {
            when (it) {
                is ConnectContactViewHolder -> it.removeObservers()
                is SmsMessageSentViewHolder -> it.removeObservers()
                is SmsMessageReceivedViewHolder -> it.removeObservers()
            }
        }
        vhSet.clear()
    }
}