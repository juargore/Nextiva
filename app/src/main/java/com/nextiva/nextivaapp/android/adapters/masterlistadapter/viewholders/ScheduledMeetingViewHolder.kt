package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemScheduledMeetingBinding
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventDetail
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import com.nextiva.nextivaapp.android.view.AvatarView
import java.text.SimpleDateFormat
import java.util.Locale


class ScheduledMeetingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private lateinit var mContext: Context
    lateinit var mAvatarView: AvatarView
    lateinit var mTitleTextView: AppCompatTextView
    lateinit var mNumberTextView: AppCompatTextView
    lateinit var mTimeTextView: AppCompatTextView

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_scheduled_meeting, parent, false)
    ) {
        mContext = parent.context
    }

    init {
        bindViews(itemView)
    }

    private fun bindViews(view: View) {
        val binding = ListItemScheduledMeetingBinding.bind(view)
        mAvatarView = binding.avScheduledMeetingListItem
        mTitleTextView = binding.tvListItemMeetingTitle
        mNumberTextView = binding.tvListItemMeetingNumber
        mTimeTextView = binding.tvListItemMeetingTime
    }

    fun bind(listItem: CalendarApiEventDetail) {
        val avatarInfo = AvatarInfo.Builder()
            .setDisplayName(listItem.name)
            .setFontAwesomeIconResId(R.string.fa_user)
            .isConnect(true).build()

        mAvatarView.setAvatar(avatarInfo, false)

        val iconDrawable = FontDrawable(
            mContext,
            R.string.fa_calendar,
            Enums.FontAwesomeIconType.REGULAR
        )
            .withColor(ContextCompat.getColor(mContext, R.color.connectGrey10))
        mNumberTextView.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)

        mTitleTextView.text = listItem.name

        val date = SimpleDateFormat(
            mContext.getString(R.string.date_format_short_time_12_hour),
            Locale.getDefault()
        )

        mNumberTextView.text = mContext.getString(
            R.string.meeting_ended_format,
            date.format(
                listItem.endTime.toLong()
            ).lowercase()
        )

        mTimeTextView.text = FormatterManager.getInstance().format_humanReadableForListItems(
            mContext,
            listItem.endTime.toLong()
        )

        setContentDescriptions()
    }

    private fun setContentDescriptions() {
        mTitleTextView.contentDescription = mContext.getString(
            R.string.meeting_list_item_title_content_description,
            mTitleTextView.text
        )
        mNumberTextView.contentDescription = mContext.getString(
            R.string.meeting_list_item_schedule_content_description,
            mTitleTextView.text
        )
        mAvatarView.contentDescription = mContext.getString(
            R.string.meeting_list_item_avatar_content_description,
            mTitleTextView.text
        )
    }
}