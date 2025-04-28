package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactCategoryListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemConnectContactCategoryBinding
import com.nextiva.nextivaapp.android.util.extensions.setBlockedContactTypeLabel
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

internal class ConnectContactCategoryViewHolder private constructor(itemView: View, val context: Context, masterListListener: MasterListListener):
        BaseViewHolder<ConnectContactCategoryListItem>(itemView, context, masterListListener), View.OnLongClickListener, View.OnClickListener {

    private val masterItemView: View

    private lateinit var icon: FontTextView
    private lateinit var arrow: FontTextView
    private lateinit var title: ViewGroup
    private lateinit var labelBlocked: TextView
    private var topPadding = 0

    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_connect_contact_category,
            parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
    }

    override fun bind(listItem: ConnectContactCategoryListItem) {
        mListItem = listItem

        itemView.setPadding(
            itemView.paddingLeft,
            topPadding + (mListItem.topPadding?.let { top ->
                itemView.context.resources.getDimensionPixelSize(top)
            } ?: 0),
            itemView.paddingRight,
            itemView.paddingBottom
        )

        val titleText: String? = when (mListItem?.title) {
            is String -> mListItem?.title as String
            is Int -> itemView.context.getString(mListItem?.title as Int)
            else -> null
        }

        val textColor = if (listItem.isBlocked == true) {
            ContextCompat.getColor(context, R.color.connectGreyDisabled)
        } else {
            ContextCompat.getColor(context, listItem.textColor)
        }

        title.apply {
            removeAllViews()
            addView(
                TextView(
                    ContextThemeWrapper(itemView.context, listItem.textStyle),
                    null,
                    listItem.textStyle
                ).apply {
                    layoutParams = LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT
                    )
                    text = titleText
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    setTextColor(textColor)
                    setOnClickListener(mListItem.data?.let { this@ConnectContactCategoryViewHolder })
                    setOnLongClickListener(mListItem.data?.let { this@ConnectContactCategoryViewHolder })
                })
        }

        if (listItem.isPhoneOrExtension == true) {
            arrow.visibility = View.VISIBLE
            arrow.setOnClickListener {
                mMasterListListener?.onConnectContactCategoryItemClicked(mListItem)
            }
            if (listItem.isBlocked == true) {
                labelBlocked.setBlockedContactTypeLabel()
            }
        }

        listItem.iconId?.let { resourceId ->
            icon.setTextColor(ContextCompat.getColor(context, listItem.textColor))
            icon.setIcon(
                resourceId,
                listItem.iconType ?: Enums.FontAwesomeIconType.REGULAR
            )
        }

        icon.visibility = mListItem.data?.let { View.INVISIBLE } ?: View.VISIBLE
        masterItemView.contentDescription = mContext.getString(
            R.string.detail_list_item_content_description,
            listItem.title
        )
        title.contentDescription = titleText
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectContactCategoryBinding.bind(view)
        icon = binding.iconFonttextview
        arrow = binding.iconArrowDown
        title = binding.categoryTitleLayout
        labelBlocked = binding.categoryBlockedLabel
        topPadding = itemView.paddingTop
    }

    private fun copySubtitleText() {
        val clipboard = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val text: String = (title.getChildAt(0) as? TextView)?.text.toString()
        val clip = ClipData.newPlainText(mListItem.clipboardTag.toString(), text)

        clipboard.setPrimaryClip(clip)
        Toast.makeText(mContext, mContext.getString(R.string.general_copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }

    override fun onClick(view: View?) {
        mMasterListListener?.onConnectContactCategoryItemClicked(mListItem)
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
