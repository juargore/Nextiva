package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectCallDetailListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemConnectCallDetailsBinding
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.models.CallLogEntry
import com.nextiva.nextivaapp.android.models.Voicemail
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import org.threeten.bp.Instant
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

internal class ConnectCallDetailViewHolder @Inject constructor(
    itemView: View,
    context: Context,
    masterListListener: MasterListListener
) : BaseViewHolder<ConnectCallDetailListItem>(itemView, context, masterListListener) {

    private val formatterManager = FormatterManager.getInstance()

    private lateinit var callTypeIcon: FontTextView
    private lateinit var callType: TextView
    private lateinit var extendedTime: TextView
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var duration: TextView

    init {
        bindViews(itemView)
    }

    override fun bind(listItem: ConnectCallDetailListItem) {
        removeItemViewFromParent()
        mListItem = listItem

        endTime.visibility = View.GONE
        duration.visibility = View.GONE

        when {
            listItem.callLogEntry != null -> listItem.callLogEntry?.let { setupCallLogEntry(it) }
            listItem.voicemail != null -> listItem.voicemail?.let { setupVoicemail(it) }
        }

        setContentDescriptions()
    }

    private fun setupCallLogEntry(callLogEntry: CallLogEntry) {
        when (callLogEntry.callType) {
            Enums.Calls.CallTypes.MISSED -> {
                callTypeIcon.setIcon(R.string.fa_phone_alt, Enums.FontAwesomeIconType.REGULAR)
                callTypeIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectPrimaryRed))
                callType.text = mContext.getString(R.string.call_details_call_type_missed_title)
            }
            Enums.Calls.CallTypes.RECEIVED -> {
                callTypeIcon.setIcon(R.string.fa_phone_alt, Enums.FontAwesomeIconType.REGULAR)
                callTypeIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectGrey09))
                callType.text = mContext.getString(R.string.call_details_call_type_received_title)
            }
            Enums.Calls.CallTypes.PLACED -> {
                callTypeIcon.setIcon(R.string.fa_custom_outbound_call, Enums.FontAwesomeIconType.CUSTOM)
                callTypeIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectGrey09))
                callType.text = mContext.getString(R.string.call_details_call_type_placed_title)
            }
        }

        callLogEntry.callInstant?.let {
            setTimeText(it.minusSeconds(callLogEntry.callDuration.toLong()))
            if (callLogEntry.callType != Enums.Calls.CallTypes.MISSED && callLogEntry.callDuration > 0) {
                endTime.visibility = View.VISIBLE
                duration.visibility = View.VISIBLE
                endTime.text = mContext.getString(
                    R.string.connect_call_details_end_time,
                    formatterManager.format_connectHourMinuteTimeStamp(mContext, it)
                )
                duration.text = mContext.getString(
                    R.string.connect_call_details_duration_time,
                    formatterManager.format_humanReadableForCallDuration(
                        callLogEntry.callDuration.toDuration(
                            DurationUnit.SECONDS
                        ), mContext
                    )
                )
            }
        }
    }

    private fun setupVoicemail(voicemail: Voicemail) {
        callTypeIcon.setIcon(R.string.fa_phone_alt, Enums.FontAwesomeIconType.REGULAR)
        callTypeIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectGrey09))
        callType.text = mContext.getString(R.string.call_details_call_type_received_title)

        voicemail.voicemailInstant?.let { setTimeText(it) }
    }

    private fun setTimeText(instant: Instant) {
        extendedTime.text = if (TextUtils.equals(
                formatterManager.format_humanReadableForListItems(mContext, instant.toEpochMilli()),
                formatterManager.format_connectHourMinuteTimeStamp(mContext, instant)
            )
        ) {
            formatterManager.format_connectHourMinuteTimeStamp(mContext, instant)
        } else {
            mContext.getString(
                R.string.connect_call_details_extended_time,
                formatterManager.format_humanReadableForListItems(mContext, instant.toEpochMilli()),
                formatterManager.format_connectHourMinuteTimeStamp(mContext, instant)
            )
        }

        startTime.text = mContext.getString(
            R.string.connect_call_details_start_time,
            formatterManager.format_connectHourMinuteTimeStamp(mContext, instant)
        )
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectCallDetailsBinding.bind(view)

        callTypeIcon = binding.listItemConnectCallDetailsTypeIcon
        callType = binding.listItemConnectCallDetailsCallType
        extendedTime = binding.listItemConnectCallDetailsExtendedTime
        startTime = binding.listItemConnectCallDetailsStartTime
        endTime = binding.listItemConnectCallDetailsEndTime
        duration = binding.listItemConnectCallDetailsDuration
    }

    private fun setContentDescriptions() {
    }
}