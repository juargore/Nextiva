package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeHeaderListItem
import com.nextiva.nextivaapp.android.databinding.ListItemConnectHomeHeaderBinding
import javax.inject.Inject

internal class ConnectHomeHeaderViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener):
        BaseViewHolder<ConnectHomeHeaderListItem>(itemView, context, masterListListener) {

    private lateinit var title: TextView

    private val masterItemView: View

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_connect_home_header, parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
    }

    override fun bind(listItem: ConnectHomeHeaderListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        title.text = listItem.title

        setContentDescriptions()
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectHomeHeaderBinding.bind(view)

        title = binding.listItemConnectHomeHeaderTitle
    }

    private fun setContentDescriptions() {
        title.contentDescription = mContext.getString(R.string.connect_home_list_item_header_content_description, title.text)
    }
}