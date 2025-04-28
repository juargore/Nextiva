package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BottomSheetMenuListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemBottomSheetMenuBinding
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

class BottomSheetMenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private lateinit var context: Context

    constructor(parent: ViewGroup): this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_bottom_sheet_menu, parent, false)) {
        context = parent.context
    }

    private lateinit var layout: ConstraintLayout
    private lateinit var icon: FontTextView
    private lateinit var text: TextView
    private lateinit var label: TextView
    private lateinit var checked: FontTextView

    init {
        bindViews(itemView)
    }

    fun bind(listItem: BottomSheetMenuListItem, itemClickListener: (BottomSheetMenuListItem) -> Unit) {
        if (listItem.icon != null) {
            icon.setIcon(listItem.icon, listItem.iconType ?: Enums.FontAwesomeIconType.REGULAR)

        } else {
            icon.visibility = View.GONE
        }

        text.text = listItem.text
        text.setTypeface(text.typeface, if (listItem.isSelected) Typeface.BOLD else Typeface.NORMAL)

        if (listItem.isChecked) {
            checked.visibility = View.VISIBLE

            checked.setIcon(R.string.fa_check, Enums.FontAwesomeIconType.REGULAR)

        } else {
            checked.visibility = View.GONE
        }

        if (listItem.label.isNullOrEmpty()) {
            label.visibility = View.GONE

        } else {
            label.visibility = View.VISIBLE
            label.text = listItem.label
            label.backgroundTintList = ColorStateList.valueOf(listItem.labelColor ?: context.getColor(R.color.connectGrey03))
        }

        itemView.setOnClickListener { itemClickListener(listItem) }
    }

    private fun bindViews(view: View) {
        val binding = ListItemBottomSheetMenuBinding.bind(view)
        layout = binding.listItemBottomSheetMenuLayout
        icon = binding.listItemBottomSheetMenuIcon
        text = binding.listItemBottomSheetMenuText
        label = binding.listItemBottomSheetMenuLabel
        checked = binding.listItemBottomSheetMenuChecked
    }

    private fun setContentDescriptions() {

    }
}