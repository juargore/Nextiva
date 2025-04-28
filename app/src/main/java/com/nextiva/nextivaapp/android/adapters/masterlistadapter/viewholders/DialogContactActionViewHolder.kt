package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemDialogContactActionBinding
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import javax.inject.Inject

internal class DialogContactActionViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener):
        BaseViewHolder<DialogContactActionListItem>(itemView, context, masterListListener), View.OnClickListener {

    private lateinit var icon: FontTextView
    private lateinit var title: TextView
    private lateinit var textSwitcher: TextSwitcher

    private val masterItemView: View

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_dialog_contact_action, parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
        masterItemView.setOnClickListener(this)
    }

    override fun bind(listItem: DialogContactActionListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        title.text = listItem.title
        icon.setIcon(listItem.icon, Enums.FontAwesomeIconType.REGULAR)

        if (listItem.isEnabled) {
            icon.setTextColor(ContextCompat.getColor(mContext, R.color.connectSecondaryDarkBlue))
            title.setTextColor(ContextCompat.getColor(mContext, R.color.connectSecondaryDarkBlue))

            if (listItem.title == mContext.getString(R.string.connect_contact_long_click_block_number)) {
                icon.setTextColor(ContextCompat.getColor(mContext, R.color.connectSecondaryRed))
                title.setTextColor(ContextCompat.getColor(mContext, R.color.connectSecondaryRed))
            }
        } else {
            icon.setTextColor(ContextCompat.getColor(mContext, R.color.connectGrey09))
            title.setTextColor(ContextCompat.getColor(mContext, R.color.connectGrey09))
        }

        if (listItem.isExpandable) {
            textSwitcher.visibility = View.VISIBLE
            textSwitcher.setCurrentText(if (mListItem.isExpanded) mContext.getString(R.string.fa_chevron_down) else mContext.getString(R.string.fa_chevron_right))

        } else {
            textSwitcher.visibility = View.GONE
        }

        setContentDescriptions()
    }

    override fun onClick(view: View?) {
        if (mMasterListListener != null && mListItem != null) {
            if (mListItem.isEnabled) {
                if (mListItem.isExpandable) {
                    mListItem.isExpanded = !mListItem.isExpanded
                    textSwitcher.setCurrentText(if (mListItem.isExpanded) mContext.getString(R.string.fa_chevron_down) else mContext.getString(R.string.fa_chevron_right))
                }

                mMasterListListener.onDialogContactActionListItemClicked(mListItem)
            }
        }
    }

    private fun bindViews(view: View) {
        val binding = ListItemDialogContactActionBinding.bind(view)

        icon = binding.listItemDialogContactActionIcon
        title = binding.listItemDialogContactActionTitle
        textSwitcher = binding.listItemDialogContactActionArrowImageSwitcher
    }

    private fun setContentDescriptions() {
        title.contentDescription = title.text
    }
}