package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.AssignCoHostAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemAssignCohostBinding
import com.nextiva.nextivaapp.android.meetings.data.CoHostItem
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.view.AvatarView
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

class AssignCoHostViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
    private lateinit var mContext: Context
    private lateinit var mAvatarView: AvatarView
    private lateinit var mNameView: AppCompatTextView
    private lateinit var mAssignIcon: FontTextView
    private lateinit var mAssignCohostItem : LinearLayout
    private lateinit var mItemClickListener: AssignCoHostAdapter.ItemClickListener

    constructor(parent: ViewGroup, itemClickListener: AssignCoHostAdapter.ItemClickListener): this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_assign_cohost, parent, false)) {
        mContext = parent.context
        mItemClickListener = itemClickListener
    }

    init {
        bindViews(itemView)
    }

    private fun bindViews(view: View) {
        val binding = ListItemAssignCohostBinding.bind(view)
        mAvatarView = binding.avListItemAssignCohost
        mNameView = binding.atvListItemAttende
        mAssignIcon = binding.ftvListItemAssignIcon
        mAssignCohostItem = binding.llAssignCohostItem
    }

    fun bind(listItem: CoHostItem) {

        val avatarInfo = AvatarInfo.Builder()
            .setDisplayName(listItem.name)
            .setFontAwesomeIconResId(R.string.fa_user)
            .isConnect(true).build()
        mAvatarView.setAvatar(avatarInfo, false)

        mNameView.text = listItem.name

        when(listItem.type){
            Enums.MediaCall.AttendeeTypes.REGULAR -> {
                mAssignIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectWhite))
                mAssignIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.connectGrey03))
            }
            Enums.MediaCall.AttendeeTypes.MODERATOR -> {
                mAssignIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectWhite))
                mAssignIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.connectSecondaryBlue))
            }
            else -> mAssignIcon.visibility = View.GONE
        }

        mAssignCohostItem.setOnClickListener {
            if(!mItemClickListener.progressBarIsVisible() && listItem.type != null){
                if(listItem.type == Enums.MediaCall.AttendeeTypes.MODERATOR){
                    listItem.type = Enums.MediaCall.AttendeeTypes.REGULAR
                    mAssignIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectWhite))
                    mAssignIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.connectGrey03))
                } else {
                    listItem.type = Enums.MediaCall.AttendeeTypes.MODERATOR
                    mAssignIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectWhite))
                    mAssignIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.connectSecondaryBlue))
                }
                listItem.wasModified = !listItem.wasModified
                mItemClickListener.onItemClick()
            }
        }

        setContentDescriptions()
    }

    private fun setContentDescriptions() {
        mAvatarView.contentDescription = mContext.getString(R.string.assign_cohost_item_avatar_content_desciption, mNameView.text)
        mNameView.contentDescription = mContext.getString(R.string.assign_cohost_item_name_content_desciption, mNameView.text)
        mAssignIcon.contentDescription = mContext.getString(R.string.assign_cohost_item_assign_icon_content_desciption, mNameView.text)
    }
}