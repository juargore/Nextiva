package com.nextiva.nextivaapp.android.features.rooms.viewmodel

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsHeaderListItem
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsListItem

class ConnectRoomsPagedDiffCallback : DiffUtil.ItemCallback<BaseListItem>() {

    override fun areItemsTheSame(oldItem: BaseListItem, newItem: BaseListItem): Boolean {
        when {
            oldItem is ConnectRoomsHeaderListItem && newItem is ConnectRoomsHeaderListItem -> {
                return (oldItem.itemType == newItem.itemType)
            }
            oldItem is ConnectRoomsListItem && newItem is ConnectRoomsListItem -> {
                    return TextUtils.equals(oldItem.room.roomId, newItem.room.roomId) &&
                            (oldItem.groupValue == newItem.groupValue)
            }
        }

        return false
    }

    override fun areContentsTheSame(oldItem: BaseListItem, newItem: BaseListItem): Boolean {
        when {
            oldItem is ConnectRoomsHeaderListItem && newItem is ConnectRoomsHeaderListItem -> {
                return (oldItem.itemType == newItem.itemType) &&
                        oldItem.isExpanded == newItem.isExpanded
            }
            oldItem is ConnectRoomsListItem && newItem is ConnectRoomsListItem -> {
                return TextUtils.equals(oldItem.room.name, newItem.room.name) &&
                        TextUtils.equals(oldItem.room.description, newItem.room.description) &&
                        (oldItem.room.requestorFavorite == newItem.room.requestorFavorite) &&
                        (oldItem.room.locked == newItem.room.locked)
            }
        }

        return false
    }
}