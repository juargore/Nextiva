/*
 * Copyright (c) 2022. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.snackbar.Snackbar
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.util.extensions.withFontAwesomeDrawable
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView


/**
 * Created by Thaddeus Dannar on 10/19/22.
 */
open class MeetingInfoView : ConstraintLayout {
    val MEETING_URL_COPY_LABEL = "Nextiva One Meeting Url"

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        init(context)
    }

    lateinit var meetingInfoJoinLinkTextView: TextView
    lateinit var meetingInfoDialInPhoneNumberTextView: TextView
    lateinit var meetingInfoDialInTollFreePhoneNumberTextView: TextView
    lateinit var meetingInfoMeetingIdTextView: TextView
    lateinit var meetingShareInvitationButton: AppCompatButton
    lateinit var meetingJoinUsingAPhoneForAudioButton: AppCompatButton
    lateinit var meetingInfoJoinLinkCopyButton: FontTextView


    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.meeting_info_view, this, true)


        meetingInfoJoinLinkTextView = findViewById(R.id.meeting_info_join_link_text_view)
        meetingInfoDialInPhoneNumberTextView =
            findViewById(R.id.meeting_info_dial_in_phone_number_text_view)
        meetingInfoDialInTollFreePhoneNumberTextView =
            findViewById(R.id.meeting_info_dial_in_toll_free_phone_number_text_view)
        meetingInfoMeetingIdTextView = findViewById(R.id.meeting_info_meeting_id_text_view)
        meetingShareInvitationButton = findViewById(R.id.share_invitation_button)
        meetingJoinUsingAPhoneForAudioButton =
            findViewById(R.id.join_using_a_phone_for_audio_button)
        meetingInfoJoinLinkCopyButton =
            findViewById(R.id.meeting_info_join_link_copy_button_font_text_view)

        meetingInfoJoinLinkCopyButton.setOnClickListener {
            val clip =
                ClipData.newPlainText(MEETING_URL_COPY_LABEL, meetingInfoJoinLinkTextView.text)
            (context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?)?.setPrimaryClip(clip)
            Snackbar.make(this,
                R.string.meeting_link_successfully_copied_to_clipboard,
                Snackbar.LENGTH_LONG)
                .withFontAwesomeDrawable(
                    FontDrawable(context,
                        R.string.fa_check_circle,
                        Enums.FontAwesomeIconType.SOLID)
                        .withColor(ContextCompat.getColor(context,
                            R.color.connectPrimaryGreen))).show()

        }

        meetingInfoJoinLinkTextView.doAfterTextChanged {
            if (meetingInfoJoinLinkTextView.text.trim().isEmpty())
                meetingInfoJoinLinkCopyButton.visibility = GONE
        }

        showTextViewIfText(meetingInfoDialInPhoneNumberTextView)
        showTextViewIfText(meetingInfoMeetingIdTextView)
        showTextViewIfText(meetingInfoDialInTollFreePhoneNumberTextView)

    }

    private fun showTextViewIfText(view: TextView) {
        view.doAfterTextChanged {
            if (view.text.trim().isEmpty())
                view.visibility = GONE
            else
                view.visibility = VISIBLE
        }
    }


}