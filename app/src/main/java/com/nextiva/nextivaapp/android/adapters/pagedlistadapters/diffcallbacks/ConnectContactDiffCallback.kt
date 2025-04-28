package com.nextiva.nextivaapp.android.adapters.pagedlistadapters.diffcallbacks

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem

class ConnectContactDiffCallback(val oldList: ArrayList<BaseListItem>, val newList: ArrayList<BaseListItem>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        when {
            oldItem is ConnectContactHeaderListItem && newItem is ConnectContactHeaderListItem -> {
                return TextUtils.equals(oldItem.itemType, newItem.itemType)
            }
            oldItem is ConnectContactListItem && newItem is ConnectContactListItem -> {
                return TextUtils.equals(oldItem.strippedContact?.contactTypeId
                        ?: oldItem.nextivaContact?.userId,
                        newItem.strippedContact?.contactTypeId
                                ?: newItem.nextivaContact?.userId) &&
                        oldItem.groupValue == newItem.groupValue
            }
        }

        return false
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        when {
            oldItem is ConnectContactHeaderListItem && newItem is ConnectContactHeaderListItem -> {
                return TextUtils.equals(oldItem.itemType, newItem.itemType) &&
                        oldItem.isExpanded == newItem.isExpanded
            }
            oldItem is ConnectContactListItem && newItem is ConnectContactListItem -> {
                return (oldItem.strippedContact?.favorite ?: oldItem.nextivaContact?.isFavorite) ==
                        (newItem.strippedContact?.favorite ?: newItem.nextivaContact?.isFavorite) &&
                        oldItem.searchTerm == newItem.searchTerm &&
                        TextUtils.equals(oldItem.strippedContact?.contactTypeId ?: oldItem.nextivaContact?.userId,
                                newItem.strippedContact?.contactTypeId ?: newItem.nextivaContact?.userId) &&
                        oldItem.importState == newItem.importState &&
                        TextUtils.equals(oldItem.strippedContact?.uiName ?: oldItem.nextivaContact?.uiName,
                                newItem.strippedContact?.uiName ?: newItem.nextivaContact?.uiName)
            }
        }

        return false
    }
}