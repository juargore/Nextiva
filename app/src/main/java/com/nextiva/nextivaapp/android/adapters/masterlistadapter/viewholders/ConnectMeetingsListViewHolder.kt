package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.Manifest
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MeetingListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.MediaCall.CallCategories.SCHEDULED
import com.nextiva.nextivaapp.android.databinding.ListItemConnectMeetingsBinding
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.meetings.MeetingActivity
import com.nextiva.nextivaapp.android.meetings.MeetingUtil
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventDetail
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import com.nextiva.nextivaapp.android.view.AvatarView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

internal class ConnectMeetingsListViewHolder private constructor(
    itemView: View,
    context: Context,
    masterListListener: MasterListListener
) : BaseViewHolder<MeetingListItem>(itemView, context, masterListListener) {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var mIntentManager: IntentManager

    private val mMasterItemView: View
    lateinit var mAvatarView: AvatarView
    lateinit var mTitleTextView: AppCompatTextView
    lateinit var mNumberTextView: AppCompatTextView
    lateinit var mTimeTextView: AppCompatTextView
    lateinit var mJoinButton: AppCompatTextView

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_connect_meetings, parent, false),
        context,
        masterListListener
    )

    init {
        bindViews(itemView)
        mMasterItemView = itemView
    }

    private fun bindViews(view: View) {

        val binding = ListItemConnectMeetingsBinding.bind(view)

        mAvatarView = binding.listItemMeetingsAvatarView
        mTitleTextView = binding.listItemMeetingTitleTextView
        mNumberTextView = binding.listItemMeetingNumberTextView
        mTimeTextView = binding.listItemMeetingTimeTextView
        mJoinButton = binding.listItemMeetingJoinBtn
    }

    override fun bind(listItem: MeetingListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        if (mListItem != null) {
            validateTitle()
            val avatarInfo = AvatarInfo.Builder()
                .setDisplayName(mListItem.title)
                .setFontAwesomeIconResId(R.string.fa_user)
                .isConnect(true).build()

            mAvatarView.setAvatar(avatarInfo, false)

            val iconDrawable = FontDrawable(
                mContext,
                if (mListItem.type.equals(SCHEDULED)) R.string.fa_calendar else R.string.fa_video,
                Enums.FontAwesomeIconType.REGULAR
            )
                .withColor(ContextCompat.getColor(mContext, R.color.connectGrey10))
            mNumberTextView.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)

            if (mListItem.startTime != "") {
                val now = Calendar.getInstance()
                if (mListItem.calendarEvent.allDay == true && isMeetingSameDay(mListItem.calendarEvent)) {
                    mTimeTextView.text = allDayTime(now)
                } else {
                    mTimeTextView.text =
                        FormatterManager.getInstance().format_humanReadableForListItems(
                            mContext,
                            mListItem.startTime.toLong()
                        )
                }
            }

            when (mListItem.calendarEvent.status) {
                Enums.MediaCall.CallStatuses.ACTIVE -> {
                    primaryTitle()
                    enableJoinButton()
                    mNumberTextView.text = mContext.getString(R.string.meeting_in_progress_format, MeetingUtil.getPeopleJoined(mListItem.calendarEvent))
                }
                Enums.MediaCall.CallStatuses.NOT_STARTED -> {
                    primaryTitle()
                    enableJoinButton()
                    val now = Calendar.getInstance()
                    if (mListItem.calendarEvent.allDay == true) {
                        mNumberTextView.text = mContext.getString(R.string.meeting_in_progress_format, 0)
                    } else if (mListItem.calendarEvent.startTime.toLong() >= now.timeInMillis) {
                        mNumberTextView.text = scheduledTime(
                            mListItem.calendarEvent.startTime,
                            mListItem.calendarEvent.endTime
                        )
                    } else
                        mNumberTextView.text = mContext.getString(R.string.meeting_in_progress_format, 0)
                }
                Enums.MediaCall.CallStatuses.INACTIVE -> {
                    secondaryTitle()
                    val time0 = Calendar.getInstance()
                    time0.timeInMillis = mListItem.calendarEvent.startTime.toLong()
                    if(mListItem.calendarEvent.allDay != null && mListItem.calendarEvent.allDay == true && isMeetingSameDay(mListItem.calendarEvent)) {
                        enableJoinButton()
                    }
                    else
                    {
                        mJoinButton.visibility = View.GONE
                    }
                    mNumberTextView.text =
                        if (mListItem.calendarEvent.allDay == true && isMeetingSameDay(mListItem.calendarEvent)
                        ) {
                            allDayTime(time0)
                        } else {
                            scheduledTime(
                                mListItem.calendarEvent.startTime,
                                mListItem.calendarEvent.endTime
                            )
                        }
                }
            }
        }
        setContentDescriptions()
    }

    private fun isMeetingSameDay(calendarEvent: CalendarApiEventDetail): Boolean{
        val time0 = Calendar.getInstance()
        time0.timeInMillis = calendarEvent.startTime.toLong()
        return MeetingUtil.isSameDay(
            calendarEvent.startDate,
            time0.get(Calendar.YEAR),
            time0.get(Calendar.DAY_OF_YEAR)
        )
    }

    private fun allDayTime(calendar: Calendar): String {
        val date = SimpleDateFormat(
            mContext.getString(R.string.date_format_month_day),
            Locale.getDefault()
        )
        return date.format(calendar.time)
    }

    private fun scheduledTime(startTime: String, endTime: String): String {
        val date = SimpleDateFormat(
            mContext.getString(R.string.date_format_short_time_12_hour),
            Locale.getDefault()
        )
        val time0 = date.format(startTime.toLong()).lowercase()
        val time1 = date.format(endTime.toLong()).lowercase()
        return mContext.getString(R.string.meeting_scheduled_format, time0, time1)
    }

    private fun setContentDescriptions() {
        mTitleTextView.contentDescription = mContext.getString(
            R.string.meeting_list_item_title_content_description,
            mTitleTextView.text
        )
        mNumberTextView.contentDescription = mContext.getString(
            R.string.meeting_list_item_number_content_description,
            mTitleTextView.text
        )
        mAvatarView.contentDescription = mContext.getString(
            R.string.meeting_list_item_avatar_content_description,
            mTitleTextView.text
        )
        mJoinButton.contentDescription = mContext.getString(
            R.string.meeting_list_item_join_button_description,
            mTimeTextView.text
        )
    }

    private fun validateTitle() {
        if (mListItem.title.isNullOrBlank() && !mListItem.calendarEvent.attendees.isNullOrEmpty()) {
            mListItem.calendarEvent.attendees!!.sortedBy { it.lastName }
            var names = ""
            mListItem.calendarEvent.attendees!!.forEach { names += it.firstName + " " + it.lastName + "," }
            mListItem.title = names
            mTitleTextView.text = names
        } else {
            mTitleTextView.text = mListItem.title
        }
    }

    private fun primaryTitle() {
        mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.connectPrimaryBlue))
    }

    private fun secondaryTitle() {
        mTitleTextView.setTextColor(
            ContextCompat.getColor(
                mContext,
                R.color.connectSecondaryDarkBlue
            )
        )
    }

    private fun enableJoinButton() {
        mJoinButton.visibility = View.VISIBLE
        val callback = PermissionManager.PermissionGrantedCallback {
            val callTitle = mListItem.title ?: ""
            val callId = mListItem.callId ?: ""
            val meetingStartTime = mListItem.startTime
            val meetingEventId = mListItem.calendarEvent.eventId
            val meetingIntent = sessionManager.userDetails?.email?.let { it1 ->
                sessionManager.userDetails?.fullName?.let { it2 ->
                    MeetingActivity.newIntent(
                        mContext,
                        false,
                        callTitle,
                        callId,
                        meetingEventId.toString(),
                        meetingStartTime,
                        it1,
                        it2,
                        mListItem.calendarEvent
                    )
                }
            }
            LogUtil.d(mListItem.calendarEvent.userUuid)
            mListItem.calendarEvent
            mContext.startActivity(meetingIntent)
        }

        mJoinButton.setOnClickListener {
            (mContext as Activity).let {
                permissionManager.checkMeetingPermission(
                    it,
                    Enums.Analytics.ScreenName.MEETING_LIST_FRAGMENT_SCREEN,
                    Manifest.permission.RECORD_AUDIO,
                    R.string.permission_video_call_record_audio_denied_message,
                    callback,
                    null
                )
            }
        }
    }

}