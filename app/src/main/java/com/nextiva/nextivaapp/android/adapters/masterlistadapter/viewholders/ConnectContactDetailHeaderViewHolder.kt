package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageSwitcher
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailHeaderListItem
import com.nextiva.nextivaapp.android.databinding.ListItemConnectContactDetailHeaderBinding
import javax.inject.Inject

internal class ConnectContactDetailHeaderViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener):
        BaseViewHolder<ConnectContactDetailHeaderListItem>(itemView, context, masterListListener), View.OnClickListener {

    private val masterItemView: View

    private lateinit var headerTextView: TextView
    private lateinit var headerArrowImageSwitcher: ImageSwitcher
    private lateinit var headerConstraintView: ConstraintLayout

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_connect_contact_detail_header, parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
        masterItemView.setOnClickListener(this)
    }

    override fun bind(listItem: ConnectContactDetailHeaderListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        headerTextView.text = mListItem.data.title
        headerTextView.contentDescription = mContext.getString(
            R.string.contacts_list_group_content_description,
            mListItem.data.title
        )

        headerArrowImageSwitcher.setImageResource(if (mListItem.isExpanded) R.drawable.ic_group_header_arrow_down else R.drawable.ic_group_header_arrow_side)
        headerArrowImageSwitcher.visibility = View.VISIBLE

        headerConstraintView.visibility = if(mListItem.shouldShowHeaderDetails) View.VISIBLE else View.GONE

        setContentDescriptions()
    }

    override fun onClick(p0: View?) {
        if (mMasterListListener != null && mListItem != null) {
            mListItem.isExpanded = !mListItem.isExpanded
            headerArrowImageSwitcher.setImageResource(if (mListItem.isExpanded) R.drawable.ic_group_header_arrow_down else R.drawable.ic_group_header_arrow_side)
            mMasterListListener.onConnectContactDetailHeaderListItemClicked(mListItem)
        }
    }

    private fun bindViews(view: View) {
        val binding: ListItemConnectContactDetailHeaderBinding = ListItemConnectContactDetailHeaderBinding.bind(view)
        headerTextView = binding.listItemConnectContactDetailHeaderTextView
        headerArrowImageSwitcher = binding.listItemConnectContactDetailHeaderArrowImageSwitcher
        headerConstraintView = binding.listItemConnectContactDetailHeaderView

    }

    private fun setContentDescriptions() {

    }
}