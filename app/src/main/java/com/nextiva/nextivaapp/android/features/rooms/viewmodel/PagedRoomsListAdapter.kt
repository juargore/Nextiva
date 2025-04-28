package com.nextiva.nextivaapp.android.features.rooms.viewmodel

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.features.rooms.RoomsMasterListListener
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsHeaderListItem
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsHeaderViewHolder
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsListItem
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsViewHolder
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager

class PagedRoomsListAdapter(private val context: Context,
                            private val masterListListener: RoomsMasterListListener,
                            private val sessionManager: SessionManager,
                            private val roomsDbManager: RoomsDbManager,
                            val diffCallback: ConnectRoomsPagedDiffCallback = ConnectRoomsPagedDiffCallback()) :
    PagingDataAdapter<BaseListItem, RecyclerView.ViewHolder>(diffCallback) {
    private val connectRoomHeaderItemType = 0
    private val connectRoomItemType = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            connectRoomHeaderItemType -> ConnectRoomsHeaderViewHolder(parent, context, masterListListener)
            connectRoomItemType -> ConnectRoomsViewHolder(parent, context, masterListListener, sessionManager, roomsDbManager)
            else -> ConnectRoomsViewHolder(parent, context, masterListListener, sessionManager, roomsDbManager)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listItem = getItem(position)
            when {
                holder is ConnectRoomsHeaderViewHolder && listItem is ConnectRoomsHeaderListItem -> holder.bind(listItem)
                holder is ConnectRoomsViewHolder && listItem is ConnectRoomsListItem -> holder.bind(listItem)
            }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is ConnectRoomsListItem -> connectRoomItemType
        is ConnectRoomsHeaderListItem -> connectRoomHeaderItemType

        else -> -1
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is ConnectRoomsViewHolder -> holder.observeLiveData()
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is ConnectRoomsViewHolder -> holder.removeObservers()
        }
    }
}