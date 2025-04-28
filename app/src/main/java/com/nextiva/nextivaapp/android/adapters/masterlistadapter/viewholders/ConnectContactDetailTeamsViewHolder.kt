package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailTeamsListItem
import com.nextiva.nextivaapp.android.databinding.ListItemConnectContactDetailsTeamsBinding
import com.nextiva.nextivaapp.android.view.MaxLineChipGroup
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

internal class ConnectContactDetailTeamsViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener):
        BaseViewHolder<ConnectContactDetailTeamsListItem>(itemView, context, masterListListener) {

    private val masterItemView: View

    private lateinit var icon: FontTextView
    private lateinit var title: TextView
    private lateinit var chipGroup: MaxLineChipGroup
    private lateinit var showMore: TextView

    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_connect_contact_details_teams,
                    parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
    }

    override fun bind(listItem: ConnectContactDetailTeamsListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        title.text = listItem.title
        icon.setIcon(listItem.iconId, listItem.iconType)

        showMore.visibility = View.GONE
        showMore.setOnClickListener {
            chipGroup.setMaxRows(Int.MAX_VALUE)
            showMore.visibility = View.GONE
        }

        chipGroup.setChips(listItem.teamsList, ContextCompat.getColor(mContext, R.color.connectSecondaryDarkBlue), R.color.connectGrey03, { shouldShowMore ->
            showMore.visibility = if (shouldShowMore) View.VISIBLE else View.GONE
        }) { chip -> onChipLongClick(chip) }

        setContentDescriptions()
    }

    private fun onChipLongClick(chip: Chip): Boolean {
        val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popup = layoutInflater.inflate(R.layout.popup_copy, null)
        val copyTextView: TextView = popup.findViewById(R.id.connect_copy_text)
        val popupWindow = PopupWindow(popup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.setOnDismissListener { masterItemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.connectWhite)) }

        copyTextView.setOnClickListener {
            copySubtitleText(chip.text as String)
            popupWindow.dismiss()
        }

        popup.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

        popupWindow.showAsDropDown(chip,
                0,
                -chip.height - popup.measuredHeight)

        return true
    }

    private fun copySubtitleText(chipText: String) {
        val clipboard = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip = ClipData.newPlainText(
                mContext.getString(R.string.general_copied_to_clipboard_label,
                        title,
                        mListItem.uiName),
                        chipText)

        clipboard.setPrimaryClip(clip)
        Toast.makeText(mContext, mContext.getString(R.string.general_copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectContactDetailsTeamsBinding.bind(view)

        icon = binding.connectContactDetailListItemIconTeams
        title = binding.connectContactDetailListItemTitleTeams
        chipGroup = binding.connectContactDetailListItemChipGroup
        showMore = binding.connectContactDetailListItemTeamsShowMore
    }

    private fun setContentDescriptions() {
        masterItemView.contentDescription = mContext.getString(R.string.detail_list_item_content_description, title.text)
        title.contentDescription = title.text
        chipGroup.contentDescription = mContext.getString(R.string.connect_contact_details_chip_group_content_description)
    }
}