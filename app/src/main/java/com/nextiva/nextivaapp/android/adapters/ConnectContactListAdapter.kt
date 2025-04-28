package com.nextiva.nextivaapp.android.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactViewHolder
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.diffcallbacks.ConnectContactDiffCallback
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager

class ConnectContactListAdapter(val contactList: ArrayList<BaseListItem>, private val mContext: Context,
                                private val mMasterListListener: MasterListListener,
                                val dbManager: DbManager,
                                val sessionManager: SessionManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val connectContactItemType = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConnectContactViewHolder(parent, mContext, mMasterListListener, dbManager, sessionManager)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listItem = contactList[position]

        if (holder is ConnectContactViewHolder && listItem is ConnectContactListItem) {
            holder.bind(listItem)
        }
    }

    override fun getItemViewType(position: Int) = connectContactItemType

    override fun getItemCount(): Int {
        return contactList.size
    }

    fun updateList(newList: ArrayList<BaseListItem>) {
        val diffResult = DiffUtil.calculateDiff(ConnectContactDiffCallback(contactList, newList))
        contactList.clear()
        contactList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}