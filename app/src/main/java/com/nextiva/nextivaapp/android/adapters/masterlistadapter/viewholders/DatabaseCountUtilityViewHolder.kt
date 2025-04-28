package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DatabaseCountUtilityListItem
import com.nextiva.nextivaapp.android.databinding.ListItemDatabaseCountUtilityBinding
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

internal class DatabaseCountUtilityViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener):
        BaseViewHolder<DatabaseCountUtilityListItem>(itemView, context, masterListListener) {

    private val masterItemView: View

    private lateinit var icon: FontTextView
    private lateinit var title: TextView
    private lateinit var countText: TextView

    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_database_count_utility, parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
    }

    override fun bind(listItem: DatabaseCountUtilityListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        icon.setIcon(listItem.icon, listItem.iconType)

        title.text = listItem.title
        countText.text = listItem.count.toString()

        setContentDescriptions()
    }

    private fun bindViews(view: View) {
        val binding = ListItemDatabaseCountUtilityBinding.bind(view)

        icon = binding.listItemDatabaseCountUtilityIcon
        title = binding.listItemDatabaseCountUtilityTitle
        countText = binding.listItemDatabaseCountUtilityCount
    }

    private fun setContentDescriptions() {
        title.contentDescription = title.text
        countText.contentDescription = mContext.getString(R.string.connect_home_list_item_count_content_description, countText.text, title.text)
    }
}