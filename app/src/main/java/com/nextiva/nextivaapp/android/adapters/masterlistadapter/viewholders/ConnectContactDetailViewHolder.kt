package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem
import com.nextiva.nextivaapp.android.constants.Enums.Platform.ConnectContactDetailClickAction
import com.nextiva.nextivaapp.android.databinding.ListItemConnectContactDetailBinding
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import javax.inject.Inject

internal class ConnectContactDetailViewHolder private constructor(itemView: View, val context: Context, masterListListener: MasterListListener):
        BaseViewHolder<ConnectContactDetailListItem>(itemView, context, masterListListener), View.OnLongClickListener, View.OnClickListener {

    private val masterItemView: View

    private lateinit var icon: FontTextView
    private lateinit var title: TextView
    private lateinit var linkTitle: TextView
    private lateinit var subtitle: TextView
    private lateinit var arrowDown: FontTextView

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_connect_contact_detail,
            parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
        subtitle.setOnClickListener(this)
        arrowDown.setOnClickListener(this)
        linkTitle.setOnClickListener(this)
        subtitle.setOnLongClickListener(this)
    }

    override fun bind(listItem: ConnectContactDetailListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        if (listItem.iconId == R.string.fa_envelope || listItem.title == mContext.getString(R.string.user_details_item_time_zone)){
            subtitle.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        }

        title.text = listItem.title
        linkTitle.text = listItem.title
        if (listItem.isClickable && TextUtils.isEmpty(listItem.subtitle)) {
            title.visibility = View.GONE
            linkTitle.visibility = View.VISIBLE
            subtitle.visibility = View.GONE
        } else {
            title.visibility = View.VISIBLE
            linkTitle.visibility = View.GONE
            subtitle.visibility = View.VISIBLE
        }
        icon.setIcon(listItem.iconId, listItem.iconType)
        subtitle.maxLines = listItem.maxSubtitleLines
        subtitle.text = listItem.subtitle
        subtitle.setTextColor(
                if (listItem.actionType == ConnectContactDetailClickAction.EMAIL) {
                    ContextCompat.getColor(context, R.color.connectPrimaryBlue)
                } else {
                    if (listItem.isBlocked) {
                        ContextCompat.getColor(context, R.color.connectGreyDisabled)
                    } else {
                        ContextCompat.getColor(context, R.color.connectSecondaryDarkBlue)
                    }
                }
        )
        arrowDown.visibility =
                if (listItem.actionType == ConnectContactDetailClickAction.PHONE) View.VISIBLE else View.GONE

        setContentDescriptions()
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectContactDetailBinding.bind(view)

        icon = binding.connectContactDetailListItemIcon
        title = binding.connectContactDetailListItemTitle
        linkTitle = binding.connectContactDetailListItemLinkTitle
        subtitle = binding.connectContactDetailListItemSubtitle
        arrowDown = binding.connectContactDetailListItemArrow
    }

    private fun copySubtitleText() {
        val clipboard = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip = ClipData.newPlainText(
                mContext.getString(R.string.general_copied_to_clipboard_label,
                        title,
                        mListItem.uiName ),
                subtitle.text)

        clipboard.setPrimaryClip(clip)
        Toast.makeText(mContext, mContext.getString(R.string.general_copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }

    private fun setContentDescriptions() {
        masterItemView.contentDescription = mContext.getString(R.string.detail_list_item_content_description, title.text)
        title.contentDescription = title.text
        subtitle.contentDescription = mContext.getString(R.string.detail_list_item_subtitle_content_description, title.text)
    }

    override fun onClick(view: View?) {
        mMasterListListener?.onConnectContactDetailListItemClicked(mListItem)
    }

    override fun onLongClick(view: View?): Boolean {
        val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popup = layoutInflater.inflate(R.layout.popup_copy, null)
        val copyTextView: TextView = popup.findViewById(R.id.connect_copy_text)
        val popupWindow = PopupWindow(popup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.setOnDismissListener { masterItemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.connectWhite)) }

        copyTextView.setOnClickListener {
            copySubtitleText()
            popupWindow.dismiss()
        }

        popup.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

        popupWindow.showAsDropDown(masterItemView,
                masterItemView.width / 2,
                -masterItemView.height - popup.measuredHeight + 24)

        masterItemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.connectGrey02))

        return true
    }
}