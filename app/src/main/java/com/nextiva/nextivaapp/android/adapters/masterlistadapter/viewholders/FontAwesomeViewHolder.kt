package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.FontAwesomeListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemFontAwesomeBinding
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

internal class FontAwesomeViewHolder private constructor(itemView: View, var context: Context, masterListListener: MasterListListener) : BaseViewHolder<FontAwesomeListItem>(itemView, context, masterListListener) {
    private val mMasterItemView: View

    lateinit var iconText: FontTextView
    lateinit var iconName: TextView
    lateinit var fontType: TextView

    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_font_awesome, parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        mMasterItemView = itemView
    }

    override fun bind(listItem: FontAwesomeListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        iconText.setIcon(listItem.iconId, listItem.fontType)
        iconName.text = listItem.iconName
        fontType.text = when (listItem.fontType) {
            Enums.FontAwesomeIconType.REGULAR -> "regular"
            Enums.FontAwesomeIconType.SOLID -> "solid"
            Enums.FontAwesomeIconType.CUSTOM -> "custom"
            else -> "unknown"
        }
    }

    private fun bindViews(view: View) {
        val binding = ListItemFontAwesomeBinding.bind(view)

        iconText = binding.fontAwesomeListItemIcon
        iconName = binding.fontAwesomeListItemIconName
        fontType = binding.fontAwesomeListItemFontType
    }
}