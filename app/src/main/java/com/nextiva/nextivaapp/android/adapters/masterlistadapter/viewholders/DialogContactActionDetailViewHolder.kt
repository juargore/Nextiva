package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem
import com.nextiva.nextivaapp.android.databinding.ListItemDialogContactActionDetailBinding
import javax.inject.Inject

internal class DialogContactActionDetailViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener):
        BaseViewHolder<DialogContactActionDetailListItem>(itemView, context, masterListListener), View.OnClickListener {

    private lateinit var title: TextView
    private lateinit var subtitle: TextView

    private val masterItemView: View

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_dialog_contact_action_detail, parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
        masterItemView.setOnClickListener(this)
    }

    override fun bind(listItem: DialogContactActionDetailListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        title.text = listItem.title
        subtitle.text = listItem.subtitle

        setContentDescriptions()
    }

    override fun onClick(p0: View?) {
        if (mMasterListListener != null && mListItem != null) {
            mMasterListListener.onDialogContactActionDetailListItemClicked(mListItem)
        }
    }

    private fun bindViews(view: View) {
        val binding = ListItemDialogContactActionDetailBinding.bind(view)

        title = binding.listItemDialogContactActionDetailTitle
        subtitle = binding.listItemDialogContactActionDetailSubtitle
    }

    private fun setContentDescriptions() {
        title.contentDescription = title.text
        subtitle.contentDescription = subtitle.text
    }
}