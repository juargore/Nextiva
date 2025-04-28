package com.nextiva.nextivaapp.android.adapters.pagedlistadapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactDetailHeaderViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactDetailViewHolder
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactViewHolder
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.diffcallbacks.ConnectContactDiffCallback
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager

class ConnectContactListAdapter(private val mContext: Context,
                                private val mMasterListListener: MasterListListener,
                                private val dbManager: DbManager,
                                private val sessionManager: SessionManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val connectContactItemType = 1
    private val connectContactDetailListItem = 2 
    private val connectContactDetailHeaderListItem = 3

    private var updatedList: ArrayList<BaseListItem> = ArrayList()
    var contactList: ArrayList<BaseListItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            connectContactItemType -> ConnectContactViewHolder(parent, mContext, mMasterListListener, dbManager, sessionManager)
            connectContactDetailListItem -> ConnectContactDetailViewHolder(parent, mContext, mMasterListListener)
            connectContactDetailHeaderListItem -> ConnectContactDetailHeaderViewHolder(parent, mContext, mMasterListListener)
            else -> ConnectContactViewHolder(parent, mContext, mMasterListListener, dbManager, sessionManager)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listItem = updatedList[position]

        if (contactList.getOrNull(position) == null) {
            contactList.add(listItem)
        } else {
            contactList[position] = listItem
        }

        if (holder is ConnectContactViewHolder && listItem is ConnectContactListItem) {
            holder.bind(listItem)
        } else if (holder is ConnectContactDetailHeaderViewHolder && listItem is ConnectContactDetailHeaderListItem) {
            holder.bind(listItem)
        } else if (holder is ConnectContactDetailViewHolder && listItem is ConnectContactDetailListItem) {
            holder.bind(listItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (updatedList[position]) {
            is ConnectContactListItem -> connectContactItemType
            is ConnectContactDetailListItem -> connectContactDetailListItem
            is ConnectContactDetailHeaderListItem -> connectContactDetailHeaderListItem
            else -> -1
        }
    }

    override fun getItemCount(): Int {
        return updatedList.size
    }

    fun updatePresence(presence: DbPresence) {
        updatedList.forEachIndexed { index, baseListItem ->
            (baseListItem as? ConnectContactListItem)?.nextivaContact?.let { contact ->
                if (contact.userId == presence.userId &&
                    (contact.presence?.state != presence.state || contact.presence?.status != presence.status)) {
                        contact.presence = presence
                        notifyItemChanged(index)
                }
            }
        }
    }

    fun updateList(newList: ArrayList<BaseListItem>) {
        val diffResult = DiffUtil.calculateDiff(ConnectContactDiffCallback(updatedList, newList))
        updatedList.clear()
        updatedList.addAll(newList)

        while (contactList.size > updatedList.size) {
            contactList.removeLastOrNull()
        }

        diffResult.dispatchUpdatesTo(this)
    }
}