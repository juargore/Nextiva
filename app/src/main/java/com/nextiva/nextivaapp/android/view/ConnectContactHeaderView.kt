package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.CallLogEntry
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.Voicemail

class ConnectContactHeaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private lateinit var avatarView: AvatarView
    private lateinit var secondAvatarView: AvatarView
    private lateinit var thirdAvatarView: AvatarView
    private lateinit var nameTextView: TextView
    private lateinit var jobTitleTextView: TextView
    private lateinit var detailTextView: TextView
    lateinit var showProfileTextView: TextView

    init {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_connect_contact_header, this, true)
        avatarView = findViewById(R.id.connect_contact_header_avatar_view)
        secondAvatarView = findViewById(R.id.connect_contact_header_second_avatar_view)
        thirdAvatarView = findViewById(R.id.connect_contact_header_third_avatar_view)
        nameTextView = findViewById(R.id.connect_contact_header_name_text_view)
        jobTitleTextView = findViewById(R.id.connect_contact_header_job_title_text_view)
        detailTextView = findViewById(R.id.connect_contact_header_presence_text_view)
        showProfileTextView = findViewById(R.id.connect_contact_header_show_full_profile_text_view)
    }

    fun setAvatar(avatarInfo: AvatarInfo) {
        avatarInfo.size = AvatarInfo.SIZE_LARGE
        avatarInfo.setIsConnect(true)
        avatarView.setAvatar(avatarInfo)
    }

    fun updatePresence(presence: DbPresence?) {
        avatarView.updatePresence(presence)
        presence?.let {
            detailTextView.visibility = View.VISIBLE
            detailTextView.text = presence.humanReadablePresenceText
        }
    }

    fun setNameText(contact: NextivaContact) {
        setNameText(contact, false)
    }

    fun setNameText(contact: NextivaContact, showProfileVisible: Boolean) {
        nameTextView.text = contact.uiName

        if (contact.contactType == Enums.Contacts.ContactTypes.CONNECT_USER) {
            contact.presence?.let { presence ->
                detailTextView.visibility = View.VISIBLE

                detailTextView.text = presence.humanReadablePresenceText
            }
        }

        // We do not have this data yet
//        else {
//            detailTextView.text = context.getString(R.string.connect_contact_details_last_contacted, "3 min", "you")
//            detailTextView.visibility = View.VISIBLE
//        }

        if (!TextUtils.isEmpty(contact.title)) {
            if (contact.contactType == Enums.Contacts.ContactTypes.CONNECT_USER) {
                jobTitleTextView.text = contact.title

            } else {
                jobTitleTextView.text = if (!TextUtils.isEmpty(contact.company)) {
                    context.getString(R.string.connect_contact_details_business_job, contact.title, contact.company)
                } else {
                    contact.title
                }
            }

            jobTitleTextView.visibility = View.VISIBLE

        } else {
            jobTitleTextView.text = null
            jobTitleTextView.visibility = View.GONE
        }

        if (showProfileVisible) {
            showProfileTextView.visibility = View.VISIBLE
        }
    }

    fun setNameText(callLogEntry: CallLogEntry) {
        nameTextView.text = callLogEntry.humanReadableName ?: context.getString(R.string.connect_contact_details_unknown_contact)
        jobTitleTextView.visibility = View.GONE
    }

    fun setNameText(voicemail: Voicemail) {
        nameTextView.text = voicemail.uiName ?: voicemail.name ?: context.getString(R.string.connect_contact_details_unknown_contact)
        jobTitleTextView.visibility = View.GONE
    }
    
    fun setupWithGroupSms(name: String) {
        nameTextView.maxLines = 2
        nameTextView.ellipsize = TextUtils.TruncateAt.END
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.material_text_headline))
        nameTextView.text = name
    }

    fun setAvatars(avatars: ArrayList<AvatarInfo>) {
        avatars.firstOrNull()?.let { avatarInfo ->
            avatarInfo.strokeWidthResId = R.dimen.hairline_xlarge
            avatarInfo.setIsConnect(true)
            avatarView.setAvatar(avatarInfo, false)
        }

        avatars.getOrNull(1)?.let { avatarInfo ->
            secondAvatarView.visibility = View.VISIBLE
            avatarInfo.strokeWidthResId = R.dimen.hairline_xlarge
            avatarInfo.setIsConnect(true)
            secondAvatarView.setAvatar(avatarInfo, false)
        }
        avatars.getOrNull(2)?.let { avatarInfo ->
            thirdAvatarView.visibility = View.VISIBLE
            avatarInfo.strokeWidthResId = R.dimen.hairline_xlarge

            if (avatars.size > 3) {
                val overflowAvatarInfo = AvatarInfo.Builder()
                        .setFontAwesomeIconResId(R.string.fa_user)
                        .setStrokeWidthResId(R.dimen.hairline_large)
                        .isConnect(true).build()

                overflowAvatarInfo.textColor = R.color.connectSecondaryDarkBlue
                overflowAvatarInfo.setCounter(avatars.size - 2)
                thirdAvatarView.setAvatar(overflowAvatarInfo, false)

            } else {
                avatarInfo.setIsConnect(true)
                avatarInfo.strokeWidthResId = R.dimen.hairline_xlarge
                thirdAvatarView.setAvatar(avatarInfo, false)
            }
        }
    }
}