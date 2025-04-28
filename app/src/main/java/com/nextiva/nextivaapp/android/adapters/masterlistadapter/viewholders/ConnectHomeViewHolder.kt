package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemConnectHomeBinding
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import javax.inject.Inject

internal class ConnectHomeViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener):
        BaseViewHolder<ConnectHomeListItem>(itemView, context, masterListListener), View.OnClickListener {

    private val masterItemView: View

    private lateinit var icon: FontTextView
    private lateinit var title: TextView
    private lateinit var countText: TextView
    private lateinit var divider: View

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_connect_home, parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
        masterItemView.setOnClickListener(this)
    }

    override fun bind(listItem: ConnectHomeListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        icon.setIcon(listItem.icon, Enums.FontAwesomeIconType.REGULAR)

        title.text = when (listItem.channel) {
            Enums.Platform.ConnectHomeChannels.CALLS -> mContext.getString(R.string.connect_home_call_channel_title)
            Enums.Platform.ConnectHomeChannels.MESSAGES -> mContext.getString(R.string.connect_home_messages_channel_title)
            Enums.Platform.ConnectHomeChannels.TEAM_CHATS -> mContext.getString(R.string.connect_home_team_chats_channel_title)
            else -> ""
        }

        updateCount(listItem.count ?: 0)
        mListItem.updateCount = { updateCount(it) }

        divider.visibility = if (listItem.showDivider) View.VISIBLE else View.GONE

        setContentDescriptions()
    }

    private fun updateCount(count: Int) {
        if (count > 0) {
            countText.visibility = View.VISIBLE
            countText.text = count.toString()

        } else {
            countText.visibility = View.GONE
        }

        setContentDescriptions()
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectHomeBinding.bind(view)

        icon = binding.listItemConnectHomeIcon
        title = binding.listItemConnectHomeTitle
        countText = binding.listItemConnectHomeCount
        divider = binding.listItemConnectHomeDivider
    }

    private fun setContentDescriptions() {
        title.contentDescription = title.text
        countText.contentDescription = mContext.getString(R.string.connect_home_list_item_count_content_description, countText.text, title.text)
    }

    override fun onClick(view: View?) {
        mMasterListListener?.onConnectHomeListItemClicked(mListItem)
    }
}