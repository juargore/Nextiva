package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionHeaderListItem
import com.nextiva.nextivaapp.android.databinding.ListItemDialogContactActionHeaderBinding
import com.nextiva.nextivaapp.android.view.AvatarView
import javax.inject.Inject

internal class DialogContactActionHeaderViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener):
        BaseViewHolder<DialogContactActionHeaderListItem>(itemView, context, masterListListener), View.OnClickListener {

    private lateinit var avatarView: AvatarView
    private lateinit var contactName: TextView
    private lateinit var numberType: TextView
    private lateinit var import: TextView

    private val masterItemView: View

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_dialog_contact_action_header, parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
        import.setOnClickListener(this)
    }

    override fun bind(listItem: DialogContactActionHeaderListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        listItem.phoneTypeInfo?.let { phoneTypeInfo ->
            avatarView.visibility = View.GONE
            numberType.apply {
                visibility = View.VISIBLE
                text = itemView.context.getString(phoneTypeInfo.numberType)
                backgroundTintList = ColorStateList.valueOf(itemView.context.getColor(phoneTypeInfo.color))
            }
            contactName.text = phoneTypeInfo.phoneNumber
        } ?: run {
            numberType.visibility = View.GONE
            val avatarInfo = listItem.contact.avatarInfo
            avatarInfo.fontAwesomeIconResId = R.string.fa_user
            avatarInfo.setIsConnect(true)
            avatarView.setAvatar(avatarInfo)
            avatarView.visibility = View.VISIBLE
            contactName.text = mListItem.contact.uiName ?: mListItem.contact.displayName
        }

        if (listItem.showImport && !listItem.isImported) {
            import.visibility = View.VISIBLE

        } else {
            import.visibility = View.GONE
        }

        setContentDescriptions()
    }

    override fun onClick(view: View?) {
        if (mMasterListListener != null && mListItem != null) {
            mMasterListListener.onDialogContactActionHeaderListItemClicked(mListItem)
        }
    }

    private fun bindViews(view: View) {
        val binding = ListItemDialogContactActionHeaderBinding.bind(view)
        avatarView = binding.listItemDialogContactActionHeaderAvatarView
        contactName = binding.listItemDialogContactActionHeaderContactName
        numberType = binding.listItemDialogContactActionHeaderNumberType
        import = binding.listItemDialogContactActionHeaderButton
    }

    private fun setContentDescriptions() {
        contactName.contentDescription = contactName.text
        numberType.contentDescription = numberType.text
        import.contentDescription = import.text
    }
}