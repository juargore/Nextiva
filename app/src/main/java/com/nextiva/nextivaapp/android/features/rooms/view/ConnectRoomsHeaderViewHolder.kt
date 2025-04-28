package com.nextiva.nextivaapp.android.features.rooms.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.BaseViewHolder
import com.nextiva.nextivaapp.android.databinding.ListItemConnectContactHeaderBinding
import com.nextiva.nextivaapp.android.features.rooms.RoomsMasterListListener
import javax.inject.Inject

internal class ConnectRoomsHeaderViewHolder @Inject constructor(
    itemView: View,
    context: Context,
    masterListListener: MasterListListener
): BaseViewHolder<ConnectRoomsHeaderListItem>(itemView, context, masterListListener), View.OnClickListener {

    private val masterItemView: View

    private lateinit var headerTextView: TextView
    private lateinit var headerArrowTextSwitcher: TextSwitcher
    private lateinit var countTextView: TextView
    private lateinit var divider: View

    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_connect_contact_header, parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
        masterItemView.setOnClickListener(this)
    }

    override fun bind(listItem: ConnectRoomsHeaderListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        headerTextView.text = mListItem.data.title
        headerTextView.contentDescription = mContext.getString(R.string.contacts_list_group_content_description, mListItem.data.title)

        headerArrowTextSwitcher.setCurrentText(if (mListItem.isExpanded) mContext.getString(R.string.fa_chevron_down) else mContext.getString(R.string.fa_chevron_right))
        headerArrowTextSwitcher.visibility = View.VISIBLE

        divider.visibility = if (mListItem.isExpanded) View.GONE else View.VISIBLE

        countTextView.text = mListItem.currentCount.toString()

        mListItem.updateCount = { count ->
            countTextView.text = count.toString()
        }

        setContentDescriptions()
    }

    override fun onClick(p0: View?) {
        if (mMasterListListener != null && mListItem != null) {
            mListItem.isExpanded = !mListItem.isExpanded
            headerArrowTextSwitcher.setCurrentText(if (mListItem.isExpanded) mContext.getString(R.string.fa_chevron_down) else mContext.getString(R.string.fa_chevron_right))
            divider.visibility = if (mListItem.isExpanded) View.GONE else View.VISIBLE
            (mMasterListListener as RoomsMasterListListener).onConnectRoomsHeaderListItemClicked(mListItem)
        }
    }

    private fun bindViews(view: View) {
        val binding: ListItemConnectContactHeaderBinding = ListItemConnectContactHeaderBinding.bind(view)
        headerTextView = binding.listItemConnectContactHeaderTextView
        headerArrowTextSwitcher = binding.listItemConnectContactHeaderArrowImageSwitcher
        countTextView = binding.listItemConnectContactHeaderCount
        divider = binding.listItemConnectContactHeaderDivider
    }

    private fun setContentDescriptions() {
        masterItemView.contentDescription = mContext.getString(R.string.connect_contact_header_list_item_content_description, headerTextView.text)
        headerTextView.contentDescription = mContext.getString(R.string.connect_contact_header_list_item_title_content_description, headerTextView.text)
        countTextView.contentDescription = mContext.getString(R.string.connect_contact_header_list_item_count_content_description, headerTextView.text, countTextView.text)
    }
}